package com.taiyitistmc.mixin.world.entity.player;

import com.taiyitistmc.injection.world.entity.player.InjectionInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

// CraftBukkit start
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Shadow;
// CraftBukkit end

@Mixin(Inventory.class)
public abstract class MixinInventory implements Container, Nameable, InjectionInventory {

    @Shadow @Final public static int SLOT_OFFHAND;
    @Shadow @Final public static int INVENTORY_SIZE;
    @Shadow @Final public Player player;
    @Shadow @Final private NonNullList<ItemStack> items;

    @Shadow protected abstract boolean hasRemainingSpaceForItem(ItemStack itemStack, ItemStack itemStack2);

    @Shadow @Final private EntityEquipment equipment;
    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<>();
    private int maxStack = MAX_STACK;

    @Override
    public List<ItemStack> getContents() {
        List<ItemStack> combined = new ArrayList<>(SLOT_OFFHAND + 1);
        for (int i = 0; i <= SLOT_OFFHAND; i++) {
            combined.add(getItem(i));
        }

        return combined;
    }

    public List<ItemStack> getArmorContents() {
        List<ItemStack> combined = new ArrayList<>(SLOT_OFFHAND - INVENTORY_SIZE);
        for (int i = INVENTORY_SIZE; i < SLOT_OFFHAND; i++) {
            combined.add(getItem(i));
        }

        return combined;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner() {
        return this.player.getBukkitEntity();
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    @Override
    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    @Override
    public Location getLocation() {
        return player.getBukkitEntity().getLocation();
    }
    // CraftBukkit end

    // CraftBukkit start - Watch method above! :D
    @Override
    public int canHold(ItemStack itemstack) {
        int remains = itemstack.getCount();
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemstack1 = this.getItem(i);
            if (itemstack1.isEmpty()) return itemstack.getCount();

            if (this.hasRemainingSpaceForItem(itemstack1, itemstack)) {
                remains -= (itemstack1.getMaxStackSize() < this.getMaxStackSize() ? itemstack1.getMaxStackSize() : this.getMaxStackSize()) - itemstack1.getCount();
            }
            if (remains <= 0) return itemstack.getCount();
        }
        ItemStack offhandItemStack = this.equipment.get(EquipmentSlot.OFFHAND);
        if (this.hasRemainingSpaceForItem(offhandItemStack, itemstack)) {
            remains -= (offhandItemStack.getMaxStackSize() < this.getMaxStackSize() ? offhandItemStack.getMaxStackSize() : this.getMaxStackSize()) - offhandItemStack.getCount();
        }
        if (remains <= 0) return itemstack.getCount();

        return itemstack.getCount() - remains;
    }
    // CraftBukkit end


    @Override
    public void setOwner(InventoryHolder owner) {
        this.setOwner(this.getOwner());
    }
}
