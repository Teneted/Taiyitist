package org.bukkit.craftbukkit.v1_21_R5.registry;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.minecraft.core.Holder;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.util.HolderHandleable;
import org.bukkit.registry.RegistryAware;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CraftRegistryItem<M> implements RegistryAware, HolderHandleable<M> {
   private final NamespacedKey key;
   private final Holder<M> handle;

   protected CraftRegistryItem(NamespacedKey key, Holder<M> handle) {
      this.key = key;
      this.handle = handle;
   }

   public Holder<M> getHandleHolder() {
      return this.handle;
   }

   public M getHandle() {
      return this.getHandleHolder().value();
   }

   @NotNull
   public NamespacedKey getKeyOrThrow() {
      Preconditions.checkState(this.isRegistered(), "Cannot get key of this registry item, because it is not registered. Use #isRegistered() before calling this method.");
      return this.key;
   }

   @Nullable
   public NamespacedKey getKeyOrNull() {
      return this.key;
   }

   public boolean isRegistered() {
      return this.key != null;
   }

   public int hashCode() {
      return this.isRegistered() ? this.getKeyOrThrow().hashCode() : this.getHandle().hashCode();
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         CraftRegistryItem<?> other = (CraftRegistryItem)obj;
         return !this.isRegistered() && !other.isRegistered() ? Objects.equals(this.getHandle(), other.getHandle()) : Objects.equals(this.key, other.key);
      } else {
         return false;
      }
   }

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", handle=" + String.valueOf(this.handle) + "}";
   }
}
