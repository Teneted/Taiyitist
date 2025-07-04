package com.mohistmc.banner.mixin.network.syncher;

import com.mohistmc.banner.injection.network.syncher.InjectionSynchedEntityData;
import java.util.List;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SynchedEntityData.class)
public abstract class MixinSynchedEntityData implements InjectionSynchedEntityData {

    @Shadow
    private boolean isDirty;
    @Shadow
    @Final
    private SyncedDataHolder entity;

    @Shadow
    protected abstract <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> key);

    @Shadow
    @Nullable
    public abstract List<SynchedEntityData.DataValue<?>> getNonDefaultValues();

    @Override
    public <T> void markDirty(EntityDataAccessor<T> datawatcherobject) {
        this.getItem(datawatcherobject).setDirty(true);
        this.isDirty = true;
    }

    @Override
    public void refresh(ServerPlayer player) {
        var list = this.getNonDefaultValues();
        if (list != null && this.entity instanceof Entity entity) {
            player.connection.send(new ClientboundSetEntityDataPacket(entity.getId(), list));
        }
    }
}
