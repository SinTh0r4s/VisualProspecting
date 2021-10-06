package com.sinthoras.visualprospecting.hooks;

import api.visualprospecting.VPOreGenCallbackHandler;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPConfig;
import com.sinthoras.visualprospecting.database.cachebuilder.VPWorldAnalysis;
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
import gregtech.api.GregTech_API;
import gregtech.common.GT_Worldgenerator;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;


public class VPHooksShared {
	
	// preInit "Run before anything else. Read your config, create blocks, items, 
	// etc, and register them with the GameRegistry."
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) 	{
		VP.configFile = event.getSuggestedConfigurationFile();
		VPConfig.syncronizeConfiguration();
	}
	
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new VPHooksFML());
		MinecraftForge.EVENT_BUS.register(new VPHooksEVENT_BUS());
	}
	
	// postInit "Handle interaction with other mods, complete your setup based on this."
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event) {
		GregTech_API.sAfterGTPostload.add(new VPVeinTypeCaching());
		GregTech_API.sAfterGTPostload.add(() -> GT_Worldgenerator.registerOreGenCallback(new VPOreGenCallbackHandler() {
			@Override
			public void prospectPotentialNewVein(String oreMixName, World aWorld, int aX, int aZ) {
				VP.serverVeinCache.putVeinType(aWorld.provider.dimensionId, aX, aZ, VPVeinTypeCaching.getVeinType(oreMixName));
			}
		}));
	}
	
	public void fmlLifeCycleEvent(FMLServerAboutToStartEvent event) {

	}

	// register server commands in this event handler
	public void fmlLifeCycleEvent(FMLServerStartingEvent event) {
		final File worldDirectory = event.getServer().getEntityWorld().getSaveHandler().getWorldDirectory();
		if(VP.serverVeinCache.loadVeinCache(event.getServer().getEntityWorld()) == false || VPConfig.recacheVeins) {
			try {
				VPWorldAnalysis world = new VPWorldAnalysis(worldDirectory);
				world.cacheVeins();
				VP.serverVeinCache.saveVeinCache();
			} catch (IOException | DataFormatException e) {
				VP.info("Could not load world save files to build vein cache!");
				e.printStackTrace();
			}
		}
	}
	
	public void fmlLifeCycleEvent(FMLServerStartedEvent event) {
		
	}
	
	public void fmlLifeCycleEvent(FMLServerStoppingEvent event) {
		VP.serverVeinCache.saveVeinCache();
	}
	
	public void fmlLifeCycleEvent(FMLServerStoppedEvent event) {
		
	}
}
