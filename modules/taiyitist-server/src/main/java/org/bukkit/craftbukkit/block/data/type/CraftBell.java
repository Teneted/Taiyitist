package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.Bell;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftBell extends CraftBlockData implements Bell {
   private static final CraftBlockStateEnum<?, Bell.Attachment> ATTACHMENT = getEnum("attachment", Bell.Attachment.class);

   public Bell.Attachment getAttachment() {
      return (Bell.Attachment)this.get(ATTACHMENT);
   }

   public void setAttachment(Bell.Attachment leaves) {
      this.set(ATTACHMENT, leaves);
   }
}
