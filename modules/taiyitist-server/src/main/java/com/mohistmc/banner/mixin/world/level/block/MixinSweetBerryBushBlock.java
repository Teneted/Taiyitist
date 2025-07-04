package com.mohistmc.banner.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Collections;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SweetBerryBushBlock.class)
public class MixinSweetBerryBushBlock {

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean banner$cropGrow(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, blockPos, blockState, i);
    }

    @Redirect(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SweetBerryBushBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void banner$playerHarvest(Level level, BlockPos blockPos, ItemStack itemStack, @Local(argsOnly = true) Player player) {
        PlayerHarvestBlockEvent event = CraftEventFactory.callPlayerHarvestBlockEvent(level, blockPos, player, InteractionHand.MAIN_HAND, Collections.singletonList(new ItemStack(Items.SWEET_BERRIES)));
        if (!event.isCancelled()) {
            for (org.bukkit.inventory.ItemStack stack : event.getItemsHarvested()) {
                Block.popResource(level, blockPos, CraftItemStack.asNMSCopy(stack));
            }
        } else {
            return;
        }
    }
}
