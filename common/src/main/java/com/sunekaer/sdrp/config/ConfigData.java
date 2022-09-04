package com.sunekaer.sdrp.config;

public class ConfigData {
    public ConfigEntry<Long> clientId;
    public ConfigEntry<Boolean> enabled;
    public ConfigEntry<Boolean> screenEvent;
    public ConfigEntry<Boolean> clientJoinEvent;

    public static ConfigData defaultData() {
        var data = new ConfigData();

        data.clientId = new ConfigEntry<>(608012526537408579L, "Your Discord App ID");
        data.enabled = new ConfigEntry<>(true, "If you wish to disable Discord Rich Presence, set this to false.");

        data.screenEvent = new ConfigEntry<>(true, "If set to false, it disables the build in screenEvent, which is used to tell when we are on the main menu.");
        data.clientJoinEvent = new ConfigEntry<>(true, "If set to false, it disables the build in clientJoinEvent, which is used to tell when the player is join a world and changing Dimension.");

        return data;
    }
}
