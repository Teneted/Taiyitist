package org.teneted.taiyitist.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.teneted.taiyitist.asm.annotation.InvokeSpecial;

public class InvokeSpecialProcessor implements MixinProcessor {

    private static final String TYPE = Type.getDescriptor(InvokeSpecial.class);

    @Override
    public void accept(String className, ClassNode classNode, IMixinInfo mixinInfo) {
        for (MethodNode method : classNode.methods) {
            if (method.invisibleAnnotations == null) {
                continue;
            }
            for (AnnotationNode annotation : method.invisibleAnnotations) {
                if (annotation.desc.equals(TYPE)) {
                    validateAndTransform(method);
                    break;
                }
            }
        }
    }

    public void validateAndTransform(MethodNode method) {
        MethodInsnNode invoke = null;
        for (AbstractInsnNode insn: method.instructions) {
            if (insn instanceof MethodInsnNode invocation) {
                if (invoke != null) {
                    throw new IllegalArgumentException("Invalid invoke special invoker: too many invocations");
                }
                invoke = invocation;
            }
        }

        if (invoke == null) {
            throw new IllegalArgumentException("Invalid invoke special invoker: no invocation");
        }

        if (invoke.getOpcode() == Opcodes.INVOKESTATIC) {
            throw new IllegalArgumentException("Invalid invoke special invoker: cannot INVOKESPECIAL on static methods");
        }

        invoke.setOpcode(Opcodes.INVOKESPECIAL);
    }
}
