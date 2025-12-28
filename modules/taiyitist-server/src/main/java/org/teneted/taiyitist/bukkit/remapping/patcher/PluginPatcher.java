package org.teneted.taiyitist.bukkit.remapping.patcher;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;

/**
 * Service Provider Interface of plugin patcher.
 * <p>
 * Patchers will be loaded from plugins folder, where in plugin.yml:
 *
 * <pre>
 *   patcher: path.to.PatcherClass
 * </pre>
 * <p>
 * The patcher class shall implement PluginTransformer interface.
 * <p>
 * The patcher shall be loaded before main class initialized, under an independent classloader that has access to
 * Minecraft and other classes.
 * <p>
 * Patchers won't get reloaded or unloaded even if its plugin unload.
 */
public interface PluginPatcher {

    void handleClass(ClassNode node, ClassRepo classRepo);

    /**
     * Priority of this patcher instance. Lower priority runs first.
     */
    default int priority() {
        return 0;
    }

    /**
     * Returns the version of plugin patcher.
     * <p>
     * Changes to this version string and/or main class name could cause an invalidation to plugin class cache.
     *
     * @return the patcher version. Defaults to "unknown".
     */
    default String version() {
        String implVersion = getClass().getPackage().getImplementationVersion();
        return implVersion == null ? "unknown" : implVersion;
    }

    interface ClassRepo {

        /**
         * Find class in plugin jars without running transformer
         *
         * @param internalName   internal form of class name
         * @param parsingOptions {@link org.objectweb.asm.ClassReader#accept(ClassVisitor, int)}
         * @return class node or null if nothing is found
         */
        ClassNode findClass(String internalName, int parsingOptions);
    }
}