package com.sinthoras.visualprospecting.integration.voxelmap;

import java.util.TreeSet;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.hooks.ProspectingEvent;
import com.thevoxelbox.voxelmap.interfaces.AbstractVoxelMap;
import com.thevoxelbox.voxelmap.interfaces.IWaypointManager;
import com.thevoxelbox.voxelmap.util.Waypoint;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.GregTech_API;
import gregtech.api.items.GT_MetaGenerated_Tool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class EventHandler {
	
	@SubscribeEvent
	public void onVeinProspected(ProspectingEvent.OreVein event) {
		if(event.isCanceled()) {
			return;
		}
		
		OreVeinPosition pos = event.getPosition();
		IWaypointManager waypointManager = AbstractVoxelMap.getInstance().getWaypointManager();
		short[] color = GregTech_API.sGeneratedMaterials[pos.veinType.primaryOreMeta].getRGBA();
		TreeSet<Integer> dim = new TreeSet<>();
		dim.add(pos.dimensionId);
		
		waypointManager.addWaypoint(new Waypoint(
				StatCollector.translateToLocal(pos.veinType.name), // name
				pos.getBlockX(), // X
				pos.getBlockZ(), // Z
				getY(), // Y
				Config.vmEnableWaypointsByDefault, // enabled
				(float) color[0] / 255.0f, // red
				(float) color[1] / 255.0f, // green
				(float) color[2] / 255.0f, // blue
				"Pickaxe", // icon
				IWaypointManagerReflection.getCurrentSubworldDescriptor(waypointManager, false), // world
				dim)); // dimension
	}
	
	@SubscribeEvent
	public void onFluidProspected(ProspectingEvent.UndergroundFluid event) {
		if(event.isCanceled()) {
			return;
		}
		
		UndergroundFluidPosition pos = event.getPosition();
		IWaypointManager waypointManager = AbstractVoxelMap.getInstance().getWaypointManager();
		int x = Utils.coordChunkToBlock(pos.chunkX);
		int z = Utils.coordChunkToBlock(pos.chunkZ);
		int color = pos.fluid.getColor();
		TreeSet<Integer> dim = new TreeSet<>();
		dim.add(pos.dimensionId);
		
		waypointManager.addWaypoint(new Waypoint(
				pos.fluid.getLocalizedName(), // name
				x, // X
				z, // Z
				Minecraft.getMinecraft().theWorld.getHeightValue(x, z), // Y
				Config.vmEnableWaypointsByDefault, // enabled
				(float) (color >> 16 & 0xFF) / 255.0f, // red
				(float) (color >>  8 & 0xFF) / 255.0f, // green
				(float) (color       & 0xFF) / 255.0f, // blue
				"Science", // icon
				IWaypointManagerReflection.getCurrentSubworldDescriptor(waypointManager, false), // world
				dim)); // dimension
	}
	
	private static int getY() {
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		ItemStack heldItem = player.getHeldItem();
		if(heldItem == null) {
			return (int) player.posY;
		}
		if(heldItem.getUnlocalizedName().contains("gt.detrav.metatool.01")) {
			return 65;
		}
		return (int) player.posY;
	}

}
