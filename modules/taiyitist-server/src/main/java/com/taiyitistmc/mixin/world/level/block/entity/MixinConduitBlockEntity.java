package com.taiyitistmc.mixin.world.level.block.entity;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public class MixinConduitBlockEntity {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static int getRange(List<BlockPos> list) {
        // CraftBukkit end
        int i = list.size();
        int j = i / 7 * 16;
        // CraftBukkit start
        return j;
    }
}
