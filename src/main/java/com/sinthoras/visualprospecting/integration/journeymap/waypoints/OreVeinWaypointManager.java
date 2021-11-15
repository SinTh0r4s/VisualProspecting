package com.sinthoras.visualprospecting.integration.journeymap.waypoints;

import com.sinthoras.visualprospecting.integration.model.layers.OreVeinLayerManager;

public class OreVeinWaypointManager extends WaypointManager {

    public static final OreVeinWaypointManager instance = new OreVeinWaypointManager();

    public OreVeinWaypointManager() {
        super(OreVeinLayerManager.instance);
    }
}
