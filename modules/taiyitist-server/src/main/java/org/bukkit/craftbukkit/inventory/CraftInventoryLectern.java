package org.bukkit.craftbukkit.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import org.bukkit.block.Lectern;
import org.bukkit.inventory.LecternInventory;

public class CraftInventoryLectern extends CraftInventory implements LecternInventory {

    public MenuProvider tile;

    public CraftInventoryLectern(Container inventory) {
        super(inventory);
        // Banner TODO fixme
        /*
        if (inventory instanceof BannerLecternInventory) {
            this.tile = ((BannerLecternInventory) inventory).getLectern();
        }*/
    }

    @Override
    public Lectern getHolder() {
        return (Lectern) this.inventory.getOwner();
    }
}
