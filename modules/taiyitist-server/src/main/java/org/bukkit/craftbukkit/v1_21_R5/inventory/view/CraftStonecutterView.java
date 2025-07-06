package org.bukkit.craftbukkit.v1_21_R5.inventory.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.view.StonecutterView;
import org.jetbrains.annotations.NotNull;

public class CraftStonecutterView extends CraftInventoryView<StonecutterMenu, StonecutterInventory> implements StonecutterView {
   public CraftStonecutterView(HumanEntity player, StonecutterInventory viewing, StonecutterMenu container) {
      super(player, viewing, container);
   }

   public int getSelectedRecipeIndex() {
      return ((StonecutterMenu)this.container).getSelectedRecipeIndex();
   }

   @NotNull
   public List<StonecuttingRecipe> getRecipes() {
      List<StonecuttingRecipe> recipes = new ArrayList();
      Iterator var2 = ((StonecutterMenu)this.container).getVisibleRecipes().entries().iterator();

      while(var2.hasNext()) {
         SelectableRecipe.SingleInputEntry<StonecutterRecipe> recipe = (SelectableRecipe.SingleInputEntry)var2.next();
         recipe.recipe().recipe().map(RecipeHolder::toBukkitRecipe).ifPresent((bukkit) -> {
            recipes.add((StonecuttingRecipe)bukkit);
         });
      }

      return recipes;
   }

   public int getRecipeAmount() {
      return ((StonecutterMenu)this.container).getNumberOfVisibleRecipes();
   }
}
