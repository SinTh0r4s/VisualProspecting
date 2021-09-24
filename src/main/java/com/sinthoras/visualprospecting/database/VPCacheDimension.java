package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;

public class VPCacheDimension {

    private HashMap<Long, VPVeinType> oreChunks = new HashMap<>();
    private HashSet<Long> changedOrNew = new HashSet<>();
    private boolean needsSaving = false;
    public final int dimensionId;

    public VPCacheDimension(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public ByteBuffer saveVeinCache() {
        if(needsSaving) {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(changedOrNew.size() * 10);
            for (long key : changedOrNew) {
                byteBuffer.putLong(key);
                byteBuffer.putShort(VPVeinTypeCaching.getVeinTypeId(oreChunks.get(key)));
            }
            needsSaving = false;
            return byteBuffer;
        }
        return null;
    }

    public void loadVeinCache(ByteBuffer byteBuffer) {
        while(byteBuffer.remaining() >= 10) {
            final long key = byteBuffer.getLong();
            final VPVeinType veinType = VPVeinTypeCaching.getVeinType(byteBuffer.getShort());
            oreChunks.put(key, veinType);
        }
    }

    public boolean putVeinType(int chunkX, int chunkZ, final VPVeinType veinType) {
        final long key = VPUtils.chunkCoordsToKey(VPUtils.mapToCenterOreChunkCoord(chunkX), VPUtils.mapToCenterOreChunkCoord(chunkZ));
        if(oreChunks.containsKey(key) == false || oreChunks.get(key) != veinType) {
            changedOrNew.add(key);
            oreChunks.put(key, veinType);
            needsSaving = true;
            return true;
        }
        return false;
    }

    public VPVeinType getVeinType(int chunkX, int chunkZ) {
        final long key = VPUtils.chunkCoordsToKey(VPUtils.mapToCenterOreChunkCoord(chunkX), VPUtils.mapToCenterOreChunkCoord(chunkZ));
        return oreChunks.get(key);
    }
}
