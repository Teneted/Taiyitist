package org.teneted.taiyitist.bukkit.remapping;

public interface RemappingClassLoader {

    ClassLoaderRemapper getRemapper();

    TaiyitistRemapConfig getRemapConfig();
}