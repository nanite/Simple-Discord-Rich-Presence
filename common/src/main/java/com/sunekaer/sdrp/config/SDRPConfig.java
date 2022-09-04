package com.sunekaer.sdrp.config;

import com.google.gson.GsonBuilder;
import com.sunekaer.sdrp.SDRP;
import com.sunekaer.sdrp.SDRPCrossPlatform;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Config(name = SDRP.MOD_ID + "-common")
public class SDRPConfig implements ConfigData {
    @Comment("Your Discord App ID")
    public long clientId = 608012526537408579L;

    @Comment("If you wish to disable Discord Rich Presence, set this to false.")
    public boolean enabled = true;

    @Comment("If set to false, it disables the build in screenEvent, which is used to tell when we are on the main menu.")
    public boolean screenEvent = true;

    @Comment("If set to false, it disables the build in clientJoinEvent, which is used to tell when the player is join a world and changing Dimension.")
    public boolean clientJoinEvent = true;

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
}
