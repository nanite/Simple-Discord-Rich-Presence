package com.sunekaer.sdrp.integration.kubejs;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class SDRPKubeJSIntegration extends KubeJSPlugin {
    static EventGroup GROUP = EventGroup.of("sdrp");
    static EventHandler DIMENSION_CHANGE = GROUP.client("dimension_change", () -> ClientDimensionChangeEvent.class);

    public static void setup() {
        EntityEvent.ADD.register(SDRPKubeJSIntegration::clientJoinEvent);
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("SDRP", SDRPKubeJSWrapper.class);
    }

    @Override
    public void registerEvents() {
        GROUP.register();
    }

    private static EventResult clientJoinEvent(Entity entity, Level level) {
        if (!(entity instanceof Player player)) {
            return EventResult.pass();
        }

        if (!level.isClientSide) {
            return EventResult.pass();
        }

        DIMENSION_CHANGE.post(new ClientDimensionChangeEvent(level.dimensionType(), player, level));
        return EventResult.pass();
    }

    public static class ClientDimensionChangeEvent extends EventJS {
        DimensionType dimensionType;
        Player player;
        Level level;

        public ClientDimensionChangeEvent(DimensionType dimensionType, Player player, Level level) {
            this.dimensionType = dimensionType;
            this.player = player;
            this.level = level;
        }

        public void updateSDRPState(String message, String imageName, String imageKey) {
            SDRPKubeJSWrapper.setState(message, imageName, imageKey);
        }
    }
}
