package com.sunekaer.mods.sdrp;

import com.mojang.realmsclient.RealmsMainScreen;
import com.sunekaer.mods.sdrp.config.Config;
import com.sunekaer.mods.sdrp.discord.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerChangeGameTypeEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SDRP.MODID)
public class SDRP {
    public static final String MODID = "sdrp";
    public static final Logger LOGGER = LogManager.getLogger("Simple Discord Rich Presence");

    public SDRP() {
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
    }

    private void initGui(ScreenEvent.InitScreenEvent event) {
        if (!DiscordRichPresence.isEnabled()) {
            return;
        }

        if (event.getScreen() instanceof TitleScreen || event.getScreen() instanceof JoinMultiplayerScreen || event.getScreen() instanceof SelectWorldScreen || event.getScreen() instanceof RealmsMainScreen) {
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
        if (event.getEntity() instanceof AbstractClientPlayer){
            //assert Minecraft.getInstance().player != null;
            if (event.getEntity().getUUID().equals(Minecraft.getInstance().player.getUUID())){
                DiscordRichPresence.setDimension(event.getEntity().getLevel());
            }
        }
    }
}
