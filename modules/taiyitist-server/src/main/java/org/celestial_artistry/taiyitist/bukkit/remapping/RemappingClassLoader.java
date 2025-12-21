package org.celestial_artistry.taiyitist.bukkit.remapping;

public interface RemappingClassLoader {

    ClassLoaderRemapper getRemapper();

    TaiyitistRemapConfig getRemapConfig();
}