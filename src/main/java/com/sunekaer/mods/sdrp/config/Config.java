package com.sunekaer.mods.sdrp.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final ForgeConfigSpec configSpec;
    public static final ConfigValues CONFIG;

    static {
        Pair<ConfigValues, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ConfigValues::new);
        configSpec = pair.getRight();
        CONFIG = pair.getLeft();
    }

    public static class ConfigValues {
        public ForgeConfigSpec.BooleanValue discordRP;
        public ForgeConfigSpec.LongValue clientID;

        public ConfigValues(ForgeConfigSpec.Builder builder) {
            builder.comment("Simple Discord Rich Presence config")
                    .push("common");

            discordRP = builder
                    .comment("Enable Discord Rich Presence")
                    .define("discordRP", true);
            clientID = builder
                    .comment("Client ID")
                    .defineInRange("clientID", 608012526537408579L, Long.MIN_VALUE, Long.MAX_VALUE);

            builder.pop();
        }
    }
}

