package com.taiyitistmc.mixin.world.inventory;

import com.taiyitistmc.config.TaiyitistConfig;
import com.taiyitistmc.injection.world.inventory.InjectionAnvilMenu;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.inventory.view.CraftAnvilView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class MixinAnvilMenu extends ItemCombinerMenu implements InjectionAnvilMenu {

    // CraftBukkit start
    private static final int DEFAULT_DENIED_COST = -1;
    @Shadow
    @Final
    public DataSlot cost;
    public int maximumRepairCost = Math.min(Short.MAX_VALUE, Math.max(41, TaiyitistConfig.maximumRepairCost));
    private CraftAnvilView bukkitEntity;

    public MixinAnvilMenu(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess, ItemCombinerMenuSlotDefinition itemCombinerMenuSlotDefinition) {
        super(menuType, i, inventory, containerLevelAccess, itemCombinerMenuSlotDefinition);
    }
    // CraftBukkit end

    /**
     * @author wdog5
     * @reason bukkit things
     */
    @Overwrite
    protected boolean mayPickup(Player player, boolean hasStack) {
        return (player.getAbilities().instabuild || player.experienceLevel >= this.cost.get()) && this.cost.get() > DEFAULT_DENIED_COST && hasStack; // CraftBukkit - allow cost 0 like a free item
    }

    @Redirect(method = "onTake", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"))
    private void taiyitist$anvilEvent(ContainerLevelAccess instance, BiConsumer<Level, BlockPos> levelPosConsumer) {
        this.access.execute((level, blockPos) -> {
            BlockState blockState = level.getBlockState(blockPos);
            if (!player.getAbilities().instabuild && blockState.is(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
                BlockState blockState2 = AnvilBlock.damage(blockState);
                if (blockState2 == null) {
                    level.removeBlock(blockPos, false);
                    level.levelEvent(1029, blockPos, 0);
                } else {
                    level.setBlock(blockPos, blockState2, 2);
                    level.levelEvent(1030, blockPos, 0);
                }
            } else {
                level.levelEvent(1030, blockPos, 0);
            }

        });
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V", ordinal = 1))
    private void taiyitist$resetAnvilCost1(DataSlot instance, int i) {
        this.cost.set(DEFAULT_DENIED_COST);
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V", ordinal = 2))
    private void taiyitist$resetAnvilCost2(DataSlot instance, int i) {
        this.cost.set(DEFAULT_DENIED_COST);
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V", ordinal = 3))
    private void taiyitist$resetAnvilCost3(DataSlot instance, int i) {
        this.cost.set(DEFAULT_DENIED_COST);
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V", ordinal = 4))
    private void taiyitist$resetAnvilCost4(DataSlot instance, int i) {
        this.cost.set(DEFAULT_DENIED_COST);
    }

    @Redirect(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V"))
    private void taiyitist$reset(DataSlot instance, int i) {
        this.cost.set(DEFAULT_DENIED_COST);
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0))
    private void taiyitist$addAnvilCause0(ResultContainer instance, int slot, ItemStack stack) {
        CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.EMPTY); // CraftBukkit
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1))
    private void taiyitist$addAnvilCause1(ResultContainer instance, int slot, ItemStack stack) {
        CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.EMPTY); // CraftBukkit
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 2))
    private void taiyitist$addAnvilCause2(ResultContainer instance, int slot, ItemStack stack) {
        CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.EMPTY); // CraftBukkit
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 3))
    private void taiyitist$addAnvilCause3(ResultContainer instance, int slot, ItemStack stack) {
        CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.EMPTY); // CraftBukkit
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
    private void taiyitist$addAnvilCause4(ResultContainer instance, int slot, ItemStack stack) {
        CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), stack); // CraftBukkit
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;broadcastChanges()V", shift = At.Shift.BEFORE))
    private void taiyitist$addMessage(CallbackInfo ci) {
        sendAllDataToRemote(); // CraftBukkit - SPIGOT-6686: Always send completed inventory to stay in sync with client
    }

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40))
    private int taiyitist$maxRepairCost(int constant) {
        return maximumRepairCost;
    }

    @Override
    public int bridge$getDeniedCost() {
        return DEFAULT_DENIED_COST;
    }

    @Override
    public int bridge$maximumRepairCost() {
        return maximumRepairCost;
    }

    @Override
    public void taiyitist$setMaximumRepairCost(int maximumRepairCost) {
        this.maximumRepairCost = maximumRepairCost;
    }

    // CraftBukkit start
    @Override
    public CraftAnvilView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }
        CraftInventoryAnvil inventory = new CraftInventoryAnvil(
                access.getLocation(), this.inputSlots, this.resultSlots);
        bukkitEntity = new CraftAnvilView(this.player.getBukkitEntity(), inventory, ((AnvilMenu) (Object) this));
        return bukkitEntity;
    }
    // CraftBukkit end
}
