package com.sinthoras.visualprospecting.hooks;

import api.visualprospecting.VPOreGenCallbackHandler;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPConfig;
import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.database.VPWorldIdHandler;
import com.sinthoras.visualprospecting.database.cachebuilder.VPWorldAnalysis;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import com.sinthoras.visualprospecting.network.VPProspectingNotification;
import com.sinthoras.visualprospecting.network.VPProspectingRequest;
import com.sinthoras.visualprospecting.network.VPWorldId;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import gregtech.api.GregTech_API;
import gregtech.common.GT_Worldgenerator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.io.IOException;
import java.util.zip.DataFormatException;


public class VPHooksShared {
	
	// preInit "Run before anything else. Read your config, create blocks, items, 
	// etc, and register them with the GameRegistry."
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) 	{
		VPConfig.syncronizeConfiguration(event.getSuggestedConfigurationFile());

		VP.network = NetworkRegistry.INSTANCE.newSimpleChannel(VPTags.MODID);
		int networkId = 0;
		VP.network.registerMessage(VPProspectingRequest.Handler.class, VPProspectingRequest.class, networkId++, Side.SERVER);
		VP.network.registerMessage(VPProspectingNotification.Handler.class, VPProspectingNotification.class, networkId++, Side.CLIENT);
		VP.network.registerMessage(VPWorldId.Handler.class, VPWorldId.class, networkId++, Side.CLIENT);
	}
	
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new VPHooksFML());
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
		final MinecraftServer minecraftServer = event.getServer();
		VPWorldIdHandler.load(minecraftServer.worldServers[0]);
		if(VP.serverVeinCache.loadVeinCache(VPWorldIdHandler.getWorldId()) == false || VPConfig.recacheVeins) {
			try {
				VPWorldAnalysis world = new VPWorldAnalysis(minecraftServer.getEntityWorld().getSaveHandler().getWorldDirectory());
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
