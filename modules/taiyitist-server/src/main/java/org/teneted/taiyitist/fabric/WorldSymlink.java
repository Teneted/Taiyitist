package org.teneted.taiyitist.fabric;

import org.teneted.taiyitist.TaiyitistMod;
import org.teneted.taiyitist.util.I18n;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.world.level.storage.DerivedLevelData;
import org.bukkit.Bukkit;

public class WorldSymlink {

    public static void create(DerivedLevelData worldInfo, File dimensionFolder) {
        String name = worldInfo.getLevelName();
        Path source = new File(Bukkit.getWorldContainer(), name).toPath();
        Path dest = dimensionFolder.toPath();
        try {
            if (!Files.isSymbolicLink(source)) {
                if (Files.exists(source)) {
                    TaiyitistMod.LOGGER.warn(I18n.as("symlink-file-exist"), source);
                    return;
                }
                Files.createSymbolicLink(source, dest);
            }
        } catch (Exception ignored) {
        }
    }
}