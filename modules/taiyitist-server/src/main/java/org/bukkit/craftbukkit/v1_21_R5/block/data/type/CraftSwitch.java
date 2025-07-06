package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import org.bukkit.block.data.type.Switch;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftSwitch extends CraftBlockData implements Switch {
   private static final CraftBlockStateEnum<?, Switch.Face> FACE = getEnum("face", Switch.Face.class);

   public Switch.Face getFace() {
      return (Switch.Face)this.get(FACE);
   }

   public void setFace(Switch.Face face) {
      this.set(FACE, face);
   }
}
