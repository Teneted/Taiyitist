package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BundleContents;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBundle extends CraftMetaItem implements BundleMeta {
   static final CraftMetaItem.ItemMetaKeyType<BundleContents> ITEMS;
   private List<ItemStack> items;

   CraftMetaBundle(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaBundle bundle) {
         if (bundle.hasItems()) {
            this.items = new ArrayList(bundle.items);
         }

      }
   }

   CraftMetaBundle(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, ITEMS).ifPresent((bundle) -> {
         bundle.items().forEach((item) -> {
            ItemStack itemStack = CraftItemStack.asCraftMirror(item);
            if (!((ItemStack)itemStack).getType().isAir()) {
               this.addItem(itemStack);
            }

         });
      });
   }

   CraftMetaBundle(Map<String, Object> map) {
      super(map);
      Iterable<?> items = (Iterable)SerializableMeta.getObject(Iterable.class, map, ITEMS.BUKKIT, true);
      if (items != null) {
         Iterator var3 = items.iterator();

         while(var3.hasNext()) {
            Object stack = var3.next();
            if (stack instanceof ItemStack) {
               ItemStack itemStack = (ItemStack)stack;
               if (!itemStack.getType().isAir()) {
                  this.addItem(itemStack);
               }
            }
         }
      }

   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.hasItems()) {
         List<net.minecraft.world.item.ItemStack> list = new ArrayList();
         Iterator var3 = this.items.iterator();

         while(var3.hasNext()) {
            ItemStack item = (ItemStack)var3.next();
            list.add(CraftItemStack.asNMSCopy(item));
         }

         tag.put(ITEMS, new BundleContents(list));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isBundleEmpty();
   }

   boolean isBundleEmpty() {
      return !this.hasItems();
   }

   public boolean hasItems() {
      return this.items != null && !this.items.isEmpty();
   }

   public List<ItemStack> getItems() {
      return this.items == null ? ImmutableList.of() : ImmutableList.copyOf(this.items);
   }

   public void setItems(List<ItemStack> items) {
      this.items = null;
      if (items != null) {
         Iterator var2 = items.iterator();

         while(var2.hasNext()) {
            ItemStack i = (ItemStack)var2.next();
            this.addItem(i);
         }

      }
   }

   public void addItem(ItemStack item) {
      Preconditions.checkArgument(item != null && !item.getType().isAir(), "item is null or air");
      if (this.items == null) {
         this.items = new ArrayList();
      }

      this.items.add(item);
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaBundle)) {
         return true;
      } else {
         CraftMetaBundle that = (CraftMetaBundle)meta;
         return this.hasItems() ? that.hasItems() && this.items.equals(that.items) : !that.hasItems();
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaBundle || this.isBundleEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasItems()) {
         hash = 61 * hash + this.items.hashCode();
      }

      return original != hash ? CraftMetaBundle.class.hashCode() ^ hash : hash;
   }

   public CraftMetaBundle clone() {
      return (CraftMetaBundle)super.clone();
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasItems()) {
         builder.put(ITEMS.BUKKIT, this.items);
      }

      return builder;
   }

   static {
      ITEMS = new CraftMetaItem.ItemMetaKeyType(DataComponents.BUNDLE_CONTENTS, "items");
   }
}
