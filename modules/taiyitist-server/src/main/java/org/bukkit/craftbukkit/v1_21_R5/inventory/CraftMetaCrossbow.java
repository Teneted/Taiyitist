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
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaCrossbow extends CraftMetaItem implements CrossbowMeta {
   static final ItemMetaKey CHARGED = new ItemMetaKey("charged");
   static final CraftMetaItem.ItemMetaKeyType<ChargedProjectiles> CHARGED_PROJECTILES;
   private List<ItemStack> chargedProjectiles;

   CraftMetaCrossbow(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaCrossbow crossbow) {
         if (crossbow.hasChargedProjectiles()) {
            this.chargedProjectiles = new ArrayList(crossbow.chargedProjectiles);
         }

      }
   }

   CraftMetaCrossbow(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, CHARGED_PROJECTILES).ifPresent((p) -> {
         List<net.minecraft.world.item.ItemStack> list = p.getItems();
         if (list != null && !list.isEmpty()) {
            this.chargedProjectiles = new ArrayList();

            for(int i = 0; i < list.size(); ++i) {
               net.minecraft.world.item.ItemStack nbttagcompound1 = (net.minecraft.world.item.ItemStack)list.get(i);
               this.chargedProjectiles.add(CraftItemStack.asCraftMirror(nbttagcompound1));
            }
         }

      });
   }

   CraftMetaCrossbow(Map<String, Object> map) {
      super(map);
      Iterable<?> projectiles = (Iterable)SerializableMeta.getObject(Iterable.class, map, CHARGED_PROJECTILES.BUKKIT, true);
      if (projectiles != null) {
         Iterator var3 = projectiles.iterator();

         while(var3.hasNext()) {
            Object stack = var3.next();
            if (stack instanceof ItemStack) {
               this.addChargedProjectile((ItemStack)stack);
            }
         }
      }

   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.hasChargedProjectiles()) {
         List<net.minecraft.world.item.ItemStack> list = new ArrayList();
         Iterator var3 = this.chargedProjectiles.iterator();

         while(var3.hasNext()) {
            ItemStack item = (ItemStack)var3.next();
            list.add(CraftItemStack.asNMSCopy(item));
         }

         tag.put(CHARGED_PROJECTILES, ChargedProjectiles.of(list));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isCrossbowEmpty();
   }

   boolean isCrossbowEmpty() {
      return !this.hasChargedProjectiles();
   }

   public boolean hasChargedProjectiles() {
      return this.chargedProjectiles != null;
   }

   public List<ItemStack> getChargedProjectiles() {
      return this.chargedProjectiles == null ? ImmutableList.of() : ImmutableList.copyOf(this.chargedProjectiles);
   }

   public void setChargedProjectiles(List<ItemStack> projectiles) {
      this.chargedProjectiles = null;
      if (projectiles != null) {
         Iterator var2 = projectiles.iterator();

         while(var2.hasNext()) {
            ItemStack i = (ItemStack)var2.next();
            this.addChargedProjectile(i);
         }

      }
   }

   public void addChargedProjectile(ItemStack item) {
      Preconditions.checkArgument(item != null, "item");
      Preconditions.checkArgument(item.getType() == Material.FIREWORK_ROCKET || CraftItemType.bukkitToMinecraft(item.getType()) instanceof ArrowItem, "Item %s is not an arrow or firework rocket", item);
      if (this.chargedProjectiles == null) {
         this.chargedProjectiles = new ArrayList();
      }

      this.chargedProjectiles.add(item);
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaCrossbow)) {
         return true;
      } else {
         CraftMetaCrossbow that = (CraftMetaCrossbow)meta;
         return this.hasChargedProjectiles() ? that.hasChargedProjectiles() && this.chargedProjectiles.equals(that.chargedProjectiles) : !that.hasChargedProjectiles();
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaCrossbow || this.isCrossbowEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasChargedProjectiles()) {
         hash = 61 * hash + this.chargedProjectiles.hashCode();
      }

      return original != hash ? CraftMetaCrossbow.class.hashCode() ^ hash : hash;
   }

   public CraftMetaCrossbow clone() {
      return (CraftMetaCrossbow)super.clone();
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasChargedProjectiles()) {
         builder.put(CHARGED_PROJECTILES.BUKKIT, this.chargedProjectiles);
      }

      return builder;
   }

   static {
      CHARGED_PROJECTILES = new CraftMetaItem.ItemMetaKeyType(DataComponents.CHARGED_PROJECTILES, "charged-projectiles");
   }
}
