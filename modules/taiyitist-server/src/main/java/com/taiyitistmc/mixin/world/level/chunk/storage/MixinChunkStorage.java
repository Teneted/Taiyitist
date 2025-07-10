package com.taiyitistmc.mixin.world.level.chunk.storage;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.MapCodec;
import com.taiyitistmc.asm.annotation.TransformAccess;
import com.taiyitistmc.injection.world.level.chunk.storage.InjectionChunkStorage;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.LegacyStructureDataHandler;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Mixin(ChunkStorage.class)
public abstract class MixinChunkStorage implements InjectionChunkStorage {

    @Shadow public abstract CompletableFuture<Optional<CompoundTag>> read(ChunkPos chunkPos);

    @Shadow @Nullable private volatile LegacyStructureDataHandler legacyStructureHandler;

    @Shadow
    public static int getVersion(CompoundTag compoundTag) {
        return 0;
    }

    @Shadow @Final protected DataFixer fixerUpper;

    @Shadow
    private static void removeDatafixingContext(CompoundTag compoundTag) {
    }

    private AtomicReference<ChunkPos> taiyitist$chunkPos = new AtomicReference<>(null);
    private AtomicReference<LevelAccessor> taiyitist$generatorAccess = new AtomicReference<>(null);

    @Override
    public void pushChunkPos(ChunkPos pos) {
        taiyitist$chunkPos.set(pos);
    }

    @Override
    public void pushLevelAccessor(LevelAccessor generatoraccess) {
        taiyitist$generatorAccess.set(generatoraccess);
    }

    @Inject(method = "upgradeChunkTag", at = @At(value = "CONSTANT", args = "intValue=1493"))
    private void taiyitist$addChunkTag(ResourceKey<Level> resourceKey, Supplier<DimensionDataStorage> supplier, CompoundTag compoundTag, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> optional, CallbackInfoReturnable<CompoundTag> cir, @Local int i) {
        // CraftBukkit start
        if (i < 1466) {
            CompoundTag level = compoundTag.getCompoundOrEmpty("Level");
            if (level.getBooleanOr("TerrainPopulated", false) && !level.getBooleanOr("LightPopulated", false)) {
                ServerChunkCache cps = (taiyitist$generatorAccess.get() == null) ? null : ((ServerLevel) taiyitist$generatorAccess.get()).getChunkSource();
                if (check(cps, taiyitist$chunkPos.get().x - 1, taiyitist$chunkPos.get().z) && check(cps, taiyitist$chunkPos.get().x - 1, taiyitist$chunkPos.get().z - 1) && check(cps, taiyitist$chunkPos.get().x, taiyitist$chunkPos.get().z - 1)) {
                    level.putBoolean("LightPopulated", true);
                }
            }
        }
        // CraftBukkit end
    }

    @Override
    public CompoundTag upgradeChunkTag(ResourceKey<LevelStem> resourceKey, Supplier<DimensionDataStorage> supplier, CompoundTag compoundTag, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> optional, ChunkPos pos, @Nullable LevelAccessor generatoraccess) {
        taiyitist$generatorAccess.set(generatoraccess);
        taiyitist$chunkPos.set(pos);
        int i = getVersion(compoundTag);
        if (i == SharedConstants.getCurrentVersion().dataVersion().version()) {
            return compoundTag;
        } else {
            try {
                // CraftBukkit start
                if (i < 1466) {
                    CompoundTag level = compoundTag.getCompoundOrEmpty("Level");
                    if (level.getBooleanOr("TerrainPopulated", false) && !level.getBooleanOr("LightPopulated", false)) {
                        ServerChunkCache cps = (generatoraccess == null) ? null : ((ServerLevel) generatoraccess).getChunkSource();
                        if (check(cps, pos.x - 1, pos.z) && check(cps, pos.x - 1, pos.z - 1) && check(cps, pos.x, pos.z - 1)) {
                            level.putBoolean("LightPopulated", true);
                        }
                    }
                }
                // CraftBukkit end
                if (i < 1493) {
                    compoundTag = DataFixTypes.CHUNK.update(this.fixerUpper, compoundTag, i, 1493);
                    if ((Boolean)compoundTag.getCompound("Level").flatMap((compoundTagx) -> {
                        return compoundTagx.getBoolean("hasLegacyStructureData");
                    }).orElse(false)) {
                        LegacyStructureDataHandler legacyStructureDataHandler = this.getLegacyStructureHandler(resourceKey, supplier);
                        compoundTag = legacyStructureDataHandler.updateFromLegacy(compoundTag);
                    }
                }

                injectDatafixingContext(compoundTag, resourceKey, optional);
                compoundTag = DataFixTypes.CHUNK.updateToCurrentVersion(this.fixerUpper, compoundTag, Math.max(1493, i));
                removeDatafixingContext(compoundTag);
                NbtUtils.addCurrentDataVersion(compoundTag);
                return compoundTag;
            } catch (Exception var9) {
                Exception exception = var9;
                CrashReport crashReport = CrashReport.forThrowable(exception, "Updated chunk");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Updated chunk details");
                crashReportCategory.setDetail("Data version", i);
                throw new ReportedException(crashReport);
            }
        }
    }

    // CraftBukkit start
    private boolean check(ServerChunkCache cps, int x, int z) {
        ChunkPos pos = new ChunkPos(x, z);
        if (cps != null) {
            if (cps.hasChunk(x, z)) {
                return true;
            }
        }

        CompoundTag nbt;
        try {
            nbt = read(pos).get().orElse(null);
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        if (nbt != null) {
            CompoundTag level = nbt.getCompoundOrEmpty("Level");
            if (level.getBooleanOr("TerrainPopulated", false)) {
                return true;
            }

            ChunkStatus status = ChunkStatus.byName(level.getStringOr("Status", ""));
            if (status != null && status.isOrAfter(ChunkStatus.FEATURES)) {
                return true;
            }
        }

        return false;
    }

    private LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<LevelStem> resourceKey, Supplier<DimensionDataStorage> supplier) {
        LegacyStructureDataHandler legacyStructureDataHandler = this.legacyStructureHandler;
        if (legacyStructureDataHandler == null) {
            synchronized(this) {
                legacyStructureDataHandler = this.legacyStructureHandler;
                if (legacyStructureDataHandler == null) {
                    this.legacyStructureHandler = legacyStructureDataHandler = getLegacyStructureHandler(resourceKey, (Supplier<DimensionDataStorage>) supplier.get());
                }
            }
        }

        return legacyStructureDataHandler;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static void injectDatafixingContext(CompoundTag compoundTag, ResourceKey<LevelStem> resourceKey, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> optional) {
        CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putString("dimension", resourceKey.location().toString());
        optional.ifPresent((resourceKeyx) -> {
            compoundTag2.putString("generator", resourceKeyx.location().toString());
        });
        compoundTag.put("__context", compoundTag2);
    }
}
