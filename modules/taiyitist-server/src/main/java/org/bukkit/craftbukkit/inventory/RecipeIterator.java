package org.bukkit.craftbukkit.inventory;

import java.util.Iterator;
import java.util.Map;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.bukkit.inventory.Recipe;

public class RecipeIterator implements Iterator<Recipe> {
   private final Iterator<Map.Entry<RecipeType<?>, RecipeHolder<?>>> recipes;

   public RecipeIterator() {
      this.recipes = null; // Taiyitist - TODO fixme
     /* this.recipes = BukkitMethodHooks.getServer().getRecipeManager().getRecipes().byType.entries().iterator();*/
   }

   public boolean hasNext() {
      return this.recipes.hasNext();
   }

   public Recipe next() {
      return ((RecipeHolder)((Map.Entry)this.recipes.next()).getValue()).toBukkitRecipe();
   }

   public void remove() {
      this.recipes.remove();
   }
}
