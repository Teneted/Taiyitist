package com.taiyitistmc.mixin.world.entity.decoration;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Painting.class)
public abstract class MixinPainting {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static AABB calculateBoundingBoxStatic(BlockPos blockPos, Direction direction, int width, int height) {
        float f = 0.46875F;
        Vec3 vec3 = Vec3.atCenterOf(blockPos).relative(direction, -0.46875);
        // CraftBukkit start
        double d = taiyitst$offsetForPaintingSize(width);
        double e = taiyitst$offsetForPaintingSize(height);
        // CraftBukkit end
        Direction direction2 = direction.getCounterClockWise();
        Vec3 vec32 = vec3.relative(direction2, d).relative(Direction.UP, e);
        Direction.Axis axis = direction.getAxis();
        // CraftBukkit start
        double g = axis == Direction.Axis.X ? 0.0625D : (double) width;
        double h = (double) height;
        double i = axis == Direction.Axis.Z ? 0.0625D : (double) width;
        // CraftBukkit end
        return AABB.ofSize(vec32, g, h, i);
    }

    private static double taiyitst$offsetForPaintingSize(int i) { // CraftBukkit - static
        return i % 2 == 0 ? 0.5 : 0.0;
    }

}
