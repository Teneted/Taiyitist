package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.CreakingHeart;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftCreakingHeart extends CraftBlockData implements CreakingHeart {
   private static final CraftBlockStateEnum<?, CreakingHeart.State> CREAKING_HEART_STATE = getEnum("creaking_heart_state", CreakingHeart.State.class);
   private static final BooleanProperty NATURAL = getBoolean("natural");

   public boolean isActive() {
      return this.getCreakingHeartState() == State.AWAKE;
   }

   public void setActive(boolean active) {
      this.setCreakingHeartState(State.AWAKE);
   }

   public boolean isNatural() {
      return (Boolean)this.get(NATURAL);
   }

   public void setNatural(boolean natural) {
      this.set(NATURAL, natural);
   }

   public CreakingHeart.State getCreakingHeartState() {
      return (CreakingHeart.State)this.get(CREAKING_HEART_STATE);
   }

   public void setCreakingHeartState(CreakingHeart.State state) {
      this.set(CREAKING_HEART_STATE, state);
   }
}
