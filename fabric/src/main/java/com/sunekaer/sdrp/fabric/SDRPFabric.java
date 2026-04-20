package com.sunekaer.sdrp.fabric;

import com.sunekaer.sdrp.SDRP;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class SDRPFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SDRP.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(ignored -> SDRP.setup());

        ClientEntityEvents.ENTITY_LOAD.register(SDRP::onClientJoin);

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            SDRP.onScreenInit(screen);
        });
    }
}
