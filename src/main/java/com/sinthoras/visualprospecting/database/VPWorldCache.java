package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class VPWorldCache {

    private HashMap<Integer, VPDimensionCache> dimensions = new HashMap<>();
    private boolean needsSaving = false;
    private File worldCacheDirectory;

    protected abstract File getStorageDirectory();

    public boolean loadVeinCache(String worldId) {
        worldCacheDirectory = new File(getStorageDirectory(), worldId);
        worldCacheDirectory.mkdirs();
        final HashMap<Integer, ByteBuffer> dimensionBuffers = VPUtils.getDIMFiles(worldCacheDirectory);
        if(dimensionBuffers.isEmpty())
            return false;

        for(int dimensionId : dimensionBuffers.keySet()) {
            final VPDimensionCache dimension = dimensions.containsKey(dimensionId) ? dimensions.get(dimensionId) : new VPDimensionCache(dimensionId);
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
        final boolean updatedDimensionCache = dimension.putVeinType(chunkX, chunkZ, veinType);
        needsSaving |= updatedDimensionCache;
        if(updatedDimensionCache) {
            onNewVein(veinType);
        }
    }

    public VPVeinType getVeinType(int dimensionId, int chunkX, int chunkZ) {
        VPDimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null)
            return VPVeinType.NO_VEIN;
        return dimension.getVeinType(chunkX, chunkZ);
    }

    protected void onNewVein(VPVeinType veinType) {

    }
}
