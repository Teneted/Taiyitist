package org.bukkit.craftbukkit.v1_21_R5.inventory;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.TransmuteRecipe;

public class CraftTransmuteRecipe extends TransmuteRecipe implements CraftRecipe {
   public CraftTransmuteRecipe(NamespacedKey key, ItemStack result, RecipeChoice input, RecipeChoice material) {
      super(key, result, input, material);
   }

   public static CraftTransmuteRecipe fromBukkitRecipe(TransmuteRecipe recipe) {
      if (recipe instanceof CraftTransmuteRecipe) {
         return (CraftTransmuteRecipe)recipe;
      } else {
         CraftTransmuteRecipe ret = new CraftTransmuteRecipe(recipe.getKey(), recipe.getResult(), recipe.getInput(), recipe.getMaterial());
         ret.setGroup(recipe.getGroup());
         ret.setCategory(recipe.getCategory());
         return ret;
      }
   }

   public void addToCraftingManager() {
      MinecraftServer.getServer().getRecipeManager().addRecipe(new RecipeHolder(CraftRecipe.toMinecraft(this.getKey()), new net.minecraft.world.item.crafting.TransmuteRecipe(this.getGroup(), CraftRecipe.getCategory(this.getCategory()), this.toNMS(this.getInput(), true), this.toNMS(this.getMaterial(), true), this.toNMS(this.getResult()))));
   }
}
