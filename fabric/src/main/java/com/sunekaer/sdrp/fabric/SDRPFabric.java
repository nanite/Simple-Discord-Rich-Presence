package com.sunekaer.sdrp.fabric;

import com.sunekaer.sdrp.SDRP;
import net.fabricmc.api.ModInitializer;

public class SDRPFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SDRP.init();
    }
}
