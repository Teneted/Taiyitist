package org.bukkit.craftbukkit.v1_21_R5.scoreboard;

import com.google.common.base.Preconditions;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftChatMessage;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;

final class CraftObjective extends CraftScoreboardComponent implements Objective {
   private final net.minecraft.world.scores.Objective objective;
   private final CraftCriteria criteria;

   CraftObjective(CraftScoreboard scoreboard, net.minecraft.world.scores.Objective objective) {
      super(scoreboard);
      this.objective = objective;
      this.criteria = CraftCriteria.getFromNMS(objective);
   }

   net.minecraft.world.scores.Objective getHandle() {
      return this.objective;
   }

   public String getName() {
      this.checkState();
      return this.objective.getName();
   }

   public String getDisplayName() {
      this.checkState();
      return CraftChatMessage.fromComponent(this.objective.getDisplayName());
   }

   public void setDisplayName(String displayName) {
      Preconditions.checkArgument(displayName != null, "Display name cannot be null");
      this.checkState();
      this.objective.setDisplayName(CraftChatMessage.fromString(displayName)[0]);
   }

   public String getCriteria() {
      this.checkState();
      return this.criteria.bukkitName;
   }

   public Criteria getTrackedCriteria() {
      this.checkState();
      return this.criteria;
   }

   public boolean isModifiable() {
      this.checkState();
      return !this.criteria.criteria.isReadOnly();
   }

   public void setDisplaySlot(DisplaySlot slot) {
      CraftScoreboard scoreboard = this.checkState();
      Scoreboard board = scoreboard.board;
      net.minecraft.world.scores.Objective objective = this.objective;
      net.minecraft.world.scores.DisplaySlot[] var5 = net.minecraft.world.scores.DisplaySlot.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         net.minecraft.world.scores.DisplaySlot i = var5[var7];
         if (board.getDisplayObjective(i) == objective) {
            board.setDisplayObjective(i, (net.minecraft.world.scores.Objective)null);
         }
      }

      if (slot != null) {
         net.minecraft.world.scores.DisplaySlot slotNumber = CraftScoreboardTranslations.fromBukkitSlot(slot);
         board.setDisplayObjective(slotNumber, this.getHandle());
      }

   }

   public DisplaySlot getDisplaySlot() {
      CraftScoreboard scoreboard = this.checkState();
      Scoreboard board = scoreboard.board;
      net.minecraft.world.scores.Objective objective = this.objective;
      net.minecraft.world.scores.DisplaySlot[] var4 = net.minecraft.world.scores.DisplaySlot.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         net.minecraft.world.scores.DisplaySlot i = var4[var6];
         if (board.getDisplayObjective(i) == objective) {
            return CraftScoreboardTranslations.toBukkitSlot(i);
         }
      }

      return null;
   }

   public void setRenderType(RenderType renderType) {
      Preconditions.checkArgument(renderType != null, "RenderType cannot be null");
      this.checkState();
      this.objective.setRenderType(CraftScoreboardTranslations.fromBukkitRender(renderType));
   }

   public RenderType getRenderType() {
      this.checkState();
      return CraftScoreboardTranslations.toBukkitRender(this.objective.getRenderType());
   }

   public Score getScore(OfflinePlayer player) {
      this.checkState();
      return new CraftScore(this, CraftScoreboard.getScoreHolder(player));
   }

   public Score getScore(String entry) {
      Preconditions.checkArgument(entry != null, "Entry cannot be null");
      Preconditions.checkArgument(entry.length() <= 32767, "Score '" + entry + "' is longer than the limit of 32767 characters");
      this.checkState();
      return new CraftScore(this, CraftScoreboard.getScoreHolder(entry));
   }

   public void unregister() {
      CraftScoreboard scoreboard = this.checkState();
      scoreboard.board.removeObjective(this.objective);
   }

   CraftScoreboard checkState() {
      Preconditions.checkState(this.getScoreboard().board.getObjective(this.objective.getName()) != null, "Unregistered scoreboard component");
      return this.getScoreboard();
   }

   public int hashCode() {
      int hash = 7;
      hash = 31 * hash + (this.objective != null ? this.objective.hashCode() : 0);
      return hash;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftObjective other = (CraftObjective)obj;
         return this.objective == other.objective || this.objective != null && this.objective.equals(other.objective);
      }
   }
}
