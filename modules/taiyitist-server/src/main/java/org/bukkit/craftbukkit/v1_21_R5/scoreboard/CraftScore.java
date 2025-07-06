package org.bukkit.craftbukkit.v1_21_R5.scoreboard;

import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

final class CraftScore implements Score {
   private final ScoreHolder entry;
   private final CraftObjective objective;

   CraftScore(CraftObjective objective, ScoreHolder entry) {
      this.objective = objective;
      this.entry = entry;
   }

   public OfflinePlayer getPlayer() {
      return Bukkit.getOfflinePlayer(this.entry.getScoreboardName());
   }

   public String getEntry() {
      return this.entry.getScoreboardName();
   }

   public Objective getObjective() {
      return this.objective;
   }

   public int getScore() {
      Scoreboard board = this.objective.checkState().board;
      ReadOnlyScoreInfo score = board.getPlayerScoreInfo(this.entry, this.objective.getHandle());
      return score != null ? score.value() : 0;
   }

   public void setScore(int score) {
      this.objective.checkState().board.getOrCreatePlayerScore(this.entry, this.objective.getHandle()).set(score);
   }

   public boolean isScoreSet() {
      Scoreboard board = this.objective.checkState().board;
      return board.getPlayerScoreInfo(this.entry, this.objective.getHandle()) != null;
   }

   public CraftScoreboard getScoreboard() {
      return this.objective.getScoreboard();
   }
}
