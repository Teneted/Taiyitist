package com.taiyitistmc.mixin.commands;

import com.google.common.base.Joiner;
import com.mojang.brigadier.tree.RootCommandNode;
import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.bukkit.BukkitDispatcher;
import com.taiyitistmc.fabric.CommandNodeHooks;
import com.taiyitistmc.injection.commands.InjectionCommands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Shadow @Final private static ClientboundCommandsPacket.NodeInspector<CommandSourceStack> COMMAND_NODE_INSPECTOR;

    @CreateConstructor
    public void taiyitist$constructor() {
        this.dispatcher = new BukkitDispatcher((Commands) (Object) this);
        this.dispatcher.setConsumer(ExecutionCommandSource.resultConsumer());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext, CallbackInfo ci) {
        this.dispatcher = new BukkitDispatcher((Commands) (Object) this);
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

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public void sendCommands(ServerPlayer serverPlayer) {
        if (SpigotConfig.tabComplete < 0) return;
        // CraftBukkit start
        // Register Vanilla commands into builtRoot as before
        Map<CommandNode<CommandSourceStack>, CommandNode<CommandSourceStack>> map = new IdentityHashMap<>(); // Use identity to prevent aliasing issues
        RootCommandNode<CommandSourceStack> vanillaRoot = new RootCommandNode();

        RootCommandNode<CommandSourceStack> vanilla = serverPlayer.server.bridge$getVanillaCommands().getDispatcher().getRoot();
        map.put(vanilla, vanillaRoot);
        fillUsableCommands(vanilla, vanillaRoot, serverPlayer.createCommandSourceStack(), (Map) map);

        // Now build the global commands in a second pass
        RootCommandNode<CommandSourceStack> rootCommandNode = new RootCommandNode();
        map.put(this.dispatcher.getRoot(), rootCommandNode);
        fillUsableCommands(this.dispatcher.getRoot(), rootCommandNode, serverPlayer.createCommandSourceStack(), map);

        Collection<String> bukkit = new LinkedHashSet<>();
        for (CommandNode node : rootCommandNode.getChildren()) {
            bukkit.add(node.getName());
        }

        PlayerCommandSendEvent event = new PlayerCommandSendEvent(serverPlayer.getBukkitEntity(), new LinkedHashSet<>(bukkit));
        event.getPlayer().getServer().getPluginManager().callEvent(event);

        // Remove labels that were removed during the event
        for (String orig : bukkit) {
            if (!event.getCommands().contains(orig)) {
                CommandNodeHooks.removeCommand(rootCommandNode, orig);
            }
        }
        // CraftBukkit end
        serverPlayer.connection.send(new ClientboundCommandsPacket(rootCommandNode, COMMAND_NODE_INSPECTOR));
    }

    @Redirect(method = "fillUsableCommands", at = @At(value = "INVOKE", remap = false, target = "Lcom/mojang/brigadier/tree/CommandNode;canUse(Ljava/lang/Object;)Z"))
    private static <S> boolean taiyitist$canUse(CommandNode<S> commandNode, S source) {
        return CommandNodeHooks.canUse(commandNode, source);
    }
}
