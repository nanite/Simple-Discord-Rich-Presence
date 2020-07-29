package com.sunekaer.mods.sdrp;

import com.sunekaer.mods.sdrp.config.Config;
import com.sunekaer.mods.sdrp.discord.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SDRP.MODID)
public class SDRP {
    public static final String MODID = "sdrp";
    public static final Logger LOGGER = LogManager.getLogger("Simple Discord Rich Presence");

    public SDRP() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.configSpec);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
            MinecraftForge.EVENT_BUS.addListener(this::initGui);
            MinecraftForge.EVENT_BUS.addListener(this::entityJoinWorld);
        });
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> LOGGER.warn("This is a client only mod!"));
    }

    private void setup(FMLCommonSetupEvent event) {
        DiscordRichPresence.start();

        if (!DiscordRichPresence.isEnabled()) {
            return;
        }
        final DiscordRichPresence.State state = DiscordRichPresence.getCurrent();
        if (state == null || state != DiscordRichPresence.map.get("loading")) {
            DiscordRichPresence.setState(DiscordRichPresence.map.get("loading"));
        }
    }

    private void initGui(InitGuiEvent.Pre event) {
        if (!DiscordRichPresence.isEnabled()) {
            return;
        }
        if (event.getGui() instanceof MainMenuScreen || event.getGui() instanceof WorldSelectionScreen || event.getGui() instanceof MultiplayerScreen) {
            final DiscordRichPresence.State state = DiscordRichPresence.getCurrent();
            if (state != DiscordRichPresence.map.get("menu")) {
                DiscordRichPresence.setState(DiscordRichPresence.map.get("menu"));
            }
        }
    }

    private void entityJoinWorld(EntityJoinWorldEvent event) {
        if (!DiscordRichPresence.isEnabled()) {
            return;
        }
        if (event.getEntity() instanceof ClientPlayerEntity) {
            final ClientPlayerEntity player = (ClientPlayerEntity) event.getEntity();
            if (player.getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) {
                DiscordRichPresence.setDimension(player.getEntityWorld());
            }
        }
    }



}
