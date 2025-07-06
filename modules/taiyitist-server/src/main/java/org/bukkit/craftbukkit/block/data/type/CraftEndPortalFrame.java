package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftEndPortalFrame extends CraftBlockData implements EndPortalFrame {
   private static final BooleanProperty EYE = getBoolean("eye");

   public boolean hasEye() {
      return (Boolean)this.get(EYE);
   }

   public void setEye(boolean eye) {
      this.set(EYE, eye);
   }
}
