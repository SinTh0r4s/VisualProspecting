package com.sinthoras.visualprospecting.gui.journeymap.waypoints;

import com.sinthoras.visualprospecting.gui.model.layers.ThaumcraftNodeLayerManager;

public class ThaumcraftNodeWaypointManager extends WaypointManager {

    public static final ThaumcraftNodeWaypointManager instance = new ThaumcraftNodeWaypointManager();

    public ThaumcraftNodeWaypointManager() {
        super(ThaumcraftNodeLayerManager.instance);
    }
}
