package com.taiyitistmc.mixin.world.entity.projectile;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.ChickenVariant;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.Optional;

@Mixin(ThrownEgg.class)
public abstract class MixinThrownEgg extends ThrowableItemProjectile {

    @Shadow @Final private static EntityDimensions ZERO_SIZED_DIMENSIONS;

    public MixinThrownEgg(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            // CraftBukkit start
            boolean hatching = this.random.nextInt(8) == 0;
            if (true) {
                // CraftBukkit end
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }

                // CraftBukkit start
                org.bukkit.entity.EntityType hatchingType = org.bukkit.entity.EntityType.CHICKEN;

                Entity shooter = this.getOwner();
                if (!hatching) {
                    i = 0;
                }
                if (shooter instanceof ServerPlayer) {
                    PlayerEggThrowEvent event = new PlayerEggThrowEvent((Player) shooter.getBukkitEntity(), (org.bukkit.entity.Egg) this.getBukkitEntity(), hatching, (byte) i, hatchingType);
                    this.level().getCraftServer().getPluginManager().callEvent(event);

                    i = event.getNumHatches();
                    hatching = event.isHatching();
                    hatchingType = event.getHatchingType();
                    // If hatching is set to false, ensure child count is 0
                    if (!hatching) {
                        i = 0;
                    }
                }
                // CraftBukkit end

                for(int j = 0; j < i; ++j) {
                    Entity chicken = this.level().getWorld().makeEntity(new org.bukkit.Location(this.level().getWorld(), this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F), hatchingType.getEntityClass()); // CraftBukkit
                    if (chicken != null) {
                        // CraftBukkit start
                        if (chicken.getBukkitEntity() instanceof Ageable) {
                            ((Ageable) chicken.getBukkitEntity()).setBaby();
                        }
                        // CraftBukkit end
                        chicken.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                        Optional<Holder<ChickenVariant>> optional = Optional.ofNullable((EitherHolder)this.getItem().get(DataComponents.CHICKEN_VARIANT)).flatMap((eitherHolder) -> {
                            return eitherHolder.unwrap(this.registryAccess());
                        });
                        Objects.requireNonNull(chicken);
                        // CraftBukkit start
                        if (chicken instanceof Chicken chicken1) {
                            optional.ifPresent(chicken1::setVariant);
                        }
                        // CraftBukkit end
                        if (!chicken.fudgePositionAfterSizeChange(ZERO_SIZED_DIMENSIONS)) {
                            break;
                        }

                        this.level().pushAddEntityReason(CreatureSpawnEvent.SpawnReason.EGG);
                        this.level().addFreshEntity(chicken);
                    }
                }
            }

            this.level().broadcastEntityEvent(this, (byte)3);
            this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
            this.discard();
        }

    }
}
