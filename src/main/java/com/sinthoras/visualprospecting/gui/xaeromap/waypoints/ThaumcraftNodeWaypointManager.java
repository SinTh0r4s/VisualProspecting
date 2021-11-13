package com.sinthoras.visualprospecting.gui.xaeromap.waypoints;

import com.sinthoras.visualprospecting.gui.model.layers.ThaumcraftNodeLayerManager;
import com.sinthoras.visualprospecting.gui.model.waypoints.Waypoint;

public class ThaumcraftNodeWaypointManager extends WaypointManager {

	public static ThaumcraftNodeWaypointManager instance = new ThaumcraftNodeWaypointManager();

	public ThaumcraftNodeWaypointManager() {
		super(ThaumcraftNodeLayerManager.instance, WaypointTypes.TC_NODES_WAYPOINT);
	}

	@Override
	protected String getSymbol(Waypoint waypoint) {
		return "@";
	}
}
