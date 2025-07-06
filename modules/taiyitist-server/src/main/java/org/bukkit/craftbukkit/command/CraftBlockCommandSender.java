package org.bukkit.craftbukkit.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;

public class CraftBlockCommandSender extends ServerCommandSender implements BlockCommandSender {
   private static final PermissibleBase SHARED_PERM = new PermissibleBase(new ServerOperator() {
      public boolean isOp() {
         return true;
      }

      public void setOp(boolean value) {
         throw new UnsupportedOperationException("Cannot change operator status of a block");
      }
   });
   private final CommandSourceStack block;
   private final BlockEntity tile;

   public CraftBlockCommandSender(CommandSourceStack commandBlockListenerAbstract, BlockEntity tile) {
      super(SHARED_PERM);
      this.block = commandBlockListenerAbstract;
      this.tile = tile;
   }

   public Block getBlock() {
      return CraftBlock.at(this.tile.getLevel(), this.tile.getBlockPos());
   }

   public void sendMessage(String message) {
      Component[] var2 = CraftChatMessage.fromString(message);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Component component = var2[var4];
         this.block.source.sendSystemMessage(component);
      }

   }

   public void sendMessage(String... messages) {
      String[] var2 = messages;
      int var3 = messages.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String message = var2[var4];
         this.sendMessage(message);
      }

   }

   public String getName() {
      return this.block.getTextName();
   }

   public boolean isOp() {
      return SHARED_PERM.isOp();
   }

   public void setOp(boolean value) {
      SHARED_PERM.setOp(value);
   }

   public CommandSourceStack getWrapper() {
      return this.block;
   }
}
