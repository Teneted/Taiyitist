package com.taiyitistmc.mixin.core.dispenser;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.taiyitistmc.bukkit.BukkitFieldHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShearsDispenseItemBehavior.class)
public class MixinShearsDispenseItemBehavior {

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isClientSide()Z"), cancellable = true)
    private void taiyitist$dispenseEvent(BlockSource blockSource, ItemStack itemStack,
                                         CallbackInfoReturnable<ItemStack> cir,
                                         @Local ServerLevel serverLevel,
                                         @Share("taiyitist$bukkitBlock") LocalRef<org.bukkit.block.Block> taiyitist$bukkitBlock,
                                         @Share("taiyitist$craftItem") LocalRef<CraftItemStack> taiyitist$craftItem) {
        // CraftBukkit start
        org.bukkit.block.Block bukkitBlock = CraftBlock.at(serverLevel, blockSource.pos());
        taiyitist$bukkitBlock.set(bukkitBlock);
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);
        taiyitist$craftItem.set(craftItem);

        BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
        if (!BukkitFieldHooks.isEventFired()) {
            serverLevel.getCraftServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            cir.setReturnValue(itemStack);
        }

        if (!event.getItem().equals(craftItem)) {
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
            if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                idispensebehavior.dispense(blockSource, eventStack);
                cir.setReturnValue(itemStack);
            }
        }
        // CraftBukkit end
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/ShearsDispenseItemBehavior;tryShearEntity(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean taiyitist$shearEvent(ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack,
                                         @Share("taiyitist$bukkitBlock") LocalRef<org.bukkit.block.Block> taiyitist$bukkitBlock,
                                         @Share("taiyitist$craftItem") LocalRef<CraftItemStack> taiyitist$craftItem) {
        return tryShearEntity(serverLevel, blockPos, itemStack, taiyitist$bukkitBlock.get(), taiyitist$craftItem.get());
    }

    private static boolean tryShearEntity(ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, org.bukkit.block.Block bukkitBlock, CraftItemStack craftItem) { // CraftBukkit - add args
        List<Entity> list = serverLevel.getEntitiesOfClass(Entity.class, new AABB(blockPos), EntitySelector.NO_SPECTATORS);

        for (Entity entity : list) {
            if (entity.shearOffAllLeashConnections((Player) null)) {
                return true;
            }

            if (entity instanceof Shearable shearable) {
                if (shearable.readyForShearing()) {
                    // CraftBukkit start
                    if (CraftEventFactory.callBlockShearEntityEvent(entity, bukkitBlock, craftItem).isCancelled()) {
                        continue;
                    }
                    // CraftBukkit end
                    shearable.shear(serverLevel, SoundSource.BLOCKS, itemStack);
                    serverLevel.gameEvent((Entity) null, GameEvent.SHEAR, blockPos);
                    return true;
                }
            }
        }

        return false;
    }
}
