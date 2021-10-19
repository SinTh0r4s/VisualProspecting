package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;

public class UndergroundFluidPosition {

    public static final int BYTES = (3 + 1 + VP.undergroundFluidSizeChunkX * VP.undergroundFluidSizeChunkZ) * Integer.BYTES;

    public final int dimensionId;
    public final int chunkX;
    public final int chunkZ;
    public final UndergroundFluid undergroundFluid;

    public UndergroundFluidPosition(int dimensionId, int chunkX, int chunkZ, UndergroundFluid undergroundFluid) {
        this.dimensionId = dimensionId;
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
