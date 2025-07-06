package org.bukkit.craftbukkit.generator.structure;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructurePiece;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.BoundingBox;

public class CraftGeneratedStructure implements GeneratedStructure {
   private final StructureStart handle;
   private List<StructurePiece> pieces;

   public CraftGeneratedStructure(StructureStart handle) {
      this.handle = handle;
   }

   public BoundingBox getBoundingBox() {
      net.minecraft.world.level.levelgen.structure.BoundingBox bb = this.handle.getBoundingBox();
      return new BoundingBox((double)bb.minX(), (double)bb.minY(), (double)bb.minZ(), (double)bb.maxX(), (double)bb.maxY(), (double)bb.maxZ());
   }

   public Structure getStructure() {
      return CraftStructure.minecraftToBukkit(this.handle.getStructure());
   }

   public Collection<StructurePiece> getPieces() {
      if (this.pieces == null) {
         ImmutableList.Builder<StructurePiece> builder = new ImmutableList.Builder();
         Iterator var2 = this.handle.getPieces().iterator();

         while(var2.hasNext()) {
            net.minecraft.world.level.levelgen.structure.StructurePiece piece = (net.minecraft.world.level.levelgen.structure.StructurePiece)var2.next();
            builder.add(new CraftStructurePiece(piece));
         }

         this.pieces = builder.build();
      }

      return this.pieces;
   }

   public PersistentDataContainer getPersistentDataContainer() {
      return this.handle.bridge$persistentDataContainer();
   }
}
