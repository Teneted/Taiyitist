package org.bukkit.craftbukkit.v1_21_R5;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.bukkit.Art;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_21_R5.registry.CraftOldEnumRegistryItem;
import org.jetbrains.annotations.NotNull;

public class CraftArt extends CraftOldEnumRegistryItem<Art, PaintingVariant> implements Art {
   private static int count = 0;

   public static Art minecraftToBukkit(PaintingVariant minecraft) {
      return (Art)CraftRegistry.minecraftToBukkit(minecraft, Registries.PAINTING_VARIANT, Registry.ART);
   }

   public static Art minecraftHolderToBukkit(Holder<PaintingVariant> minecraft) {
      return minecraftToBukkit((PaintingVariant)minecraft.value());
   }

   public static PaintingVariant bukkitToMinecraft(Art bukkit) {
      return (PaintingVariant)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<PaintingVariant> bukkitToMinecraftHolder(Art bukkit) {
      Preconditions.checkArgument(bukkit != null);
      net.minecraft.core.Registry<PaintingVariant> registry = CraftRegistry.getMinecraftRegistry(Registries.PAINTING_VARIANT);
      Holder var3 = registry.wrapAsHolder(bukkitToMinecraft(bukkit));
      if (var3 instanceof Holder.Reference<PaintingVariant> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own painting variant with out properly registering it.");
      }
   }

   public CraftArt(NamespacedKey key, Holder<PaintingVariant> handle) {
      super(key, handle, count++);
   }

   public int getBlockWidth() {
      return ((PaintingVariant)this.getHandle()).width();
   }

   public int getBlockHeight() {
      return ((PaintingVariant)this.getHandle()).height();
   }

   public int getId() {
      return CraftRegistry.getMinecraftRegistry(Registries.PAINTING_VARIANT).getId((PaintingVariant)this.getHandle());
   }

   @NotNull
   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }
}
