package com.sunekaer.sdrp;

import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.mojang.realmsclient.RealmsMainScreen;
import com.sunekaer.sdrp.config.SDRPConfig;
import com.sunekaer.sdrp.discord.RPClient;
import com.sunekaer.sdrp.discord.State;
import com.sunekaer.sdrp.integration.kubejs.SDRPKubeJSIntegration;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.platform.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.time.OffsetDateTime;

public class SDRP {
    public static final String MOD_ID = "sdrp";
    public static RPClient RP_CLIENT;
    public static final OffsetDateTime START_TIME = OffsetDateTime.now();

    public static SDRPConfig config;

    public static void init() {
        AutoConfig.register(SDRPConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(SDRPConfig.class).getConfig();

        RP_CLIENT = new RPClient();

        ClientLifecycleEvent.CLIENT_STOPPING.register((minecraft) -> shutdownDiscordClient());
        Runtime.getRuntime().addShutdownHook(new Thread(SDRP::shutdownDiscordClient));

        EntityEvent.ADD.register(SDRP::clientJoinEvent);
        ClientGuiEvent.INIT_POST.register(SDRP::screenEvent);

        if (Platform.isModLoaded("kubejs")) {
            SDRPKubeJSIntegration.setup();
        }
    }

    /**
     * When the screen is part of the main menu screens, attempt to update discord about it
     */
    private static void screenEvent(Screen screen, ScreenAccess screenAccess) {
        if (!config.enabled || !State.PRESETS.containsKey("menu") || !config.screenEvent) {
            return;
        }

        if (screen instanceof TitleScreen || screen instanceof JoinMultiplayerScreen || screen instanceof SelectWorldScreen || screen instanceof RealmsMainScreen) {
            var menuState = State.PRESETS.get("menu").createPresence();
            var currentState = RP_CLIENT.getCurrentState();
            if (currentState != menuState) {
                RP_CLIENT.setState(menuState);
            }
        }
    }

    /**
     * When the client joins, send out a setDim event to discord
     */
    private static EventResult clientJoinEvent(Entity entity, Level level) {
        if (!config.enabled || !config.clientJoinEvent) {
            return EventResult.pass();
        }

        if (entity instanceof AbstractClientPlayer) {
            if (entity.getUUID().equals(Minecraft.getInstance().player.getUUID())){
                setDimension(level);
            }
        }

        return EventResult.pass();
    }

    /**
     * Dynamically create an entry on a dimension change
     */
    public static void setDimension(Level level) {
        State dim = State.PRESETS.get(level.dimension().toString());
        if (dim != null) {
            RP_CLIENT.setState(dim.createPresence());
        } else {
            String name = I18n.get("sdrp." + level.dimension().location().getPath());
            String in = I18n.get("sdrp." + level.dimension().location().getPath() + ".in");
            String key = level.dimension().location().getPath();

            RP_CLIENT.setState(new State(in,  name, key).createPresence());
        }
    }

    /**
     * Shutdown various threads
     */
    private static void shutdownDiscordClient() {
        if (!RPClient.EXECUTOR_SERVICE.isShutdown()) {
            RPClient.EXECUTOR_SERVICE.shutdown();
        }

        if (RP_CLIENT == null
                || RP_CLIENT.getClient() == null
                || RP_CLIENT.getClient().getStatus() != PipeStatus.CLOSED
                || RP_CLIENT.getClient().getStatus() != PipeStatus.DISCONNECTED
        ) {
            return;
        }

        RP_CLIENT.getClient().close();
    }
}
