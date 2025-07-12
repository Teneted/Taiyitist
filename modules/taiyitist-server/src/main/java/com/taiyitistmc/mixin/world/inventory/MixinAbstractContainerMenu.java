package com.taiyitistmc.mixin.world.inventory;

import com.google.common.base.Preconditions;
import com.taiyitistmc.injection.world.inventory.InjectionAbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// Taiyitist - TODO fix mixins
@Mixin(AbstractContainerMenu.class)
public abstract class MixinAbstractContainerMenu implements InjectionAbstractContainerMenu {

    @Shadow public abstract ItemStack getCarried();

    @Shadow private RemoteSlot remoteCarried;
    @Shadow @Nullable private ContainerSynchronizer synchronizer;
    // CraftBukkit start
    public boolean checkReachable = true;
    private Component title;
    protected boolean opened;

    @Override
    public boolean bridge$checkReachable() {
        return checkReachable;
    }

    @Override
    public void taiyitist$setCheckReachable(boolean checkReachable) {
       this.checkReachable = checkReachable;
    }

    @Override
    public abstract InventoryView getBukkitView();

    @Override
    public void setBukkitView(InventoryView view) {

    }

    @Override
    public void transferTo(AbstractContainerMenu other, CraftHumanEntity player) {
        InventoryView source = this.getBukkitView(), destination = other.getBukkitView();
        ((CraftInventory) source.getTopInventory()).getInventory().onClose(player);
        ((CraftInventory) source.getBottomInventory()).getInventory().onClose(player);
        ((CraftInventory) destination.getTopInventory()).getInventory().onOpen(player);
        ((CraftInventory) destination.getBottomInventory()).getInventory().onOpen(player);
    }

    @Override
    public Component getTitle() {
        Preconditions.checkState(this.title != null, "Title not set");
        return this.title;
    }

    @Override
    public void setTitle(Component title) {
        Preconditions.checkState(this.title == null, "Title already set");
        this.title = title;
    }

    // CraftBukkit start
    public void broadcastCarriedItem() {
        ItemStack itemstack = this.getCarried().copy();
        this.remoteCarried.force(itemstack);
        if (this.synchronizer != null) {
            this.synchronizer.sendCarriedChange(((AbstractContainerMenu) (Object) this), itemstack);
        }
    }
    // CraftBukkit end

    @Override
    public void startOpen() {
        this.opened = true;
    }
}
