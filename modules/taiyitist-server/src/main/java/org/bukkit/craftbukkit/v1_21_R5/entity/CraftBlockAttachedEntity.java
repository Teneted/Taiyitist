package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;

public class CraftBlockAttachedEntity extends CraftEntity {
   public CraftBlockAttachedEntity(CraftServer server, BlockAttachedEntity entity) {
      super(server, entity);
   }

   public BlockAttachedEntity getHandle() {
      return (BlockAttachedEntity)this.entity;
   }

   public String toString() {
      return "CraftBlockAttachedEntity";
   }
}
