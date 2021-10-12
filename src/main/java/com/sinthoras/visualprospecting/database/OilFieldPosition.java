package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.Utils;

public class OilFieldPosition {
    public final int chunkX;
    public final int chunkZ;
    public final OilField oilField;

    public OilFieldPosition(int chunkX, int chunkZ, OilField oilField) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.oilField = oilField;
    }

    public int getBlockX() {
        return Utils.coordChunkToBlock(chunkX);
    }

    public int getBlockZ() {
        return Utils.coordChunkToBlock(chunkZ);
    }
}
