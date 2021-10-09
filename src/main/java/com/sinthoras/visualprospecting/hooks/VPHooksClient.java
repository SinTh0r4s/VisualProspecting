package com.sinthoras.visualprospecting.hooks;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

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
