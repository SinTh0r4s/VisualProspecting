package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPUtils;

public class VPOilFieldPosition {
    public final int chunkX;
    public final int chunkZ;
    public final VPOilField oilField;

    public VPOilFieldPosition(int chunkX, int chunkZ, VPOilField oilField) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.oilField = oilField;
    }

    public int getBlockX() {
        return VPUtils.coordChunkToBlock(chunkX);
    }

    public int getBlockZ() {
        return VPUtils.coordChunkToBlock(chunkZ);
    }
}
