package org.bukkit.craftbukkit.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team.CollisionRule;
import net.minecraft.world.scores.Team.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

final class CraftTeam extends CraftScoreboardComponent implements Team {
   private final PlayerTeam team;

   CraftTeam(CraftScoreboard scoreboard, PlayerTeam team) {
      super(scoreboard);
      this.team = team;
   }

   public String getName() {
      this.checkState();
      return this.team.getName();
   }

   public String getDisplayName() {
      this.checkState();
      return CraftChatMessage.fromComponent(this.team.getDisplayName());
   }

   public void setDisplayName(String displayName) {
      Preconditions.checkArgument(displayName != null, "Display name cannot be null");
      this.checkState();
      this.team.setDisplayName(CraftChatMessage.fromString(displayName)[0]);
   }

   public String getPrefix() {
      this.checkState();
      return CraftChatMessage.fromComponent(this.team.getPlayerPrefix());
   }

   public void setPrefix(String prefix) {
      Preconditions.checkArgument(prefix != null, "Prefix cannot be null");
      this.checkState();
      this.team.setPlayerPrefix(CraftChatMessage.fromStringOrNull(prefix));
   }

   public String getSuffix() {
      this.checkState();
      return CraftChatMessage.fromComponent(this.team.getPlayerSuffix());
   }

   public void setSuffix(String suffix) {
      Preconditions.checkArgument(suffix != null, "Suffix cannot be null");
      this.checkState();
      this.team.setPlayerSuffix(CraftChatMessage.fromStringOrNull(suffix));
   }

   public ChatColor getColor() {
      this.checkState();
      return CraftChatMessage.getColor(this.team.getColor());
   }

   public void setColor(ChatColor color) {
      Preconditions.checkArgument(color != null && !color.isFormat(), "Color cannot be null or a format");
      this.checkState();
      this.team.setColor(CraftChatMessage.getColor(color));
   }

   public boolean allowFriendlyFire() {
      this.checkState();
      return this.team.isAllowFriendlyFire();
   }

   public void setAllowFriendlyFire(boolean enabled) {
      this.checkState();
      this.team.setAllowFriendlyFire(enabled);
   }

   public boolean canSeeFriendlyInvisibles() {
      this.checkState();
      return this.team.canSeeFriendlyInvisibles();
   }

   public void setCanSeeFriendlyInvisibles(boolean enabled) {
      this.checkState();
      this.team.setSeeFriendlyInvisibles(enabled);
   }

   public NameTagVisibility getNameTagVisibility() throws IllegalArgumentException {
      this.checkState();
      return notchToBukkit(this.team.getNameTagVisibility());
   }

   public void setNameTagVisibility(NameTagVisibility visibility) throws IllegalArgumentException {
      this.checkState();
      this.team.setNameTagVisibility(bukkitToNotch(visibility));
   }

   public Set<OfflinePlayer> getPlayers() {
      this.checkState();
      ImmutableSet.Builder<OfflinePlayer> players = ImmutableSet.builder();
      Iterator var2 = this.team.getPlayers().iterator();

      while(var2.hasNext()) {
         String playerName = (String)var2.next();
         players.add(Bukkit.getOfflinePlayer(playerName));
      }

      return players.build();
   }

   public Set<String> getEntries() {
      this.checkState();
      ImmutableSet.Builder<String> entries = ImmutableSet.builder();
      Iterator var2 = this.team.getPlayers().iterator();

      while(var2.hasNext()) {
         String playerName = (String)var2.next();
         entries.add(playerName);
      }

      return entries.build();
   }

   public int getSize() {
      this.checkState();
      return this.team.getPlayers().size();
   }

   public void addPlayer(OfflinePlayer player) {
      Preconditions.checkArgument(player != null, "OfflinePlayer cannot be null");
      this.addEntry(player.getName());
   }

   public void addEntry(String entry) {
      Preconditions.checkArgument(entry != null, "Entry cannot be null");
      CraftScoreboard scoreboard = this.checkState();
      scoreboard.board.addPlayerToTeam(entry, this.team);
   }

   public boolean removePlayer(OfflinePlayer player) {
      Preconditions.checkArgument(player != null, "OfflinePlayer cannot be null");
      return this.removeEntry(player.getName());
   }

   public boolean removeEntry(String entry) {
      Preconditions.checkArgument(entry != null, "Entry cannot be null");
      CraftScoreboard scoreboard = this.checkState();
      if (!this.team.getPlayers().contains(entry)) {
         return false;
      } else {
         scoreboard.board.removePlayerFromTeam(entry, this.team);
         return true;
      }
   }

   public boolean hasPlayer(OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
      Preconditions.checkArgument(player != null, "OfflinePlayer cannot be null");
      return this.hasEntry(player.getName());
   }

   public boolean hasEntry(String entry) throws IllegalArgumentException, IllegalStateException {
      Preconditions.checkArgument(entry != null, "Entry cannot be null");
      this.checkState();
      return this.team.getPlayers().contains(entry);
   }

   public void unregister() {
      CraftScoreboard scoreboard = this.checkState();
      scoreboard.board.removePlayerTeam(this.team);
   }

   public Team.OptionStatus getOption(Team.Option option) {
      this.checkState();
      switch (option) {
         case NAME_TAG_VISIBILITY -> {
            return OptionStatus.values()[this.team.getNameTagVisibility().ordinal()];
         }
         case DEATH_MESSAGE_VISIBILITY -> {
            return OptionStatus.values()[this.team.getDeathMessageVisibility().ordinal()];
         }
         case COLLISION_RULE -> {
            return OptionStatus.values()[this.team.getCollisionRule().ordinal()];
         }
         default -> throw new IllegalArgumentException("Unrecognised option " + String.valueOf(option));
      }
   }

   public void setOption(Team.Option option, Team.OptionStatus status) {
      this.checkState();
      switch (option) {
         case NAME_TAG_VISIBILITY -> this.team.setNameTagVisibility(Visibility.values()[status.ordinal()]);
         case DEATH_MESSAGE_VISIBILITY -> this.team.setDeathMessageVisibility(Visibility.values()[status.ordinal()]);
         case COLLISION_RULE -> this.team.setCollisionRule(CollisionRule.values()[status.ordinal()]);
         default -> throw new IllegalArgumentException("Unrecognised option " + String.valueOf(option));
      }

   }

   public static net.minecraft.world.scores.Team.Visibility bukkitToNotch(NameTagVisibility visibility) {
      switch (visibility) {
         case ALWAYS -> {
            return Visibility.ALWAYS;
         }
         case NEVER -> {
            return Visibility.NEVER;
         }
         case HIDE_FOR_OTHER_TEAMS -> {
            return Visibility.HIDE_FOR_OTHER_TEAMS;
         }
         case HIDE_FOR_OWN_TEAM -> {
            return Visibility.HIDE_FOR_OWN_TEAM;
         }
         default -> throw new IllegalArgumentException("Unknown visibility level " + String.valueOf(visibility));
      }
   }

   public static NameTagVisibility notchToBukkit(net.minecraft.world.scores.Team.Visibility visibility) {
      switch (visibility) {
         case ALWAYS -> {
            return NameTagVisibility.ALWAYS;
         }
         case NEVER -> {
            return NameTagVisibility.NEVER;
         }
         case HIDE_FOR_OTHER_TEAMS -> {
            return NameTagVisibility.HIDE_FOR_OTHER_TEAMS;
         }
         case HIDE_FOR_OWN_TEAM -> {
            return NameTagVisibility.HIDE_FOR_OWN_TEAM;
         }
         default -> throw new IllegalArgumentException("Unknown visibility level " + String.valueOf(visibility));
      }
   }

   CraftScoreboard checkState() {
      Preconditions.checkState(this.getScoreboard().board.getPlayerTeam(this.team.getName()) != null, "Unregistered scoreboard component");
      return this.getScoreboard();
   }

   public int hashCode() {
      int hash = 7;
      hash = 43 * hash + (this.team != null ? this.team.hashCode() : 0);
      return hash;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftTeam other = (CraftTeam)obj;
         return this.team == other.team || this.team != null && this.team.equals(other.team);
      }
   }
}
