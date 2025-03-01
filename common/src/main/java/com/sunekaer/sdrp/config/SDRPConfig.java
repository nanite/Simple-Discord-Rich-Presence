package com.sunekaer.sdrp.config;

import com.sunekaer.sdrp.SDRP;
import com.sunekaer.sdrp.discord.State;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

@Config(name = SDRP.MOD_ID + "-common")
public class SDRPConfig implements ConfigData {
    @Comment("Your Discord App ID")
    public long clientId = 608012526537408579L;

    @Comment("If you wish to disable Discord Rich Presence, set this to false.")
    public boolean enabled = true;

    @Comment("When enabled, the mod will log the current state being sent to Discord")
    public boolean logState = false;

    @Comment("Set custom buttons for the Discord Rich Presences. You can only have 2 buttons, each button has a label and a URL.")
    public List<Button> buttons = new ArrayList<>();

    @Comment("If set to false, it disables the build-in clientJoinEvent, which is used to tell when the player joins a world and changing Dimension.")
    public boolean enableUpdateDimensionPresence = true;

    @Comment("Invert the whitelist, if set to true, the whitelist will be treated as a blacklist.")
    public boolean invertWhitelist = false;

    @Comment("Add dimensions here that you would like to trigger Rich Presence updates for.")
    public List<String> dimensionsWhitelist = new ArrayList<>() {{
        add("minecraft:overworld");
        add("minecraft:the_nether");
        add("minecraft:the_end");
    }};

    @Comment("If set to false, it disables the build-in screenEvent, which is used to tell when we are on the main menu, or other enabled screens.")
    public boolean enableUpdateScreenPresence = true;

    @Comment("Screens")
    public List<ScreenTranslation> screens = new ArrayList<>() {{
        add(new ScreenTranslation(
                List.of(
                        "net.minecraft.client.gui.screens.TitleScreen",
                        "net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen",
                        "net.minecraft.client.gui.screens.worldselection.SelectWorldScreen"
                ),
                "sdrp.mainmenu",
                "sdrp.mainmenu",
                "menu"
        ));

    }};

    public static final class Button {
        public String label;
        public String url;
    }

    public static final class ScreenTranslation extends State {
        public List<String> screenClass;

        public ScreenTranslation(List<String> screenClass, String message, String imageName, String imageKey) {
            super(message, imageName, imageKey);
            this.screenClass = screenClass;
        }
    }
}
