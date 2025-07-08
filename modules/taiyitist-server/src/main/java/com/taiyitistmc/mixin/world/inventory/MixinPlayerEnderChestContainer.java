package com.taiyitistmc.mixin.world.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEnderChestContainer.class)
public abstract class MixinPlayerEnderChestContainer extends SimpleContainer {

    // @formatter:off
    @Shadow private EnderChestBlockEntity activeChest;
    // @formatter:on

    private Player owner;

    public MixinPlayerEnderChestContainer(Player owner) {
        super(27);
        this.owner = owner;
    }

    /*
    @ShadowConstructor
    public void taiyitist$constructor$super(int numSlots) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(Player owner) {
        taiyitist$constructor$super(27);
        this.owner = owner;
    }*/

    @Override
    public InventoryHolder getOwner() {
        return owner.getBukkitEntity();
    }

    @Override
    public void setOwner(InventoryHolder owner) {
        if (owner instanceof HumanEntity) {
            this.owner = ((CraftHumanEntity) owner).getHandle();
        }
    }

    @Override
    public Location getLocation() {
        return CraftBlock.at(this.activeChest.getLevel(), this.activeChest.getBlockPos()).getLocation();
    }
}
