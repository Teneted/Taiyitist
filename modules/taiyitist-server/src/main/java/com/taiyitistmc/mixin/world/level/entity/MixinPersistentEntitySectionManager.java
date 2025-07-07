package com.taiyitistmc.mixin.world.level.entity;

import com.google.common.collect.ImmutableList;
import com.taiyitistmc.injection.world.level.entity.InjectionPersistentEntitySectionManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PersistentEntitySectionManager.class)
public abstract class MixinPersistentEntitySectionManager<T extends EntityAccess> implements AutoCloseable,InjectionPersistentEntitySectionManager {

    @Shadow @Final
    EntitySectionStorage<T> sectionStorage;

    @Shadow @Final private Long2ObjectMap<PersistentEntitySectionManager.ChunkLoadStatus> chunkLoadStatuses;

    @Shadow @Final public EntityPersistentStorage<T> permanentStorage;

    @Shadow public abstract void close() throws IOException;

    @Shadow protected abstract void requestChunkLoad(long chunkPosValue);

    @Unique
    private AtomicBoolean taiyitist$fireEvent = new AtomicBoolean(false);

    @Override
    public void close(boolean save) throws IOException {
        if (save) {
            this.close();
        } else {
            this.permanentStorage.close();
        }
    }


    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    private boolean storeChunkSections(long chunkPosValue, Consumer<T> consumer) {
        boolean callEvent = this.taiyitist$fireEvent.getAndSet(false);
        PersistentEntitySectionManager.ChunkLoadStatus chunkLoadStatus = this.chunkLoadStatuses.get(chunkPosValue);
        if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.PENDING) {
            return false;
        } else {
            List<T> list = this.sectionStorage.getExistingSectionsInChunk(chunkPosValue).flatMap((entitySection) -> {
                return entitySection.getEntities().filter(EntityAccess::shouldBeSaved);
            }).collect(Collectors.toList());
            if (list.isEmpty()) {
                if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.LOADED) {
                    if (callEvent) {
                        CraftEventFactory.callEntitiesUnloadEvent(((EntityStorage) permanentStorage).level, new ChunkPos(chunkPosValue), ImmutableList.of());
                    }
                    this.permanentStorage.storeEntities(new ChunkEntities<>(new ChunkPos(chunkPosValue), ImmutableList.of()));
                }
                return true;
            } else if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.FRESH) {
                this.requestChunkLoad(chunkPosValue);
                return false;
            } else {
                if (callEvent) {
                    CraftEventFactory.callEntitiesUnloadEvent(((EntityStorage) permanentStorage).level, new ChunkPos(chunkPosValue),
                            list.stream().map(entity -> (Entity) entity).collect(Collectors.toList()));
                }
                this.permanentStorage.storeEntities(new ChunkEntities<>(new ChunkPos(chunkPosValue), list));
                list.forEach(consumer);
                return true;
            }
        }
    }

    @Inject(method = "processChunkUnload", at = @At("HEAD"))
    private void taiyitist$fireEvent(long pChunkPosValue, CallbackInfoReturnable<Boolean> cir) {
        this.taiyitist$fireEvent.set(true); // Banner
    }

    @Inject(method = "processPendingLoads", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, remap = false, target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;put(JLjava/lang/Object;)Ljava/lang/Object;"))
    private void taiyitist$fireLoad(CallbackInfo ci, ChunkEntities<T> chunkEntities) {
        List<Entity> entities = getEntities(chunkEntities.getPos());
        CraftEventFactory.callEntitiesLoadEvent(((EntityStorage) permanentStorage).level, chunkEntities.getPos(), entities);
    }

    @Override
    public List<Entity> getEntities(ChunkPos chunkCoordIntPair) {
        return sectionStorage.getExistingSectionsInChunk(chunkCoordIntPair.toLong()).flatMap(EntitySection::getEntities).map(entity -> (Entity) entity).collect(Collectors.toList());
    }

    @Override
    public boolean isPending(long pair) {
        return chunkLoadStatuses.get(pair) == PersistentEntitySectionManager.ChunkLoadStatus.PENDING;
    }

    private boolean storeChunkSections(long i, Consumer<T> consumer, boolean callEvent) {
        // CraftBukkit start - add boolean for event call
        this.taiyitist$fireEvent.set(callEvent);
        return storeChunkSections(i, consumer);
    }
}
