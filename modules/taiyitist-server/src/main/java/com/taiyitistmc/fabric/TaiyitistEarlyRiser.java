package com.taiyitistmc.fabric;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class TaiyitistEarlyRiser implements Runnable{

    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        String loginProtocolState = remapper.mapClassName("intermediary", "net.minecraft.class_3248$class_3249");
        ClassTinkerers.enumBuilder(loginProtocolState).addEnum("WAITING_FOR_COOKIES").addEnum("DISCONNECTED").build();
    }
}
