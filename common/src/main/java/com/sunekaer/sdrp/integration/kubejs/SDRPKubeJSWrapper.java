package com.sunekaer.sdrp.integration.kubejs;

import com.jagrosh.discordipc.entities.RichPresence;
import com.sunekaer.sdrp.discord.State;
import net.minecraft.client.resources.language.I18n;

import static com.sunekaer.sdrp.SDRP.RP_CLIENT;

public class SDRPKubeJSWrapper {
    public static final SDRPKubeJSWrapper INSTANCE = new SDRPKubeJSWrapper();

    public void setState(String message, String imageName, String imageKey) {
        RP_CLIENT.setState(new State(I18n.get(message),  I18n.get(imageName), imageKey).createPresence());
    }

    public RichPresence getCurrentState() {
        return RP_CLIENT.getCurrentState();
    }
}
