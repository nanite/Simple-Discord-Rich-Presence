package com.sunekaer.sdrp.discord;

import com.google.common.collect.ImmutableMap;
import com.jagrosh.discordipc.entities.RichPresence;
import com.sunekaer.sdrp.SDRP;
import net.minecraft.client.resources.language.I18n;

import java.util.Map;

public class State {
    public static final Map<String, State> PRESETS = ImmutableMap.of(
            "loading", new State("Starting Minecraft", "Starting Minecraft", "loading"),
            "menu", new State("sdrp.mainmenu", "sdrp.mainmenu", "menu")
    );

    public String message;
    public String imageName;
    public String imageKey;

    public State(String m, String n, String k) {
        message = m;
        imageName = n;
        imageKey = k;
    }

    public RichPresence createPresence() {
        return new RichPresence.Builder()
                .setState(I18n.get(message))
                .setStartTimestamp(SDRP.START_TIME)
                .setLargeImage("logo", I18n.get("sdrp.logo"))
                .setSmallImage(imageKey, I18n.get(imageName))
                .build();
    }
}
