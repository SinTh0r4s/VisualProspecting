package com.sinthoras.visualprospecting.integration.tcnodetracker;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.sinthoras.visualprospecting.integration.model.SupportedMods;
import com.sinthoras.visualprospecting.integration.model.layers.ThaumcraftNodeLayerManager;
import com.sinthoras.visualprospecting.integration.model.waypoints.Waypoint;

public class NTNodeTrackerWaypointManager extends com.sinthoras.visualprospecting.integration.model.waypoints.WaypointManager {

    public NTNodeTrackerWaypointManager() {
        super(ThaumcraftNodeLayerManager.instance, SupportedMods.TCNodeTracker);
    }

    @Override
    public void clearActiveWaypoint() {
        TCNodeTracker.yMarker = -1;
    }

    @Override
    public void updateActiveWaypoint(Waypoint waypoint) {
        TCNodeTracker.xMarker = waypoint.blockX;
        TCNodeTracker.yMarker = waypoint.blockY;
        TCNodeTracker.zMarker = waypoint.blockZ;
    }
}
