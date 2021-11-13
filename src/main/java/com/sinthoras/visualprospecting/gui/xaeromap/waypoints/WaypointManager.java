package com.sinthoras.visualprospecting.gui.xaeromap.waypoints;

import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.gui.model.SupportedMods;
import com.sinthoras.visualprospecting.gui.model.layers.WaypointProviderManager;
import com.sinthoras.visualprospecting.gui.model.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.Hashtable;

public class WaypointManager extends com.sinthoras.visualprospecting.gui.model.waypoints.WaypointManager {

	private static final Hashtable<Integer, xaero.common.minimap.waypoints.Waypoint> xWaypointTable = WaypointsManager.getCustomWaypoints(Tags.MODID);

	private WaypointWithDimension xWaypoint;
	private final WaypointTypes id;

	public WaypointManager(WaypointProviderManager layerManager, WaypointTypes id) {
		super(layerManager, SupportedMods.XaeroMap);
		this.id = id;
	}

	@Override
	public void clearActiveWaypoint() {
		xWaypoint = null;
		xWaypointTable.remove(id.ordinal());
	}

	public boolean hasWaypoint() {
		return xWaypoint != null;
	}

	public WaypointWithDimension getXWaypoint() {
		return xWaypoint;
	}

	@Override
	public void updateActiveWaypoint(Waypoint waypoint) {
		if (!hasWaypoint() || waypoint.blockX != xWaypoint.getX() || waypoint.blockY != xWaypoint.getY() ||
				waypoint.blockZ != xWaypoint.getZ() || waypoint.dimensionId != xWaypoint.getDimID()) {
			xWaypoint = new WaypointWithDimension(waypoint.blockX, waypoint.blockY, waypoint.blockZ, waypoint.label, getSymbol(waypoint), 15, waypoint.dimensionId);
			xWaypointTable.put(id.ordinal(), xWaypoint);
		}
	}

	protected String getSymbol(Waypoint waypoint) {
		return waypoint.label.substring(0, 1);
	}
}
