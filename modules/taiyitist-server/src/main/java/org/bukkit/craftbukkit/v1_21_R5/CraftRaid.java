package org.bukkit.craftbukkit.v1_21_R5;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.World;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftLocation;
import org.bukkit.entity.Raider;

public final class CraftRaid implements Raid {
   private final net.minecraft.world.entity.raid.Raid handle;
   private final Level world;

   public CraftRaid(net.minecraft.world.entity.raid.Raid handle, Level world) {
      this.handle = handle;
      this.world = world;
   }

   public boolean isStarted() {
      return this.handle.isStarted();
   }

   public long getActiveTicks() {
      return this.handle.ticksActive;
   }

   public int getBadOmenLevel() {
      return this.handle.raidOmenLevel;
   }

   public void setBadOmenLevel(int badOmenLevel) {
      int max = this.handle.getMaxRaidOmenLevel();
      Preconditions.checkArgument(0 <= badOmenLevel && badOmenLevel <= max, "Bad Omen level must be between 0 and %s", max);
      this.handle.raidOmenLevel = badOmenLevel;
   }

   public Location getLocation() {
      BlockPos pos = this.handle.getCenter();
      return CraftLocation.toBukkit((BlockPos)pos, (World)this.world.getWorld());
   }

   public Raid.RaidStatus getStatus() {
      if (this.handle.isStopped()) {
         return RaidStatus.STOPPED;
      } else if (this.handle.isVictory()) {
         return RaidStatus.VICTORY;
      } else {
         return this.handle.isLoss() ? RaidStatus.LOSS : RaidStatus.ONGOING;
      }
   }

   public int getSpawnedGroups() {
      return this.handle.getGroupsSpawned();
   }

   public int getTotalGroups() {
      return this.handle.numGroups + (this.handle.raidOmenLevel > 1 ? 1 : 0);
   }

   public int getTotalWaves() {
      return this.handle.numGroups;
   }

   public float getTotalHealth() {
      return this.handle.getHealthOfLivingRaiders();
   }

   public Set<UUID> getHeroes() {
      return Collections.unmodifiableSet(this.handle.heroesOfTheVillage);
   }

   public List<Raider> getRaiders() {
      return (List)this.handle.getRaiders().stream().map(new Function<net.minecraft.world.entity.raid.Raider, Raider>(this) {
         public Raider apply(net.minecraft.world.entity.raid.Raider entityRaider) {
            return (Raider)entityRaider.getBukkitEntity();
         }
      }).collect(ImmutableList.toImmutableList());
   }

   public net.minecraft.world.entity.raid.Raid getHandle() {
      return this.handle;
   }
}
