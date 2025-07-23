package com.taiyitistmc.mixin.world.entity.ambient;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Bat.class)
public abstract class MixinBat extends AmbientCreature {

    @Shadow public abstract boolean isResting();

    @Shadow @Final private static TargetingConditions BAT_RESTING_TARGETING;

    @Shadow public abstract void setResting(boolean bl);

    @Shadow @Nullable private BlockPos targetPosition;

    protected MixinBat(EntityType<? extends AmbientCreature> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        BlockPos blockPos = this.blockPosition();
        BlockPos blockPos2 = blockPos.above();
        if (this.isResting()) {
            boolean bl = this.isSilent();
            if (serverLevel.getBlockState(blockPos2).isRedstoneConductor(serverLevel, blockPos)) {
                if (this.random.nextInt(200) == 0) {
                    this.yHeadRot = (float)this.random.nextInt(360);
                }

                if (serverLevel.getNearestPlayer(BAT_RESTING_TARGETING, this) != null && CraftEventFactory.handleBatToggleSleepEvent(this, true)) { // CraftBukkit - Call BatToggleSleepEvent
                    this.setResting(false);
                    if (!bl) {
                        serverLevel.levelEvent((Entity)null, 1025, blockPos, 0);
                    }
                }
            } else if (CraftEventFactory.handleBatToggleSleepEvent(this, true)) { // CraftBukkit - Call BatToggleSleepEvent
                this.setResting(false);
                if (!bl) {
                    serverLevel.levelEvent((Entity)null, 1025, blockPos, 0);
                }
            }
        } else {
            if (this.targetPosition != null && (!serverLevel.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= serverLevel.getMinY())) {
                this.targetPosition = null;
            }

            if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0)) {
                this.targetPosition = BlockPos.containing(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
            }

            double d = (double)this.targetPosition.getX() + 0.5 - this.getX();
            double e = (double)this.targetPosition.getY() + 0.1 - this.getY();
            double f = (double)this.targetPosition.getZ() + 0.5 - this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec32 = vec3.add((Math.signum(d) * 0.5 - vec3.x) * 0.10000000149011612, (Math.signum(e) * 0.699999988079071 - vec3.y) * 0.10000000149011612, (Math.signum(f) * 0.5 - vec3.z) * 0.10000000149011612);
            this.setDeltaMovement(vec32);
            float g = (float)(Mth.atan2(vec32.z, vec32.x) * 57.2957763671875) - 90.0F;
            float h = Mth.wrapDegrees(g - this.getYRot());
            this.zza = 0.5F;
            this.setYRot(this.getYRot() + h);
            if (this.random.nextInt(100) == 0 && serverLevel.getBlockState(blockPos2).isRedstoneConductor(serverLevel, blockPos2) && CraftEventFactory.handleBatToggleSleepEvent(this, false)) { // CraftBukkit - Call BatToggleSleepEvent
                this.setResting(true);
            }
        }
    }

    @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ambient/Bat;isResting()Z"))
    private boolean taiyitist$batEvent(boolean original) {
        return original && CraftEventFactory.handleBatToggleSleepEvent(this, true);// CraftBukkit - Call BatToggleSleepEvent
    }
}
