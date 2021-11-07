package com.sinthoras.visualprospecting.gui.xaeromap;

import xaero.common.minimap.waypoints.Waypoint;

public class WaypointWithDimension extends Waypoint {

	private final int dimID;
	private int currentDim;

	public WaypointWithDimension(int x, int y, int z, String name, String symbol, int color, int dimID) {
		super(x, y, z, name, symbol, color);
		this.dimID = dimID;
		this.currentDim = dimID;
	}

	public void notifyDimension(int newDimID) {
		currentDim = newDimID;
	}

	@Override
	public boolean isDisabled() {
		return super.isDisabled() || currentDim != dimID;
	}
}
