package com.taiyitistmc.mixin.world.level.block;

import com.taiyitistmc.injection.world.level.block.InjectionBaseFireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Taiyitist - TODO fixme
@Mixin(BaseFireBlock.class)
public class MixinBaseFireBlock implements InjectionBaseFireBlock {

    @Redirect(method = "fireIgnite", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private static void taiyitist$entityCombustEvent(Entity instance, float f) {
        // CraftBukkit start
        org.bukkit.event.entity.EntityCombustEvent event = new org.bukkit.event.entity.EntityCombustByBlockEvent(instance.getBukkitEntity().getLocation().getBlock(), instance.getBukkitEntity(), 8.0F); // PAIL - TODO
        instance.level().getCraftServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            instance.igniteForSeconds(event.getDuration(), false);
        }
        // CraftBukkit end
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    private static boolean inPortalDimension(Level level) {
        return level.getTypeKey() == net.minecraft.world.level.dimension.LevelStem.OVERWORLD || level.getTypeKey() == net.minecraft.world.level.dimension.LevelStem.NETHER; // CraftBukkit - getTypeKey()
    }

    @Redirect(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean taiyitist$fireExtinguished(Level instance, BlockPos blockPos, boolean bl) {
        fireExtinguished(instance, blockPos);
        return false;
    }

    // CraftBukkit start
    @Override
    public void fireExtinguished(net.minecraft.world.level.LevelAccessor world, BlockPos position) {
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world, position, Blocks.AIR.defaultBlockState()).isCancelled()) {
            world.removeBlock(position, false);
        }
    }
    // CraftBukkit end
}
