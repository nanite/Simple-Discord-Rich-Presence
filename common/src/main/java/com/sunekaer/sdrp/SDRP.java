package com.sunekaer.sdrp;

import com.sunekaer.sdrp.config.SDRPConfig;
import com.sunekaer.sdrp.discord.RPClient;
import dev.nanite.library.core.config.ConfigManager;
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

    public static void init() {
        ConfigManager.register(SDRPConfig.CONFIG);
    }


    public static void setup() {
        RP_CLIENT = new RPClient();
    }

    /**
     * Called by platform event handlers when a screen is initialized.
     */
    public static void onScreenInit(Screen screen) {
        if (!SDRPConfig.enabled.get() || !SDRPConfig.enableUpdateScreenPresence.get()) {
            return;
        }

        updateScreen(screen);
    }

    private static void updateScreen(Screen screen) {
        var screenClassName = screen.getClass().getName();
        var screenPresence = SDRPConfig.screens.get().stream()
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
        if (!SDRPConfig.enabled.get() || !SDRPConfig.enableUpdateDimensionPresence.get()) {
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

        for (var entry : SDRPConfig.dimensionsSupport.get()) {
            if (entry.matches(dimensionName)) {
                var state = entry.createPresence(level.dimension().identifier(), player);
                RP_CLIENT.setState(state);
                return;
            }
        }
    }
}
