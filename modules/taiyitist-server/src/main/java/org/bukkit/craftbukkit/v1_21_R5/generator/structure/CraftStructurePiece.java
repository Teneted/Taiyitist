package org.bukkit.craftbukkit.v1_21_R5.generator.structure;

import org.bukkit.generator.structure.StructurePiece;
import org.bukkit.util.BoundingBox;

public class CraftStructurePiece implements StructurePiece {
   private final net.minecraft.world.level.levelgen.structure.StructurePiece handle;

   public CraftStructurePiece(net.minecraft.world.level.levelgen.structure.StructurePiece handle) {
      this.handle = handle;
   }

   public BoundingBox getBoundingBox() {
      net.minecraft.world.level.levelgen.structure.BoundingBox bb = this.handle.getBoundingBox();
      return new BoundingBox((double)bb.minX(), (double)bb.minY(), (double)bb.minZ(), (double)bb.maxX(), (double)bb.maxY(), (double)bb.maxZ());
   }
}
