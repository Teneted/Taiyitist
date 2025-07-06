package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaLeatherArmor extends CraftMetaItem implements LeatherArmorMeta {
   static final CraftMetaItem.ItemMetaKeyType<DyedItemColor> COLOR;
   private Color color;

   CraftMetaLeatherArmor(CraftMetaItem meta) {
      super(meta);
      this.color = CraftItemFactory.DEFAULT_LEATHER_COLOR;
      readColor(this, (CraftMetaItem)meta);
   }

   CraftMetaLeatherArmor(DataComponentPatch tag) {
      super(tag);
      this.color = CraftItemFactory.DEFAULT_LEATHER_COLOR;
      readColor(this, (DataComponentPatch)tag);
   }

   CraftMetaLeatherArmor(Map<String, Object> map) {
      super(map);
      this.color = CraftItemFactory.DEFAULT_LEATHER_COLOR;
      readColor(this, (Map)map);
   }

   void applyToItem(CraftMetaItem.Applicator itemTag) {
      super.applyToItem(itemTag);
      applyColor(this, itemTag);
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isLeatherArmorEmpty();
   }

   boolean isLeatherArmorEmpty() {
      return !this.hasColor();
   }

   boolean applicableTo(Material type) {
      if (!type.isItem()) {
         return false;
      } else {
         return type.asItemType().getItemMetaClass() == LeatherArmorMeta.class || type.asItemType().getItemMetaClass() == ColorableArmorMeta.class;
      }
   }

   public CraftMetaLeatherArmor clone() {
      return (CraftMetaLeatherArmor)super.clone();
   }

   public Color getColor() {
      return this.color;
   }

   public void setColor(Color color) {
      this.color = color == null ? CraftItemFactory.DEFAULT_LEATHER_COLOR : color;
   }

   boolean hasColor() {
      return hasColor(this);
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      serialize(this, builder);
      return builder;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaLeatherArmor) {
         CraftMetaLeatherArmor that = (CraftMetaLeatherArmor)meta;
         return this.color.equals(that.color);
      } else {
         return true;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaLeatherArmor || this.isLeatherArmorEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasColor()) {
         hash ^= this.color.hashCode();
      }

      return original != hash ? CraftMetaLeatherArmor.class.hashCode() ^ hash : hash;
   }

   static void readColor(LeatherArmorMeta meta, CraftMetaItem other) {
      if (other instanceof CraftMetaLeatherArmor armorMeta) {
         meta.setColor(armorMeta.color);
      }
   }

   static void readColor(LeatherArmorMeta meta, DataComponentPatch tag) {
      getOrEmpty(tag, COLOR).ifPresent((dyedItemColor) -> {
         try {
            meta.setColor(Color.fromRGB(dyedItemColor.rgb()));
         } catch (IllegalArgumentException var3) {
         }

      });
   }

   static void readColor(LeatherArmorMeta meta, Map<String, Object> map) {
      meta.setColor((Color)SerializableMeta.getObject(Color.class, map, COLOR.BUKKIT, true));
   }

   static boolean hasColor(LeatherArmorMeta meta) {
      return !CraftItemFactory.DEFAULT_LEATHER_COLOR.equals(meta.getColor());
   }

   static void applyColor(LeatherArmorMeta meta, CraftMetaItem.Applicator tag) {
      if (hasColor(meta)) {
         tag.put(COLOR, new DyedItemColor(meta.getColor().asRGB()));
      }

   }

   static void serialize(LeatherArmorMeta meta, ImmutableMap.Builder<String, Object> builder) {
      if (hasColor(meta)) {
         builder.put(COLOR.BUKKIT, meta.getColor());
      }

   }

   static {
      COLOR = new CraftMetaItem.ItemMetaKeyType(DataComponents.DYED_COLOR, "color");
   }
}
