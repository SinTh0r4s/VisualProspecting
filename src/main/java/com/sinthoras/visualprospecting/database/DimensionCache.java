package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;

public class DimensionCache implements Serializable {

    public enum UpdateResult {
        AlreadyKnown,
        Updated,
        New
    }

    private final Map<Long, OreVeinPosition> oreChunks = new HashMap<>();
    private final Map<Long, UndergroundFluidPosition> undergroundFluids = new HashMap<>();
    private final transient Set<Long> changedOrNewOreChunks = new HashSet<>();
    private final transient Set<Long> changedOrNewUndergroundFluids = new HashSet<>();
    private transient boolean oreChunksNeedsSaving = false;
    private transient boolean undergroundFluidsNeedsSaving = false;
    public final int dimensionId;

    public DimensionCache(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public ByteBuffer saveOreChunks() {
        if(oreChunksNeedsSaving) {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(changedOrNewOreChunks.size() * (Long.BYTES + Short.BYTES));
            for (long key : changedOrNewOreChunks) {
                byteBuffer.putLong(key);
                final OreVeinPosition oreVeinPosition = oreChunks.get(key);
                short veinTypeId = VeinTypeCaching.getVeinTypeId(oreVeinPosition.veinType);
                if(oreVeinPosition.isDepleted()) {
                    veinTypeId |= 0x8000;
                }
                byteBuffer.putShort(veinTypeId);
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
                final UndergroundFluidPosition undergroundFluid = undergroundFluids.get(key);
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
                final int chunkX = (int)(key >> 32);
                final int chunkZ = (int)key;
                final short veinTypeId = oreChunksBuffer.getShort();
                final boolean depleted = (veinTypeId & 0x8000) > 0;
                final VeinType veinType = VeinTypeCaching.getVeinType((short)(veinTypeId & 0x7FFF));
                oreChunks.put(key, new OreVeinPosition(dimensionId, chunkX, chunkZ, veinType, depleted));
            }
        }
        if(undergroundFluidsBuffer != null) {
            while (undergroundFluidsBuffer.remaining() >= Long.BYTES + Integer.BYTES * (1 + VP.undergroundFluidSizeChunkX * VP.undergroundFluidSizeChunkZ)) {
                final long key = undergroundFluidsBuffer.getLong();
                final int chunkX = (int)(key >> 32);
                final int chunkZ = (int)key;
                final Fluid fluid = FluidRegistry.getFluid(undergroundFluidsBuffer.getInt());
                final int[][] chunks = new int[VP.undergroundFluidSizeChunkX][VP.undergroundFluidSizeChunkZ];
                for(int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
                    for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                        chunks[offsetChunkX][offsetChunkZ] = undergroundFluidsBuffer.getInt();
                    }
                }
                undergroundFluids.put(key, new UndergroundFluidPosition(dimensionId, chunkX, chunkZ, fluid, chunks));
            }
        }
    }

    private long getOreVeinKey(int chunkX, int chunkZ) {
        return Utils.chunkCoordsToKey(Utils.mapToCenterOreChunkCoord(chunkX), Utils.mapToCenterOreChunkCoord(chunkZ));
    }

    public UpdateResult putOreVein(final OreVeinPosition oreVeinPosition) {
        final long key = getOreVeinKey(oreVeinPosition.chunkX, oreVeinPosition.chunkZ);
        if(oreChunks.containsKey(key) == false) {
            oreChunks.put(key, oreVeinPosition);
            changedOrNewOreChunks.add(key);
            oreChunksNeedsSaving = true;
            return UpdateResult.New;
        }
        final OreVeinPosition storedOreVeinPosition = oreChunks.get(key);
        if(storedOreVeinPosition.veinType != oreVeinPosition.veinType) {
            oreChunks.put(key, oreVeinPosition.joinDepletedState(storedOreVeinPosition));
            changedOrNewOreChunks.add(key);
            oreChunksNeedsSaving = true;
            return UpdateResult.New;
        }
        return UpdateResult.AlreadyKnown;
    }

    public void toggleOreVein(int chunkX, int chunkZ) {
        final long key = getOreVeinKey(chunkX, chunkZ);
        if(oreChunks.containsKey(key)) {
            oreChunks.get(key).toggleDepleted();
        }
        changedOrNewOreChunks.add(key);
        oreChunksNeedsSaving = true;
    }

    public OreVeinPosition getOreVein(int chunkX, int chunkZ) {
        final long key = getOreVeinKey(chunkX, chunkZ);
        return oreChunks.getOrDefault(key, new OreVeinPosition(dimensionId, chunkX, chunkZ, VeinType.NO_VEIN, true));
    }

    private long getUndergroundFluidKey(int chunkX, int chunkZ) {
        return Utils.chunkCoordsToKey(Utils.mapToCornerUndergroundFluidChunkCoord(chunkX), Utils.mapToCornerUndergroundFluidChunkCoord(chunkZ));
    }

    public UpdateResult putUndergroundFluid(final UndergroundFluidPosition undergroundFluid) {
        final long key = getUndergroundFluidKey(undergroundFluid.chunkX, undergroundFluid.chunkZ);
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

    public UndergroundFluidPosition getUndergroundFluid(int chunkX, int chunkZ) {
        final long key = getUndergroundFluidKey(chunkX, chunkZ);
        return undergroundFluids.getOrDefault(key, UndergroundFluidPosition.getNotProspected(dimensionId, chunkX, chunkZ));
    }

    public Collection<OreVeinPosition> getAllOreVeins() {
        return oreChunks.values();
    }

    public Collection<UndergroundFluidPosition> getAllUndergroundFluids() {
        return undergroundFluids.values();
    }
}
