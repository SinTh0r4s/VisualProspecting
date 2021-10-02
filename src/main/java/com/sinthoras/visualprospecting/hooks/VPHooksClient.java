package com.sinthoras.visualprospecting.hooks;

import api.visualprospecting.VPProspectingCallbackHandler;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPConfig;
import com.sinthoras.visualprospecting.VPMod;
import com.sinthoras.visualprospecting.client.VPProspector;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import gregtech.api.GregTech_API;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class VPHooksClient extends VPHooksShared {

	@Override
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) {
		super.fmlLifeCycleEvent(event);
	}
	
	@Override
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		super.fmlLifeCycleEvent(event);
	}
	
	@Override
	// postInit "Handle interaction with other mods, complete your setup based on this."
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event) {
		super.fmlLifeCycleEvent(event);
		GregTech_API.sAfterGTPostload.add(() -> GT_Block_Ores_Abstract.registerProspectingCallback(new VPProspectingCallbackHandler() {
			@Override
			public void prospectPotentialNewVein(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer) {
				// TODO: move checks into receiving method
				if(VPConfig.enableProspecting
						&& VPMod.proxy instanceof VPHooksClient
						&& Minecraft.getMinecraft().thePlayer == aPlayer) {
					final TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
					if (tTileEntity instanceof GT_TileEntity_Ores) {
						final short oreMeta = ((GT_TileEntity_Ores) tTileEntity).mMetaData;
						if (oreMeta < VP.gregTechSmallOreMinimumMeta)
							VPProspector.prospectPotentialNewVein(aWorld, aX, aY, aZ, oreMeta);
					}
				}
			}
		}));
	}
	@Override
	public void fmlLifeCycleEvent(FMLServerAboutToStartEvent event) {
		super.fmlLifeCycleEvent(event);
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLServerStartingEvent event) {
		super.fmlLifeCycleEvent(event);
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLServerStartedEvent event) {
		super.fmlLifeCycleEvent(event);
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLServerStoppingEvent event) {
		super.fmlLifeCycleEvent(event);
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLServerStoppedEvent event) {
		super.fmlLifeCycleEvent(event);
	}
}
