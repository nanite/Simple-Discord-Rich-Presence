package com.sunekaer.sdrp.discord;

import com.jagrosh.discordipc.entities.RichPresence;
import com.sunekaer.sdrp.SDRP;
import net.minecraft.client.resources.language.I18n;

public class State {
    public static final State LOADING_STATE = new State("Starting Minecraft", "Starting Minecraft", "loading");

    public String message;
    public String imageName;
    public String imageKey;

    public State(String message, String imageName, String imageKey) {
        this.message = message;
        this.imageName = imageName;
        this.imageKey = imageKey;
    }

    public RichPresence createPresence() {
        RichPresence.Builder presence = new RichPresence.Builder()
                .setState(I18n.get(message))
                .setStartTimestamp(SDRP.START_TIME)
                .setLargeImage("logo", I18n.get("sdrp.logo"))
                .setSmallImage(imageKey, I18n.get(imageName));

        if (!SDRP.config.buttons.isEmpty()) {
            var buttonOne = SDRP.config.buttons.get(0);
            presence.setButton1(buttonOne.label, buttonOne.url);

            if (SDRP.config.buttons.size() > 1) {
                var buttonTwo = SDRP.config.buttons.get(1);
                presence.setButton2(buttonTwo.label, buttonTwo.url);
            }
        }

        return presence.build();
    }
}
