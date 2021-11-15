package com.sinthoras.visualprospecting.integration.xaerominimap.waypoints;

import com.sinthoras.visualprospecting.integration.model.layers.ThaumcraftNodeLayerManager;
import com.sinthoras.visualprospecting.integration.model.waypoints.Waypoint;

public class ThaumcraftNodeWaypointManager extends WaypointManager {

    public static ThaumcraftNodeWaypointManager instance = new ThaumcraftNodeWaypointManager();

    public ThaumcraftNodeWaypointManager() {
        super(ThaumcraftNodeLayerManager.instance, WaypointType.TC_NODES_WAYPOINT);
    }

    @Override
    protected String getSymbol(Waypoint waypoint) {
        return "@";
    }
}
