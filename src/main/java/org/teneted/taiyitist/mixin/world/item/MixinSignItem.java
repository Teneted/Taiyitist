package org.teneted.taiyitist.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.bukkit.BukkitFieldHooks;

@Mixin(SignItem.class)
public class MixinSignItem {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static BlockPos openSign; // CraftBukkit

    @Redirect(method = "updateCustomBlockEntityTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SignBlock;openTextEdit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/entity/SignBlockEntity;Z)V"))
    private void taiyitist$markOpenSign(SignBlock instance, Player player, SignBlockEntity sign, boolean isFrontText, @Local(argsOnly = true) BlockPos pos) {
        // CraftBukkit start - SPIGOT-4678
        // signblock.openTextEdit(player, signblockentity, true);
        BukkitFieldHooks.setOpenSign(pos);
        // CraftBukkit end
    }
}
