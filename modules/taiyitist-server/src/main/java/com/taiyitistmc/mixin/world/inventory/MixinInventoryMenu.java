package com.taiyitistmc.mixin.world.inventory;

import com.taiyitistmc.bukkit.BukkitSnapshotCaptures;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Taiyitist - TODO fix mixins
@Mixin(InventoryMenu.class)
public abstract class MixinInventoryMenu extends AbstractCraftingMenu {

    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    // CraftBukkit end
    private Inventory playerInventory;

    public MixinInventoryMenu(MenuType<?> menuType, int i, int j, int k) {
        super(menuType, i, j, k);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void taiyitist$init(Inventory playerInventory, boolean localWorld, Player playerIn, CallbackInfo ci) {
        this.playerInventory = playerInventory;
        ((TransientCraftingContainer) this.craftSlots).bridge$setResultInventory(this.resultSlots);
        ((TransientCraftingContainer) this.craftSlots).setOwner(playerInventory.player);
        this.setTitle(Component.translatable("container.crafting"));
    }

    @Inject(method = "slotsChanged", at = @At("HEAD"))
    public void taiyitist$captureContainer(Container inventoryIn, CallbackInfo ci) {
        BukkitSnapshotCaptures.captureWorkbenchContainer((AbstractContainerMenu) this);
    }

    // CraftBukkit start
    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryCrafting inventory = new CraftInventoryCrafting(this.craftSlots, this.resultSlots);
        bukkitEntity = new CraftInventoryView(this.owner().getBukkitEntity(), inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end
}
