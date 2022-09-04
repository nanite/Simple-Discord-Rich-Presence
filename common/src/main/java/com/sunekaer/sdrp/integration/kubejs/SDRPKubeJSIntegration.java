package com.sunekaer.sdrp.integration.kubejs;


import dev.latvian.mods.kubejs.script.BindingsEvent;

public class SDRPKubeJSIntegration {
    public static void init() {
        BindingsEvent.EVENT.register(SDRPKubeJSIntegration::registerBindings);
    }

    public static void registerBindings(BindingsEvent event) {
        event.add("SDRP", SDRPKubeJSWrapper.INSTANCE);
    }

}
