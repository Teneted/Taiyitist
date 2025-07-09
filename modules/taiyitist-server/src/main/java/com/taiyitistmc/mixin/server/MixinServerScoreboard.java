package com.taiyitistmc.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(ServerScoreboard.class)
public class MixinServerScoreboard {

    @Shadow @Final private MinecraftServer server;

    @Redirect(method = "onScoreChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$onScoreChanged(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) ScoreHolder scoreHolder, @Local(argsOnly = true) Objective objective, @Local(argsOnly = true) Score score) {
        this.broadcastAll(new ClientboundSetScorePacket(scoreHolder.getScoreboardName(), objective.getName(), score.value(), Optional.ofNullable(score.display()), Optional.ofNullable(score.numberFormat())));
    }

    @Redirect(method = "onPlayerRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$onPlayerRemoved(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) ScoreHolder scoreHolder) {
        this.broadcastAll(new ClientboundResetScorePacket(scoreHolder.getScoreboardName(), (String)null));
    }

    @Redirect(method = "setDisplayObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0))
    private void taiyitist$setDisplayObjective(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) DisplaySlot displaySlot, @Local(ordinal = 0, argsOnly = true) Objective objective) {
        this.broadcastAll(new ClientboundSetDisplayObjectivePacket(displaySlot, objective));
    }

    @Redirect(method = "setDisplayObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 1))
    private void taiyitist$setDisplayObjective0(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) DisplaySlot displaySlot, @Local(ordinal = 0, argsOnly = true) Objective objective) {
        this.broadcastAll(new ClientboundSetDisplayObjectivePacket(displaySlot, objective));
    }

    @Redirect(method = "onPlayerScoreRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$onPlayerScoreRemoved(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) ScoreHolder scoreHolder, @Local(argsOnly = true) Objective objective) {
        this.broadcastAll(new ClientboundResetScorePacket(scoreHolder.getScoreboardName(), objective.getName()));
    }

    @Redirect(method = "addPlayerToTeam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$addPlayerToTeam(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) String string, @Local(argsOnly = true) PlayerTeam playerTeam) {
        this.broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket(playerTeam, string, ClientboundSetPlayerTeamPacket.Action.ADD));
    }

    @Redirect(method = "removePlayerFromTeam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$removePlayerFromTeam(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) String string, @Local(argsOnly = true) PlayerTeam playerTeam) {
        this.broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket(playerTeam, string, ClientboundSetPlayerTeamPacket.Action.REMOVE));
    }

    @Redirect(method = "onObjectiveChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$onObjectiveChanged(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) Objective objective) {
        this.broadcastAll(new ClientboundSetObjectivePacket(objective, 2));
    }

    @Redirect(method = "onTeamAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$onTeamAdded(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) PlayerTeam playerTeam) {
        this.broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, true));
    }

    @Redirect(method = "onTeamChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$onTeamChanged(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) PlayerTeam playerTeam) {
        this.broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, false));
    }

    @Redirect(method = "onTeamRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$onTeamRemoved(PlayerList instance, Packet<?> packet, @Local(argsOnly = true) PlayerTeam playerTeam) {
        this.broadcastAll(ClientboundSetPlayerTeamPacket.createRemovePacket(playerTeam));
    }

    @Inject(method = "startTrackingObjective", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 1), cancellable = true)
    private void taiyitist$checkHasPlayer(Objective objective, CallbackInfo ci, @Local ServerPlayer serverPlayer) {
        if (serverPlayer.getBukkitEntity().getScoreboard().getHandle() == ((Scoreboard) (Object) this)) ci.cancel(); // CraftBukkit - Only players on this board
    }

    @Inject(method = "stopTrackingObjective", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 1), cancellable = true)
    private void taiyitist$checkHasPlayer0(Objective objective, CallbackInfo ci, @Local ServerPlayer serverPlayer) {
        if (serverPlayer.getBukkitEntity().getScoreboard().getHandle() == ((Scoreboard) (Object) this)) ci.cancel(); // CraftBukkit - Only players on this board

    }

    // CraftBukkit start - Send to players
    private void broadcastAll(Packet packet) {
        for (ServerPlayer entityplayer : (List<ServerPlayer>) this.server.getPlayerList().players) {
            if (entityplayer.getBukkitEntity().getScoreboard().getHandle() == ((Scoreboard) (Object) this)) {
                entityplayer.connection.send(packet);
            }
        }
    }
    // CraftBukkit end
}
