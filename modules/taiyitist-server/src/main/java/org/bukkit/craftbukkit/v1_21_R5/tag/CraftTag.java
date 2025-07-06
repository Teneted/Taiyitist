package org.bukkit.craftbukkit.v1_21_R5.tag;

import java.util.Objects;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftNamespacedKey;

public abstract class CraftTag<N, B extends Keyed> implements Tag<B> {
   protected final Registry<N> registry;
   protected final TagKey<N> tag;
   private HolderSet.Named<N> handle;

   public CraftTag(Registry<N> registry, TagKey<N> tag) {
      this.registry = registry;
      this.tag = tag;
      this.handle = (HolderSet.Named)registry.get(this.tag).orElseThrow();
   }

   public HolderSet.Named<N> getHandle() {
      return this.handle;
   }

   public NamespacedKey getKey() {
      return CraftNamespacedKey.fromMinecraft(this.tag.location());
   }

   public int hashCode() {
      int hash = 3;
      hash = 59 * hash + Objects.hashCode(this.registry);
      hash = 59 * hash + Objects.hashCode(this.tag);
      return hash;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof CraftTag)) {
         return false;
      } else {
         CraftTag<?, ?> other = (CraftTag)obj;
         return Objects.equals(this.registry, other.registry) && Objects.equals(this.tag, other.tag);
      }
   }

   public String toString() {
      return "CraftTag{" + String.valueOf(this.tag) + "}";
   }
}
