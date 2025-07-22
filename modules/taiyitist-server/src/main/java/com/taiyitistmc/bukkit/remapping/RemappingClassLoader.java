package com.taiyitistmc.bukkit.remapping;

public interface RemappingClassLoader {

    ClassLoaderRemapper getRemapper();

    TaiyitistRemapConfig getRemapConfig();
}