package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Jukebox;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftJukebox extends CraftBlockData implements Jukebox {
   private static final BooleanProperty HAS_RECORD = getBoolean("has_record");

   public boolean hasRecord() {
      return (Boolean)this.get(HAS_RECORD);
   }
}
