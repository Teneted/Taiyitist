package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_21_R5.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.v1_21_R5.inventory.trim.CraftTrimPattern;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaArmor extends CraftMetaItem implements ArmorMeta {
   static final CraftMetaItem.ItemMetaKeyType<ArmorTrim> TRIM;
   static final ItemMetaKey TRIM_MATERIAL;
   static final ItemMetaKey TRIM_PATTERN;
   private org.bukkit.inventory.meta.trim.ArmorTrim trim;

   CraftMetaArmor(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaArmor armorMeta) {
         this.trim = armorMeta.trim;
      }

   }

   CraftMetaArmor(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, TRIM).ifPresent((trimCompound) -> {
         TrimMaterial trimMaterial = CraftTrimMaterial.minecraftHolderToBukkit(trimCompound.material());
         TrimPattern trimPattern = CraftTrimPattern.minecraftHolderToBukkit(trimCompound.pattern());
         this.trim = new org.bukkit.inventory.meta.trim.ArmorTrim(trimMaterial, trimPattern);
      });
   }

   CraftMetaArmor(Map<String, Object> map) {
      super(map);
      Map<?, ?> trimData = (Map)SerializableMeta.getObject(Map.class, map, TRIM.BUKKIT, true);
      if (trimData != null) {
         String materialKeyString = SerializableMeta.getString(trimData, TRIM_MATERIAL.BUKKIT, true);
         String patternKeyString = SerializableMeta.getString(trimData, TRIM_PATTERN.BUKKIT, true);
         if (materialKeyString != null && patternKeyString != null) {
            NamespacedKey materialKey = NamespacedKey.fromString(materialKeyString);
            NamespacedKey patternKey = NamespacedKey.fromString(patternKeyString);
            if (materialKey != null && patternKey != null) {
               TrimMaterial trimMaterial = (TrimMaterial)Registry.TRIM_MATERIAL.get(materialKey);
               TrimPattern trimPattern = (TrimPattern)Registry.TRIM_PATTERN.get(patternKey);
               if (trimMaterial != null && trimPattern != null) {
                  this.trim = new org.bukkit.inventory.meta.trim.ArmorTrim(trimMaterial, trimPattern);
               }
            }
         }
      }

   }

   void applyToItem(CraftMetaItem.Applicator itemTag) {
      super.applyToItem(itemTag);
      if (this.hasTrim()) {
         itemTag.put(TRIM, new ArmorTrim(CraftTrimMaterial.bukkitToMinecraftHolder(this.trim.getMaterial()), CraftTrimPattern.bukkitToMinecraftHolder(this.trim.getPattern())));
      }

   }

   boolean equalsCommon(CraftMetaItem that) {
      if (!super.equalsCommon(that)) {
         return false;
      } else if (that instanceof CraftMetaArmor) {
         CraftMetaArmor armorMeta = (CraftMetaArmor)that;
         return Objects.equals(this.trim, armorMeta.trim);
      } else {
         return true;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaArmor || this.isArmorEmpty());
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isArmorEmpty();
   }

   private boolean isArmorEmpty() {
      return !this.hasTrim();
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasTrim()) {
         hash = 61 * hash + this.trim.hashCode();
      }

      return original != hash ? CraftMetaArmor.class.hashCode() ^ hash : hash;
   }

   public CraftMetaArmor clone() {
      CraftMetaArmor meta = (CraftMetaArmor)super.clone();
      meta.trim = this.trim;
      return meta;
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasTrim()) {
         Map<String, String> trimData = new HashMap();
         trimData.put(TRIM_MATERIAL.BUKKIT, this.trim.getMaterial().getKey().toString());
         trimData.put(TRIM_PATTERN.BUKKIT, this.trim.getPattern().getKey().toString());
         builder.put(TRIM.BUKKIT, trimData);
      }

      return builder;
   }

   public boolean hasTrim() {
      return this.trim != null;
   }

   public void setTrim(org.bukkit.inventory.meta.trim.ArmorTrim trim) {
      this.trim = trim;
   }

   public org.bukkit.inventory.meta.trim.ArmorTrim getTrim() {
      return this.trim;
   }

   static {
      TRIM = new CraftMetaItem.ItemMetaKeyType(DataComponents.TRIM, "trim");
      TRIM_MATERIAL = new ItemMetaKey("material");
      TRIM_PATTERN = new ItemMetaKey("pattern");
   }
}
