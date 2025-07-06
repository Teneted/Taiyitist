package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.inventory.util.CraftMenus;
import org.bukkit.craftbukkit.v1_21_R5.registry.CraftRegistryItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;

public class CraftMenuType<V extends InventoryView, B extends InventoryViewBuilder<V>> extends CraftRegistryItem<MenuType<?>> implements org.bukkit.inventory.MenuType.Typed<V, B> {
   private final Supplier<CraftMenus.MenuTypeData<V, B>> typeData = Suppliers.memoize(() -> {
      return CraftMenus.getMenuTypeData(this);
   });

   public CraftMenuType(NamespacedKey key, Holder<MenuType<?>> handle) {
      super(key, handle);
   }

   public V create(HumanEntity player, String title) {
      return this.builder().title(title).build(player);
   }

   public B builder() {
      return (InventoryViewBuilder)((CraftMenus.MenuTypeData)this.typeData.get()).viewBuilder().get();
   }

   public org.bukkit.inventory.MenuType.Typed<InventoryView, InventoryViewBuilder<InventoryView>> typed() {
      return this.typed(InventoryView.class);
   }

   public <V extends InventoryView, B extends InventoryViewBuilder<V>> org.bukkit.inventory.MenuType.Typed<V, B> typed(Class<V> clazz) {
      if (clazz.isAssignableFrom(((CraftMenus.MenuTypeData)this.typeData.get()).viewClass())) {
         return this;
      } else {
         String var10002 = String.valueOf(this.isRegistered() ? this.getKeyOrThrow() : this.toString());
         throw new IllegalArgumentException("Cannot type InventoryView " + var10002 + " to InventoryView type " + clazz.getSimpleName());
      }
   }

   public Class<? extends InventoryView> getInventoryViewClass() {
      return ((CraftMenus.MenuTypeData)this.typeData.get()).viewClass();
   }

   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   public static MenuType<?> bukkitToMinecraft(org.bukkit.inventory.MenuType bukkit) {
      return (MenuType)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static org.bukkit.inventory.MenuType minecraftToBukkit(MenuType<?> minecraft) {
      return (org.bukkit.inventory.MenuType)CraftRegistry.minecraftToBukkit(minecraft, Registries.MENU, Registry.MENU);
   }

   public static org.bukkit.inventory.MenuType minecraftHolderToBukkit(Holder<MenuType<?>> minecraft) {
      return minecraftToBukkit((MenuType)minecraft.value());
   }
}
