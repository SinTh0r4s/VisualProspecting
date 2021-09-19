package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;

import java.io.File;
import java.util.HashMap;

public class VPCacheWorld {
    private static HashMap<Integer, VPCacheDimension> dimensions = new HashMap<>();

    public static boolean loadVeinCache(File worldDirectory) {
        // TODO
        return true;
    }

    public static void saveVeinCache(File worldDirectory) {
        // TODO
    }

    public static void reset() {
        dimensions = new HashMap<>();
    }

    public static void putVeinType(int dimensionId, int chunkX, int chunkZ, final VPVeinType veinType) {
        VPCacheDimension dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            dimension = new VPCacheDimension();
            dimensions.put(dimensionId, dimension);
        }
        dimension.putVeinType(chunkX, chunkZ, veinType);
    }

    public static VPVeinType getVeinType(int dimensionId, int chunkX, int chunkZ) {
        VPCacheDimension dimension = dimensions.get(dimensionId);
        if(dimension == null)
            return VPVeinType.NO_VEIN;
        return dimension.getVeinType(chunkX, chunkZ);
    }
}
