package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;

public class DimensionCache {

    public enum UpdateResult {
        AlreadyKnown,
        Updated,
        New
    }

    private final HashMap<Long, VeinType> oreChunks = new HashMap<>();
    private final HashMap<Long, OilField> oilFields = new HashMap<>();
    private final HashSet<Long> changedOrNewOreChunks = new HashSet<>();
    private final HashSet<Long> changedOrNewOilFields = new HashSet<>();
    private boolean oreChunksNeedsSaving = false;
    private boolean oilFieldsNeedsSaving = false;
    public final int dimensionId;

    public DimensionCache(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public ByteBuffer saveOreChunks() {
        if(oreChunksNeedsSaving) {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(changedOrNewOreChunks.size() * (Long.BYTES + Short.BYTES));
            for (long key : changedOrNewOreChunks) {
                byteBuffer.putLong(key);
                byteBuffer.putShort(VeinTypeCaching.getVeinTypeId(oreChunks.get(key)));
            }
            oreChunksNeedsSaving = false;
            changedOrNewOreChunks.clear();
            byteBuffer.flip();
            return byteBuffer;
        }
        return null;
    }

    public ByteBuffer saveOilFields() {
        if(oilFieldsNeedsSaving) {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(changedOrNewOilFields.size() * (Long.BYTES + Integer.BYTES * (1 + VP.oilFieldSizeChunkX * VP.oilFieldSizeChunkZ)));
            for (long key : changedOrNewOilFields) {
                byteBuffer.putLong(key);
                final OilField oilField = oilFields.get(key);
                byteBuffer.putInt(oilField.oil.getID());
                for(int offsetChunkX = 0; offsetChunkX < VP.oilFieldSizeChunkX; offsetChunkX++) {
                    for (int offsetChunkZ = 0; offsetChunkZ < VP.oilFieldSizeChunkZ; offsetChunkZ++) {
                        byteBuffer.putInt(oilField.chunks[offsetChunkX][offsetChunkZ]);
                    }
                }
            }
            oilFieldsNeedsSaving = false;
            changedOrNewOilFields.clear();
            byteBuffer.flip();
            return byteBuffer;
        }
        return null;
    }

    public void loadCache(ByteBuffer oreChunksBuffer, ByteBuffer oilFieldsBuffer) {
        if(oreChunksBuffer != null) {
            while (oreChunksBuffer.remaining() >= Long.BYTES + Short.BYTES) {
                final long key = oreChunksBuffer.getLong();
                final VeinType veinType = VeinTypeCaching.getVeinType(oreChunksBuffer.getShort());
                oreChunks.put(key, veinType);
            }
        }
        if(oilFieldsBuffer != null) {
            while (oilFieldsBuffer.remaining() >= Long.BYTES + Integer.BYTES * (1 + VP.oilFieldSizeChunkX * VP.oilFieldSizeChunkZ)) {
                final long key = oilFieldsBuffer.getLong();
                final Fluid oil = FluidRegistry.getFluid(oilFieldsBuffer.getInt());
                final int[][] chunks = new int[VP.oilFieldSizeChunkX][VP.oilFieldSizeChunkZ];
                for(int offsetChunkX = 0; offsetChunkX < VP.oilFieldSizeChunkX; offsetChunkX++) {
                    for (int offsetChunkZ = 0; offsetChunkZ < VP.oilFieldSizeChunkZ; offsetChunkZ++) {
                        chunks[offsetChunkX][offsetChunkZ] = oilFieldsBuffer.getInt();
                    }
                }
                oilFields.put(key, new OilField(oil, chunks));
            }
        }
    }

    private long getOreVeinKey(int chunkX, int chunkZ) {
        return Utils.chunkCoordsToKey(Utils.mapToCenterOreChunkCoord(chunkX), Utils.mapToCenterOreChunkCoord(chunkZ));
    }

    public UpdateResult putOreVein(int chunkX, int chunkZ, final VeinType veinType) {
        final long key = getOreVeinKey(chunkX, chunkZ);
        if(oreChunks.containsKey(key) == false || oreChunks.get(key) != veinType) {
            changedOrNewOreChunks.add(key);
            oreChunks.put(key, veinType);
            oreChunksNeedsSaving = true;
            return UpdateResult.New;
        }
        return UpdateResult.AlreadyKnown;
    }

    public VeinType getOreVein(int chunkX, int chunkZ) {
        final long key = getOreVeinKey(chunkX, chunkZ);
        return oreChunks.getOrDefault(key, VeinType.NO_VEIN);
    }

    private long getOilFieldKey(int chunkX, int chunkZ) {
        return Utils.chunkCoordsToKey(Utils.mapToCornerOilFieldChunkCoord(chunkX), Utils.mapToCornerOilFieldChunkCoord(chunkZ));
    }

    public UpdateResult putOilField(int chunkX, int chunkZ, final OilField oilField) {
        final long key = getOilFieldKey(chunkX, chunkZ);
        if(oilFields.containsKey(key) == false) {
            changedOrNewOilFields.add(key);
            oilFields.put(key, oilField);
            oilFieldsNeedsSaving = true;
            return UpdateResult.New;
        }
        else if(oilFields.get(key).equals(oilField) == false) {
            changedOrNewOilFields.add(key);
            oilFields.put(key, oilField);
            oilFieldsNeedsSaving = true;
            return UpdateResult.Updated;
        }
        return UpdateResult.AlreadyKnown;
    }

    public OilField getOilField(int chunkX, int chunkZ) {
        final long key = getOilFieldKey(chunkX, chunkZ);
        return oilFields.getOrDefault(key, OilField.NOT_PROSPECTED);
    }
}
