package com.sinthoras.visualprospecting.gui.model.locations;

import com.sinthoras.visualprospecting.gui.model.waypoints.Waypoint;

public interface IWaypointAndLocationProvider extends ILocationProvider {

    Waypoint toWaypoint();

    boolean isActiveAsWaypoint();

    void onWaypointCleared();

    void onWaypointUpdated(Waypoint waypoint);
}
