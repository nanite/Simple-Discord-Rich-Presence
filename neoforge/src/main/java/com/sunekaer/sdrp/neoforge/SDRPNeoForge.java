package com.sunekaer.sdrp.neoforge;

import com.sunekaer.sdrp.SDRP;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(SDRP.MOD_ID)
public class SDRPNeoForge {
    public SDRPNeoForge() {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }

        SDRP.init();
//        if (Platform.isModLoaded("kubejs")) {
//            SDRPKubeJSIntegration.setup();
//        }
    }
}
