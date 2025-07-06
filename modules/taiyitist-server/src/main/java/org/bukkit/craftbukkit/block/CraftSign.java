package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.craftbukkit.block.sign.CraftSignSide;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSignOpenEvent.Cause;
import org.jetbrains.annotations.NotNull;

public class CraftSign<T extends SignBlockEntity> extends CraftBlockEntityState<T> implements Sign {
   private final CraftSignSide front = new CraftSignSide(((SignBlockEntity)this.getSnapshot()).getFrontText());
   private final CraftSignSide back = new CraftSignSide(((SignBlockEntity)this.getSnapshot()).getBackText());

   public CraftSign(World world, T tileEntity) {
      super(world, tileEntity);
   }

   protected CraftSign(CraftSign<T> state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public String[] getLines() {
      return this.front.getLines();
   }

   public String getLine(int index) throws IndexOutOfBoundsException {
      return this.front.getLine(index);
   }

   public void setLine(int index, String line) throws IndexOutOfBoundsException {
      this.front.setLine(index, line);
   }

   public boolean isEditable() {
      return !this.isWaxed();
   }

   public void setEditable(boolean editable) {
      this.setWaxed(!editable);
   }

   public boolean isWaxed() {
      return ((SignBlockEntity)this.getSnapshot()).isWaxed();
   }

   public void setWaxed(boolean waxed) {
      ((SignBlockEntity)this.getSnapshot()).setWaxed(waxed);
   }

   public boolean isGlowingText() {
      return this.front.isGlowingText();
   }

   public void setGlowingText(boolean glowing) {
      this.front.setGlowingText(glowing);
   }

   @NotNull
   public SignSide getSide(Side side) {
      Preconditions.checkArgument(side != null, "side == null");
      switch (side) {
         case FRONT -> {
            return this.front;
         }
         case BACK -> {
            return this.back;
         }
         default -> throw new IllegalArgumentException();
      }
   }

   public SignSide getTargetSide(Player player) {
      this.ensureNoWorldGeneration();
      Preconditions.checkArgument(player != null, "player cannot be null");
      return ((SignBlockEntity)this.getSnapshot()).isFacingFrontText(((CraftPlayer)player).getHandle()) ? this.front : this.back;
   }

   public Player getAllowedEditor() {
      this.ensureNoWorldGeneration();
      UUID id = ((SignBlockEntity)this.getTileEntity()).getPlayerWhoMayEdit();
      return id == null ? null : Bukkit.getPlayer(id);
   }

   public DyeColor getColor() {
      return this.front.getColor();
   }

   public void setColor(DyeColor color) {
      this.front.setColor(color);
   }

   protected void load(T tileEntity) {
      super.load(tileEntity);
      if (this.front != null) {
         this.front.discardLines();
      }

      if (this.back != null) {
         this.back.discardLines();
      }

   }

   protected void applyTo(T sign) {
      ((SignBlockEntity)this.getSnapshot()).setText(this.front.applyLegacyStringToSignSide(), true);
      ((SignBlockEntity)this.getSnapshot()).setText(this.back.applyLegacyStringToSignSide(), false);
      super.applyTo(sign);
   }

   public CraftSign<T> copy() {
      return new CraftSign(this, (Location)null);
   }

   public CraftSign<T> copy(Location location) {
      return new CraftSign(this, location);
   }

   public static void openSign(Sign sign, Player player, Side side) {
      Preconditions.checkArgument(sign != null, "sign == null");
      Preconditions.checkArgument(side != null, "side == null");
      Preconditions.checkArgument(sign.isPlaced(), "Sign must be placed");
      Preconditions.checkArgument(sign.getWorld() == player.getWorld(), "Sign must be in same world as Player");
      if (CraftEventFactory.callPlayerSignOpenEvent(player, sign, side, Cause.PLUGIN)) {
         SignBlockEntity handle = (SignBlockEntity)((CraftSign)sign).getTileEntity();
         handle.setAllowedPlayerEditor(player.getUniqueId());
         ((CraftPlayer)player).getHandle().openTextEdit(handle, Side.FRONT == side);
      }
   }

   public static Component[] sanitizeLines(String[] lines) {
      Component[] components = new Component[4];

      for(int i = 0; i < 4; ++i) {
         if (i < lines.length && lines[i] != null) {
            components[i] = CraftChatMessage.fromString(lines[i])[0];
         } else {
            components[i] = Component.empty();
         }
      }

      return components;
   }

   public static String[] revertComponents(Component[] components) {
      String[] lines = new String[components.length];

      for(int i = 0; i < lines.length; ++i) {
         lines[i] = revertComponent(components[i]);
      }

      return lines;
   }

   private static String revertComponent(Component component) {
      return CraftChatMessage.fromComponent(component);
   }
}
