package org.teneted.taiyitist.eventhandler;

import org.teneted.taiyitist.TaiyitistMod;
import org.teneted.taiyitist.eventhandler.dispatcher.EntityEventDispatcher;
import org.teneted.taiyitist.eventhandler.dispatcher.LevelEventDispatcher;
import org.teneted.taiyitist.eventhandler.dispatcher.PlayerEventDispatcher;
import org.teneted.taiyitist.util.I18n;

public class BannerEventDispatcherRegistry {

    public static void registerEventDispatchers() {
        TaiyitistMod.LOGGER.info(I18n.as("banner.event_handler.register"));
        LevelEventDispatcher.dispatchLevel();
        PlayerEventDispatcher.dispatcherPlayer();
        EntityEventDispatcher.dispatchEntityEvent();
        // FabricToBukkitEventDispatcher.dispatchFabric2Bukkit();
    }
}
