package org.bukkit.craftbukkit.entity;

import java.util.stream.Collectors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import org.bukkit.TreeSpecies;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;

public abstract class CraftBoat extends CraftVehicle implements Boat {
   public CraftBoat(CraftServer server, AbstractBoat entity) {
      super(server, entity);
   }

   public TreeSpecies getWoodType() {
      return getTreeSpecies(this.getHandle().getType());
   }

   public void setWoodType(TreeSpecies species) {
      throw new UnsupportedOperationException("Not supported - you must spawn a new entity to change boat type.");
   }

   public Boat.Type getBoatType() {
      return boatTypeFromNms(this.getHandle().getType());
   }

   public void setBoatType(Boat.Type type) {
      throw new UnsupportedOperationException("Not supported - you must spawn a new entity to change boat type.");
   }

   public double getMaxSpeed() {
      return this.getHandle().maxSpeed;
   }

   public void setMaxSpeed(double speed) {
      if (speed >= 0.0) {
         this.getHandle().maxSpeed = speed;
      }

   }

   public double getOccupiedDeceleration() {
      return this.getHandle().occupiedDeceleration;
   }

   public void setOccupiedDeceleration(double speed) {
      if (speed >= 0.0) {
         this.getHandle().occupiedDeceleration = speed;
      }

   }

   public double getUnoccupiedDeceleration() {
      return this.getHandle().unoccupiedDeceleration;
   }

   public void setUnoccupiedDeceleration(double speed) {
      this.getHandle().unoccupiedDeceleration = speed;
   }

   public boolean getWorkOnLand() {
      return this.getHandle().landBoats;
   }

   public void setWorkOnLand(boolean workOnLand) {
      this.getHandle().landBoats = workOnLand;
   }

   public Boat.Status getStatus() {
      return boatStatusFromNms(this.getHandle().status);
   }

   public AbstractBoat getHandle() {
      return (AbstractBoat)this.entity;
   }

   public String toString() {
      String var10000 = String.valueOf(this.getBoatType());
      return "CraftBoat{boatType=" + var10000 + ",status=" + String.valueOf(this.getStatus()) + ",passengers=" + (String)this.getPassengers().stream().map(Entity::toString).collect(Collectors.joining("-", "{", "}")) + "}";
   }

   public static Boat.Type boatTypeFromNms(EntityType<?> boatType) {
      if (boatType != EntityType.OAK_BOAT && boatType != EntityType.OAK_CHEST_BOAT) {
         if (boatType != EntityType.BIRCH_BOAT && boatType != EntityType.BIRCH_CHEST_BOAT) {
            if (boatType != EntityType.ACACIA_BOAT && boatType != EntityType.ACACIA_CHEST_BOAT) {
               if (boatType != EntityType.CHERRY_BOAT && boatType != EntityType.CHERRY_CHEST_BOAT) {
                  if (boatType != EntityType.JUNGLE_BOAT && boatType != EntityType.JUNGLE_CHEST_BOAT) {
                     if (boatType != EntityType.SPRUCE_BOAT && boatType != EntityType.SPRUCE_CHEST_BOAT) {
                        if (boatType != EntityType.DARK_OAK_BOAT && boatType != EntityType.DARK_OAK_CHEST_BOAT) {
                           if (boatType != EntityType.MANGROVE_BOAT && boatType != EntityType.MANGROVE_CHEST_BOAT) {
                              if (boatType != EntityType.BAMBOO_RAFT && boatType != EntityType.BAMBOO_CHEST_RAFT) {
                                 throw new EnumConstantNotPresentException(Boat.Type.class, boatType.toString());
                              } else {
                                 return Type.BAMBOO;
                              }
                           } else {
                              return Type.MANGROVE;
                           }
                        } else {
                           return Type.DARK_OAK;
                        }
                     } else {
                        return Type.SPRUCE;
                     }
                  } else {
                     return Type.JUNGLE;
                  }
               } else {
                  return Type.CHERRY;
               }
            } else {
               return Type.ACACIA;
            }
         } else {
            return Type.BIRCH;
         }
      } else {
         return Type.OAK;
      }
   }

   public static Boat.Status boatStatusFromNms(AbstractBoat.Status enumStatus) {
      Boat.Status var10000;
      switch (enumStatus) {
         case IN_AIR -> var10000 = Status.IN_AIR;
         case ON_LAND -> var10000 = Status.ON_LAND;
         case UNDER_WATER -> var10000 = Status.UNDER_WATER;
         case UNDER_FLOWING_WATER -> var10000 = Status.UNDER_FLOWING_WATER;
         case IN_WATER -> var10000 = Status.IN_WATER;
         default -> throw new EnumConstantNotPresentException(Boat.Status.class, enumStatus.name());
      }

      return var10000;
   }

   /** @deprecated */
   @Deprecated
   public static TreeSpecies getTreeSpecies(EntityType<?> boatType) {
      if (boatType != EntityType.SPRUCE_BOAT && boatType != EntityType.SPRUCE_CHEST_BOAT) {
         if (boatType != EntityType.BIRCH_BOAT && boatType != EntityType.BIRCH_CHEST_BOAT) {
            if (boatType != EntityType.JUNGLE_BOAT && boatType != EntityType.JUNGLE_CHEST_BOAT) {
               if (boatType != EntityType.ACACIA_BOAT && boatType != EntityType.ACACIA_CHEST_BOAT) {
                  return boatType != EntityType.DARK_OAK_BOAT && boatType != EntityType.DARK_OAK_CHEST_BOAT ? TreeSpecies.GENERIC : TreeSpecies.DARK_OAK;
               } else {
                  return TreeSpecies.ACACIA;
               }
            } else {
               return TreeSpecies.JUNGLE;
            }
         } else {
            return TreeSpecies.BIRCH;
         }
      } else {
         return TreeSpecies.REDWOOD;
      }
   }
}
