package org.bukkit.craftbukkit.block.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftTripwire extends CraftBlockData implements Tripwire, Attachable, MultipleFacing, Powerable {
   private static final BooleanProperty DISARMED = getBoolean(TripWireBlock.class, "disarmed");
   private static final BooleanProperty ATTACHED = getBoolean(TripWireBlock.class, "attached");
   private static final BooleanProperty[] FACES = new BooleanProperty[]{getBoolean(TripWireBlock.class, "north", true), getBoolean(TripWireBlock.class, "east", true), getBoolean(TripWireBlock.class, "south", true), getBoolean(TripWireBlock.class, "west", true), getBoolean(TripWireBlock.class, "up", true), getBoolean(TripWireBlock.class, "down", true)};
   private static final BooleanProperty POWERED = getBoolean(TripWireBlock.class, "powered");

   public CraftTripwire() {
   }

   public CraftTripwire(BlockState state) {
      super(state);
   }

   public boolean isDisarmed() {
      return (Boolean)this.get(DISARMED);
   }

   public void setDisarmed(boolean disarmed) {
      this.set(DISARMED, disarmed);
   }

   public boolean isAttached() {
      return (Boolean)this.get(ATTACHED);
   }

   public void setAttached(boolean attached) {
      this.set(ATTACHED, attached);
   }

   public boolean hasFace(BlockFace face) {
      BooleanProperty state = FACES[face.ordinal()];
      if (state == null) {
         throw new IllegalArgumentException("Non-allowed face " + String.valueOf(face) + ". Check MultipleFacing.getAllowedFaces.");
      } else {
         return (Boolean)this.get(state);
      }
   }

   public void setFace(BlockFace face, boolean has) {
      BooleanProperty state = FACES[face.ordinal()];
      if (state == null) {
         throw new IllegalArgumentException("Non-allowed face " + String.valueOf(face) + ". Check MultipleFacing.getAllowedFaces.");
      } else {
         this.set(state, has);
      }
   }

   public Set<BlockFace> getFaces() {
      ImmutableSet.Builder<BlockFace> faces = ImmutableSet.builder();

      for(int i = 0; i < FACES.length; ++i) {
         if (FACES[i] != null && (Boolean)this.get(FACES[i])) {
            faces.add(BlockFace.values()[i]);
         }
      }

      return faces.build();
   }

   public Set<BlockFace> getAllowedFaces() {
      ImmutableSet.Builder<BlockFace> faces = ImmutableSet.builder();

      for(int i = 0; i < FACES.length; ++i) {
         if (FACES[i] != null) {
            faces.add(BlockFace.values()[i]);
         }
      }

      return faces.build();
   }

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }
}
