package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class CraftMerchantRecipe extends MerchantRecipe {
   private final MerchantOffer handle;

   public CraftMerchantRecipe(MerchantOffer merchantRecipe) {
      super(CraftItemStack.asBukkitCopy(merchantRecipe.result), 0);
      this.handle = merchantRecipe;
      this.addIngredient(CraftItemStack.asBukkitCopy(merchantRecipe.baseCostA.itemStack()));
      merchantRecipe.costB.ifPresent((costB) -> {
         this.addIngredient(CraftItemStack.asBukkitCopy(costB.itemStack()));
      });
   }

   /** @deprecated */
   @Deprecated
   public CraftMerchantRecipe(ItemStack result, int uses, int maxUses, boolean experienceReward, int experience, float priceMultiplier) {
      this(result, uses, maxUses, experienceReward, experience, priceMultiplier, 0, 0);
   }

   public CraftMerchantRecipe(ItemStack result, int uses, int maxUses, boolean experienceReward, int experience, float priceMultiplier, int demand, int specialPrice) {
      super(result, uses, maxUses, experienceReward, experience, priceMultiplier, demand, specialPrice);
      this.handle = new MerchantOffer(new ItemCost(Items.AIR), Optional.empty(), CraftItemStack.asNMSCopy(result), uses, maxUses, experience, priceMultiplier, demand/*, this Taiyitist - TODO fimxe*/);
      this.setSpecialPrice(specialPrice);
      this.setExperienceReward(experienceReward);
   }

   public int getSpecialPrice() {
      return this.handle.getSpecialPriceDiff();
   }

   public void setSpecialPrice(int specialPrice) {
      this.handle.specialPriceDiff = specialPrice;
   }

   public int getDemand() {
      return this.handle.demand;
   }

   public void setDemand(int demand) {
      this.handle.demand = demand;
   }

   public int getUses() {
      return this.handle.uses;
   }

   public void setUses(int uses) {
      this.handle.uses = uses;
   }

   public int getMaxUses() {
      return this.handle.maxUses;
   }

   public void setMaxUses(int maxUses) {
      this.handle.maxUses = maxUses;
   }

   public boolean hasExperienceReward() {
      return this.handle.rewardExp;
   }

   public void setExperienceReward(boolean flag) {
      this.handle.rewardExp = flag;
   }

   public int getVillagerExperience() {
      return this.handle.xp;
   }

   public void setVillagerExperience(int villagerExperience) {
      this.handle.xp = villagerExperience;
   }

   public float getPriceMultiplier() {
      return this.handle.priceMultiplier;
   }

   public void setPriceMultiplier(float priceMultiplier) {
      this.handle.priceMultiplier = priceMultiplier;
   }

   public MerchantOffer toMinecraft() {
      List<ItemStack> ingredients = this.getIngredients();
      Preconditions.checkState(!ingredients.isEmpty(), "No offered ingredients");
      net.minecraft.world.item.ItemStack baseCostA = CraftItemStack.asNMSCopy((ItemStack)ingredients.get(0));
      DataComponentExactPredicate baseCostAPredicate = DataComponentExactPredicate.allOf(PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, baseCostA.getComponentsPatch()));
      this.handle.baseCostA = new ItemCost(baseCostA.getItemHolder(), baseCostA.getCount(), baseCostAPredicate, baseCostA);
      if (ingredients.size() > 1) {
         net.minecraft.world.item.ItemStack costB = CraftItemStack.asNMSCopy((ItemStack)ingredients.get(1));
         DataComponentExactPredicate costBPredicate = DataComponentExactPredicate.allOf(PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, costB.getComponentsPatch()));
         this.handle.costB = Optional.of(new ItemCost(costB.getItemHolder(), costB.getCount(), costBPredicate, costB));
      } else {
         this.handle.costB = Optional.empty();
      }

      return this.handle;
   }

   public static CraftMerchantRecipe fromBukkit(MerchantRecipe recipe) {
      if (recipe instanceof CraftMerchantRecipe) {
         return (CraftMerchantRecipe)recipe;
      } else {
         CraftMerchantRecipe craft = new CraftMerchantRecipe(recipe.getResult(), recipe.getUses(), recipe.getMaxUses(), recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier(), recipe.getDemand(), recipe.getSpecialPrice());
         craft.setIngredients(recipe.getIngredients());
         return craft;
      }
   }
}
