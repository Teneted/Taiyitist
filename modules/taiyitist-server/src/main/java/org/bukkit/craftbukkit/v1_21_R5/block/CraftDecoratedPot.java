package org.bukkit.craftbukkit.v1_21_R5.block;

import com.google.common.base.Preconditions;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.DecoratedPot.Side;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryDecoratedPot;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemType;
import org.bukkit.inventory.DecoratedPotInventory;

public class CraftDecoratedPot extends CraftBlockEntityState<DecoratedPotBlockEntity> implements DecoratedPot {
   public CraftDecoratedPot(World world, DecoratedPotBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftDecoratedPot(CraftDecoratedPot state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public DecoratedPotInventory getSnapshotInventory() {
      return new CraftInventoryDecoratedPot((Container)this.getSnapshot());
   }

   public DecoratedPotInventory getInventory() {
      return (DecoratedPotInventory)(!this.isPlaced() ? this.getSnapshotInventory() : new CraftInventoryDecoratedPot((Container)this.getTileEntity()));
   }

   public void setSherd(DecoratedPot.Side face, Material sherd) {
      Preconditions.checkArgument(face != null, "face must not be null");
      Preconditions.checkArgument(sherd == null || sherd == Material.BRICK || Tag.ITEMS_DECORATED_POT_SHERDS.isTagged(sherd), "sherd is not a valid sherd material: %s", sherd);
      Optional<Item> sherdItem = sherd != null ? Optional.of(CraftItemType.bukkitToMinecraft(sherd)) : Optional.of(Items.BRICK);
      PotDecorations decorations = ((DecoratedPotBlockEntity)this.getSnapshot()).getDecorations();
      switch (face) {
         case BACK -> ((DecoratedPotBlockEntity)this.getSnapshot()).decorations = new PotDecorations(sherdItem, decorations.left(), decorations.right(), decorations.front());
         case LEFT -> ((DecoratedPotBlockEntity)this.getSnapshot()).decorations = new PotDecorations(decorations.back(), sherdItem, decorations.right(), decorations.front());
         case RIGHT -> ((DecoratedPotBlockEntity)this.getSnapshot()).decorations = new PotDecorations(decorations.back(), decorations.left(), sherdItem, decorations.front());
         case FRONT -> ((DecoratedPotBlockEntity)this.getSnapshot()).decorations = new PotDecorations(decorations.back(), decorations.left(), decorations.right(), sherdItem);
         default -> throw new IllegalArgumentException("Unexpected value: " + String.valueOf(face));
      }

   }

   public Material getSherd(DecoratedPot.Side face) {
      Preconditions.checkArgument(face != null, "face must not be null");
      PotDecorations decorations = ((DecoratedPotBlockEntity)this.getSnapshot()).getDecorations();
      Optional var10000;
      switch (face) {
         case BACK -> var10000 = decorations.back();
         case LEFT -> var10000 = decorations.left();
         case RIGHT -> var10000 = decorations.right();
         case FRONT -> var10000 = decorations.front();
         default -> throw new IllegalArgumentException("Unexpected value: " + String.valueOf(face));
      }

      Optional<Item> sherdItem = var10000;
      return CraftItemType.minecraftToBukkit((Item)sherdItem.orElse(Items.BRICK));
   }

   public Map<DecoratedPot.Side, Material> getSherds() {
      PotDecorations decorations = ((DecoratedPotBlockEntity)this.getSnapshot()).getDecorations();
      Map<DecoratedPot.Side, Material> sherds = new EnumMap(DecoratedPot.Side.class);
      sherds.put(Side.BACK, CraftItemType.minecraftToBukkit((Item)decorations.back().orElse(Items.BRICK)));
      sherds.put(Side.LEFT, CraftItemType.minecraftToBukkit((Item)decorations.left().orElse(Items.BRICK)));
      sherds.put(Side.RIGHT, CraftItemType.minecraftToBukkit((Item)decorations.right().orElse(Items.BRICK)));
      sherds.put(Side.FRONT, CraftItemType.minecraftToBukkit((Item)decorations.front().orElse(Items.BRICK)));
      return sherds;
   }

   public List<Material> getShards() {
      return (List)((DecoratedPotBlockEntity)this.getSnapshot()).getDecorations().ordered().stream().map(CraftItemType::minecraftToBukkit).collect(Collectors.toUnmodifiableList());
   }

   public CraftDecoratedPot copy() {
      return new CraftDecoratedPot(this, (Location)null);
   }

   public CraftDecoratedPot copy(Location location) {
      return new CraftDecoratedPot(this, location);
   }
}
