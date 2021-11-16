package com.sinthoras.visualprospecting.integration.model.waypoints;

import com.sinthoras.visualprospecting.integration.model.SupportedMods;
import com.sinthoras.visualprospecting.integration.model.layers.WaypointProviderManager;

public abstract class WaypointManager {

    public WaypointManager(WaypointProviderManager layerManager, SupportedMods map) {
        layerManager.registerWaypointManager(map, this);
    }

    public abstract void clearActiveWaypoint();

    public abstract void updateActiveWaypoint(Waypoint waypoint);
}
