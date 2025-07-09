package com.taiyitistmc.mixin.server.commonds;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LootCommand.class)
public class MixinLootCommand {

    @Inject(method = "dropInWorld", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private static void taiyitist$removeIf(CommandSourceStack commandSourceStack, Vec3 vec3, List<ItemStack> list, LootCommand.Callback callback, CallbackInfoReturnable<Integer> cir) {
        list.removeIf(ItemStack::isEmpty); // CraftBukkit - SPIGOT-6959 Remove empty items for avoid throw an error in new EntityItem
    }
}
