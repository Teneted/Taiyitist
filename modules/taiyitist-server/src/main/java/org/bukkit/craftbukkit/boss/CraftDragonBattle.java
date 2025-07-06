package org.bukkit.craftbukkit.boss;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.dimension.end.DragonRespawnAnimation;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEnderCrystal;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;

public class CraftDragonBattle implements DragonBattle {
   private final EndDragonFight handle;

   public CraftDragonBattle(EndDragonFight handle) {
      this.handle = handle;
   }

   public EnderDragon getEnderDragon() {
      Entity entity = this.handle.level.getEntity(this.handle.dragonUUID);
      return entity != null ? (EnderDragon)entity.getBukkitEntity() : null;
   }

   public BossBar getBossBar() {
      return new CraftBossBar(this.handle.dragonEvent);
   }

   public Location getEndPortalLocation() {
      return this.handle.portalLocation == null ? null : CraftLocation.toBukkit((BlockPos)this.handle.portalLocation, (World)this.handle.level.getWorld());
   }

   public boolean generateEndPortal(boolean withPortals) {
      if (this.handle.portalLocation == null && this.handle.findExitPortal() == null) {
         this.handle.spawnExitPortal(withPortals);
         return true;
      } else {
         return false;
      }
   }

   public boolean hasBeenPreviouslyKilled() {
      return this.handle.hasPreviouslyKilledDragon();
   }

   public void setPreviouslyKilled(boolean previouslyKilled) {
      this.handle.previouslyKilled = previouslyKilled;
   }

   public void initiateRespawn() {
      this.handle.tryRespawn();
   }

   public boolean initiateRespawn(Collection<EnderCrystal> list) {
      if (this.hasBeenPreviouslyKilled() && this.getRespawnPhase() == RespawnPhase.NONE) {
         // Copy from EnderDragonBattle#tryRespawn for generate exit portal if not exists
         if (this.handle.portalLocation == null) {
            BlockPattern.BlockPatternMatch shapedetector_shapedetectorcollection = this.handle.findExitPortal();
            if (shapedetector_shapedetectorcollection == null) {
               this.handle.spawnExitPortal(true);
            }
         }

         list = (list != null) ? new ArrayList<>(list) : Collections.emptyList();
         list.removeIf(enderCrystal -> {
            if (enderCrystal == null) {
               return true;
            }

            World world = enderCrystal.getWorld();
            return !((CraftWorld) world).getHandle().equals(this.handle.level);
         });

         this.handle.respawnDragon(list.stream().map(enderCrystal -> ((CraftEnderCrystal) enderCrystal).getHandle()).collect(Collectors.toList()));
         return this.handle.bridge$isRespawnDragon();
      }
      return false;
   }

   public DragonBattle.RespawnPhase getRespawnPhase() {
      return this.toBukkitRespawnPhase(this.handle.respawnStage);
   }

   public boolean setRespawnPhase(DragonBattle.RespawnPhase phase) {
      Preconditions.checkArgument(phase != null && phase != RespawnPhase.NONE, "Invalid respawn phase provided: %s", phase);
      if (this.handle.respawnStage == null) {
         return false;
      } else {
         this.handle.setRespawnStage(this.toNMSRespawnPhase(phase));
         return true;
      }
   }

   public void resetCrystals() {
      this.handle.resetSpikeCrystals();
   }

   public int hashCode() {
      return this.handle.hashCode();
   }

   public boolean equals(Object obj) {
      return obj instanceof CraftDragonBattle && ((CraftDragonBattle)obj).handle == this.handle;
   }

   private DragonBattle.RespawnPhase toBukkitRespawnPhase(DragonRespawnAnimation phase) {
      return phase != null ? RespawnPhase.values()[phase.ordinal()] : RespawnPhase.NONE;
   }

   private DragonRespawnAnimation toNMSRespawnPhase(DragonBattle.RespawnPhase phase) {
      return phase != RespawnPhase.NONE ? DragonRespawnAnimation.values()[phase.ordinal()] : null;
   }
}
