package com.sinthoras.visualprospecting.gui.journeymap.waypoints;

import com.sinthoras.visualprospecting.gui.model.SupportedMap;
import com.sinthoras.visualprospecting.gui.model.layers.WaypointProviderManager;
import com.sinthoras.visualprospecting.gui.model.waypoints.Waypoint;

import java.awt.*;

public class WaypointManager extends com.sinthoras.visualprospecting.gui.model.waypoints.WaypointManager {

    private journeymap.client.model.Waypoint jmWaypoint;

    public WaypointManager(WaypointProviderManager layerManager) {
        super(layerManager, SupportedMap.JourneyMap);
    }

    @Override
    public void clearActiveWaypoint() {
        jmWaypoint = null;
    }

    public boolean hasWaypoint() {
        return jmWaypoint != null;
    }

    public journeymap.client.model.Waypoint getJmWaypoint() {
        return jmWaypoint;
    }

    @Override
    public void updateActiveWaypoint(Waypoint waypoint) {
        if(hasWaypoint() == false
                || waypoint.blockX != jmWaypoint.getX()
                || waypoint.blockY != jmWaypoint.getY()
                || waypoint.blockZ != jmWaypoint.getZ()
                || jmWaypoint.getDimensions().contains(waypoint.dimensionId) == false) {
            jmWaypoint = new journeymap.client.model.Waypoint(
                    waypoint.label,
                    waypoint.blockX, waypoint.blockY, waypoint.blockZ,
                    new Color(waypoint.color),
                    journeymap.client.model.Waypoint.Type.Normal,
                    waypoint.dimensionId);
        }
    }
}
