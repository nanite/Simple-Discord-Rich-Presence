package com.sunekaer.sdrp.neoforge.kubejs;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class SDRPKubeJSIntegration implements KubeJSPlugin {
    static EventGroup GROUP = EventGroup.of("sdrp");
    static EventHandler DIMENSION_CHANGE = GROUP.client("dimension_change", () -> ClientDimensionChangeEvent.class);

    public static void setup() {
        EntityEvent.ADD.register(SDRPKubeJSIntegration::clientJoinEvent);
    }


    @Override
    public void registerBindings(BindingRegistry event) {
        event.add("SDRP", SDRPKubeJSWrapper.class);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(GROUP);
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

    public static class ClientDimensionChangeEvent implements KubeEvent {
        public DimensionType dimensionType;
        public Player player;
        public Level level;

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
