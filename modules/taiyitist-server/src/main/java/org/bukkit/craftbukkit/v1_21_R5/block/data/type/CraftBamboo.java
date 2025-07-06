package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import org.bukkit.block.data.type.Bamboo;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftBamboo extends CraftBlockData implements Bamboo {
   private static final CraftBlockStateEnum<?, Bamboo.Leaves> LEAVES = getEnum("leaves", Bamboo.Leaves.class);

   public Bamboo.Leaves getLeaves() {
      return (Bamboo.Leaves)this.get(LEAVES);
   }

   public void setLeaves(Bamboo.Leaves leaves) {
      this.set(LEAVES, leaves);
   }
}
