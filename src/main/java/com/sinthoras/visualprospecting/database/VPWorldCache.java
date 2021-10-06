package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class VPWorldCache {
    private HashMap<Integer, VPDimensionCache> dimensions = new HashMap<>();
    private boolean needsSaving = false;
    private File worldCacheDirectory;

    public boolean loadVeinCache(File directory) {
        worldCacheDirectory = directory;
        worldCacheDirectory.mkdirs();
        final HashMap<Integer, ByteBuffer> dimensionBuffers = VPUtils.getDIMFiles(worldCacheDirectory);
        if(dimensionBuffers.size() == 0)
            return false;

        for(int dimensionId : dimensionBuffers.keySet()) {
            final VPDimensionCache dimension = new VPDimensionCache(dimensionId);
            dimension.loadVeinCache(dimensionBuffers.get(dimensionId));
            dimensions.put(dimensionId, dimension);
        }
        return true;
    }

    public void saveVeinCache() {
        if(needsSaving) {
            for (VPDimensionCache dimension : dimensions.values()) {
                final ByteBuffer byteBuffer = dimension.saveVeinCache();
                if (byteBuffer != null)
                    VPUtils.appendToFile(new File(worldCacheDirectory.toPath() + "/DIM" + dimension.dimensionId), byteBuffer);
            }
            needsSaving = false;
        }
    }

    public void reset() {
        dimensions = new HashMap<>();
        needsSaving = false;
    }

    public void putVeinType(int dimensionId, int chunkX, int chunkZ, final VPVeinType veinType) {
        VPDimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            dimension = new VPDimensionCache(dimensionId);
            dimensions.put(dimensionId, dimension);
        }
        needsSaving |= dimension.putVeinType(chunkX, chunkZ, veinType);
    }

    public VPVeinType getVeinType(int dimensionId, int chunkX, int chunkZ) {
        VPDimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null)
            return VPVeinType.NO_VEIN;
        return dimension.getVeinType(chunkX, chunkZ);
    }
}
