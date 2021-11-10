package com.sinthoras.visualprospecting.gui.tcnodetracker;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.sinthoras.visualprospecting.gui.model.SupportedMap;
import com.sinthoras.visualprospecting.gui.model.layers.ThaumcraftNodeLayerManager;
import com.sinthoras.visualprospecting.gui.model.waypoints.Waypoint;

public class NTNodeTrackerWaypointManager extends com.sinthoras.visualprospecting.gui.model.waypoints.WaypointManager {

    public static final NTNodeTrackerWaypointManager instance = new NTNodeTrackerWaypointManager();

    public NTNodeTrackerWaypointManager() {
        super(ThaumcraftNodeLayerManager.instance, SupportedMap.TCNodeTracker);
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
