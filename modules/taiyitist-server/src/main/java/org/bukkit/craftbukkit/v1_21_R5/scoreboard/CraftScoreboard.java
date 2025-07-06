package org.bukkit.craftbukkit.v1_21_R5.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftChatMessage;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class CraftScoreboard implements Scoreboard {
   final net.minecraft.world.scores.Scoreboard board;

   CraftScoreboard(net.minecraft.world.scores.Scoreboard board) {
      this.board = board;
   }

   public CraftObjective registerNewObjective(String name, String criteria) {
      return this.registerNewObjective(name, criteria, name);
   }

   public CraftObjective registerNewObjective(String name, String criteria, String displayName) {
      return this.registerNewObjective(name, (Criteria)CraftCriteria.getFromBukkit(criteria), displayName, RenderType.INTEGER);
   }

   public CraftObjective registerNewObjective(String name, String criteria, String displayName, RenderType renderType) {
      return this.registerNewObjective(name, (Criteria)CraftCriteria.getFromBukkit(criteria), displayName, renderType);
   }

   public CraftObjective registerNewObjective(String name, Criteria criteria, String displayName) {
      return this.registerNewObjective(name, criteria, displayName, RenderType.INTEGER);
   }

   public CraftObjective registerNewObjective(String name, Criteria criteria, String displayName, RenderType renderType) {
      Preconditions.checkArgument(name != null, "Objective name cannot be null");
      Preconditions.checkArgument(criteria != null, "Criteria cannot be null");
      Preconditions.checkArgument(displayName != null, "Display name cannot be null");
      Preconditions.checkArgument(renderType != null, "RenderType cannot be null");
      Preconditions.checkArgument(name.length() <= 32767, "The name '%s' is longer than the limit of 32767 characters (%s)", name, name.length());
      Preconditions.checkArgument(this.board.getObjective(name) == null, "An objective of name '%s' already exists", name);
      Objective objective = this.board.addObjective(name, ((CraftCriteria)criteria).criteria, CraftChatMessage.fromStringOrEmpty(displayName), CraftScoreboardTranslations.fromBukkitRender(renderType), true, (NumberFormat)null);
      return new CraftObjective(this, objective);
   }

   public org.bukkit.scoreboard.Objective getObjective(String name) {
      Preconditions.checkArgument(name != null, "Objective name cannot be null");
      Objective nms = this.board.getObjective(name);
      return nms == null ? null : new CraftObjective(this, nms);
   }

   public ImmutableSet<org.bukkit.scoreboard.Objective> getObjectivesByCriteria(String criteria) {
      Preconditions.checkArgument(criteria != null, "Criteria name cannot be null");
      ImmutableSet.Builder<org.bukkit.scoreboard.Objective> objectives = ImmutableSet.builder();
      Iterator var3 = this.board.getObjectives().iterator();

      while(var3.hasNext()) {
         Objective netObjective = (Objective)var3.next();
         CraftObjective objective = new CraftObjective(this, netObjective);
         if (objective.getCriteria().equals(criteria)) {
            objectives.add(objective);
         }
      }

      return objectives.build();
   }

   public ImmutableSet<org.bukkit.scoreboard.Objective> getObjectivesByCriteria(Criteria criteria) {
      Preconditions.checkArgument(criteria != null, "Criteria cannot be null");
      ImmutableSet.Builder<org.bukkit.scoreboard.Objective> objectives = ImmutableSet.builder();
      Iterator var3 = this.board.getObjectives().iterator();

      while(var3.hasNext()) {
         Objective netObjective = (Objective)var3.next();
         CraftObjective objective = new CraftObjective(this, netObjective);
         if (objective.getTrackedCriteria().equals(criteria)) {
            objectives.add(objective);
         }
      }

      return objectives.build();
   }

   public ImmutableSet<org.bukkit.scoreboard.Objective> getObjectives() {
      return ImmutableSet.copyOf(Iterables.transform(this.board.getObjectives(), (input) -> {
         return new CraftObjective(this, input);
      }));
   }

   public org.bukkit.scoreboard.Objective getObjective(DisplaySlot slot) {
      Preconditions.checkArgument(slot != null, "Display slot cannot be null");
      Objective objective = this.board.getDisplayObjective(CraftScoreboardTranslations.fromBukkitSlot(slot));
      return objective == null ? null : new CraftObjective(this, objective);
   }

   public ImmutableSet<Score> getScores(OfflinePlayer player) {
      return this.getScores(getScoreHolder(player));
   }

   public ImmutableSet<Score> getScores(String entry) {
      return this.getScores(getScoreHolder(entry));
   }

   private ImmutableSet<Score> getScores(ScoreHolder entry) {
      Preconditions.checkArgument(entry != null, "Entry cannot be null");
      ImmutableSet.Builder<Score> scores = ImmutableSet.builder();
      Iterator var3 = this.board.getObjectives().iterator();

      while(var3.hasNext()) {
         Objective objective = (Objective)var3.next();
         scores.add(new CraftScore(new CraftObjective(this, objective), entry));
      }

      return scores.build();
   }

   public void resetScores(OfflinePlayer player) {
      this.resetScores(getScoreHolder(player));
   }

   public void resetScores(String entry) {
      this.resetScores(getScoreHolder(entry));
   }

   private void resetScores(ScoreHolder entry) {
      Preconditions.checkArgument(entry != null, "Entry cannot be null");
      Iterator var2 = this.board.getObjectives().iterator();

      while(var2.hasNext()) {
         Objective objective = (Objective)var2.next();
         this.board.resetSinglePlayerScore(entry, objective);
      }

   }

   public Team getPlayerTeam(OfflinePlayer player) {
      Preconditions.checkArgument(player != null, "OfflinePlayer cannot be null");
      PlayerTeam team = this.board.getPlayersTeam(player.getName());
      return team == null ? null : new CraftTeam(this, team);
   }

   public Team getEntryTeam(String entry) {
      Preconditions.checkArgument(entry != null, "Entry cannot be null");
      PlayerTeam team = this.board.getPlayersTeam(entry);
      return team == null ? null : new CraftTeam(this, team);
   }

   public Team getTeam(String teamName) {
      Preconditions.checkArgument(teamName != null, "Team name cannot be null");
      PlayerTeam team = this.board.getPlayerTeam(teamName);
      return team == null ? null : new CraftTeam(this, team);
   }

   public ImmutableSet<Team> getTeams() {
      return ImmutableSet.copyOf(Iterables.transform(this.board.getPlayerTeams(), (input) -> {
         return new CraftTeam(this, input);
      }));
   }

   public Team registerNewTeam(String name) {
      Preconditions.checkArgument(name != null, "Team name cannot be null");
      Preconditions.checkArgument(name.length() <= 32767, "Team name '%s' is longer than the limit of 32767 characters (%s)", name, name.length());
      Preconditions.checkArgument(this.board.getPlayerTeam(name) == null, "Team name '%s' is already in use", name);
      return new CraftTeam(this, this.board.addPlayerTeam(name));
   }

   public ImmutableSet<OfflinePlayer> getPlayers() {
      ImmutableSet.Builder<OfflinePlayer> players = ImmutableSet.builder();
      Iterator var2 = this.board.getTrackedPlayers().iterator();

      while(var2.hasNext()) {
         ScoreHolder playerName = (ScoreHolder)var2.next();
         players.add(Bukkit.getOfflinePlayer(playerName.getScoreboardName()));
      }

      return players.build();
   }

   public ImmutableSet<String> getEntries() {
      ImmutableSet.Builder<String> entries = ImmutableSet.builder();
      Iterator var2 = this.board.getTrackedPlayers().iterator();

      while(var2.hasNext()) {
         ScoreHolder entry = (ScoreHolder)var2.next();
         entries.add(entry.getScoreboardName());
      }

      return entries.build();
   }

   public void clearSlot(DisplaySlot slot) {
      Preconditions.checkArgument(slot != null, "Slot cannot be null");
      this.board.setDisplayObjective(CraftScoreboardTranslations.fromBukkitSlot(slot), (Objective)null);
   }

   public net.minecraft.world.scores.Scoreboard getHandle() {
      return this.board;
   }

   static ScoreHolder getScoreHolder(String entry) {
      return () -> {
         return entry;
      };
   }

   static ScoreHolder getScoreHolder(OfflinePlayer player) {
      Preconditions.checkArgument(player != null, "OfflinePlayer cannot be null");
      if (player instanceof CraftPlayer craft) {
         return craft.getHandle();
      } else {
         return getScoreHolder(player.getName());
      }
   }
}
