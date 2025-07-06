package org.bukkit.craftbukkit.util;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;

public class LimitedClassRemapper extends ClassRemapper {
   public LimitedClassRemapper(ClassVisitor classVisitor, Remapper remapper) {
      super(classVisitor, remapper);
   }

   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      this.className = name;
      this.cv.visit(version, access, this.remapper.mapType(name), this.remapper.mapSignature(signature, false), superName, interfaces);
   }

   protected MethodVisitor createMethodRemapper(MethodVisitor methodVisitor) {
      return new LimitedMethodRemapper(this, this.api, methodVisitor, this.remapper);
   }

   private class LimitedMethodRemapper extends MethodRemapper {
      protected LimitedMethodRemapper(final LimitedClassRemapper var1, int api, MethodVisitor methodVisitor, Remapper remapper) {
         super(api, methodVisitor, remapper);
      }

      public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
         if (owner != null && owner.equals("java/lang/Enum") && name != null && name.equals("<init>")) {
            this.mv.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
         } else {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
         }
      }
   }
}
