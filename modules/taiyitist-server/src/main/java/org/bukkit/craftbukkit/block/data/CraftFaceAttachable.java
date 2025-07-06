package org.bukkit.craftbukkit.block.data;

import org.bukkit.block.data.FaceAttachable;

public abstract class CraftFaceAttachable extends CraftBlockData implements FaceAttachable {
   private static final CraftBlockStateEnum<?, FaceAttachable.AttachedFace> ATTACH_FACE = getEnum("face", FaceAttachable.AttachedFace.class);

   public FaceAttachable.AttachedFace getAttachedFace() {
      return (FaceAttachable.AttachedFace)this.get(ATTACH_FACE);
   }

   public void setAttachedFace(FaceAttachable.AttachedFace face) {
      this.set(ATTACH_FACE, face);
   }
}
