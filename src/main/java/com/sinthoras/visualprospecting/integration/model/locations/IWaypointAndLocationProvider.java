package com.sinthoras.visualprospecting.integration.model.locations;

import com.sinthoras.visualprospecting.integration.model.waypoints.Waypoint;

public interface IWaypointAndLocationProvider extends ILocationProvider {

    Waypoint toWaypoint();

    boolean isActiveAsWaypoint();

    void onWaypointCleared();

    void onWaypointUpdated(Waypoint waypoint);
}
