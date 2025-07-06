package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.Display;
import org.bukkit.Color;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.TextDisplay;

public class CraftTextDisplay extends CraftDisplay implements TextDisplay {
   public CraftTextDisplay(CraftServer server, Display.TextDisplay entity) {
      super(server, entity);
   }

   public Display.TextDisplay getHandle() {
      return (Display.TextDisplay)super.getHandle();
   }

   public String toString() {
      return "CraftTextDisplay";
   }

   public String getText() {
      return CraftChatMessage.fromComponent(this.getHandle().getText());
   }

   public void setText(String text) {
      this.getHandle().setText(CraftChatMessage.fromString(text, true)[0]);
   }

   public int getLineWidth() {
      return this.getHandle().getLineWidth();
   }

   public void setLineWidth(int width) {
      this.getHandle().getEntityData().set(net.minecraft.world.entity.Display.TextDisplay.DATA_LINE_WIDTH_ID, width);
   }

   public Color getBackgroundColor() {
      int color = this.getHandle().getBackgroundColor();
      return color == -1 ? null : Color.fromARGB(color);
   }

   public void setBackgroundColor(Color color) {
      if (color == null) {
         this.getHandle().getEntityData().set(net.minecraft.world.entity.Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, -1);
      } else {
         this.getHandle().getEntityData().set(net.minecraft.world.entity.Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, color.asARGB());
      }

   }

   public byte getTextOpacity() {
      return this.getHandle().getTextOpacity();
   }

   public void setTextOpacity(byte opacity) {
      this.getHandle().setTextOpacity(opacity);
   }

   public boolean isShadowed() {
      return this.getFlag(1);
   }

   public void setShadowed(boolean shadow) {
      this.setFlag(1, shadow);
   }

   public boolean isSeeThrough() {
      return this.getFlag(2);
   }

   public void setSeeThrough(boolean seeThrough) {
      this.setFlag(2, seeThrough);
   }

   public boolean isDefaultBackground() {
      return this.getFlag(4);
   }

   public void setDefaultBackground(boolean defaultBackground) {
      this.setFlag(4, defaultBackground);
   }

   public TextDisplay.TextAlignment getAlignment() {
      Display.TextDisplay.Align nms = net.minecraft.world.entity.Display.TextDisplay.getAlign(this.getHandle().getFlags());
      return TextAlignment.valueOf(nms.name());
   }

   public void setAlignment(TextDisplay.TextAlignment alignment) {
      Preconditions.checkArgument(alignment != null, "Alignment cannot be null");
      switch (alignment) {
         case LEFT:
            this.setFlag(8, true);
            this.setFlag(16, false);
            break;
         case RIGHT:
            this.setFlag(8, false);
            this.setFlag(16, true);
            break;
         case CENTER:
            this.setFlag(8, false);
            this.setFlag(16, false);
            break;
         default:
            throw new IllegalArgumentException("Unknown alignment " + String.valueOf(alignment));
      }

   }

   private boolean getFlag(int flag) {
      return (this.getHandle().getFlags() & flag) != 0;
   }

   private void setFlag(int flag, boolean set) {
      byte flagBits = this.getHandle().getFlags();
      if (set) {
         flagBits = (byte)(flagBits | flag);
      } else {
         flagBits = (byte)(flagBits & ~flag);
      }

      this.getHandle().setFlags(flagBits);
   }
}
