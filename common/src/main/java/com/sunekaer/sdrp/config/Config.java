package com.sunekaer.sdrp.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.sunekaer.sdrp.SDRPCrossPlatform;
import com.sunekaer.sdrp.discord.RPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Super simple config for super simple people
 */
public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(RPClient.class);
    private static Config instance;

    public static Config get() {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }

    public final ConfigData data;

    public Config() {
        data = this.getOrCreateData();
    }

    private ConfigData getOrCreateData() {
        var location = SDRPCrossPlatform.getConfigDirectory().resolve("sdrp.json");
        if (!Files.exists(location)) {
            var defaultData = ConfigData.defaultData();
            try {
                Files.writeString(location, new GsonBuilder().setPrettyPrinting().create().toJson(defaultData));
            } catch (IOException e) {
                LOGGER.error("Failed to write default data to config, running on in-memory config data");
                return defaultData;
            }
        }

        try {
            return new GsonBuilder().create().fromJson(Files.readString(location), ConfigData.class);
        } catch (IOException e) {
            LOGGER.warn("Failed to read config data from {}, running on in-memory, default, config data", location);
            return ConfigData.defaultData();
        }
    }
}

