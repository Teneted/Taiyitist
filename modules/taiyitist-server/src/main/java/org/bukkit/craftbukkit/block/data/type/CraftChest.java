package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.Chest;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftChest extends CraftBlockData implements Chest {
   private static final CraftBlockStateEnum<?, Chest.Type> TYPE = getEnum("type", Chest.Type.class);

   public Chest.Type getType() {
      return (Chest.Type)this.get(TYPE);
   }

   public void setType(Chest.Type type) {
      this.set(TYPE, type);
   }
}
