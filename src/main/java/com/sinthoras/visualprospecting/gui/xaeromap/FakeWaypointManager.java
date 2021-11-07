package com.sinthoras.visualprospecting.gui.xaeromap;

import com.sinthoras.visualprospecting.Tags;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.Hashtable;

public class FakeWaypointManager {
	public static Hashtable<Integer, Waypoint> waypointsTable = WaypointsManager.getCustomWaypoints(Tags.MODID);
	public static final int ORE_VEINS_WAYPOINT = 0;
	public static final int TC_NODES_WAYPOINT = 1;

	public static void addWaypoint(int index, int x, int y, int z, String name, String symbol, int color, int dimID) {
		waypointsTable.put(index, new WaypointWithDimension(x, y, z, name, symbol, color, dimID));
	}

	public static void removeWaypoint(int index) {
		waypointsTable.remove(index);
	}

	public static boolean hasWaypoint(int index) {
		return waypointsTable.containsKey(index);
	}

	public static Waypoint getWaypoint(int index) {
		return waypointsTable.get(index);
	}

	public static boolean isWaypointAtCoords(int index, int x, int y, int z) {
		if(!hasWaypoint(index))
			return false;
		Waypoint wp = getWaypoint(index);
		return wp.getX() == x && wp.getY() == y && wp.getZ() == z;
	}

	public static void toggleWaypoint(int index, int x, int y, int z, String name, String symbol, int color, int dimID) {
		if(isWaypointAtCoords(index, x, y, z)) {
			removeWaypoint(index);
		}
		else {
			addWaypoint(index, x, y, z, name, symbol, color, dimID);
		}
	}

	public static void notifyDimension(int dimID) {
		for(Waypoint wp : waypointsTable.values()) {
			if(wp instanceof WaypointWithDimension) {
				((WaypointWithDimension) wp).notifyDimension(dimID);
			}
		}
	}
}
