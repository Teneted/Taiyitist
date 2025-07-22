package com.taiyitistmc.bukkit.remapping.patcher.fix;

import com.taiyitistmc.bukkit.remapping.patcher.PluginPatcher;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Objects;

public class ProtocolLib {

    public static void removeProtocolASM(ClassNode node, PluginPatcher.ClassRepo repo) {
        for (MethodNode method : node.methods) {
            if (Objects.equals(method.name, "isBackgroundCompilerEnabled") && Objects.equals(method.desc, "()Z")) {
                method.instructions.clear();
                method.instructions.add(new InsnNode(Opcodes.ICONST_0));
                method.instructions.add(new InsnNode(Opcodes.IRETURN));
            }
        }

        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
    }
}
