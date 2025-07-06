package org.bukkit.craftbukkit.v1_21_R5.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

public class CraftShapelessRecipe extends ShapelessRecipe implements CraftRecipe {
   private net.minecraft.world.item.crafting.ShapelessRecipe recipe;

   public CraftShapelessRecipe(NamespacedKey key, ItemStack result) {
      super(key, result);
   }

   public CraftShapelessRecipe(NamespacedKey key, ItemStack result, net.minecraft.world.item.crafting.ShapelessRecipe recipe) {
      this(key, result);
      this.recipe = recipe;
   }

   public static CraftShapelessRecipe fromBukkitRecipe(ShapelessRecipe recipe) {
      if (recipe instanceof CraftShapelessRecipe) {
         return (CraftShapelessRecipe)recipe;
      } else {
         CraftShapelessRecipe ret = new CraftShapelessRecipe(recipe.getKey(), recipe.getResult());
         ret.setGroup(recipe.getGroup());
         ret.setCategory(recipe.getCategory());
         Iterator var2 = recipe.getChoiceList().iterator();

         while(var2.hasNext()) {
            RecipeChoice ingred = (RecipeChoice)var2.next();
            ret.addIngredient(ingred);
         }

         return ret;
      }
   }

   public void addToCraftingManager() {
      List<RecipeChoice> ingred = this.getChoiceList();
      List<Ingredient> data = new ArrayList(ingred.size());
      Iterator var3 = ingred.iterator();

      while(var3.hasNext()) {
         RecipeChoice i = (RecipeChoice)var3.next();
         data.add(this.toNMS(i, true));
      }

      MinecraftServer.getServer().getRecipeManager().addRecipe(new RecipeHolder(CraftRecipe.toMinecraft(this.getKey()), new net.minecraft.world.item.crafting.ShapelessRecipe(this.getGroup(), CraftRecipe.getCategory(this.getCategory()), CraftItemStack.asNMSCopy(this.getResult()), data)));
   }
}
