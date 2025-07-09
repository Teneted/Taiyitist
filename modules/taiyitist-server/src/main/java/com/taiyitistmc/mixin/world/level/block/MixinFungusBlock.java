package com.taiyitistmc.mixin.world.level.block;

import com.taiyitistmc.bukkit.BukkitFieldHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(FungusBlock.class)
public class MixinFungusBlock {

    private static AtomicReference<FungusBlock> taiyitist$this = new AtomicReference<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$setThis(ResourceKey resourceKey, Block block, BlockBehaviour.Properties properties, CallbackInfo ci) {
        taiyitist$this.set(((FungusBlock) (Object) this));
    }

    @Inject(method = "method_46682", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z"))
    private static void taiyitist$setTreeType(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, Holder holder, CallbackInfo ci) {
        // CraftBukkit start
        if (taiyitist$this.get() == Blocks.WARPED_FUNGUS) {
            BukkitFieldHooks.setTreeType(org.bukkit.TreeType.WARPED_FUNGUS);
        } else if (taiyitist$this.get() == Blocks.CRIMSON_FUNGUS) {
            BukkitFieldHooks.setTreeType(org.bukkit.TreeType.CRIMSON_FUNGUS);
        }
        // CraftBukkit end
    }
}
