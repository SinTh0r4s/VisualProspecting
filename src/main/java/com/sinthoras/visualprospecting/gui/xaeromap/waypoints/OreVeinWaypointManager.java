package com.sinthoras.visualprospecting.gui.xaeromap.waypoints;

import com.sinthoras.visualprospecting.gui.model.layers.OreVeinLayerManager;
import com.sinthoras.visualprospecting.gui.model.waypoints.Waypoint;

public class OreVeinWaypointManager extends WaypointManager {

	public static OreVeinWaypointManager instance = new OreVeinWaypointManager();

	public OreVeinWaypointManager() {
		super(OreVeinLayerManager.instance, WaypointTypes.ORE_VEINS_WAYPOINT);
	}

	@Override
	protected String getSymbol(Waypoint waypoint) {
		return "!";
	}
}
