package com.sunekaer.mods.sdrp.discord;

import com.sunekaer.mods.sdrp.config.Config;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityType;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordRichPresence {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final OffsetDateTime TIME = OffsetDateTime.now();
    private static State currentState;

//    private static Core core;
    private static Activity activity;
    private static boolean isEnabled = false;
    private static int errorCount = 0;
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

//    private static final Runnable TIMER_TASK = () -> {
//        if (currentState != null && CLIENT.getStatus() == IPCClient.Status.CONNECTED) {
//            setState(currentState);
//        }
//    };
//
//    static {
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            LOGGER.info("Shutting down Discord Rich Presence client");
//            stop();
//            try {
//                EXECUTOR_SERVICE.shutdown();
//            } catch (RuntimeException error) {
//                LOGGER.error("Error shutting down Discord Rich Presence timer", error);
//            }
//        }, "DiscordRP Stop"));
//    }

    public static void start() {
        Util.backgroundExecutor().execute(() -> {
            try {
                String fileName;
                if (SystemUtils.IS_OS_WINDOWS)
                    fileName = "./sdk/discord_game_sdk.dll";
                else if (SystemUtils.IS_OS_MAC)
                    fileName = "./sdk/discord_game_sdk.dylib";
                else if (SystemUtils.IS_OS_LINUX)
                    fileName = "./sdk/discord_game_sdk.so";
                else
                    throw new RuntimeException("cannot determine OS type: " + System.getProperty("os.name"));

                System.out.println(fileName);

                Core.initDiscordNative(fileName);

                try(CreateParams params = new CreateParams())
                {
                    params.setClientID(Config.CONFIG.clientID.get());
                    params.setFlags(CreateParams.getDefaultFlags());

                    try(Core core = new Core(params))
                    {
                        activity = new Activity();
                        activity.setState("World!");

                        activity.setDetails("Hello");

                        activity.setType(ActivityType.PLAYING);

                        activity.party().setID("1234");

                        activity.party().size().setCurrentSize(10);
                        activity.party().size().setMaxSize(100);

                        activity.timestamps().setStart(Instant.now());
                        activity.timestamps().setEnd(Instant.now().plusSeconds(16));

                        activity.assets().setLargeImage("test");
                        activity.assets().setLargeText("Just a test!");

                        activity.assets().setSmallImage("test");
                        activity.assets().setSmallText("It's a TEST!!!");

                        activity.secrets().setMatchSecret("match");
                        activity.secrets().setJoinSecret("join");
                        activity.secrets().setSpectateSecret("spectate");

                        activity.setInstance(true);
                    } catch (Error er) {
                        LOGGER.error("Failed to create Core");
                    }


//                    core.activityManager().updateActivity(activity, result -> {
//
//                    });
                }

//
//                EXECUTOR_SERVICE.scheduleAtFixedRate(TIMER_TASK, 1000, 1000 * 120, TimeUnit.MILLISECONDS);
//                isEnabled = true;
//
//                final State state = DiscordRichPresence.getCurrent();
//                if (state == null || state != map.get("loading")) {
//                    setState(DiscordRichPresence.map.get("loading"));
//                }

                LOGGER.info("Discord client found and connected.");
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        });
    }

//    public static void stop() {
//        try {
//            CLIENT.close();
//        } catch (Exception ex) {
//            LOGGER.error("Error closing Discord connection.", ex);
//        }
//        errorCount = 0;
//        isEnabled = false;
//        LOGGER.info("Discord client closed.");
//    }

//    public static void setDimension(Level level) {
//        State dim = map.get(level.dimension().toString());
//        if (dim != null) {
//            setState(dim);
//        } else {
//            String name = I18n.get("sdrp." + level.dimension().location().getPath());
//            String in = I18n.get("sdrp." + level.dimension().location().getPath() + ".in");
//            String key = level.dimension().location().getPath();
//
//            State dim2 = new State(in,  name, key);
//            setState(dim2);
//        }
//    }

//    public static void setState(State state) {
//        if (state == null) {
//            return;
//        }
//
//        Util.backgroundExecutor().execute(() -> {
//            currentState = state;
//            RichPresence.Builder builder = new RichPresence.Builder();
//
//            builder.setState(state.message);
//            builder.setStartTimestamp(TIME);
//            String name = I18n.get("sdrp.logo");
//            builder.setLargeImage("logo", name);
//            builder.setSmallImage(state.imageKey, state.imageName);
//            try {
//                CLIENT.sendRichPresence(builder.build());
//            } catch (Exception ex) {
//                LOGGER.error(ex);
//                try {
//                    CLIENT.connect();
//                    errorCount = 0;
//                    CLIENT.sendRichPresence(builder.build());
//                } catch (Exception ex2) {
//                    try {
//                        CLIENT.close();
//                    } catch (Exception ex3) {
//                    }
//                    errorCount++;
//                    if (errorCount > 10) {
//                        LOGGER.info("DiscordRP connection failed.");
//                        stop();
//                    }
//                }
//            }
//        });
//    }

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