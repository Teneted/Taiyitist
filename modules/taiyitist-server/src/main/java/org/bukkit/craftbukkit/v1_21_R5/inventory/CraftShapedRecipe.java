package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class CraftShapedRecipe extends ShapedRecipe implements CraftRecipe {
   private net.minecraft.world.item.crafting.ShapedRecipe recipe;

   public CraftShapedRecipe(NamespacedKey key, ItemStack result) {
      super(key, result);
   }

   public CraftShapedRecipe(NamespacedKey key, ItemStack result, net.minecraft.world.item.crafting.ShapedRecipe recipe) {
      this(key, result);
      this.recipe = recipe;
   }

   public static CraftShapedRecipe fromBukkitRecipe(ShapedRecipe recipe) {
      if (recipe instanceof CraftShapedRecipe) {
         return (CraftShapedRecipe)recipe;
      } else {
         CraftShapedRecipe ret = new CraftShapedRecipe(recipe.getKey(), recipe.getResult());
         ret.setGroup(recipe.getGroup());
         ret.setCategory(recipe.getCategory());
         String[] shape = recipe.getShape();
         ret.shape(shape);
         Map<Character, RecipeChoice> ingredientMap = recipe.getChoiceMap();
         Iterator var4 = ingredientMap.keySet().iterator();

         while(var4.hasNext()) {
            char c = (Character)var4.next();
            RecipeChoice stack = (RecipeChoice)ingredientMap.get(c);
            if (stack != null) {
               ret.setIngredient(c, stack);
            }
         }

         return ret;
      }
   }

   public void addToCraftingManager() {
      Map<Character, RecipeChoice> ingred = this.getChoiceMap();
      String[] shape = replaceUndefinedIngredientsWithEmpty(this.getShape(), ingred);
      ingred.values().removeIf(Objects::isNull);
      Map<Character, Ingredient> data = Maps.transformValues(ingred, (bukkit) -> {
         return this.toNMS(bukkit, false);
      });
      ShapedRecipePattern pattern = ShapedRecipePattern.of(data, shape);
      MinecraftServer.getServer().getRecipeManager().addRecipe(new RecipeHolder(CraftRecipe.toMinecraft(this.getKey()), new net.minecraft.world.item.crafting.ShapedRecipe(this.getGroup(), CraftRecipe.getCategory(this.getCategory()), pattern, CraftItemStack.asNMSCopy(this.getResult()))));
   }

   private static String[] replaceUndefinedIngredientsWithEmpty(String[] shape, Map<Character, RecipeChoice> ingredients) {
      for(int i = 0; i < shape.length; ++i) {
         String row = shape[i];
         StringBuilder filteredRow = new StringBuilder(row.length());
         char[] var5 = row.toCharArray();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            char character = var5[var7];
            filteredRow.append(ingredients.get(character) == null ? ' ' : character);
         }

         shape[i] = filteredRow.toString();
      }

      return shape;
   }
}
