package com.sunekaer.mods.sdrp.discord;

import com.sunekaer.mods.sdrp.config.Config;
import com.sunekaer.mods.sdrp.discord.discordipc.IPCClient;
import com.sunekaer.mods.sdrp.discord.discordipc.entities.RichPresence;
import com.sunekaer.mods.sdrp.discord.discordipc.exceptions.NoDiscordClientException;

import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordRichPresence {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final OffsetDateTime TIME = OffsetDateTime.now();
    private static State currentState;
    private static final IPCClient CLIENT = new IPCClient(Config.CONFIG.clientID.get());
    private static boolean isEnabled = false;
    private static int errorCount = 0;
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private static final Runnable TIMER_TASK = () -> {
        if (currentState != null && CLIENT.getStatus() == IPCClient.Status.CONNECTED) {
            setState(currentState);
        }
    };

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down Discord Rich Presence client");
            stop();
            try {
                EXECUTOR_SERVICE.shutdown();
            } catch (RuntimeException error) {
                LOGGER.error("Error shutting down Discord Rich Presence timer", error);
            }
        }, "DiscordRP Stop"));
    }

    public static void start() {
        Util.backgroundExecutor().execute(() -> {
            try {
                CLIENT.connect();
                EXECUTOR_SERVICE.scheduleAtFixedRate(TIMER_TASK, 1000, 1000 * 120, TimeUnit.MILLISECONDS);
                isEnabled = true;

                final State state = DiscordRichPresence.getCurrent();
                if (state == null || state != map.get("loading")) {
                    setState(DiscordRichPresence.map.get("loading"));
                }

                LOGGER.info("Discord client found and connected.");
            } catch (NoDiscordClientException ex) {
                LOGGER.info("Discord client was not found.");
            }
        });
    }

    public static void stop() {
        try {
            CLIENT.close();
        } catch (Exception ex) {
            LOGGER.error("Error closing Discord connection.", ex);
        }
        errorCount = 0;
        isEnabled = false;
        LOGGER.info("Discord client closed.");
    }

    public static void setDimension(Level level) {
        State dim = map.get(level.dimension().toString());
        if (dim != null) {
            setState(dim);
        } else {
            String name = I18n.get("sdrp." + level.dimension().location().getPath());
            String in = I18n.get("sdrp." + level.dimension().location().getPath() + ".in");
            String key = level.dimension().location().getPath();

            State dim2 = new State(in,  name, key);
            setState(dim2);
        }
    }

    public static void setState(State state) {
        if (state == null) {
            return;
        }

        Util.backgroundExecutor().execute(() -> {
            currentState = state;
            RichPresence.Builder builder = new RichPresence.Builder();

            builder.setState(state.message);
            builder.setStartTimestamp(TIME);
            String name = I18n.get("sdrp.logo");
            builder.setLargeImage("logo", name);
            builder.setSmallImage(state.imageKey, state.imageName);
            try {
                CLIENT.sendRichPresence(builder.build());
            } catch (Exception ex) {
                LOGGER.error(ex);
                try {
                    CLIENT.connect();
                    errorCount = 0;
                    CLIENT.sendRichPresence(builder.build());
                } catch (Exception ex2) {
                    try {
                        CLIENT.close();
                    } catch (Exception ex3) {
                    }
                    errorCount++;
                    if (errorCount > 10) {
                        LOGGER.info("DiscordRP connection failed.");
                        stop();
                    }
                }
            }
        });
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static State getCurrent() {
        return currentState;
    }

    public static class State {

        public String message;
        public String imageName;
        public String imageKey;

        public State(String m, String n, String k) {
            message = m;
            imageName = n;
            imageKey = k;
        }
    }

    public static HashMap<String, State> map = new HashMap<>();

    static {
        map.put("loading", new State("Starting Minecraft", "Starting Minecraft", "loading"));
        map.put("menu", new State("Main Menu", "Main Menu", "menu"));
    }
}