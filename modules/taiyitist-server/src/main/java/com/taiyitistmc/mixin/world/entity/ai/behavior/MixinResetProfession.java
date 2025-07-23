package com.taiyitistmc.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.ResetProfession;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResetProfession.class)
public class MixinResetProfession {

    @Redirect(method = "method_47038", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/VillagerData;withProfession(Lnet/minecraft/core/HolderGetter$Provider;Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/entity/npc/VillagerData;"))
    private static VillagerData taiyitist$callVillagerCareerChangeEvent(VillagerData instance, HolderGetter.Provider provider, ResourceKey<VillagerProfession> resourceKey, @Cancellable CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) Villager villager, @Local(argsOnly = true) ServerLevel serverLevel) {
        // CraftBukkit start
        VillagerCareerChangeEvent event = CraftEventFactory.callVillagerCareerChangeEvent(villager, CraftVillager.CraftProfession.minecraftHolderToBukkit(serverLevel.registryAccess().getOrThrow(VillagerProfession.NONE)), VillagerCareerChangeEvent.ChangeReason.LOSING_JOB);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        return instance.withProfession(CraftVillager.CraftProfession.bukkitToMinecraftHolder(event.getProfession()));
        // CraftBukkit end
    }
}
