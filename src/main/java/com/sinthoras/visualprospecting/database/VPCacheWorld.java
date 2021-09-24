package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class VPCacheWorld {
    private static HashMap<Integer, VPCacheDimension> dimensions = new HashMap<>();
    private static boolean needsSaving = false;
    private static File saveDirectory;

    public static boolean loadVeinCache(File worldDirectory) {
        saveDirectory = VPUtils.getVPWorldStorageDirectory(worldDirectory);
        final HashMap<Integer, ByteBuffer> dimensionBuffers = VPUtils.getDIMFiles(saveDirectory);
        if(dimensionBuffers.size() == 0)
            return false;

        for(int dimensionId : dimensionBuffers.keySet()) {
            final VPCacheDimension dimension = new VPCacheDimension(dimensionId);
            dimension.loadVeinCache(dimensionBuffers.get(dimensionId));
            dimensions.put(dimensionId, dimension);
        }
        return true;
    }

    public static void saveVeinCache() {
        if(needsSaving) {
            for (VPCacheDimension dimension : dimensions.values()) {
                final ByteBuffer byteBuffer = dimension.saveVeinCache();
                if (byteBuffer != null)
                    VPUtils.appendToFile(new File(saveDirectory.toPath() + "/DIM" + dimension.dimensionId), byteBuffer);
            }
            needsSaving = false;
        }
    }

    public static void reset() {
        dimensions = new HashMap<>();
        needsSaving = false;
    }

    public static void putVeinType(int dimensionId, int chunkX, int chunkZ, final VPVeinType veinType) {
        VPCacheDimension dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            dimension = new VPCacheDimension(dimensionId);
            dimensions.put(dimensionId, dimension);
        }
        needsSaving |= dimension.putVeinType(chunkX, chunkZ, veinType);
    }

    public static VPVeinType getVeinType(int dimensionId, int chunkX, int chunkZ) {
        VPCacheDimension dimension = dimensions.get(dimensionId);
        if(dimension == null)
            return VPVeinType.NO_VEIN;
        return dimension.getVeinType(chunkX, chunkZ);
    }
}
