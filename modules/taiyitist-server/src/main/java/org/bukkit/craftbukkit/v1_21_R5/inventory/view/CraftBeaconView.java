package org.bukkit.craftbukkit.v1_21_R5.inventory.view;

import net.minecraft.world.inventory.BeaconMenu;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_21_R5.potion.CraftPotionEffectType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.view.BeaconView;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class CraftBeaconView extends CraftInventoryView<BeaconMenu, BeaconInventory> implements BeaconView {
   public CraftBeaconView(HumanEntity player, BeaconInventory viewing, BeaconMenu container) {
      super(player, viewing, container);
   }

   public int getTier() {
      return ((BeaconMenu)this.container).getLevels();
   }

   @Nullable
   public PotionEffectType getPrimaryEffect() {
      return ((BeaconMenu)this.container).getPrimaryEffect() != null ? CraftPotionEffectType.minecraftHolderToBukkit(((BeaconMenu)this.container).getPrimaryEffect()) : null;
   }

   @Nullable
   public PotionEffectType getSecondaryEffect() {
      return ((BeaconMenu)this.container).getSecondaryEffect() != null ? CraftPotionEffectType.minecraftHolderToBukkit(((BeaconMenu)this.container).getSecondaryEffect()) : null;
   }

   public void setPrimaryEffect(@Nullable PotionEffectType effectType) {
      ((BeaconMenu)this.container).setData(1, BeaconMenu.encodeEffect(effectType == null ? null : CraftPotionEffectType.bukkitToMinecraftHolder(effectType)));
   }

   public void setSecondaryEffect(@Nullable PotionEffectType effectType) {
      ((BeaconMenu)this.container).setData(2, BeaconMenu.encodeEffect(effectType == null ? null : CraftPotionEffectType.bukkitToMinecraftHolder(effectType)));
   }
}
