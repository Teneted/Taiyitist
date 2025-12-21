package org.celestial_artistry.taiyitist.eventhandler;

import org.celestial_artistry.taiyitist.TaiyitistMod;
import org.celestial_artistry.taiyitist.eventhandler.dispatcher.EntityEventDispatcher;
import org.celestial_artistry.taiyitist.eventhandler.dispatcher.LevelEventDispatcher;
import org.celestial_artistry.taiyitist.eventhandler.dispatcher.PlayerEventDispatcher;
import org.celestial_artistry.taiyitist.util.I18n;

public class BannerEventDispatcherRegistry {

    public static void registerEventDispatchers() {
        TaiyitistMod.LOGGER.info(I18n.as("banner.event_handler.register"));
        LevelEventDispatcher.dispatchLevel();
        PlayerEventDispatcher.dispatcherPlayer();
        EntityEventDispatcher.dispatchEntityEvent();
        // FabricToBukkitEventDispatcher.dispatchFabric2Bukkit();
    }
}
