package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftTechnicalPiston extends CraftBlockData implements TechnicalPiston {
   private static final CraftBlockStateEnum<?, TechnicalPiston.Type> TYPE = getEnum("type", TechnicalPiston.Type.class);

   public TechnicalPiston.Type getType() {
      return (TechnicalPiston.Type)this.get(TYPE);
   }

   public void setType(TechnicalPiston.Type type) {
      this.set(TYPE, type);
   }
}
