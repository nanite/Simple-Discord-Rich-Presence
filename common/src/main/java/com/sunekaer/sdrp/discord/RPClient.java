package com.sunekaer.sdrp.discord;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.Callback;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import com.sunekaer.sdrp.SDRP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Events handled:
 * - Startup (Loading)
 * - Gui (Main Menu)
 * - Join World (Show the dimension)
 */
public class RPClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RPClient.class);
    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private IPCClient client = null;
    private RichPresence currentState = null;

    public final ArrayBlockingQueue<RichPresence> stateUpdateQueue = new ArrayBlockingQueue<>(24);

    public RPClient() {
        if (!SDRP.config.enabled) {
            LOGGER.info("Preventing Simple Discord Rich Presence from starting as it's disabled");
            return;
        }

        LOGGER.info("Setting up discord client");
        EXECUTOR_SERVICE.submit(this::connectClient);
        EXECUTOR_SERVICE.scheduleAtFixedRate(this::processStateQueue, 0, 5, TimeUnit.SECONDS);
        EXECUTOR_SERVICE.scheduleAtFixedRate(this::maintainCorrectState, 1000, 1000 * 120, TimeUnit.MILLISECONDS);
    }

    /**
     * We connect the client outside the main thread to attempt to avoid blocking code. As this is the only location
     * we set the client, we should avoid any CME's
     */
    private void connectClient() {
        // Connect to the client in the thread to prevent blocking logic
        if (this.client != null) {
            return;
        }

        this.client = new IPCClient(SDRP.config.clientId);
        this.client.setListener(new IPCListener() {
            public void onReady(IPCClient client, User user) {
                LOGGER.info("Discord client ready");

                if (State.PRESETS.containsKey("loading")) {
                    setState(State.PRESETS.get("loading").createPresence());
                }
            }
        });

        try {
            this.client.connect();
        } catch (NoDiscordClientException e) {
            LOGGER.error("Unable to connect to the discord client", e);
        }
    }

    /**
     * Takes the first item of the queue and submits it to the client, we'll avoid doing this if the queue
     * is empty, the client isn't connected or the client is unset
     */
    private void processStateQueue() {
        if (this.client == null || this.client.getStatus() != PipeStatus.CONNECTED) {
            return;
        }

        if (stateUpdateQueue.isEmpty()) {
            return;
        }

        RichPresence state = stateUpdateQueue.poll();
        if (state == null) {
            return;
        }

        this.client.sendRichPresence(state, new Callback((s) -> {}, (e) -> {
            if (SDRP.config.logState) {
                LOGGER.error("Failed to send state to discord: {}\n {}", state.toJson().toString(), e);
            }
        }));

        if (SDRP.config.logState) {
            LOGGER.info("Sent state to discord: {}", state.toJson().toString());
        }
    }

    /**
     * Runs every couple of minutes to ensure the current state is maintained in the client's state.
     */
    private void maintainCorrectState() {
        if (currentState != null && client.getStatus() == PipeStatus.CONNECTED) {
            setState(currentState);
        }
    }

    /**
     * Simply takes and item and attempts to add it to the queue, if the queue is full, we catch the exception as
     * logically the queue should never be full as long as the client is alive and processing.
     *
     * It's important that we use a backing, threadsafe, queue here as the client might not be ready, thus we need
     * to preserve the states until it is. This is likely overkill as we could easily ignore some failed states, but
     * I like the backup.
     *
     * @param context the state update for the client
     */
    public void setState(RichPresence context) {
        // Don't work if it's disabled
        if (!SDRP.config.enabled) {
            return;
        }

        currentState = context;
        try {
            stateUpdateQueue.add(context);
        } catch (IllegalStateException exception) {
            LOGGER.warn("Attempted to add context to update queue, operation failed due to a full list, something is likely wrong here or the discord client isn't present");
        }
    }

    public RichPresence getCurrentState() {
        return currentState;
    }

    public IPCClient getClient() {
        return client;
    }
}
