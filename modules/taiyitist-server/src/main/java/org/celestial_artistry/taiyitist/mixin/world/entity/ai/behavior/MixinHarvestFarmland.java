package org.celestial_artistry.taiyitist.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.celestial_artistry.taiyitist.bukkit.BukkitSnapshotCaptures;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HarvestFarmland.class)
public abstract class MixinHarvestFarmland extends Behavior<Villager> {

    @Shadow @Nullable private BlockPos aboveFarmlandPos;
    @Unique
    private AtomicReference<Block> taiyitist$planted = new AtomicReference<>();
    @Unique
    private AtomicReference<Boolean> taiyitist$flag = new AtomicReference<>();
    @Unique
    private AtomicReference<Villager> taiyitist$villager = new AtomicReference<>();

    public MixinHarvestFarmland(Map<MemoryModuleType<?>, MemoryStatus> map) {
        super(map);
    }

    @WrapWithCondition(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean taiyitist$callFarmEvent(ServerLevel instance, BlockPos pos, boolean b, Entity entity) {
        return CraftEventFactory.callEntityChangeBlockEvent(entity, this.aboveFarmlandPos, Blocks.AIR.defaultBlockState());
    }

    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V", at = @At("HEAD"))
    private void taiyitist$getVillager(ServerLevel level, Villager owner, long gameTime, CallbackInfo ci) {
        taiyitist$villager.set(owner);
    }

    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"),
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V")),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void taiyitist$getFlag(ServerLevel level, Villager owner, long gameTime, CallbackInfo ci, BlockState blockState,
                                Block block, Block block2, SimpleContainer simpleContainer, int i,
                                ItemStack itemStack, boolean bl, BlockItem blockItem, BlockState blockState2) {
        taiyitist$flag.set(bl);
    }

    @Redirect(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"),
    slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V")))
    private boolean taiyitist$addPlantCheck(ServerLevel instance, BlockPos pos, BlockState state) {
        taiyitist$planted.set(state.getBlock());
        if (taiyitist$planted.get() != null && CraftEventFactory.callEntityChangeBlockEvent(taiyitist$villager.get(), this.aboveFarmlandPos, taiyitist$planted.get().defaultBlockState())) {
            instance.setBlockAndUpdate(this.aboveFarmlandPos, taiyitist$planted.get().defaultBlockState());
            instance.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of(taiyitist$villager.get(), taiyitist$planted.get().defaultBlockState()));
            taiyitist$flag.set(true);
        }else {
            taiyitist$flag.set(false);
        }
        return taiyitist$flag.get();
    }

    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private void taiyitist$captureOn(ServerLevel worldIn, Villager owner, long gameTime, CallbackInfo ci) {
        BukkitSnapshotCaptures.captureEntityChangeBlock(owner);
    }
}
