package com.sinthoras.visualprospecting.mixins.voxelmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.thevoxelbox.voxelmap.interfaces.IWaypointManager;

@Mixin(IWaypointManager.class)
public interface IWaypointManagerInvoker {
	
	@Invoker(value = "if", remap = false)
	String getCurrentSubworldDescriptor(boolean var1);

}
