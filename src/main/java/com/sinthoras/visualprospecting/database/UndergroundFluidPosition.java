package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.Utils;

public class UndergroundFluidPosition {
    public final int chunkX;
    public final int chunkZ;
    public final UndergroundFluid undergroundFluid;

    public UndergroundFluidPosition(int chunkX, int chunkZ, UndergroundFluid undergroundFluid) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.undergroundFluid = undergroundFluid;
    }

    public int getBlockX() {
        return Utils.coordChunkToBlock(chunkX);
    }

    public int getBlockZ() {
        return Utils.coordChunkToBlock(chunkZ);
    }
}
