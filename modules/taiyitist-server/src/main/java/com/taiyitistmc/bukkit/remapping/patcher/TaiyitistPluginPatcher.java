package com.taiyitistmc.bukkit.remapping.patcher;

import com.taiyitistmc.TaiyitistMCStart;
import com.taiyitistmc.bukkit.remapping.TaiyitistRemapConfig;
import com.taiyitistmc.bukkit.remapping.ClassLoaderRemapper;
import com.taiyitistmc.bukkit.remapping.GlobalClassRepo;
import com.taiyitistmc.bukkit.remapping.PluginTransformer;
import com.taiyitistmc.bukkit.remapping.patcher.fix.IntegratedPatcher;
import org.bukkit.configuration.file.YamlConfiguration;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TaiyitistPluginPatcher implements PluginTransformer {

    private final List<PluginPatcher> list;

    public TaiyitistPluginPatcher(List<PluginPatcher> list) {
        this.list = list;
    }

    @Override
    public void handleClass(ClassNode node, ClassLoaderRemapper remapper, TaiyitistRemapConfig config) {
        for (PluginPatcher patcher : list) {
            patcher.handleClass(node, GlobalClassRepo.INSTANCE);
        }
    }

    public static List<PluginPatcher> load(List<PluginTransformer> transformerList) {
        var list = new ArrayList<PluginPatcher>();
        File pluginFolder = new File("plugins");
        if (pluginFolder.exists()) {
            TaiyitistMCStart.LOGGER.info("patcher.loading");
            File[] files = pluginFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        loadFromJar(file).ifPresent(list::add);
                    }
                }
                if (!list.isEmpty()) {
                    TaiyitistMCStart.I18N.as("patcher.loaded", list.size());
                }
            }
        }
        list.add(new IntegratedPatcher());
        list.sort(Comparator.comparing(PluginPatcher::priority));
        transformerList.add(new TaiyitistPluginPatcher(list));
        return list;
    }

    private static Optional<PluginPatcher> loadFromJar(File file) {
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry jarEntry = jarFile.getJarEntry("plugin.yml");
            if (jarEntry != null) {
                try (InputStream stream = jarFile.getInputStream(jarEntry)) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
                    String name = configuration.getString("taiyitist.patcher");
                    if (name != null) {
                        URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, TaiyitistPluginPatcher.class.getClassLoader());
                        Class<?> clazz = Class.forName(name, false, loader);
                        PluginPatcher patcher = clazz.asSubclass(PluginPatcher.class).getConstructor().newInstance();
                        return Optional.of(patcher);
                    }
                }
            }
        } catch (Throwable e) {
        }
        return Optional.empty();
    }
}