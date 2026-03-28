package com.sunekaer.sdrp;

import com.sunekaer.sdrp.config.SDRPConfig;
import com.sunekaer.sdrp.discord.RPClient;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
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
        ConfigHolder<SDRPConfig> register = AutoConfig.register(SDRPConfig.class, JanksonConfigSerializer::new);
        config = register.getConfig();

        RP_CLIENT = new RPClient();
    }

    /**
     * Called by platform event handlers when a screen is initialized.
     */
    public static void onScreenInit(Screen screen) {
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
     * Called by platform event handlers when an entity is added to a level.
     */
    public static void onClientJoin(Entity entity, Level level) {
        if (!config.enabled || !config.enableUpdateDimensionPresence) {
            return;
        }

        if (entity instanceof AbstractClientPlayer) {
            if (entity.getUUID().equals(Minecraft.getInstance().player.getUUID())){
                setDimension(level, (Player) entity);
            }
        }
    }

    /**
     * Dynamically create an entry on a dimension change
     */
    public static void setDimension(Level level, Player player) {
        var dimensionName = level.dimension().identifier().toString();

        for (var entry : config.dimensionsSupport) {
            if (entry.matches(dimensionName)) {
                var state = entry.createPresence(level.dimension().identifier(), player);
                RP_CLIENT.setState(state);
                return;
            }
        }
    }
}
