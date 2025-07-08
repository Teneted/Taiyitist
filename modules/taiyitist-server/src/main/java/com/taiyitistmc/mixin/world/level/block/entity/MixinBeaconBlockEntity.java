package com.taiyitistmc.mixin.world.level.block.entity;

import com.taiyitistmc.asm.annotation.TransformAccess;
import com.taiyitistmc.injection.world.level.block.entity.InjectionBeaconBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
public abstract class MixinBeaconBlockEntity extends BlockEntity implements InjectionBeaconBlockEntity {

    public MixinBeaconBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    // @formatter:off
    @Shadow public int levels;
    @Shadow @Nullable public Holder<MobEffect> primaryPower;
    @Shadow @Nullable public Holder<MobEffect> secondaryPower;
    // @formatter:on

    @Inject(method = "loadAdditional", at = @At("RETURN"))
    public void arclight$level(CompoundTag compoundTag, HolderLookup.Provider provider, CallbackInfo ci) {
        this.levels = compoundTag.getInt("Levels");
    }

    @Override
    public PotionEffect getPrimaryEffect() {
        return (this.primaryPower != null) ? CraftPotionUtil.toBukkit(new MobEffectInstance(this.primaryPower, this.getEffectLevel(), this.getAmplification(), true, true)) : null;
    }

    @Override
    public PotionEffect getSecondaryEffect() {
        return (this.hasSecondaryEffect()) ? CraftPotionUtil.toBukkit(new MobEffectInstance(this.secondaryPower, getEffectLevel(), getAmplification(), true, true)) : null;
    }

    private byte getAmplification() {
        byte b0 = 0;
        if (this.levels >= 4 && this.primaryPower == this.secondaryPower) {
            b0 = 1;
        }
        return b0;
    }

    private int getEffectLevel() {
        int i = (9 + this.levels * 2) * 20;
        return i;
    }

    private boolean hasSecondaryEffect() {
        if (this.levels >= 4 && this.primaryPower != this.secondaryPower && this.secondaryPower != null) {
            return true;
        }
        return false;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static List getHumansInRange(Level world, BlockPos blockposition, int i) {
        {
            double d0 = i * 10 + 10;

            AABB axisalignedbb = (new AABB(blockposition)).inflate(d0).expandTowards(0.0D, world.getHeight(), 0.0D);
            List<Player> list = world.getEntitiesOfClass(Player.class, axisalignedbb);

            return list;
        }
    }

    @Inject(method = "loadAdditional", at = @At("RETURN"))
    public void taiyitist$level(CompoundTag compoundTag, HolderLookup.Provider provider, CallbackInfo ci) {
        this.levels = compoundTag.getInt("Levels");
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void taiyitist$beaconEvent(CallbackInfo ci) {
        // Paper start - BeaconDeactivatedEvent
        org.bukkit.block.Block block = CraftBlock.at(level, worldPosition);
        new io.papermc.paper.event.block.BeaconDeactivatedEvent(block).callEvent();
        // Paper end
    }
}
