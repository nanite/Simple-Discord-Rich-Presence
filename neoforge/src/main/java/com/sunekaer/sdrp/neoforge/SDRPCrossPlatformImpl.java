package com.sunekaer.sdrp.neoforge;

import com.sunekaer.sdrp.SDRPCrossPlatform;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class SDRPCrossPlatformImpl {
    /**
     * This is our actual method to {@link SDRPCrossPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
