package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.ColorableArmorMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaColorableArmor extends CraftMetaArmor implements ColorableArmorMeta {
   private Color color;

   CraftMetaColorableArmor(CraftMetaItem meta) {
      super(meta);
      this.color = CraftItemFactory.DEFAULT_LEATHER_COLOR;
      CraftMetaLeatherArmor.readColor(this, (CraftMetaItem)meta);
   }

   CraftMetaColorableArmor(DataComponentPatch tag) {
      super(tag);
      this.color = CraftItemFactory.DEFAULT_LEATHER_COLOR;
      CraftMetaLeatherArmor.readColor(this, (DataComponentPatch)tag);
   }

   CraftMetaColorableArmor(Map<String, Object> map) {
      super(map);
      this.color = CraftItemFactory.DEFAULT_LEATHER_COLOR;
      CraftMetaLeatherArmor.readColor(this, (Map)map);
   }

   void applyToItem(CraftMetaItem.Applicator itemTag) {
      super.applyToItem(itemTag);
      CraftMetaLeatherArmor.applyColor(this, itemTag);
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isLeatherArmorEmpty();
   }

   boolean isLeatherArmorEmpty() {
      return !this.hasColor();
   }

   public CraftMetaColorableArmor clone() {
      CraftMetaColorableArmor clone = (CraftMetaColorableArmor)super.clone();
      clone.color = this.color;
      return clone;
   }

   public Color getColor() {
      return this.color;
   }

   public void setColor(Color color) {
      this.color = color == null ? CraftItemFactory.DEFAULT_LEATHER_COLOR : color;
   }

   boolean hasColor() {
      return CraftMetaLeatherArmor.hasColor(this);
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      CraftMetaLeatherArmor.serialize(this, builder);
      return builder;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaColorableArmor) {
         CraftMetaColorableArmor that = (CraftMetaColorableArmor)meta;
         return this.color.equals(that.color);
      } else {
         return true;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaColorableArmor || this.isLeatherArmorEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasColor()) {
         hash ^= this.color.hashCode();
      }

      return original != hash ? CraftMetaColorableArmor.class.hashCode() ^ hash : hash;
   }
}
