package com.taiyitistmc.mixin.world.entity.decoration;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrame.class)
public abstract class MixinItemFrame extends HangingEntity {

    protected MixinItemFrame(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static AABB calculateBoundingBoxStatic(BlockPos blockPos, Direction direction) {
        float f = 0.46875F;
        Vec3 vec3 = Vec3.atCenterOf(blockPos).relative(direction, -0.46875);
        Direction.Axis axis = direction.getAxis();
        double d = axis == Direction.Axis.X ? 0.0625 : 0.75;
        double e = axis == Direction.Axis.Y ? 0.0625 : 0.75;
        double g = axis == Direction.Axis.Z ? 0.0625 : 0.75;
        return AABB.ofSize(vec3, d, e, g);
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ItemFrame;dropItem(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Z)V"), cancellable = true)
    private void taiyitist$handleNonLivingEntityDamageEvent(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - fire EntityDamageEvent
        if (org.bukkit.craftbukkit.event.CraftEventFactory.handleNonLivingEntityDamageEvent(this, damageSource, f, false) || this.isRemoved()) {
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }
}
