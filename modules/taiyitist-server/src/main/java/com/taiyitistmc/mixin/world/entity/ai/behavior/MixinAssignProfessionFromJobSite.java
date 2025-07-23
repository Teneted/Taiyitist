package com.taiyitistmc.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.behavior.AssignProfessionFromJobSite;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AssignProfessionFromJobSite.class)
public class MixinAssignProfessionFromJobSite {

    @Redirect(method = "method_46891", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/VillagerData;withProfession(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/entity/npc/VillagerData;"))
    private static VillagerData taiyitist$callVillagerCareerChangeEvent(VillagerData instance, Holder<VillagerProfession> holder, @Cancellable CallbackInfo ci, @Local(argsOnly = true) Villager villager) {
        // CraftBukkit start - Fire VillagerCareerChangeEvent where Villager gets employed
        VillagerCareerChangeEvent event = CraftEventFactory.callVillagerCareerChangeEvent(villager, CraftVillager.CraftProfession.minecraftHolderToBukkit(holder), VillagerCareerChangeEvent.ChangeReason.EMPLOYED);
        if (event.isCancelled()) {
            ci.cancel();
            return null;
        }

        return instance.withProfession(CraftVillager.CraftProfession.bukkitToMinecraftHolder(event.getProfession()));
        // CraftBukkit end
    }
}
