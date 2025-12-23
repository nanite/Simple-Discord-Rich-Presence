package com.sunekaer.sdrp.config;

import com.google.gson.GsonBuilder;
import com.sunekaer.sdrp.SDRP;
import com.sunekaer.sdrp.SDRPCrossPlatform;
import com.sunekaer.sdrp.discord.State;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public void validatePostLoad() {
        var oldConfig = SDRPCrossPlatform.getConfigDirectory().resolve("sdrp.json");
        if (Files.exists(oldConfig)) {
            try {
                this.migrateOldConfig(oldConfig);
            } catch (Exception ignored) {}
        }
    }

    private void migrateOldConfig(Path oldConfig) throws IOException {
        OldConfigData oldConfigData = new GsonBuilder().create().fromJson(Files.readString(oldConfig), OldConfigData.class);

        this.clientId = Optional.ofNullable(oldConfigData.clientId.value).orElse(0L);
        this.enabled = Optional.ofNullable(oldConfigData.enabled.value).orElse(true);

        Files.delete(oldConfig);
    }

    // TODO: remove in next major mc update
    public static class OldConfigData {
        public OldConfigEntry<Long> clientId;
        public OldConfigEntry<Boolean> enabled;
    }

    // TODO: remove in next major mc update
    public static final class OldConfigEntry<T> {
        private @Nullable T value;
        private String comment;
    }

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
