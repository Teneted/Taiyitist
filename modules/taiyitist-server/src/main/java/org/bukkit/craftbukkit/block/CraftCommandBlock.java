package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.craftbukkit.util.CraftChatMessage;

public class CraftCommandBlock extends CraftBlockEntityState<CommandBlockEntity> implements CommandBlock {
   public CraftCommandBlock(World world, CommandBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftCommandBlock(CraftCommandBlock state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public String getCommand() {
      return ((CommandBlockEntity)this.getSnapshot()).getCommandBlock().getCommand();
   }

   public void setCommand(String command) {
      ((CommandBlockEntity)this.getSnapshot()).getCommandBlock().setCommand(command != null ? command : "");
   }

   public String getName() {
      return CraftChatMessage.fromComponent(((CommandBlockEntity)this.getSnapshot()).getCommandBlock().getName());
   }

   public void setName(String name) {
      ((CommandBlockEntity)this.getSnapshot()).getCommandBlock().setCustomName(CraftChatMessage.fromStringOrNull(name != null ? name : "@"));
   }

   public CraftCommandBlock copy() {
      return new CraftCommandBlock(this, (Location)null);
   }

   public CraftCommandBlock copy(Location location) {
      return new CraftCommandBlock(this, location);
   }
}
