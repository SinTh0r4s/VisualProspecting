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
    private final HashMap<Long, UndergroundFluid> undergroundFluids = new HashMap<>();
    private final HashSet<Long> changedOrNewOreChunks = new HashSet<>();
    private final HashSet<Long> changedOrNewUndergroundFluids = new HashSet<>();
    private boolean oreChunksNeedsSaving = false;
    private boolean undergroundFluidsNeedsSaving = false;
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

    public ByteBuffer saveUndergroundFluids() {
        if(undergroundFluidsNeedsSaving) {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(changedOrNewUndergroundFluids.size() * (Long.BYTES + Integer.BYTES * (1 + VP.undergroundFluidSizeChunkX * VP.undergroundFluidSizeChunkZ)));
            for (long key : changedOrNewUndergroundFluids) {
                byteBuffer.putLong(key);
                final UndergroundFluid undergroundFluid = undergroundFluids.get(key);
                byteBuffer.putInt(undergroundFluid.fluid.getID());
                for(int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
                    for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                        byteBuffer.putInt(undergroundFluid.chunks[offsetChunkX][offsetChunkZ]);
                    }
                }
            }
            undergroundFluidsNeedsSaving = false;
            changedOrNewUndergroundFluids.clear();
            byteBuffer.flip();
            return byteBuffer;
        }
        return null;
    }

    public void loadCache(ByteBuffer oreChunksBuffer, ByteBuffer undergroundFluidsBuffer) {
        if(oreChunksBuffer != null) {
            while (oreChunksBuffer.remaining() >= Long.BYTES + Short.BYTES) {
                final long key = oreChunksBuffer.getLong();
                final VeinType veinType = VeinTypeCaching.getVeinType(oreChunksBuffer.getShort());
                oreChunks.put(key, veinType);
            }
        }
        if(undergroundFluidsBuffer != null) {
            while (undergroundFluidsBuffer.remaining() >= Long.BYTES + Integer.BYTES * (1 + VP.undergroundFluidSizeChunkX * VP.undergroundFluidSizeChunkZ)) {
                final long key = undergroundFluidsBuffer.getLong();
                final Fluid fluid = FluidRegistry.getFluid(undergroundFluidsBuffer.getInt());
                final int[][] chunks = new int[VP.undergroundFluidSizeChunkX][VP.undergroundFluidSizeChunkZ];
                for(int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
                    for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                        chunks[offsetChunkX][offsetChunkZ] = undergroundFluidsBuffer.getInt();
                    }
                }
                undergroundFluids.put(key, new UndergroundFluid(fluid, chunks));
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

    private long getUndergroundFluidKey(int chunkX, int chunkZ) {
        return Utils.chunkCoordsToKey(Utils.mapToCornerUndergroundFluidChunkCoord(chunkX), Utils.mapToCornerUndergroundFluidChunkCoord(chunkZ));
    }

    public UpdateResult putUndergroundFluid(int chunkX, int chunkZ, final UndergroundFluid undergroundFluid) {
        final long key = getUndergroundFluidKey(chunkX, chunkZ);
        if(undergroundFluids.containsKey(key) == false) {
            changedOrNewUndergroundFluids.add(key);
            undergroundFluids.put(key, undergroundFluid);
            undergroundFluidsNeedsSaving = true;
            return UpdateResult.New;
        }
        else if(undergroundFluids.get(key).equals(undergroundFluid) == false) {
            changedOrNewUndergroundFluids.add(key);
            undergroundFluids.put(key, undergroundFluid);
            undergroundFluidsNeedsSaving = true;
            return UpdateResult.Updated;
        }
        return UpdateResult.AlreadyKnown;
    }

    public UndergroundFluid getUndergroundFluid(int chunkX, int chunkZ) {
        final long key = getUndergroundFluidKey(chunkX, chunkZ);
        return undergroundFluids.getOrDefault(key, UndergroundFluid.NOT_PROSPECTED);
    }
}
