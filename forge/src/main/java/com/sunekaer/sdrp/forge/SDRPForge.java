package com.sunekaer.sdrp.forge;

import dev.architectury.platform.forge.EventBuses;
import com.sunekaer.sdrp.SDRP;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SDRP.MOD_ID)
public class SDRPForge {
    public SDRPForge() {
        // Submit our event bus to let architectury register our content on the right time
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            EventBuses.registerModEventBus(SDRP.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
            SDRP.init();
        });
    }
}
