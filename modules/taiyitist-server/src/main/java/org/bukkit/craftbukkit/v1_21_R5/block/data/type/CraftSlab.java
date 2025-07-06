package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftSlab extends CraftBlockData implements Slab {
   private static final CraftBlockStateEnum<?, Slab.Type> TYPE = getEnum("type", Slab.Type.class);

   public Slab.Type getType() {
      return (Slab.Type)this.get(TYPE);
   }

   public void setType(Slab.Type type) {
      this.set(TYPE, type);
   }
}
