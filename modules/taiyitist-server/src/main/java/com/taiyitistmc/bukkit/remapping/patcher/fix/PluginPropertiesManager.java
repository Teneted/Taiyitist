package com.taiyitistmc.bukkit.remapping.patcher.fix;

public class PluginPropertiesManager {

    public static void injectPluginProperties(String mainClass) {
        if (mainClass.equals("com.sk89q.worldedit.bukkit.WorldEditPlugin")) {
            System.setProperty("worldedit.bukkit.adapter", "com.sk89q.worldedit.bukkit.adapter.impl.v1_20_R1.PaperweightAdapter");
        }
        if (mainClass.contains("FastAsyncWorldEdit")) {
            System.setProperty("worldedit.bukkit.adapter", "com.sk89q.worldedit.bukkit.adapter.impl.fawe.v1_20_R1.PaperweightFaweAdapter");
        }
    }
}
