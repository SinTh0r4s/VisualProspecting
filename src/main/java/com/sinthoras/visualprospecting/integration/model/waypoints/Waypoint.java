package com.sinthoras.visualprospecting.integration.model.waypoints;

public class Waypoint {
    public final int blockX;
    public final int blockY;
    public final int blockZ;
    public final int dimensionId;
    public final String label;
    public final int color;

    public Waypoint(int blockX, int blockY, int blockZ, int dimensionId, String label, int color) {
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.dimensionId = dimensionId;
        this.label = label;
        this.color = color;
    }
}
