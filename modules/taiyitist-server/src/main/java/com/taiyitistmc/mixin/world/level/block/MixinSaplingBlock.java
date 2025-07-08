package com.taiyitistmc.mixin.world.level.block;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.world.level.block.SaplingBlock;
import org.bukkit.TreeType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SaplingBlock.class)
public class MixinSaplingBlock {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static TreeType treeType; // CraftBukkit
}
