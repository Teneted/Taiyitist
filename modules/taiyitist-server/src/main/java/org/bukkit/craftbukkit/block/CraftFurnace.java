package org.bukkit.craftbukkit.block;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Recipe;

public abstract class CraftFurnace<T extends AbstractFurnaceBlockEntity> extends CraftContainer<T> implements Furnace {
   public CraftFurnace(World world, T tileEntity) {
      super((World)world, (BaseContainerBlockEntity)tileEntity);
   }

   protected CraftFurnace(CraftFurnace<T> state, Location location) {
      super((CraftContainer)state, (Location)location);
   }

   public FurnaceInventory getSnapshotInventory() {
      return new CraftInventoryFurnace((AbstractFurnaceBlockEntity)this.getSnapshot());
   }

   public FurnaceInventory getInventory() {
      return (FurnaceInventory)(!this.isPlaced() ? this.getSnapshotInventory() : new CraftInventoryFurnace((AbstractFurnaceBlockEntity)this.getTileEntity()));
   }

   public short getBurnTime() {
      return (short)((AbstractFurnaceBlockEntity)this.getSnapshot()).litTimeRemaining;
   }

   public void setBurnTime(short burnTime) {
      ((AbstractFurnaceBlockEntity)this.getSnapshot()).litTimeRemaining = burnTime;
      this.data = (BlockState)this.data.setValue(AbstractFurnaceBlock.LIT, burnTime > 0);
   }

   public short getCookTime() {
      return (short)((AbstractFurnaceBlockEntity)this.getSnapshot()).cookingTimer;
   }

   public void setCookTime(short cookTime) {
      ((AbstractFurnaceBlockEntity)this.getSnapshot()).cookingTimer = cookTime;
   }

   public int getCookTimeTotal() {
      return ((AbstractFurnaceBlockEntity)this.getSnapshot()).cookingTotalTime;
   }

   public void setCookTimeTotal(int cookTimeTotal) {
      ((AbstractFurnaceBlockEntity)this.getSnapshot()).cookingTotalTime = cookTimeTotal;
   }

   public Map<CookingRecipe<?>, Integer> getRecipesUsed() {
      ImmutableMap.Builder<CookingRecipe<?>, Integer> recipesUsed = ImmutableMap.builder();
      ((AbstractFurnaceBlockEntity)this.getSnapshot()).recipesUsed.reference2IntEntrySet().fastForEach((entrySet) -> {
         Recipe recipe = Bukkit.getRecipe(CraftNamespacedKey.fromMinecraft(((ResourceKey)entrySet.getKey()).location()));
         if (recipe instanceof CookingRecipe<?> cookingRecipe) {
            recipesUsed.put(cookingRecipe, entrySet.getValue());
         }

      });
      return recipesUsed.build();
   }

   public abstract CraftFurnace<T> copy();

   public abstract CraftFurnace<T> copy(Location var1);
}
