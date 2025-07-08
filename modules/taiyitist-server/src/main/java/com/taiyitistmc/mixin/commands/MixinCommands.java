package com.taiyitistmc.mixin.commands;

import com.google.common.base.Joiner;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.tree.RootCommandNode;
import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.bukkit.BukkitDispatcher;
import com.taiyitistmc.injection.commands.InjectionCommandNode;
import com.taiyitistmc.injection.commands.InjectionCommands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class MixinCommands implements InjectionCommands {

    @Mutable
    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;
    @Shadow
    public abstract void performCommand(ParseResults<CommandSourceStack> parseResults, String string);

    @Shadow
    public abstract void performPrefixedCommand(CommandSourceStack commandSourceStack, String string);

    @Shadow
    private static <S> void fillUsableCommands(CommandNode<S> commandNode, CommandNode<S> commandNode2, S object, Map<CommandNode<S>, CommandNode<S>> map) {
    }

    @CreateConstructor
    public void taiyitist$constructor() {
        this.dispatcher = new BukkitDispatcher((Commands) (Object) this);
        this.dispatcher.setConsumer(ExecutionCommandSource.resultConsumer());
    }

    @Override
    public void performPrefixedCommand(CommandSourceStack commandSourceStack, String s, String label) {
        this.performPrefixedCommand(commandSourceStack, s);
    }

    @Override
    public void performCommand(ParseResults<CommandSourceStack> parseResults, String s, String label) {
        this.performCommand(parseResults, s);
    }

    @Override
    public void dispatchServerCommand(CommandSourceStack sender, String command) {
        Joiner joiner = Joiner.on(" ");
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        ServerCommandEvent event = new ServerCommandEvent(sender.taiyitist$getBukkitSender(), command);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        command = event.getCommand();

        String[] args = command.split(" ");

        String cmd = args[0];
        if (cmd.startsWith("minecraft:")) cmd = cmd.substring("minecraft:".length());
        if (cmd.startsWith("bukkit:")) cmd = cmd.substring("bukkit:".length());

        // Block disallowed commands
        if (cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("kick") || cmd.equalsIgnoreCase("op")
                || cmd.equalsIgnoreCase("deop") || cmd.equalsIgnoreCase("ban") || cmd.equalsIgnoreCase("ban-ip")
                || cmd.equalsIgnoreCase("pardon") || cmd.equalsIgnoreCase("pardon-ip") || cmd.equalsIgnoreCase("reload")) {
            return;
        }

        // Handle vanilla commands;
        if (sender.getLevel().getCraftServer().getCommandBlockOverride(args[0])) {
            args[0] = "minecraft:" + args[0];
        }

        String newCommand = joiner.join(args);
        this.performPrefixedCommand(sender, newCommand, newCommand);
    }
    // CraftBukkit end

    @Inject(method = "sendCommands", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/tree/RootCommandNode;<init>()V"))
    private void taiyitist$initCMD(ServerPlayer serverPlayer, CallbackInfo ci,
                                   @Local Map<CommandNode<CommandSourceStack>,
                                           CommandNode<CommandSourceStack>> map) {
        map = new IdentityHashMap<>(); // Use identity to prevent aliasing issues
        RootCommandNode<CommandSourceStack> vanillaRoot = new RootCommandNode();
        RootCommandNode<CommandSourceStack> vanilla = serverPlayer.server.bridge$getVanillaCommands().getDispatcher().getRoot();
        map.put(vanilla, vanillaRoot);
        fillUsableCommands(vanilla, vanillaRoot, serverPlayer.createCommandSourceStack(), (Map) map);
    }

    @Inject(method = "sendCommands", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$fireCMDEvent(ServerPlayer serverPlayer, CallbackInfo ci, @Local RootCommandNode<CommandSourceStack> rootCommandNode) {
        Collection<String> bukkit = new LinkedHashSet<>();
        for (CommandNode node : rootCommandNode.getChildren()) {
            bukkit.add(node.getName());
        }

        PlayerCommandSendEvent event = new PlayerCommandSendEvent(serverPlayer.getBukkitEntity(), new LinkedHashSet<>(bukkit));
        event.getPlayer().getServer().getPluginManager().callEvent(event);

        // Remove labels that were removed during the event
        for (String orig : bukkit) {
            if (!event.getCommands().contains(orig)) {
                ((InjectionCommandNode) rootCommandNode).taiyitist$removeCommand(orig);
            }
        }
        // CraftBukkit end
    }
}
