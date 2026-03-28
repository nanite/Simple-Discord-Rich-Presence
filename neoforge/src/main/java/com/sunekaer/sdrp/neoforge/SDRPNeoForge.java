package com.sunekaer.sdrp.neoforge;

import com.sunekaer.sdrp.SDRP;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@Mod(SDRP.MOD_ID)
public class SDRPNeoForge {
    public SDRPNeoForge() {
        if (!FMLEnvironment.getDist().isClient()) {
            return;
        }

        SDRP.init();
        NeoForge.EVENT_BUS.addListener(this::onEntityJoinLevel);
        NeoForge.EVENT_BUS.addListener(this::onScreenInit);
    }

    private void onEntityJoinLevel(EntityJoinLevelEvent event) {
        SDRP.onClientJoin(event.getEntity(), event.getLevel());
    }

    private void onScreenInit(ScreenEvent.Init.Post event) {
        SDRP.onScreenInit(event.getScreen());
    }
}
