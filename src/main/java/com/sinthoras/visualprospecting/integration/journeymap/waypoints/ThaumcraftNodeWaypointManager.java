package com.sinthoras.visualprospecting.integration.journeymap.waypoints;

import com.sinthoras.visualprospecting.integration.model.layers.ThaumcraftNodeLayerManager;

public class ThaumcraftNodeWaypointManager extends WaypointManager {

    public static final ThaumcraftNodeWaypointManager instance = new ThaumcraftNodeWaypointManager();

    public ThaumcraftNodeWaypointManager() {
        super(ThaumcraftNodeLayerManager.instance);
    }
}
