package com.taiyitistmc.mixin.world.level.entity;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.injection.world.level.entity.InjectionPersistentEntitySectionManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Mixin(PersistentEntitySectionManager.class)
public abstract class MixinPersistentEntitySectionManager<T extends EntityAccess> implements InjectionPersistentEntitySectionManager {

    @Shadow @Final
    EntitySectionStorage<T> sectionStorage;

    @Shadow @Final private Long2ObjectMap<PersistentEntitySectionManager.ChunkLoadStatus> chunkLoadStatuses;

    @Shadow @Final
    public EntityPersistentStorage<T> permanentStorage;

    @Shadow protected abstract void requestChunkLoad(long l);

    @Shadow protected abstract void unloadEntity(EntityAccess entityAccess);

    @Shadow public abstract void saveAll();

    @Override
    // CraftBukkit start - add method to get all entities in chunk
    public List<Entity> getEntities(ChunkPos chunkCoordIntPair) {
        return sectionStorage.getExistingSectionsInChunk(chunkCoordIntPair.toLong()).flatMap(EntitySection::getEntities).map(entity -> (Entity) entity).collect(Collectors.toList());
    }

    @Override
    public boolean isPending(long pair) {
        return chunkLoadStatuses.get(pair) == PersistentEntitySectionManager.ChunkLoadStatus.PENDING;
    }
    // CraftBukkit end

    @Inject(method = "addEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;getEffectiveStatus(Lnet/minecraft/world/level/entity/EntityAccess;Lnet/minecraft/world/level/entity/Visibility;)Lnet/minecraft/world/level/entity/Visibility;"))
    private void taiyitist$markEntity(T entityAccess, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit - Mark entity as in world
        if (entityAccess instanceof Entity entity) {
            entity.taiyitist$setInWorld(true);
        }
        // CraftBukkit end
    }

    private AtomicBoolean taiyitist$callEvent = new AtomicBoolean(false);

    @Inject(method = "storeChunkSections", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityPersistentStorage;storeEntities(Lnet/minecraft/world/level/entity/ChunkEntities;)V", ordinal = 0))
    private void taiyitist$callEntitiesUnloadEvent(long l, Consumer<T> consumer, CallbackInfoReturnable<Boolean> cir) {
        if (taiyitist$callEvent.get()) {
            CraftEventFactory.callEntitiesUnloadEvent(((EntityStorage) permanentStorage).level, new ChunkPos(l), ImmutableList.of()); // CraftBukkit
        }
    }

    @Inject(method = "storeChunkSections", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityPersistentStorage;storeEntities(Lnet/minecraft/world/level/entity/ChunkEntities;)V", ordinal = 0))
    private void taiyitist$callEntitiesUnloadEvent(long l, Consumer<T> consumer, CallbackInfoReturnable<Boolean> cir, @Local List<T> list) {
        if (taiyitist$callEvent.get()) {
            CraftEventFactory.callEntitiesUnloadEvent(((EntityStorage) permanentStorage).level, new ChunkPos(l), list.stream().map(entity -> (Entity) entity).collect(Collectors.toList())); // CraftBukkit
        }
    }

    @Override
    public boolean storeChunkSections(long i, Consumer consumer, boolean callEvent) {
        taiyitist$callEvent.set(callEvent);
        PersistentEntitySectionManager.ChunkLoadStatus chunkLoadStatus = (PersistentEntitySectionManager.ChunkLoadStatus)this.chunkLoadStatuses.get(i);
        if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.PENDING) {
            return false;
        } else {
            List<T> list = (List)this.sectionStorage.getExistingSectionsInChunk(i).flatMap((entitySection) -> {
                return entitySection.getEntities().filter(EntityAccess::shouldBeSaved);
            }).collect(Collectors.toList());
            if (list.isEmpty()) {
                if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.LOADED) {
                    if (callEvent) CraftEventFactory.callEntitiesUnloadEvent(((EntityStorage) permanentStorage).level, new ChunkPos(i), ImmutableList.of()); // CraftBukkit
                    this.permanentStorage.storeEntities(new ChunkEntities(new ChunkPos(i), ImmutableList.of()));
                }

                return true;
            } else if (chunkLoadStatus == PersistentEntitySectionManager.ChunkLoadStatus.FRESH) {
                this.requestChunkLoad(i);
                return false;
            } else {
                if (callEvent) CraftEventFactory.callEntitiesUnloadEvent(((EntityStorage) permanentStorage).level, new ChunkPos(i), list.stream().map(entity -> (Entity) entity).collect(Collectors.toList())); // CraftBukkit
                this.permanentStorage.storeEntities(new ChunkEntities(new ChunkPos(i), list));
                list.forEach(consumer);
                return true;
            }
        }
    }

    @Inject(method = "processChunkUnload", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;storeChunkSections(JLjava/util/function/Consumer;)Z"))
    private void taiyitist$callUnloadEvent(long l, CallbackInfoReturnable<Boolean> cir) {
        taiyitist$callEvent.set(true);
    }

    @Redirect(method = "unloadEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityAccess;setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$unloadEntityCause(EntityAccess instance, Entity.RemovalReason removalReason) {
        instance.setRemoved(removalReason, EntityRemoveEvent.Cause.UNLOAD);
    }

    @Inject(method = "processPendingLoads", at = @At("TAIL"))
    private void taiyitist$callEntitiesLoadEvent(CallbackInfo ci, @Local ChunkEntities chunkEntities) {
        // CraftBukkit start - call entity load event
        List<Entity> entities = getEntities(chunkEntities.getPos());
        CraftEventFactory.callEntitiesLoadEvent(((EntityStorage) permanentStorage).level, chunkEntities.getPos(), entities);
        // CraftBukkit end
    }

    private AtomicBoolean tayitist$save = new AtomicBoolean(false);

    @Inject(method = "close", at = @At("HEAD"))
    private void taiyitist$setSave(CallbackInfo ci) {
        tayitist$save.set(true);
    }

    @WrapWithCondition(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;saveAll()V"))
    private boolean taiyitist$checkSave(PersistentEntitySectionManager instance) {
        return tayitist$save.get();
    }

    @Override
    public void close(boolean save) throws IOException {
        tayitist$save.set(save);
        if (save) {
            this.saveAll();
        }
        // CraftBukkit end
        this.permanentStorage.close();
    }
}
