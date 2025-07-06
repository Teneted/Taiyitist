package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

public interface CraftMerchant extends Merchant {
   net.minecraft.world.item.trading.Merchant getMerchant();

   default List<MerchantRecipe> getRecipes() {
      return Collections.unmodifiableList(Lists.transform(this.getMerchant().getOffers(), new Function<MerchantOffer, MerchantRecipe>(this) {
         public MerchantRecipe apply(MerchantOffer recipe) {
            return recipe.asBukkit();
         }
      }));
   }

   default void setRecipes(List<MerchantRecipe> recipes) {
      MerchantOffers recipesList = this.getMerchant().getOffers();
      recipesList.clear();
      Iterator var3 = recipes.iterator();

      while(var3.hasNext()) {
         MerchantRecipe recipe = (MerchantRecipe)var3.next();
         recipesList.add(CraftMerchantRecipe.fromBukkit(recipe).toMinecraft());
      }

   }

   default MerchantRecipe getRecipe(int i) {
      return ((MerchantOffer)this.getMerchant().getOffers().get(i)).asBukkit();
   }

   default void setRecipe(int i, MerchantRecipe merchantRecipe) {
      this.getMerchant().getOffers().set(i, CraftMerchantRecipe.fromBukkit(merchantRecipe).toMinecraft());
   }

   default int getRecipeCount() {
      return this.getMerchant().getOffers().size();
   }

   default boolean isTrading() {
      return this.getTrader() != null;
   }

   default HumanEntity getTrader() {
      Player eh = this.getMerchant().getTradingPlayer();
      return eh == null ? null : eh.getBukkitEntity();
   }
}
