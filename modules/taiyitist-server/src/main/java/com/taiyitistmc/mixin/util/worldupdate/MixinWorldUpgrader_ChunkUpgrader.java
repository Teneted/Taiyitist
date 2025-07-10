package com.taiyitistmc.mixin.util.worldupdate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.util.worldupdate.WorldUpgrader$ChunkUpgrader")
public class MixinWorldUpgrader_ChunkUpgrader {

    @Inject(method = "tryProcessOnePosition(Lnet/minecraft/world/level/chunk/storage/ChunkStorage;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/resources/ResourceKey;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/storage/ChunkStorage;upgradeChunkTag(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Supplier;Lnet/minecraft/nbt/CompoundTag;Ljava/util/Optional;)Lnet/minecraft/nbt/CompoundTag;"))
    private void taiyitist$pushInfo(ChunkStorage chunkStorage, ChunkPos chunkPos, ResourceKey<Level> resourceKey, CallbackInfoReturnable<Boolean> cir) {
        chunkStorage.pushChunkPos(chunkPos);
        chunkStorage.pushLevelAccessor(null);
    }
}
