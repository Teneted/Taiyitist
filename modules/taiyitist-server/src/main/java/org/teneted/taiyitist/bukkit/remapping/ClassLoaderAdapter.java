package org.teneted.taiyitist.bukkit.remapping;

import com.google.common.collect.ImmutableMap;
import org.teneted.taiyitist.TaiyitistMCStart;
import org.teneted.taiyitist.bukkit.remapping.generated.RemappingURLClassLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Map;

public class ClassLoaderAdapter implements PluginTransformer {

    public static final ClassLoaderAdapter INSTANCE = new ClassLoaderAdapter();
    private static final Marker MARKER = MarkerManager.getMarker("CLADAPTER");
    private static final String CLASSLOADER = "java/lang/ClassLoader";

    private final Map<String, String> classLoaderTypes = ImmutableMap.<String, String>builder()
            .put(Type.getInternalName(URLClassLoader.class), Type.getInternalName(RemappingURLClassLoader.class))
            .build();

    @Override
    public void handleClass(ClassNode node, ClassLoaderRemapper remapper, TaiyitistRemapConfig config) {
        // Must handle all class loaders, or else we'll lose track of classloading
        for (MethodNode methodNode : node.methods) {
            for (AbstractInsnNode insnNode : methodNode.instructions) {
                if (insnNode.getOpcode() == Opcodes.NEW) {
                    TypeInsnNode typeInsnNode = (TypeInsnNode) insnNode;
                    String replace = classLoaderTypes.get(typeInsnNode.desc);
                    if (replace != null) {
                        AbstractInsnNode next = typeInsnNode.getNext();
                        while (next != null && (next.getOpcode() != Opcodes.INVOKESPECIAL || !((MethodInsnNode) next).name.equals("<init>") || !((MethodInsnNode) next).owner.equals(typeInsnNode.desc))) {
                            next = next.getNext();
                        }
                        if (next == null) continue;
                        TaiyitistMCStart.LOGGER.debug(MARKER, "Found new {}/{} call in {} {}", typeInsnNode.desc, ((MethodInsnNode) next).name + ((MethodInsnNode) next).desc, node.name, methodNode.name + methodNode.desc);
                        ((MethodInsnNode) next).owner = replace;
                        typeInsnNode.desc = replace;
                    }
                }
            }
        }
        ClassInfo info = classInfo(node);
        if (info == null) return;
        TaiyitistMCStart.LOGGER.debug(MARKER, "Transforming classloader class {}", node.name);
        if (config.remap() && !info.remapping) {
            implementIntf(node);
        }
        for (MethodNode methodNode : node.methods) {
            for (AbstractInsnNode insnNode : methodNode.instructions) {
                if (insnNode instanceof MethodInsnNode methodInsnNode) {
                    if (methodInsnNode.getOpcode() == Opcodes.INVOKESPECIAL && methodNode.name.equals("<init>") && methodInsnNode.name.equals("<init>") && methodInsnNode.owner.equals(node.superName)) {
                        methodInsnNode.owner = info.superName;
                    }
                }
            }
        }
        node.superName = info.superName;
    }

    private void implementIntf(ClassNode node) {
        TaiyitistMCStart.LOGGER.debug(MARKER, "Implementing RemappingClassLoader for class {}", node.name);
        FieldNode remapper = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC, "remapper", Type.getDescriptor(ClassLoaderRemapper.class), null, null);
        MethodNode getRemapper = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, "getRemapper", Type.getMethodDescriptor(Type.getType(ClassLoaderRemapper.class)), null, null);
        {
            InsnList list = new InsnList();
            LabelNode labelNode = new LabelNode();
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, remapper.name, remapper.desc));
            list.add(new JumpInsnNode(Opcodes.IFNONNULL, labelNode));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new InsnNode(Opcodes.DUP));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(TaiyitistRemapper.class), "createClassLoaderRemapper", Type.getMethodDescriptor(Type.getType(ClassLoaderRemapper.class), Type.getType(ClassLoader.class)), false));
            list.add(new FieldInsnNode(Opcodes.PUTFIELD, node.name, remapper.name, remapper.desc));
            list.add(labelNode);
            if ((node.version & 0xFFFF) >= Opcodes.V1_6) {
                list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
            }
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, remapper.name, remapper.desc));
            list.add(new InsnNode(Opcodes.ARETURN));
            getRemapper.instructions = list;
        }
        FieldNode remapConfig = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC, "taiyitist$remapConfig", Type.getDescriptor(TaiyitistRemapConfig.class), null, null);
        MethodNode getConfig = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, "getRemapConfig", Type.getMethodDescriptor(Type.getType(TaiyitistRemapConfig.class)), null, null);
        {
            final var config = Type.getInternalName(TaiyitistRemapConfig.class);
            final var remapping = Type.getInternalName(ClassLoaderRemapping.class);

            InsnList list = new InsnList();
            LabelNode getfield = new LabelNode();
            LabelNode putfield = new LabelNode();
            LabelNode aret = new LabelNode();

            list.add(getfield);
            list.add(new LineNumberNode(-101, getfield));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, remapConfig.name, remapConfig.desc));
            list.add(new JumpInsnNode(Opcodes.IFNONNULL, aret));

            list.add(putfield);
            list.add(new LineNumberNode(-103, putfield));
            list.add(new TypeInsnNode(Opcodes.NEW, config));
            list.add(new InsnNode(Opcodes.DUP));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, remapping, "canRemap", Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(ClassLoader.class)), false));
            list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, config, "<init>", "(Z)V"));
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new InsnNode(Opcodes.SWAP));
            list.add(new FieldInsnNode(Opcodes.PUTFIELD, node.name, remapConfig.name, remapConfig.desc));

            list.add(aret);
            list.add(new LineNumberNode(-105, aret));
            if ((node.version & 0xFFFF) >= Opcodes.V1_6) {
                list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
            }
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, remapConfig.name, remapConfig.desc));
            list.add(new InsnNode(Opcodes.ARETURN));
            getConfig.instructions = list;
            getConfig.visitMaxs(3, 0);
        }
        node.fields.add(remapper);
        node.fields.add(remapConfig);
        node.methods.add(getRemapper);
        //RemappingClassLoader.implementNeedRemap(node);
        node.methods.add(getConfig);
        node.interfaces.add(Type.getInternalName(RemappingClassLoader.class));
    }

    private ClassInfo classInfo(ClassNode node) {
        ClassInfo info = new ClassInfo();
        Collection<String> parents = GlobalClassRepo.inheritanceProvider().getAll(node.superName);
        if (!parents.contains(CLASSLOADER)) return null;
        for (String s : classLoaderTypes.keySet()) {
            if (parents.contains(s)) {
                info.remapping = true;
                break;
            }
        }
        String s = classLoaderTypes.get(node.superName);
        if (s != null) {
            info.superName = s;
        } else {
            info.superName = node.superName;
        }
        return info;
    }

    private static class ClassInfo {

        private String superName;
        private boolean remapping;
    }
}