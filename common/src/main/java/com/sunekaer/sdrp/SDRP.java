package com.sunekaer.sdrp;

import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.sunekaer.sdrp.config.SDRPConfig;
import com.sunekaer.sdrp.discord.RPClient;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.time.OffsetDateTime;

public class SDRP {
    public static final String MOD_ID = "sdrp";
    public static RPClient RP_CLIENT;
    public static final OffsetDateTime START_TIME = OffsetDateTime.now();

    public static SDRPConfig config;

    public static void init() {
        AutoConfig.register(SDRPConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(SDRPConfig.class).getConfig();

        RP_CLIENT = new RPClient();

        ClientLifecycleEvent.CLIENT_STOPPING.register((minecraft) -> shutdownDiscordClient());
        Runtime.getRuntime().addShutdownHook(new Thread(SDRP::shutdownDiscordClient));

        EntityEvent.ADD.register(SDRP::clientJoinEvent);
        ClientGuiEvent.INIT_POST.register(SDRP::screenEvent);
    }

    /**
     * When the screen is part of the main menu screens, attempt to update discord about it
     */
    private static void screenEvent(Screen screen, ScreenAccess screenAccess) {
        if (!config.enabled || !config.enableUpdateScreenPresence) {
            return;
        }

        updateScreen(screen);
    }

    private static void updateScreen(Screen screen) {
        var screenClassName = screen.getClass().getName();
        var screenPresence = config.screens.stream()
                .filter(e -> e.screenClass.contains(screenClassName))
                .findFirst();

        if (screenPresence.isEmpty()) {
            return;
        }

        var state = screenPresence.get().createPresence();
        var currentState = RP_CLIENT.getCurrentState();
        if (currentState != state) {
            RP_CLIENT.setState(state);
        }
    }

    /**
     * When the client joins, send out a setDim event to discord
     */
    private static EventResult clientJoinEvent(Entity entity, Level level) {
        if (!config.enabled || !config.enableUpdateDimensionPresence) {
            return EventResult.pass();
        }

        if (entity instanceof AbstractClientPlayer) {
            if (entity.getUUID().equals(Minecraft.getInstance().player.getUUID())){
                setDimension(level, (Player) entity);
            }
        }

        return EventResult.pass();
    }

    /**
     * Dynamically create an entry on a dimension change
     */
    public static void setDimension(Level level, Player player) {
        var dimensionName = level.dimension().location().toString();

        for (var entry : config.dimensionsSupport) {
            if (entry.matches(dimensionName)) {
                var state = entry.createPresence(level.dimension().location(), player);
                RP_CLIENT.setState(state);
                return;
            }
        }
    }

    /**
     * Shutdown various threads
     */
    private static void shutdownDiscordClient() {
        if (!RPClient.EXECUTOR_SERVICE.isShutdown()) {
            RPClient.EXECUTOR_SERVICE.shutdown();
        }

        if (RP_CLIENT == null || RP_CLIENT.getClient() == null || RP_CLIENT.getClient().getStatus() != PipeStatus.CONNECTED) {
            return;
        }

        RP_CLIENT.getClient().close();
    }
}
