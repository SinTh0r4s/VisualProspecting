package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class VPWorldCache {

    private HashMap<Integer, VPDimensionCache> dimensions = new HashMap<>();
    private boolean needsSaving = false;
    private File oreVeinCacheDirectory;
    private File oilCacheDirectory;
    private String worldId = "";

    protected abstract File getStorageDirectory();

    public boolean loadVeinCache(String worldId) {
        if(this.worldId.equals(worldId))
            return true;
        this.worldId = worldId;
        final File worldCacheDirectory = new File(getStorageDirectory(), worldId);
        oreVeinCacheDirectory = new File(worldCacheDirectory, VPTags.OREVEIN_DIR);
        oilCacheDirectory = new File(worldCacheDirectory, VPTags.OILFIELD_DIR);
        oreVeinCacheDirectory.mkdirs();
        oilCacheDirectory.mkdirs();
        final HashMap<Integer, ByteBuffer> oreVeinDimensionBuffers = VPUtils.getDIMFiles(oreVeinCacheDirectory);
        final HashMap<Integer, ByteBuffer> oilFieldDimensionBuffers = VPUtils.getDIMFiles(oilCacheDirectory);
        final Set<Integer> dimensionsIds = new HashSet<>();
        dimensionsIds.addAll(oreVeinDimensionBuffers.keySet());
        dimensionsIds.addAll(oilFieldDimensionBuffers.keySet());
        if(dimensionsIds.isEmpty())
            return false;

        dimensions.clear();
        for(int dimensionId : dimensionsIds) {
            final VPDimensionCache dimension = new VPDimensionCache(dimensionId);
            dimension.loadCache(oreVeinDimensionBuffers.get(dimensionId), oilFieldDimensionBuffers.get(dimensionId));
            dimensions.put(dimensionId, dimension);
        }
        return true;
    }

    public void saveVeinCache() {
        if(needsSaving) {
            for (VPDimensionCache dimension : dimensions.values()) {
                final ByteBuffer oreVeinBuffer = dimension.saveOreChunks();
                if (oreVeinBuffer != null) {
                    VPUtils.appendToFile(new File(oreVeinCacheDirectory.toPath() + "/DIM" + dimension.dimensionId), oreVeinBuffer);
                }
                final ByteBuffer oilFieldBuffer = dimension.saveOilFields();
                if(oilFieldBuffer != null) {
                    VPUtils.appendToFile(new File(oilCacheDirectory.toPath() + "/DIM" + dimension.dimensionId), oilFieldBuffer);
                }
            }
            needsSaving = false;
        }
    }

    public void reset() {
        dimensions = new HashMap<>();
        needsSaving = false;
    }

    private VPDimensionCache.UpdateResult updateSaveFlag(VPDimensionCache.UpdateResult updateResult) {
        needsSaving |= updateResult != VPDimensionCache.UpdateResult.AlreadyKnown;
        return updateResult;
    }

    protected VPDimensionCache.UpdateResult putOreVein(int dimensionId, int chunkX, int chunkZ, final VPVeinType veinType) {
        VPDimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            dimension = new VPDimensionCache(dimensionId);
            dimensions.put(dimensionId, dimension);
        }
        return updateSaveFlag(dimension.putOreVein(chunkX, chunkZ, veinType));
    }

    public VPVeinType getOreVein(int dimensionId, int chunkX, int chunkZ) {
        VPDimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null)
            return VPVeinType.NO_VEIN;
        return dimension.getOreVein(chunkX, chunkZ);
    }

    protected VPDimensionCache.UpdateResult putOilField(int dimensionId, int chunkX, int chunkZ, final VPOilField oilField) {
        VPDimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            dimension = new VPDimensionCache(dimensionId);
            dimensions.put(dimensionId, dimension);
        }
        return updateSaveFlag(dimension.putOilField(chunkX, chunkZ, oilField));
    }

    public VPOilField getOilField(int dimensionId, int chunkX, int chunkZ) {
        VPDimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null)
            return VPOilField.NOT_PROSPECTED;
        return dimension.getOilField(chunkX, chunkZ);
    }
}
