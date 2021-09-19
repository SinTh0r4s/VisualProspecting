package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;

import java.util.HashMap;

public class VPCacheDimension {

    private HashMap<Long, VPVeinType> oreChunks = new HashMap<>();

    public void putVeinType(int chunkX, int chunkZ, final VPVeinType veinType) {
        oreChunks.put(VPUtils.chunkCoordsToKey(VPUtils.mapToCenterOreChunkCoord(chunkX), VPUtils.mapToCenterOreChunkCoord(chunkZ)), veinType);
    }

    public VPVeinType getVeinType(int chunkX, int chunkZ) {
        return oreChunks.get(VPUtils.chunkCoordsToKey(VPUtils.mapToCenterOreChunkCoord(chunkX), VPUtils.mapToCenterOreChunkCoord(chunkZ)));
    }
}
