package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class WorldCache {

    private HashMap<Integer, DimensionCache> dimensions = new HashMap<>();
    private boolean needsSaving = false;
    private File oreVeinCacheDirectory;
    private File oilCacheDirectory;
    private String worldId = "";

    protected abstract File getStorageDirectory();

    public boolean loadVeinCache(String worldId) {
        if(this.worldId.equals(worldId)) {
            return true;
        }
        this.worldId = worldId;
        final File worldCacheDirectory = new File(getStorageDirectory(), worldId);
        oreVeinCacheDirectory = new File(worldCacheDirectory, Tags.OREVEIN_DIR);
        oilCacheDirectory = new File(worldCacheDirectory, Tags.OILFIELD_DIR);
        oreVeinCacheDirectory.mkdirs();
        oilCacheDirectory.mkdirs();
        final HashMap<Integer, ByteBuffer> oreVeinDimensionBuffers = Utils.getDIMFiles(oreVeinCacheDirectory);
        final HashMap<Integer, ByteBuffer> oilFieldDimensionBuffers = Utils.getDIMFiles(oilCacheDirectory);
        final Set<Integer> dimensionsIds = new HashSet<>();
        dimensionsIds.addAll(oreVeinDimensionBuffers.keySet());
        dimensionsIds.addAll(oilFieldDimensionBuffers.keySet());
        if(dimensionsIds.isEmpty()) {
            return false;
        }

        dimensions.clear();
        for(int dimensionId : dimensionsIds) {
            final DimensionCache dimension = new DimensionCache(dimensionId);
            dimension.loadCache(oreVeinDimensionBuffers.get(dimensionId), oilFieldDimensionBuffers.get(dimensionId));
            dimensions.put(dimensionId, dimension);
        }
        return true;
    }

    public void saveVeinCache() {
        if(needsSaving) {
            for (DimensionCache dimension : dimensions.values()) {
                final ByteBuffer oreVeinBuffer = dimension.saveOreChunks();
                if (oreVeinBuffer != null) {
                    Utils.appendToFile(new File(oreVeinCacheDirectory.toPath() + "/DIM" + dimension.dimensionId), oreVeinBuffer);
                }
                final ByteBuffer oilFieldBuffer = dimension.saveOilFields();
                if(oilFieldBuffer != null) {
                    Utils.appendToFile(new File(oilCacheDirectory.toPath() + "/DIM" + dimension.dimensionId), oilFieldBuffer);
                }
            }
            needsSaving = false;
        }
    }

    public void reset() {
        dimensions = new HashMap<>();
        needsSaving = false;
    }

    private DimensionCache.UpdateResult updateSaveFlag(DimensionCache.UpdateResult updateResult) {
        needsSaving |= updateResult != DimensionCache.UpdateResult.AlreadyKnown;
        return updateResult;
    }

    protected DimensionCache.UpdateResult putOreVein(int dimensionId, int chunkX, int chunkZ, final VeinType veinType) {
        DimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            dimension = new DimensionCache(dimensionId);
            dimensions.put(dimensionId, dimension);
        }
        return updateSaveFlag(dimension.putOreVein(chunkX, chunkZ, veinType));
    }

    public VeinType getOreVein(int dimensionId, int chunkX, int chunkZ) {
        DimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            return VeinType.NO_VEIN;
        }
        return dimension.getOreVein(chunkX, chunkZ);
    }

    protected DimensionCache.UpdateResult putOilField(int dimensionId, int chunkX, int chunkZ, final OilField oilField) {
        DimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            dimension = new DimensionCache(dimensionId);
            dimensions.put(dimensionId, dimension);
        }
        return updateSaveFlag(dimension.putOilField(chunkX, chunkZ, oilField));
    }

    public OilField getOilField(int dimensionId, int chunkX, int chunkZ) {
        DimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            return OilField.NOT_PROSPECTED;
        }
        return dimension.getOilField(chunkX, chunkZ);
    }
}
