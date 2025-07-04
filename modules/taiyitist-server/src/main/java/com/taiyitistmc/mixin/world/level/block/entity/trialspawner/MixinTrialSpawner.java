package com.taiyitistmc.mixin.world.level.block.entity.trialspawner;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrialSpawner.class)
public class MixinTrialSpawner {

    @Inject(method = "spawnMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void banner$spawnMob(ServerLevel serverLevel, BlockPos blockPos, CallbackInfoReturnable<Optional<UUID>> cir, @Local Entity entity) {
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callTrialSpawnerSpawnEvent(entity, blockPos).isCancelled()) {
            cir.setReturnValue(Optional.empty());
        }
        entity.pushSpawnCause(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER);
    }

    @Inject(method = "ejectReward", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;iterator()Lit/unimi/dsi/fastutil/objects/ObjectListIterator;"))
    private void banner$spawnerDispenseLootEvent(ServerLevel serverLevel, BlockPos blockPos, ResourceKey<LootTable> resourceKey, CallbackInfo ci, @Local ObjectArrayList<ItemStack> objectArrayList) {
        // CraftBukkit start
        BlockDispenseLootEvent spawnerDispenseLootEvent = CraftEventFactory.callBlockDispenseLootEvent(serverLevel, blockPos, null, objectArrayList);
        if (spawnerDispenseLootEvent.isCancelled()) {
            return;
        }

        objectArrayList = new ObjectArrayList<>(spawnerDispenseLootEvent.getDispensedLoot().stream().map(CraftItemStack::asNMSCopy).toList());
        // CraftBukkit end
    }
}
