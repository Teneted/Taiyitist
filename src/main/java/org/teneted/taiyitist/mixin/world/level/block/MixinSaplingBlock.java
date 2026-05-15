package org.teneted.taiyitist.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.world.StructureGrowEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.bukkit.BukkitFieldHooks;

import java.util.ArrayList;
import java.util.List;

@Mixin(SaplingBlock.class)
public class MixinSaplingBlock {

    @Shadow
    @Final
    protected TreeGrower treeGrower;
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static TreeType treeType; // CraftBukkit

    @WrapOperation(method = "advanceTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/grower/TreeGrower;growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z"))
    private boolean taiyitist$growTree(TreeGrower instance, ServerLevel level, ChunkGenerator dz, BlockPos pos, BlockState state, RandomSource random, Operation<Boolean> original) {
        // CraftBukkit start
        if (level.bridge$captureTreeGeneration()) {
            if (this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random)) {
                return original.call(instance, level, dz, pos, state, random);
            } else {
                return false;
            }
        } else {
            level.taiyitist$setCaptureTreeGeneration(true);
            this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
            level.taiyitist$setCaptureTreeGeneration(false);
            if (level.bridge$capturedBlockStates().size() > 0) {
                TreeType treeType = BukkitFieldHooks.treeType();
                BukkitFieldHooks.setTreeType(null);
                Location location = CraftLocation.toBukkit(pos, level.getWorld());
                List<org.bukkit.block.BlockState> blocks = new ArrayList<>(level.bridge$capturedBlockStates().values());
                level.bridge$capturedBlockStates().clear();
                StructureGrowEvent event = null;
                if (treeType != null) {
                    event = new StructureGrowEvent(location, treeType, false, null, blocks);
                    Bukkit.getPluginManager().callEvent(event);
                }
                if (event == null || !event.isCancelled()) {
                    for (org.bukkit.block.BlockState capturedBlockState : blocks) {
                        CapturedBlockState.setBlockState(capturedBlockState);
                    }
                }
            }
        }
        // CraftBukkit end
        return original.call(instance, level, dz, pos, state, random);
    }
}
