package org.teneted.taiyitist.mixin.commands;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.taiyitist.bukkit.DelegatePermissionSet;
import org.teneted.taiyitist.injection.commands.InjectionCommandSourceStack;

@Mixin(CommandSourceStack.class)
public class MixinCommandSourceStack implements InjectionCommandSourceStack {

    @Mutable
    @Shadow
    @Final
    public CommandSource source;
    // CraftBukkit start
    private PermissionSet bukkitPermissions;
    public volatile CommandNode currentCommand;
    // CraftBukkit end

    @Inject(method = "<init>(Lnet/minecraft/commands/CommandSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/permissions/PermissionSet;Ljava/lang/String;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/entity/Entity;ZLnet/minecraft/commands/CommandResultCallback;Lnet/minecraft/commands/arguments/EntityAnchorArgument$Anchor;Lnet/minecraft/commands/CommandSigningContext;Lnet/minecraft/util/TaskChainer;)V", at = @At("RETURN"))
    private void taiyitist$init(CommandSource source, Vec3 position, Vec2 rotation, ServerLevel level, PermissionSet permissions, String textName, Component displayName, MinecraftServer server, Entity entity, boolean silent, CommandResultCallback resultCallback, EntityAnchorArgument.Anchor anchor, CommandSigningContext signingContext, TaskChainer chatMessageChainer, CallbackInfo ci) {
        this.bukkitPermissions = new DelegatePermissionSet(permissions, ((CommandSourceStack) (Object) this));
    }

    @ModifyReturnValue(method = "permissions", at = @At("RETURN"))
    private PermissionSet taiyitist$useBukkitPermissions(PermissionSet original) {
        return bukkitPermissions;
    }

    @ModifyExpressionValue(method = "broadcastToAdmins", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;isOp(Lnet/minecraft/server/players/NameAndId;)Z"))
    private boolean taiyitist$useBukkitCheck(boolean original, @Local(name = "player") ServerPlayer player) {
        return player.getBukkitEntity().hasPermission("minecraft.admin.command_feedback");
    }

    @Override
    public void taiyitist$setSource(CommandSource source) {
        this.source = source;
    }

    @Override
    public CommandSender getBukkitSender() {
        return source.taiyitist$getBukkitSender(((CommandSourceStack) (Object) this));
    }

    @Override
    public CommandNode<?> bridge$getCurrentCommand() {
        return this.currentCommand;
    }

    @Override
    public void taiyitist$setCurrentCommand(CommandNode<?> node) {
        this.currentCommand = node;
    }
}
