package org.bukkit.craftbukkit.inventory.view.builder;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class CraftAbstractLocationInventoryViewBuilder<V extends InventoryView> extends CraftAbstractInventoryViewBuilder<V> implements LocationInventoryViewBuilder<V> {
   protected Level world;
   protected BlockPos position;

   public CraftAbstractLocationInventoryViewBuilder(MenuType<?> handle) {
      super(handle);
   }

   public LocationInventoryViewBuilder<V> title(@NotNull String title) {
      return (LocationInventoryViewBuilder)super.title(title);
   }

   public LocationInventoryViewBuilder<V> copy() {
      throw new UnsupportedOperationException("copy is not implemented on CraftAbstractLocationInventoryViewBuilder");
   }

   public LocationInventoryViewBuilder<V> checkReachable(boolean checkReachable) {
      super.checkReachable = checkReachable;
      return this;
   }

   public LocationInventoryViewBuilder<V> location(Location location) {
      Preconditions.checkArgument(location != null, "The provided location must not be null");
      Preconditions.checkArgument(location.getWorld() != null, "The provided location must be associated with a world");
      this.world = ((CraftWorld)location.getWorld()).getHandle();
      this.position = CraftLocation.toBlockPosition(location);
      return this;
   }
}
