package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Sniffer;

public class CraftSniffer extends CraftAnimals implements Sniffer {
   public CraftSniffer(CraftServer server, net.minecraft.world.entity.animal.sniffer.Sniffer entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.sniffer.Sniffer getHandle() {
      return (net.minecraft.world.entity.animal.sniffer.Sniffer)super.getHandle();
   }

   public String toString() {
      return "CraftSniffer";
   }

   public Collection<Location> getExploredLocations() {
      return (Collection)this.getHandle().getExploredPositions().map((blockPosition) -> {
         return CraftLocation.toBukkit((BlockPos)blockPosition.pos(), (Level)this.server.getServer().getLevel(blockPosition.dimension()));
      }).collect(Collectors.toList());
   }

   public void removeExploredLocation(Location location) {
      Preconditions.checkArgument(location != null, "location cannot be null");
      if (location.getWorld() == this.getWorld()) {
         BlockPos blockPosition = CraftLocation.toBlockPosition(location);
         this.getHandle().getBrain().setMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, (List)this.getHandle().getExploredPositions().filter((blockPositionExplored) -> {
            return !blockPositionExplored.equals(blockPosition);
         }).collect(Collectors.toList()));
      }
   }

   public void addExploredLocation(Location location) {
      Preconditions.checkArgument(location != null, "location cannot be null");
      if (location.getWorld() == this.getWorld()) {
         this.getHandle().storeExploredPosition(CraftLocation.toBlockPosition(location));
      }
   }

   public Sniffer.State getState() {
      return this.stateToBukkit(this.getHandle().getState());
   }

   public void setState(Sniffer.State state) {
      Preconditions.checkArgument(state != null, "state cannot be null");
      this.getHandle().transitionTo(this.stateToNMS(state));
   }

   public Location findPossibleDigLocation() {
      return (Location)this.getHandle().calculateDigPosition().map((blockPosition) -> {
         return CraftLocation.toBukkit(blockPosition, this.getLocation().getWorld());
      }).orElse((Location) null);
   }

   public boolean canDig() {
      return this.getHandle().canDig();
   }

   private net.minecraft.world.entity.animal.sniffer.Sniffer.State stateToNMS(Sniffer.State state) {
      net.minecraft.world.entity.animal.sniffer.Sniffer.State var10000;
      return switch (state) {
         case IDLING -> net.minecraft.world.entity.animal.sniffer.Sniffer.State.IDLING;
         case FEELING_HAPPY -> net.minecraft.world.entity.animal.sniffer.Sniffer.State.FEELING_HAPPY;
         case SCENTING -> net.minecraft.world.entity.animal.sniffer.Sniffer.State.SCENTING;
         case SNIFFING -> net.minecraft.world.entity.animal.sniffer.Sniffer.State.SNIFFING;
         case SEARCHING -> net.minecraft.world.entity.animal.sniffer.Sniffer.State.SEARCHING;
         case DIGGING -> net.minecraft.world.entity.animal.sniffer.Sniffer.State.DIGGING;
         case RISING -> net.minecraft.world.entity.animal.sniffer.Sniffer.State.RISING;
      };
   }

   private Sniffer.State stateToBukkit(net.minecraft.world.entity.animal.sniffer.Sniffer.State state) {
      Sniffer.State var10000;
      switch (state) {
         case IDLING -> var10000 = org.bukkit.entity.Sniffer.State.IDLING;
         case FEELING_HAPPY -> var10000 = org.bukkit.entity.Sniffer.State.FEELING_HAPPY;
         case SCENTING -> var10000 = org.bukkit.entity.Sniffer.State.SCENTING;
         case SNIFFING -> var10000 = org.bukkit.entity.Sniffer.State.SNIFFING;
         case SEARCHING -> var10000 = org.bukkit.entity.Sniffer.State.SEARCHING;
         case DIGGING -> var10000 = org.bukkit.entity.Sniffer.State.DIGGING;
         case RISING -> var10000 = org.bukkit.entity.Sniffer.State.RISING;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
