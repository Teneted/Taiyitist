package org.bukkit.craftbukkit.util;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.AABB;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.VoxelShape;

public final class CraftVoxelShape implements VoxelShape {
   private final net.minecraft.world.phys.shapes.VoxelShape shape;

   public CraftVoxelShape(net.minecraft.world.phys.shapes.VoxelShape shape) {
      this.shape = shape;
   }

   public Collection<BoundingBox> getBoundingBoxes() {
      List<AABB> boxes = this.shape.toAabbs();
      List<BoundingBox> craftBoxes = new ArrayList(boxes.size());
      Iterator var3 = boxes.iterator();

      while(var3.hasNext()) {
         AABB aabb = (AABB)var3.next();
         craftBoxes.add(new BoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ));
      }

      return craftBoxes;
   }

   public boolean overlaps(BoundingBox other) {
      Preconditions.checkArgument(other != null, "Other cannot be null");
      Iterator var2 = this.getBoundingBoxes().iterator();

      BoundingBox box;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         box = (BoundingBox)var2.next();
      } while(!box.overlaps(other));

      return true;
   }
}
