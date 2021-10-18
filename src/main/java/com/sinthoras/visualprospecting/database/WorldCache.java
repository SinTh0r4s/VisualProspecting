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
    protected File oreVeinCacheDirectory;
    protected File undergroundFluidCacheDirectory;
    private boolean isLoaded = false;

    protected abstract File getStorageDirectory();

    public boolean loadVeinCache(String worldId) {
        if(isLoaded ) {
            return true;
        }
        isLoaded = true;
        final File worldCacheDirectory = new File(getStorageDirectory(), worldId);
        oreVeinCacheDirectory = new File(worldCacheDirectory, Tags.OREVEIN_DIR);
        undergroundFluidCacheDirectory = new File(worldCacheDirectory, Tags.UNDERGROUNDFLUID_DIR);
        oreVeinCacheDirectory.mkdirs();
        undergroundFluidCacheDirectory.mkdirs();
        final HashMap<Integer, ByteBuffer> oreVeinDimensionBuffers = Utils.getDIMFiles(oreVeinCacheDirectory);
        final HashMap<Integer, ByteBuffer> undergroundFluidDimensionBuffers = Utils.getDIMFiles(undergroundFluidCacheDirectory);
        final Set<Integer> dimensionsIds = new HashSet<>();
        dimensionsIds.addAll(oreVeinDimensionBuffers.keySet());
        dimensionsIds.addAll(undergroundFluidDimensionBuffers.keySet());
        if(dimensionsIds.isEmpty()) {
            return false;
        }

        for(int dimensionId : dimensionsIds) {
            final DimensionCache dimension = new DimensionCache(dimensionId);
            dimension.loadCache(oreVeinDimensionBuffers.get(dimensionId), undergroundFluidDimensionBuffers.get(dimensionId));
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
                final ByteBuffer undergroundFluidBuffer = dimension.saveUndergroundFluids();
                if(undergroundFluidBuffer != null) {
                    Utils.appendToFile(new File(undergroundFluidCacheDirectory.toPath() + "/DIM" + dimension.dimensionId), undergroundFluidBuffer);
                }
            }
            needsSaving = false;
        }
    }

    public void reset() {
        dimensions = new HashMap<>();
        needsSaving = false;
        isLoaded = false;
    }

    private DimensionCache.UpdateResult updateSaveFlag(DimensionCache.UpdateResult updateResult) {
        needsSaving |= updateResult != DimensionCache.UpdateResult.AlreadyKnown;
        return updateResult;
    }

    protected DimensionCache.UpdateResult putOreVein(final OreVeinPosition oreVeinPosition) {
        DimensionCache dimension = dimensions.get(oreVeinPosition.dimensionId);
        if(dimension == null) {
            dimension = new DimensionCache(oreVeinPosition.dimensionId);
            dimensions.put(oreVeinPosition.dimensionId, dimension);
        }
        return updateSaveFlag(dimension.putOreVein(oreVeinPosition));
    }

    protected void toggleOreVein(int dimensionId, int chunkX, int chunkZ) {
        DimensionCache dimension = dimensions.get(dimensionId);
        if(dimension != null) {
            dimension.toggleOreVein(chunkX, chunkZ);
        }
        needsSaving = true;
    }

    public OreVeinPosition getOreVein(int dimensionId, int chunkX, int chunkZ) {
        DimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            return new OreVeinPosition(dimensionId, chunkX, chunkZ, VeinType.NO_VEIN, true);
        }
        return dimension.getOreVein(chunkX, chunkZ);
    }

    protected DimensionCache.UpdateResult putUndergroundFluids(int dimensionId, int chunkX, int chunkZ, final UndergroundFluid undergroundFluid) {
        DimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            dimension = new DimensionCache(dimensionId);
            dimensions.put(dimensionId, dimension);
        }
        return updateSaveFlag(dimension.putUndergroundFluid(chunkX, chunkZ, undergroundFluid));
    }

    public UndergroundFluid getUndergroundFluid(int dimensionId, int chunkX, int chunkZ) {
        DimensionCache dimension = dimensions.get(dimensionId);
        if(dimension == null) {
            return UndergroundFluid.NOT_PROSPECTED;
        }
        return dimension.getUndergroundFluid(chunkX, chunkZ);
    }
}
