package com.sunekaer.sdrp.integration.kubejs;


import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class SDRPKubeJSIntegration extends KubeJSPlugin {
    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("SDRP", SDRPKubeJSWrapper.class);
    }
}
