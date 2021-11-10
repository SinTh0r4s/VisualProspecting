package com.sinthoras.visualprospecting.gui.journeymap.waypoints;

import com.sinthoras.visualprospecting.gui.model.layers.OreVeinLayerManager;

public class OreVeinWaypointManager extends WaypointManager {

    public static final OreVeinWaypointManager instance = new OreVeinWaypointManager();

    public OreVeinWaypointManager() {
        super(OreVeinLayerManager.instance);
    }
}
