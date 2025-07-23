package com.taiyitistmc.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.bukkit.BukkitSnapshotCaptures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HarvestFarmland.class)
public abstract class MixinHarvestFarmland {

    @Shadow
    @Nullable
    private BlockPos aboveFarmlandPos;

    @WrapWithCondition(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean taiyitist$callFarmEvent(ServerLevel instance, BlockPos pos, boolean b, Entity entity) {
        return CraftEventFactory.callEntityChangeBlockEvent(entity, this.aboveFarmlandPos, Blocks.AIR.defaultBlockState());
    }

    @Inject(method = "tick*", at = @At("HEAD"))
    private void taiyitist$getVillager(ServerLevel level, Villager owner, long gameTime, CallbackInfo ci) {
        BukkitSnapshotCaptures.captureEntityChangeBlock(owner);
    }

    @WrapWithCondition(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean taiyitist$callChangeEvent0(ServerLevel instance, BlockPos pos, BlockState state, @Local(argsOnly = true) Villager villager, @Local(ordinal = 1) BlockState blockState) {
        return CraftEventFactory.callEntityChangeBlockEvent(villager, this.aboveFarmlandPos, blockState);
    }

    @WrapWithCondition(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private boolean taiyitist$callChangeEvent1(ServerLevel instance, Holder holder, BlockPos pos, GameEvent.Context context, @Local(argsOnly = true) Villager villager, @Local(ordinal = 1) BlockState blockState) {
        return CraftEventFactory.callEntityChangeBlockEvent(villager, this.aboveFarmlandPos, blockState);
    }

    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V", shift = At.Shift.BY, by = 2))
    private void taiyitistcallChangeEvent2(ServerLevel serverLevel, Villager villager, long l, CallbackInfo ci, @Local(ordinal = 1) BlockState blockState, @Local boolean flag) {
        flag = CraftEventFactory.callEntityChangeBlockEvent(villager, this.aboveFarmlandPos, blockState);
    }
}
