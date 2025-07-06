package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaEnchantedBook extends CraftMetaItem implements EnchantmentStorageMeta {
   static final CraftMetaItem.ItemMetaKeyType<ItemEnchantments> STORED_ENCHANTMENTS;
   private Map<Enchantment, Integer> enchantments;

   CraftMetaEnchantedBook(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaEnchantedBook that) {
         if (that.hasEnchants()) {
            this.enchantments = new LinkedHashMap(that.enchantments);
         }

      }
   }

   CraftMetaEnchantedBook(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, STORED_ENCHANTMENTS).ifPresent((itemEnchantments) -> {
         this.enchantments = buildEnchantments(itemEnchantments);
      });
   }

   CraftMetaEnchantedBook(Map<String, Object> map) {
      super(map);
      this.enchantments = buildEnchantments(map, STORED_ENCHANTMENTS);
   }

   void applyToItem(CraftMetaItem.Applicator itemTag) {
      super.applyToItem(itemTag);
      this.applyEnchantments(this.enchantments, itemTag, STORED_ENCHANTMENTS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isEnchantedEmpty();
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaEnchantedBook)) {
         return true;
      } else {
         CraftMetaEnchantedBook that = (CraftMetaEnchantedBook)meta;
         return this.hasStoredEnchants() ? that.hasStoredEnchants() && this.enchantments.equals(that.enchantments) : !that.hasStoredEnchants();
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaEnchantedBook || this.isEnchantedEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasStoredEnchants()) {
         hash = 61 * hash + this.enchantments.hashCode();
      }

      return original != hash ? CraftMetaEnchantedBook.class.hashCode() ^ hash : hash;
   }

   public CraftMetaEnchantedBook clone() {
      CraftMetaEnchantedBook meta = (CraftMetaEnchantedBook)super.clone();
      if (this.enchantments != null) {
         meta.enchantments = new LinkedHashMap(this.enchantments);
      }

      return meta;
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      serializeEnchantments(this.enchantments, builder, STORED_ENCHANTMENTS);
      return builder;
   }

   boolean isEnchantedEmpty() {
      return !this.hasStoredEnchants();
   }

   public boolean hasStoredEnchant(Enchantment ench) {
      return this.hasStoredEnchants() && this.enchantments.containsKey(ench);
   }

   public int getStoredEnchantLevel(Enchantment ench) {
      Integer level = this.hasStoredEnchants() ? (Integer)this.enchantments.get(ench) : null;
      return level == null ? 0 : level;
   }

   public Map<Enchantment, Integer> getStoredEnchants() {
      return this.hasStoredEnchants() ? ImmutableMap.copyOf(this.enchantments) : ImmutableMap.of();
   }

   public boolean addStoredEnchant(Enchantment ench, int level, boolean ignoreRestrictions) {
      if (this.enchantments == null) {
         this.enchantments = new LinkedHashMap(4);
      }

      if (!ignoreRestrictions && (level < ench.getStartLevel() || level > ench.getMaxLevel())) {
         return false;
      } else {
         Integer old = (Integer)this.enchantments.put(ench, level);
         return old == null || old != level;
      }
   }

   public boolean removeStoredEnchant(Enchantment ench) {
      return this.hasStoredEnchants() && this.enchantments.remove(ench) != null;
   }

   public boolean hasStoredEnchants() {
      return this.enchantments != null && !this.enchantments.isEmpty();
   }

   public boolean hasConflictingStoredEnchant(Enchantment ench) {
      return checkConflictingEnchants(this.enchantments, ench);
   }

   static {
      STORED_ENCHANTMENTS = new CraftMetaItem.ItemMetaKeyType(DataComponents.STORED_ENCHANTMENTS, "stored-enchants");
   }
}
