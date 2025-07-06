package org.bukkit.craftbukkit.v1_21_R5.registry;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.core.Holder;
import org.bukkit.NamespacedKey;
import org.bukkit.util.OldEnum;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated
public abstract class CraftOldEnumRegistryItem<T extends OldEnum<T>, M> extends CraftRegistryItem<M> implements OldEnum<T> {
   private final int ordinal;
   private final String name;

   protected CraftOldEnumRegistryItem(NamespacedKey key, Holder<M> handle, int ordinal) {
      super(key, handle);
      this.ordinal = ordinal;
      if (this.isRegistered()) {
         if ("minecraft".equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase(Locale.ROOT);
         } else {
            this.name = key.toString();
         }
      } else {
         this.name = null;
      }

   }

   public int compareTo(@NotNull T other) {
      this.checkState();
      return this.ordinal - other.ordinal();
   }

   @NotNull
   public String name() {
      this.checkState();
      return this.name;
   }

   public int ordinal() {
      this.checkState();
      return this.ordinal;
   }

   public String toString() {
      return this.isRegistered() ? this.name() : super.toString();
   }

   private void checkState() {
      Preconditions.checkState(this.isRegistered(), "Cannot call method for this registry item, because it is not registered. Use #isRegistered() before calling this method.");
   }
}
