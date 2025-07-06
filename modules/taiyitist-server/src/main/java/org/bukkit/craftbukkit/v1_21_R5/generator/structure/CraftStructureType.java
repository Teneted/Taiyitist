package org.bukkit.craftbukkit.v1_21_R5.generator.structure;

import com.google.common.base.Preconditions;
import net.minecraft.core.registries.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.util.Handleable;
import org.bukkit.generator.structure.StructureType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftStructureType extends StructureType implements Handleable<net.minecraft.world.level.levelgen.structure.StructureType<?>> {
   private final NamespacedKey key;
   private final net.minecraft.world.level.levelgen.structure.StructureType<?> structureType;

   public static StructureType minecraftToBukkit(net.minecraft.world.level.levelgen.structure.StructureType<?> minecraft) {
      return (StructureType)CraftRegistry.minecraftToBukkit(minecraft, Registries.STRUCTURE_TYPE, Registry.STRUCTURE_TYPE);
   }

   public static net.minecraft.world.level.levelgen.structure.StructureType<?> bukkitToMinecraft(StructureType bukkit) {
      return (net.minecraft.world.level.levelgen.structure.StructureType)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public CraftStructureType(NamespacedKey key, net.minecraft.world.level.levelgen.structure.StructureType<?> structureType) {
      this.key = key;
      this.structureType = structureType;
   }

   public net.minecraft.world.level.levelgen.structure.StructureType<?> getHandle() {
      return this.structureType;
   }

   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
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
}
