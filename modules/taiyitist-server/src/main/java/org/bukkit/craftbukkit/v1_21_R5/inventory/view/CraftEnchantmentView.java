package org.bukkit.craftbukkit.v1_21_R5.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryView;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.view.EnchantmentView;
import org.jetbrains.annotations.NotNull;

public class CraftEnchantmentView extends CraftInventoryView<EnchantmentMenu, EnchantingInventory> implements EnchantmentView {
   public CraftEnchantmentView(HumanEntity player, EnchantingInventory viewing, EnchantmentMenu container) {
      super(player, viewing, container);
   }

   public int getEnchantmentSeed() {
      return ((EnchantmentMenu)this.container).getEnchantmentSeed();
   }

   @NotNull
   public EnchantmentOffer[] getOffers() {
      IdMap<Holder<Enchantment>> registry = CraftRegistry.getMinecraftRegistry().lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
      EnchantmentOffer[] offers = new EnchantmentOffer[3];

      for(int i = 0; i < 3; ++i) {
         org.bukkit.enchantments.Enchantment enchantment = ((EnchantmentMenu)this.container).enchantClue[i] >= 0 ? CraftEnchantment.minecraftHolderToBukkit((Holder)registry.byId(((EnchantmentMenu)this.container).enchantClue[i])) : null;
         offers[i] = enchantment != null ? new EnchantmentOffer(enchantment, ((EnchantmentMenu)this.container).levelClue[i], ((EnchantmentMenu)this.container).costs[i]) : null;
      }

      return offers;
   }

   public void setOffers(@NotNull EnchantmentOffer[] offers) {
      Preconditions.checkArgument(offers.length != 3, "There must be 3 offers given");
      IdMap<Holder<Enchantment>> registry = CraftRegistry.getMinecraftRegistry().lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap();

      for(int i = 0; i < offers.length; ++i) {
         EnchantmentOffer offer = offers[i];
         if (offer == null) {
            ((EnchantmentMenu)this.container).enchantClue[i] = -1;
            ((EnchantmentMenu)this.container).levelClue[i] = -1;
            ((EnchantmentMenu)this.container).costs[i] = 0;
         } else {
            ((EnchantmentMenu)this.container).enchantClue[i] = registry.getIdOrThrow(CraftEnchantment.bukkitToMinecraftHolder(offer.getEnchantment()));
            ((EnchantmentMenu)this.container).levelClue[i] = offer.getEnchantmentLevel();
            ((EnchantmentMenu)this.container).costs[i] = offer.getCost();
         }
      }

   }
}
