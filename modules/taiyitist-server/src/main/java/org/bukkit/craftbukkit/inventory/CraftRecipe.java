package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteResult;
import net.minecraft.world.level.ItemLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

public interface CraftRecipe extends Recipe {
   void addToCraftingManager();

   default Optional<Ingredient> toNMSOptional(RecipeChoice bukkit, boolean requireNotEmpty) {
      return bukkit == null ? Optional.empty() : Optional.of(this.toNMS(bukkit, requireNotEmpty));
   }

   default Ingredient toNMS(RecipeChoice bukkit, boolean requireNotEmpty) {
      Ingredient stack;
      if (bukkit == null) {
         stack = Ingredient.of(new ItemLike[0]);
      } else if (bukkit instanceof RecipeChoice.MaterialChoice) {
         stack = Ingredient.of(((RecipeChoice.MaterialChoice)bukkit).getChoices().stream().map((mat) -> {
            return CraftItemType.bukkitToMinecraft(mat);
         }));
      } else {
         if (!(bukkit instanceof RecipeChoice.ExactChoice)) {
            throw new IllegalArgumentException("Unknown recipe stack instance " + String.valueOf(bukkit));
         }

         stack = Ingredient.ofStacks(((RecipeChoice.ExactChoice)bukkit).getChoices().stream().map((mat) -> {
            return CraftItemStack.asNMSCopy(mat);
         }).toList());
      }

      if (requireNotEmpty) {
         Preconditions.checkArgument(!stack.isEmpty(), "Recipe requires at least one non-air choice");
      }

      return stack;
   }

   default TransmuteResult toNMS(ItemStack stack) {
      net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
      return new TransmuteResult(nms.getItemHolder(), nms.getCount(), nms.getComponentsPatch());
   }

   static RecipeChoice toBukkit(Optional<Ingredient> list) {
      return (RecipeChoice)list.map(CraftRecipe::toBukkit).orElse((Object)null);
   }

   static RecipeChoice toBukkit(Ingredient list) {
      if (list.isEmpty()) {
         return null;
      } else if (!list.isExact()) {
         List<Material> choices = list.items().map((ix) -> {
            return CraftItemType.minecraftToBukkit((Item)ix.value());
         }).toList();
         return new RecipeChoice.MaterialChoice(choices);
      } else {
         List<ItemStack> choices = new ArrayList(list.itemStacks().size());
         Iterator var2 = list.itemStacks().iterator();

         while(var2.hasNext()) {
            net.minecraft.world.item.ItemStack i = (net.minecraft.world.item.ItemStack)var2.next();
            choices.add(CraftItemStack.asBukkitCopy(i));
         }

         return new RecipeChoice.ExactChoice(choices);
      }
   }

   static ItemStack toBukkit(TransmuteResult transmute) {
      net.minecraft.world.item.ItemStack nms = new net.minecraft.world.item.ItemStack(transmute.item(), transmute.count(), transmute.components());
      return CraftItemStack.asBukkitCopy(nms);
   }

   static CraftingBookCategory getCategory(org.bukkit.inventory.recipe.CraftingBookCategory bukkit) {
      return CraftingBookCategory.valueOf(bukkit.name());
   }

   static org.bukkit.inventory.recipe.CraftingBookCategory getCategory(CraftingBookCategory nms) {
      return org.bukkit.inventory.recipe.CraftingBookCategory.valueOf(nms.name());
   }

   static CookingBookCategory getCategory(org.bukkit.inventory.recipe.CookingBookCategory bukkit) {
      return CookingBookCategory.valueOf(bukkit.name());
   }

   static org.bukkit.inventory.recipe.CookingBookCategory getCategory(CookingBookCategory nms) {
      return org.bukkit.inventory.recipe.CookingBookCategory.valueOf(nms.name());
   }

   static ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> toMinecraft(NamespacedKey key) {
      return ResourceKey.create(Registries.RECIPE, CraftNamespacedKey.toMinecraft(key));
   }
}
