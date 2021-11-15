package com.sinthoras.visualprospecting.integration.xaerominimap.waypoints;

import com.sinthoras.visualprospecting.integration.model.layers.OreVeinLayerManager;
import com.sinthoras.visualprospecting.integration.model.waypoints.Waypoint;

public class OreVeinWaypointManager extends WaypointManager {

    public static OreVeinWaypointManager instance = new OreVeinWaypointManager();

    public OreVeinWaypointManager() {
        super(OreVeinLayerManager.instance, WaypointType.ORE_VEINS_WAYPOINT);
    }

    @Override
    protected String getSymbol(Waypoint waypoint) {
        return "!";
    }
}
