package org.teneted.taiyitist.mixin.world.entity.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.injection.world.entity.decoration.InjectionItemFrame;

@Mixin(ItemFrame.class)
public class MixinItemFrame implements InjectionItemFrame {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static AABB calculateBoundingBoxStatic(BlockPos blockPos, Direction direction, boolean hasFramedMap) { // CraftBukkit - static
        float shiftToBlockWall = 0.46875F;
        Vec3 position = Vec3.atCenterOf(blockPos).relative(direction, (double)-0.46875F);
        float width = hasFramedMap ? 1.0F : 0.75F;
        float height = hasFramedMap ? 1.0F : 0.75F;
        Direction.Axis axis = direction.getAxis();
        double xSize = axis == Direction.Axis.X ? (double)0.0625F : (double)width;
        double ySize = axis == Direction.Axis.Y ? (double)0.0625F : (double)height;
        double zSize = axis == Direction.Axis.Z ? (double)0.0625F : (double)width;
        return AABB.ofSize(position, xSize, ySize, zSize);
    }
}