package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;

public class VPOreVeinPosition {
    public final int chunkX;
    public final int chunkZ;
    public final VPVeinType veinType;

    public VPOreVeinPosition(int chunkX, int chunkZ, VPVeinType veinType) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.veinType = veinType;
    }

    public int getBlockX() {
        return VPUtils.coordChunkToBlock(chunkX) + 8;
    }

    public int getBlockZ() {
        return VPUtils.coordChunkToBlock(chunkZ) + 8;
    }
}
