package com.taiyitistmc.eventhandler.dispatcher;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Optional;

public class EntityEventDispatcher {

    public static void dispatchEntityEvent() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            // CraftBukkit start
            PlayerChangedWorldEvent changeEvent = new PlayerChangedWorldEvent(player.getBukkitEntity(), origin.getWorld());
            player.level().getCraftServer().getPluginManager().callEvent(changeEvent);
            // CraftBukkit end
        });
        EntityEvent.LIVING_HURT.register((livingEntity, damageSource, f) -> {
            EntityDamageEvent event = handleEntityDamage(livingEntity, damageSource, f);
            f = 0;
            f += (float) event.getDamage(EntityDamageEvent.DamageModifier.BASE);
            f += (float) event.getDamage(EntityDamageEvent.DamageModifier.FREEZING);
            f += (float) event.getDamage(EntityDamageEvent.DamageModifier.HARD_HAT);

            return EventResult.interruptDefault();
        });
    }

    private static EntityDamageEvent handleEntityDamage(LivingEntity livingEntity, final DamageSource damagesource, float f) {
        float originalDamage = f;

        com.google.common.base.Function<Double, Double> freezing = new com.google.common.base.Function<Double, Double>() {
            @Override
            public Double apply(Double f) {
                if (damagesource.is(DamageTypeTags.IS_FREEZING) && livingEntity.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                    return -(f - (f * 5.0F));
                }
                return -0.0;
            }
        };
        float freezingModifier = freezing.apply((double) f).floatValue();
        f += freezingModifier;

        com.google.common.base.Function<Double, Double> hardHat = new com.google.common.base.Function<Double, Double>() {
            @Override
            public Double apply(Double f) {
                if (damagesource.is(DamageTypeTags.DAMAGES_HELMET) && !livingEntity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    return -(f - (f * 0.75F));
                }
                return -0.0;
            }
        };
        float hardHatModifier = hardHat.apply((double) f).floatValue();
        f += hardHatModifier;

        com.google.common.base.Function<Double, Double> blocking = new com.google.common.base.Function<Double, Double>() {
            @Override
            public Double apply(Double f) {
                return -((double) calculateItemBlocking(livingEntity, damagesource, f.floatValue()));
            }
        };
        float blockingModifier = blocking.apply((double) f).floatValue();
        f += blockingModifier;

        com.google.common.base.Function<Double, Double> armor = new com.google.common.base.Function<Double, Double>() {
            @Override
            public Double apply(Double f) {
                return -(f - livingEntity.getDamageAfterArmorAbsorb(damagesource, f.floatValue()));
            }
        };
        float armorModifier = armor.apply((double) f).floatValue();
        f += armorModifier;

        com.google.common.base.Function<Double, Double> resistance = new com.google.common.base.Function<Double, Double>() {
            @Override
            public Double apply(Double f) {
                if (!damagesource.is(DamageTypeTags.BYPASSES_EFFECTS) && livingEntity.hasEffect(MobEffects.RESISTANCE) && !damagesource.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
                    int i = (livingEntity.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                    int j = 25 - i;
                    float f1 = f.floatValue() * (float) j;

                    return -(f - Math.max(f1 / 25.0F, 0.0F));
                }
                return -0.0;
            }
        };
        float resistanceModifier = resistance.apply((double) f).floatValue();
        f += resistanceModifier;

        com.google.common.base.Function<Double, Double> magic = new com.google.common.base.Function<Double, Double>() {
            @Override
            public Double apply(Double f) {
                return -(f - livingEntity.getDamageAfterMagicAbsorb(damagesource, f.floatValue()));
            }
        };
        float magicModifier = magic.apply((double) f).floatValue();
        f += magicModifier;

        com.google.common.base.Function<Double, Double> absorption = new com.google.common.base.Function<Double, Double>() {
            @Override
            public Double apply(Double f) {
                return -(Math.max(f - Math.max(f - livingEntity.getAbsorptionAmount(), 0.0F), 0.0F));
            }
        };
        float absorptionModifier = absorption.apply((double) f).floatValue();

        return CraftEventFactory.handleLivingEntityDamageEvent(livingEntity, damagesource, originalDamage, freezingModifier, hardHatModifier, blockingModifier, armorModifier, resistanceModifier, magicModifier, absorptionModifier, freezing, hardHat, blocking, armor, resistance, magic, absorption);
    }

    private static float calculateItemBlocking(LivingEntity livingEntity, DamageSource damageSource, float f) {
        if (f <= 0.0F) {
            return 0.0F;
        } else {
            ItemStack itemStack = livingEntity.getItemBlockingWith();
            if (itemStack == null) {
                return 0.0F;
            } else {
                BlocksAttacks blocksAttacks = (BlocksAttacks)itemStack.get(DataComponents.BLOCKS_ATTACKS);
                if (blocksAttacks != null) {
                    Optional<TagKey<DamageType>> var10000 = blocksAttacks.bypassedBy();
                    java.util.Objects.requireNonNull(damageSource);
                    if (!(Boolean)var10000.map(damageSource::is).orElse(false)) {
                        Entity var7 = damageSource.getDirectEntity();
                        if (var7 instanceof AbstractArrow) {
                            AbstractArrow abstractArrow = (AbstractArrow)var7;
                            if (abstractArrow.getPierceLevel() > 0) {
                                return 0.0F;
                            }
                        }

                        Vec3 vec3 = damageSource.getSourcePosition();
                        double d;
                        if (vec3 != null) {
                            Vec3 vec32 = livingEntity.calculateViewVector(0.0F, livingEntity.getYHeadRot());
                            Vec3 vec33 = vec3.subtract(livingEntity.position());
                            vec33 = (new Vec3(vec33.x, 0.0, vec33.z)).normalize();
                            d = Math.acos(vec33.dot(vec32));
                        } else {
                            d = 3.1415927410125732;
                        }

                        float g = blocksAttacks.resolveBlockedDamage(damageSource, f, d);
                        // CraftBukkit start
                        return g;
                    }
                }

                return 0.0F;
            }
        }
    }

}
