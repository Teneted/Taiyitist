package com.taiyitistmc.bukkit.remapping.patcher.fix;

import com.taiyitistmc.bukkit.remapping.patcher.PluginPatcher;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class IntegratedPatcher implements PluginPatcher {

    private static final Map<String, BiConsumer<ClassNode, ClassRepo>> SPECIFIC = new HashMap<>() {};
    private static final List<BiConsumer<ClassNode, ClassRepo>> GENERAL = new ArrayList<>();

    static {
        // Handle WorldEdit reflective using NMS
        // Their naming mapping is behind the version, syncing manually
        SPECIFIC.put("com/sk89q/worldedit/bukkit/adapter/impl/v1_20_R1/PaperweightAdapter$SpigotWatchdog", WorldEdit::handleWatchdog);
        SPECIFIC.put("com/sk89q/worldedit/bukkit/adapter/Refraction", WorldEdit::handlePickName);
        SPECIFIC.put("com/sk89q/worldedit/bukkit/BukkitAdapter", WorldEdit::handleBukkitAdapter);
        SPECIFIC.put("com/earth2me/essentials/utils/VersionUtil", (classNode, classRepo) -> helloWorld(classNode, 110, 109));
        SPECIFIC.put("net/ess3/nms/refl/providers/ReflServerStateProvider", (classNode, classRepo) -> helloWorld(classNode,"u", "U"));
        SPECIFIC.put("net/Zrips/CMILib/Reflections", (classNode, classRepo) -> helloWorld(classNode,"bR", "field_7512"));
        SPECIFIC.put("io/lumine/mythic/core/volatilecode/v1_20_R1/VolatileEntityHandlerImpl", (classNode, classRepo) -> helloWorld(classNode,"c", "d")); // mythicmobs-5.8
        SPECIFIC.put("com/comphenix/protocol/ProtocolConfig", ProtocolLib::removeProtocolASM);
        GENERAL.add(PaperLib::removePaper);
    }

    @Override
    public String version() {
        String implVersion = this.getClass().getPackage().getImplementationVersion();
        StringBuilder sb = new StringBuilder();
        if (implVersion != null) {
            sb.append("version=").append(implVersion);
        }
        sb.append(" patchers=[");
        sb.append("WorldEdit 1.20.1");
        sb.append("]");
        return sb.toString();
    }

    @Override
    public void handleClass(ClassNode node, ClassRepo classRepo) {
        BiConsumer<ClassNode, ClassRepo> consumer = SPECIFIC.get(node.name);
        if (consumer != null) {
            consumer.accept(node, classRepo);
        } else {
            for (BiConsumer<ClassNode, ClassRepo> general : GENERAL) {
                general.accept(node, classRepo);
            }
        }
    }

    private static void helloWorld(ClassNode node, int a, int b) {
        node.methods.forEach(method -> {
            for (AbstractInsnNode next : method.instructions) {
                if (next instanceof IntInsnNode ldcInsnNode) {
                    if (ldcInsnNode.operand == a) {
                        ldcInsnNode.operand = b;
                    }
                }
            }
        });
    }

    private static void helloWorld(ClassNode node, String a, String b) {
        node.methods.forEach(method -> {
            for (AbstractInsnNode next : method.instructions) {
                if (next instanceof LdcInsnNode ldcInsnNode) {
                    if (ldcInsnNode.cst instanceof String str) {
                        if (a.equals(str)) {
                            ldcInsnNode.cst = b;
                        }
                    }
                }
            }
        });
    }
}
