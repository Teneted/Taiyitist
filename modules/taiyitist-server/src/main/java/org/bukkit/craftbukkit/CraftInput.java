package org.bukkit.craftbukkit;

import java.util.Objects;
import org.bukkit.Input;

public class CraftInput implements Input {
   private final net.minecraft.world.entity.player.Input handle;

   public CraftInput(net.minecraft.world.entity.player.Input handle) {
      this.handle = handle;
   }

   public boolean isForward() {
      return this.handle.forward();
   }

   public boolean isBackward() {
      return this.handle.backward();
   }

   public boolean isLeft() {
      return this.handle.left();
   }

   public boolean isRight() {
      return this.handle.right();
   }

   public boolean isJump() {
      return this.handle.jump();
   }

   public boolean isSneak() {
      return this.handle.shift();
   }

   public boolean isSprint() {
      return this.handle.sprint();
   }

   public int hashCode() {
      int hash = 7;
      hash = 89 * hash + Objects.hashCode(this.handle);
      return hash;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftInput other = (CraftInput)obj;
         return Objects.equals(this.handle, other.handle);
      }
   }

   public String toString() {
      return "CraftInput{" + String.valueOf(this.handle) + "}";
   }
}
