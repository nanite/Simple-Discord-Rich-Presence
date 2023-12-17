package com.sunekaer.sdrp.neoforge;

import com.sunekaer.sdrp.SDRP;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;

@Mod(SDRP.MOD_ID)
public class SDRPForge {
    public SDRPForge() {
        // Submit our event bus to let architectury register our content on the right time
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
//            EventBuses.registerModEventBus(SDRP.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
            SDRP.init();
        });
    }
}
