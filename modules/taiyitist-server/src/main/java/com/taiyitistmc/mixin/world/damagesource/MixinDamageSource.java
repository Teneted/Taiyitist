package com.taiyitistmc.mixin.world.damagesource;

import com.taiyitistmc.injection.world.damagesource.InjectionDamageSource;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DamageSource.class)
public class MixinDamageSource implements InjectionDamageSource {

    @Shadow @Final @Nullable private Entity directEntity;
    @Shadow @Final @Nullable private Entity causingEntity;
    @Shadow @Final private Holder<DamageType> type;
    @Shadow @Final @Nullable private Vec3 damageSourcePosition;
    // CraftBukkit start
    @Nullable
    private org.bukkit.block.Block directBlock; // The block that caused the damage. damageSourcePosition is not used for all block damages
    @Nullable
    private org.bukkit.block.BlockState directBlockState; // The block state of the block relevant to this damage source
    private boolean sweep = false;
    private boolean melting = false;
    private boolean poison = false;
    private Entity customEntityDamager = null; // This field is a helper for when direct entity damage is not set by vanilla
    private Entity customCausingEntityDamager = null; // This field is a helper for when causing entity damage is not set by vanilla

    @Override
    public DamageSource sweep() {
        this.sweep = true;
        return ((DamageSource) (Object) this);
    }

    @Override
    public boolean isSweep() {
        return this.sweep;
    }

    @Override
    public DamageSource melting() {
        this.melting = true;
        return ((DamageSource) (Object) this);
    }

    @Override
    public boolean isMelting() {
        return this.melting;
    }

    @Override
    public DamageSource poison() {
        this.poison = true;
        return ((DamageSource) (Object) this);
    }

    @Override
    public boolean isPoison() {
        return this.poison;
    }

    @Override
    public Entity getDamager() {
        return (this.customEntityDamager != null) ? this.customEntityDamager : this.directEntity;
    }

    @Override
    public Entity getCausingDamager() {
        return (this.customCausingEntityDamager != null) ? this.customCausingEntityDamager : this.causingEntity;
    }

    @Override
    public DamageSource customEntityDamager(Entity entity) {
        // This method is not intended for change the causing entity if is already set
        // also is only necessary if the entity passed is not the direct entity or different from the current causingEntity
        if (this.customEntityDamager != null || this.directEntity == entity || this.causingEntity == entity) {
            return ((DamageSource) (Object) this);
        }
        DamageSource damageSource = this.cloneInstance();
        damageSource.taiyitist$setCustomEntityDamager(entity);
        return damageSource;
    }

    @Override
    public DamageSource customCausingEntityDamager(Entity entity) {
        // This method is not intended for change the causing entity if is already set
        // also is only necessary if the entity passed is not the direct entity or different from the current causingEntity
        if (this.customCausingEntityDamager != null || this.directEntity == entity || this.causingEntity == entity) {
            return ((DamageSource) (Object) this);
        }
        DamageSource damageSource = this.cloneInstance();
        damageSource.taiyitist$setCustomCausingEntityDamager(entity);
        return damageSource;
    }

    @Override
    public org.bukkit.block.Block getDirectBlock() {
        return this.directBlock;
    }

    @Override
    public DamageSource directBlock(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos blockPosition) {
        if (blockPosition == null || world == null) {
            return ((DamageSource) (Object) this);
        }
        return directBlock(org.bukkit.craftbukkit.block.CraftBlock.at(world, blockPosition));
    }

    public DamageSource directBlock(org.bukkit.block.Block block) {
        if (block == null) {
            return ((DamageSource) (Object) this);
        }
        // Cloning the instance lets us return unique instances of DamageSource without affecting constants defined in DamageSources
        DamageSource damageSource = this.cloneInstance();
        damageSource.taiyitist$setDirectBlock(block);
        return damageSource;
    }

    public org.bukkit.block.BlockState getDirectBlockState() {
        return this.directBlockState;
    }

    public DamageSource directBlockState(org.bukkit.block.BlockState blockState) {
        if (blockState == null) {
            return ((DamageSource) (Object) this);
        }
        // Cloning the instance lets us return unique instances of DamageSource without affecting constants defined in DamageSources
        DamageSource damageSource = this.cloneInstance();
        damageSource.taiyitist$setDirectBlockState(blockState);
        return damageSource;
    }

    public DamageSource cloneInstance() {
        DamageSource damageSource = new DamageSource(this.type, this.directEntity, this.causingEntity, this.damageSourcePosition);
        damageSource.taiyitist$setDirectBlock(this.getDirectBlock());
        damageSource.taiyitist$setDirectBlockState(this.getDirectBlockState());
        damageSource.taiyitist$setSweep(this.isSweep());
        damageSource.taiyitist$setPoison(this.isPoison());
        damageSource.taiyitist$setMelting(this.isMelting());
        return damageSource;
    }
    // CraftBukkit end

    @Override
    public void taiyitist$setCustomCausingEntityDamager(Entity entity) {
        this.customCausingEntityDamager = entity;
    }

    @Override
    public void taiyitist$setDirectBlock(Block block) {
        this.directBlock = block;
    }

    @Override
    public void taiyitist$setDirectBlockState(BlockState block) {
        this.directBlockState = block;
    }

    @Override
    public Entity getCausingEntity() {
        return this.causingEntity;
    }

    @Override
    public void taiyitist$setCustomEntityDamager(Entity entity) {
        this.customEntityDamager = entity;
    }

    @Override
    public void taiyitist$setSweep(boolean sweep) {
        this.sweep = sweep;
    }

    @Override
    public void taiyitist$setPoison(boolean poison) {
        this.poison = poison;
    }

    @Override
    public void taiyitist$setMelting(boolean melting) {
        this.melting = melting;
    }
}
