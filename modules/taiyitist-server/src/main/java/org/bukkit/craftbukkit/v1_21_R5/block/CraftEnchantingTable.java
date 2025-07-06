package org.bukkit.craftbukkit.v1_21_R5.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.EnchantingTable;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftChatMessage;

public class CraftEnchantingTable extends CraftBlockEntityState<EnchantingTableBlockEntity> implements EnchantingTable {
   public CraftEnchantingTable(World world, EnchantingTableBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftEnchantingTable(CraftEnchantingTable state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public String getCustomName() {
      EnchantingTableBlockEntity enchant = (EnchantingTableBlockEntity)this.getSnapshot();
      return enchant.hasCustomName() ? CraftChatMessage.fromComponent(enchant.getCustomName()) : null;
   }

   public void setCustomName(String name) {
      ((EnchantingTableBlockEntity)this.getSnapshot()).setCustomName(CraftChatMessage.fromStringOrNull(name));
   }

   protected void applyTo(EnchantingTableBlockEntity enchantingTable) {
      super.applyTo(enchantingTable);
      if (!((EnchantingTableBlockEntity)this.getSnapshot()).hasCustomName()) {
         enchantingTable.setCustomName((Component)null);
      }

   }

   public CraftEnchantingTable copy() {
      return new CraftEnchantingTable(this, (Location)null);
   }

   public CraftEnchantingTable copy(Location location) {
      return new CraftEnchantingTable(this, location);
   }
}
