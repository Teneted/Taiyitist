package org.teneted.taiyitist.bukkit;

import net.minecraft.server.WorldLoader;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class BukkitSnapshotCaptures {

    private static AtomicReference<WorldLoader.DataLoadContext> dataLoadContext = new AtomicReference<>();

    public static void captureDataLoadContext(WorldLoader.DataLoadContext context) {
        dataLoadContext.set(context);
    }

    public static WorldLoader.DataLoadContext getDataLoadContext() {
        try {
            return Objects.requireNonNull(dataLoadContext.get(), "dataLoadContext");
        } finally {
            dataLoadContext.set(null);
        }
    }

}
