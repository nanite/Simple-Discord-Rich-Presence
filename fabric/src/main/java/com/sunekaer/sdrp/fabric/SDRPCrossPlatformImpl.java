package com.sunekaer.sdrp.fabric;

import com.sunekaer.sdrp.SDRPCrossPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class SDRPCrossPlatformImpl {
    /**
     * This is our actual method to {@link SDRPCrossPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
