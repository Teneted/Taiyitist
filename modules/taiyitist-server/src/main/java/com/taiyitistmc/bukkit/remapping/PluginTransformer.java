package com.taiyitistmc.bukkit.remapping;

import org.objectweb.asm.tree.ClassNode;

public interface PluginTransformer {

    void handleClass(ClassNode node, ClassLoaderRemapper remapper, TaiyitistRemapConfig config);

    default int priority() {
        return 0;
    }
}