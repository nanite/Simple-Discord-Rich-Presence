package com.sunekaer.sdrp.config;

public class ConfigData {
    public ConfigEntry<Long> clientId;
    public ConfigEntry<Boolean> enabled;

    public static ConfigData defaultData() {
        var data = new ConfigData();

        data.clientId = new ConfigEntry<>(608012526537408579L, "Your Discord App ID");
        data.enabled = new ConfigEntry<>(true, "If you wish to disable Discord Rich Presence, set this to false.");

        return data;
    }
}
