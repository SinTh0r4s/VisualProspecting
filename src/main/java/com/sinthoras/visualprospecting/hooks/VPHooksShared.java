package com.sinthoras.visualprospecting.hooks;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPConfig;
import com.sinthoras.visualprospecting.blocks.VPDemoBlock;
import com.sinthoras.visualprospecting.database.VPCacheWorld;
import com.sinthoras.visualprospecting.database.cachebuilder.VPWorld;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.IOException;


public class VPHooksShared {
	
	// preInit "Run before anything else. Read your config, create blocks, items, 
	// etc, and register them with the GameRegistry."
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) 	{
		VPConfig.syncronizeConfiguration(event.getSuggestedConfigurationFile());

		GameRegistry.registerBlock(new VPDemoBlock(), VPDemoBlock.NAME);
	}
	
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new VPHooksFML());
		MinecraftForge.EVENT_BUS.register(new VPHooksEVENT_BUS());
	}
	
	// postInit "Handle interaction with other mods, complete your setup based on this."
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event) {
		GregTech_API.sAfterGTPostload.add(new VPVeinTypeCaching());
	}
	
	public void fmlLifeCycleEvent(FMLServerAboutToStartEvent event) {

	}

	// register server commands in this event handler
	public void fmlLifeCycleEvent(FMLServerStartingEvent event) {
		final File worldDirectory = event.getServer().getEntityWorld().getSaveHandler().getWorldDirectory();
		if(VPCacheWorld.loadVeinCache(worldDirectory) == false || VPConfig.recacheVeins) {
			try {
				VPWorld world = new VPWorld(worldDirectory);
				world.cacheVeins();
			} catch (IOException e) {
				VP.info("Could not load world save files to build vein cache!");
				e.printStackTrace();
			}
		}
	}
	
	public void fmlLifeCycleEvent(FMLServerStartedEvent event) {
		
	}
	
	public void fmlLifeCycleEvent(FMLServerStoppingEvent event) {
		
	}
	
	public void fmlLifeCycleEvent(FMLServerStoppedEvent event) {
		
	}
}
