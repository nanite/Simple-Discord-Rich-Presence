package com.sunekaer.sdrp.discord;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import com.sunekaer.sdrp.config.Config;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.concurrent.*;

/**
 * Events handled:
 * - Startup (Loading)
 * - Gui (Main Menu)
 * - Join World (Show the dimension)
 */
public class RPClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RPClient.class);
    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private IPCClient client;
    private RichPresence currentState = null;

    public RPClient() {
        if (!Config.get().data.enabled.get()) {
            LOGGER.info("Preventing Simple Discord Rich Presence from starting as it's disabled");
            return;
        }

        LOGGER.info("Starting discord client");
        this.client = new IPCClient(Config.get().data.clientId.get());

        this.client.setListener(new IPCListener(){
            @Override
            public void onReady(IPCClient client, User user) {
                LOGGER.info("Discord client ready");

                if (State.PRESETS.containsKey("loading")) {
                    setState(State.PRESETS.get("loading").createPresence());
                }

                // Keep running the current state whilst the connection is alive
                EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
                    if (currentState != null && client.getStatus() == PipeStatus.CONNECTED) {
                        LOGGER.warn("Attempted to send {} to the client before it was ready", currentState);
                        setState(currentState);
                    }
                }, 1000, 1000 * 120, TimeUnit.MILLISECONDS);
            }
        });

        try {
            this.client.connect();
        } catch (NoDiscordClientException e) {
            LOGGER.error("Unable to connect to the discord client", e);
        }
    }

    public void setState(RichPresence context) {
        // Don't work if it's disabled
        if (!Config.get().data.enabled.get()) {
            return;
        }

        if (client == null || client.getStatus() != PipeStatus.CONNECTED) {
            LOGGER.warn("Attempted to send {} to the client before it was ready", context);
            return;
        }

        currentState = context;

        Util.backgroundExecutor().submit(() -> client.sendRichPresence(context));
    }

    public RichPresence getCurrentState() {
        return currentState;
    }

    public IPCClient getClient() {
        return client;
    }
}
