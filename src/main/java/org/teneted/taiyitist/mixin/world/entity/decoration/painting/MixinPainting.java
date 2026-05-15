package org.teneted.taiyitist.mixin.world.entity.decoration.painting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.asm.annotation.TransformAccess;

@Mixin(Painting.class)
public class MixinPainting {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static AABB calculateBoundingBoxStatic(BlockPos pos, Direction direction, int width, int height) {
        float shiftToBlockWall = 0.46875F;
        Vec3 attachedToWall = Vec3.atCenterOf(pos).relative(direction, (double) -0.46875F);
        // CraftBukkit start
        double horizontalOffset = offsetForPaintingSize(width);
        double verticalOffset = offsetForPaintingSize(height);
        // CraftBukkit end
        Direction left = direction.getCounterClockWise();
        Vec3 position = attachedToWall.relative(left, horizontalOffset).relative(Direction.UP, verticalOffset);
        Direction.Axis axis = direction.getAxis();
        // CraftBukkit start
        double xSize = axis == Direction.Axis.X ? 0.0625D : (double) width;
        double ySize = (double) height;
        double zSize = axis == Direction.Axis.Z ? 0.0625D : (double) width;
        // CraftBukkit end
        return AABB.ofSize(position, xSize, ySize, zSize);
    }

    private static double offsetForPaintingSize(int size) { // CraftBukkit - static
        return size % 2 == 0 ? 0.5D : 0.0D;
    }
}
