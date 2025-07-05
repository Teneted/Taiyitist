package com.taiyitistmc.injection.world;

import java.util.Collections;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

public interface InjectionContainer {

    int MAX_STACK = 64;

    default java.util.List<ItemStack> getContents() {
        return Collections.emptyList();
    }

    default void onOpen(CraftHumanEntity who) {
    }

    default void onClose(CraftHumanEntity who) {
    }

    default java.util.List<org.bukkit.entity.HumanEntity> getViewers() {
        return Collections.emptyList();
    }

    default org.bukkit.inventory.InventoryHolder getOwner() {
        return null;
    }

    default Inventory getOwnerInventory() {
        InventoryHolder owner = this.getOwner();
        if (owner != null) {
            return owner.getInventory();
        } else {
            return new CraftInventory((Container) this);
        }
    }

    default void setOwner(org.bukkit.inventory.InventoryHolder owner) {
    }

    default void setMaxStackSize(int size) {
    }

    default org.bukkit.Location getLocation() {
        return null;
    }

    default Recipe<?> getCurrentRecipe() {
        return null;
    }

    default void setCurrentRecipe(Recipe<?> recipe) {
    }

    default InventoryView getBukkitView() {
        return null;
    }
}
