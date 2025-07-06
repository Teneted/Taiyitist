package org.bukkit.craftbukkit.generator.structure;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftStructure extends Structure implements Handleable<net.minecraft.world.level.levelgen.structure.Structure> {
   private final NamespacedKey key;
   private final net.minecraft.world.level.levelgen.structure.Structure structure;
   private final Supplier<StructureType> structureType;

   public static Structure minecraftToBukkit(net.minecraft.world.level.levelgen.structure.Structure minecraft) {
      return (Structure)CraftRegistry.minecraftToBukkit(minecraft, Registries.STRUCTURE, Registry.STRUCTURE);
   }

   public static net.minecraft.world.level.levelgen.structure.Structure bukkitToMinecraft(Structure bukkit) {
      return (net.minecraft.world.level.levelgen.structure.Structure)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public CraftStructure(NamespacedKey key, net.minecraft.world.level.levelgen.structure.Structure structure) {
      this.key = key;
      this.structure = structure;
      this.structureType = Suppliers.memoize(() -> {
         return CraftStructureType.minecraftToBukkit(structure.type());
      });
   }

   public net.minecraft.world.level.levelgen.structure.Structure getHandle() {
      return this.structure;
   }

   public StructureType getStructureType() {
      return (StructureType)this.structureType.get();
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
