package com.taiyitistmc.mixin.world.level.block.entity.valut;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.level.block.entity.vault.VaultState;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.block.VaultDisplayItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VaultBlockEntity.Server.class)
public class MixinVaultBlockEntity_Server {

    @Inject(method = "tryInsertKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/vault/VaultBlockEntity$Server;unlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/vault/VaultConfig;Lnet/minecraft/world/level/block/entity/vault/VaultServerData;Lnet/minecraft/world/level/block/entity/vault/VaultSharedData;Ljava/util/List;)V"), cancellable = true)
    private static void taiyitist$vaultDispenseLootEvent(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, VaultConfig vaultConfig, VaultServerData vaultServerData, VaultSharedData vaultSharedData, Player player, ItemStack itemStack, CallbackInfo ci, @Local List<ItemStack> list) {
        BlockDispenseLootEvent vaultDispenseLootEvent = CraftEventFactory.callBlockDispenseLootEvent(serverLevel, blockPos, player, list);
        if (vaultDispenseLootEvent.isCancelled()) {
            ci.cancel();
            return;
        }

        list = vaultDispenseLootEvent.getDispensedLoot().stream().map(CraftItemStack::asNMSCopy).toList();
    }

    @Inject(method = "cycleDisplayItemFromLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/vault/VaultSharedData;setDisplayItem(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 1))
    private static void taiyitist$vaultDisplayItemEvent(ServerLevel serverLevel, VaultState vaultState, VaultConfig vaultConfig, VaultSharedData vaultSharedData, BlockPos blockPos, CallbackInfo ci, @Local ItemStack itemStack) {
        VaultDisplayItemEvent event = CraftEventFactory.callVaultDisplayItemEvent(serverLevel, blockPos, itemStack);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        itemStack = CraftItemStack.asNMSCopy(event.getDisplayItem());
    }
}
