package com.taiyitistmc.mixin.util.worldupdate;


import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.util.worldupdate.WorldUpgrader$ChunkUpgrader")
public class MixinWorldUpgraderChunkUpgrader {
    @Shadow
    @Final
    private WorldUpgrader this$0;

    @ModifyVariable(
            method = "tryProcessOnePosition",
            at = @At(
                    value = "STORE",
                    opcode = Opcodes.ASTORE,
                    ordinal = 0
            ),
            index = 4
    )
    private CompoundTag modifyCompoundTag2(CompoundTag original,
                                           ChunkStorage chunkStorage,
                                           ChunkPos chunkPos,
                                           ResourceKey<Level> resourceKey,
                                           CompoundTag compoundTag) {
        ChunkGenerator chunkGenerator = this$0.dimensions
                .getValueOrThrow(Registries.levelToLevelStem(resourceKey))
                .generator();

        return chunkStorage.upgradeChunkTag(
                Registries.levelToLevelStem(resourceKey),
                () -> this$0.overworldDataStorage,
                compoundTag,
                chunkGenerator.getTypeNameForDataFixer(),
                chunkPos,
                null
        );
    }
}
