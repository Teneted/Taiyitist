package com.taiyitistmc.eventhandler;

import com.taiyitistmc.TaiyitistMod;
import com.taiyitistmc.eventhandler.dispatcher.EntityEventDispatcher;
import com.taiyitistmc.eventhandler.dispatcher.FabricToBukkitEventDispatcher;
import com.taiyitistmc.eventhandler.dispatcher.LevelEventDispatcher;
import com.taiyitistmc.eventhandler.dispatcher.PlayerEventDispatcher;
import com.taiyitistmc.eventhandler.dispatcher.ServerEventDispatcher;
import com.taiyitistmc.util.I18n;

public class BannerEventDispatcherRegistry {

    public static void registerEventDispatchers() {
        TaiyitistMod.LOGGER.info(I18n.as("taiyitist.event_handler.register"));
        LevelEventDispatcher.dispatchLevel();
        PlayerEventDispatcher.dispatcherPlayer();
        EntityEventDispatcher.dispatchEntityEvent();
        FabricToBukkitEventDispatcher.dispatchFabric2Bukkit();
        ServerEventDispatcher.dispatchServer();
    }
}
