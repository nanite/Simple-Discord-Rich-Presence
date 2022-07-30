package com.sunekaer.sdrp.forge;

import com.sunekaer.sdrp.SDRPCrossPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class SDRPCrossPlatformImpl {
    /**
     * This is our actual method to {@link SDRPCrossPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
