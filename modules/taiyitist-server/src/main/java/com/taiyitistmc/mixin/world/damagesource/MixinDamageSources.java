package com.taiyitistmc.mixin.world.damagesource;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.injection.world.damagesource.InjectionDamageSources;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSources.class)
public abstract class MixinDamageSources implements InjectionDamageSources {
    @Shadow public abstract DamageSource source(ResourceKey<DamageType> resourceKey);

    @Shadow @Final public Registry<DamageType> damageTypes;

    @Shadow public abstract DamageSource source(ResourceKey<DamageType> resourceKey, @Nullable Entity entity, @Nullable Entity entity2);

    // CraftBukkit start
    private DamageSource melting;
    private DamageSource poison;

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/damagesource/DamageSources;inFire:Lnet/minecraft/world/damagesource/DamageSource;"))
    private void taiyitist$registerDamageSources(RegistryAccess registryAccess, CallbackInfo ci) {
        this.melting = this.source(DamageTypes.ON_FIRE).melting();
        this.poison = this.source(DamageTypes.MAGIC).poison();
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public DamageSource melting() {
        return this.melting;
    }

    @Override
    public DamageSource poison() {
        return this.poison;
    }
    // CraftBukkit end

    @ModifyReturnValue(method = "explosion(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;", at = @At("RETURN"))
    private DamageSource taiyitist$resetExplosion(DamageSource original, @Local(argsOnly = true, ordinal = 0) @Nullable Entity entity, @Local(argsOnly = true, ordinal = 1) @Nullable Entity entity2) {
        return this.explosion(entity, entity2, entity2 != null && entity != null ? DamageTypes.PLAYER_EXPLOSION : DamageTypes.EXPLOSION);
    }

    @ModifyReturnValue(method = "badRespawnPointExplosion", at = @At("RETURN"))
    private DamageSource taiyitist$resetBadRespawnPointExplosion(DamageSource original, @Local(argsOnly = true) Vec3 vec3) {
        return badRespawnPointExplosion(vec3, null);
    }

    @Override
    public DamageSource explosion(@Nullable Entity entity, @Nullable Entity entity1, ResourceKey<DamageType> resourceKey) {
        return this.source(resourceKey, entity, entity1);
    }

    @Override
    public DamageSource badRespawnPointExplosion(Vec3 vec3d, BlockState blockState) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(DamageTypes.BAD_RESPAWN_POINT), vec3d).directBlockState(blockState);
    }
}
