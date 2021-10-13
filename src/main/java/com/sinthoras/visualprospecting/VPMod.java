package com.sinthoras.visualprospecting;

import com.sinthoras.visualprospecting.hooks.HooksShared;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;


@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.VISUALPROSPECTING, acceptedMinecraftVersions = "[1.7.10]", dependencies = "required-after:spongemixins@[1.1.0,);")
public class VPMod {

    @SidedProxy(clientSide= Tags.COM_SINTHORAS_VISUALPROSPECTING + ".hooks.HooksClient", serverSide= Tags.COM_SINTHORAS_VISUALPROSPECTING + ".hooks.HooksShared")
    public static HooksShared proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc, and register them with the GameRegistry."
    public void fmlLifeCycleEvent(FMLPreInitializationEvent event) {
        VP.debug("Registered sided proxy for: " + (Utils.isLogicalClient() ? "Client" : "Dedicated server"));
        VP.debug("preInit()"+event.getModMetadata().name);
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void fmlLifeCycleEvent(FMLInitializationEvent event) {
        VP.debug("init()");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void fmlLifeCycle(FMLPostInitializationEvent event) {
        VP.debug("postInit()");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerAboutToStartEvent event) {
        VP.debug("Server about to start");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler
    public void fmlLifeCycle(FMLServerStartingEvent event) {
        VP.debug("Server starting");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerStartedEvent event) {
        VP.debug("Server started");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerStoppingEvent event) {
        VP.debug("Server stopping");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerStoppedEvent event) {
        VP.debug("Server stopped");
        proxy.fmlLifeCycleEvent(event);
    }
}
