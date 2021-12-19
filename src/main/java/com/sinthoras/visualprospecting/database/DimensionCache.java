package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.nio.ByteBuffer;
import java.util.*;

public class DimensionCache {

    public enum UpdateResult {
        AlreadyKnown,
        Updated,
        New
    }

    private final Map<ChunkCoordIntPair, OreVeinPosition> oreChunks = new HashMap<>();
    private final Map<ChunkCoordIntPair, UndergroundFluidPosition> undergroundFluids = new HashMap<>();
    private final Set<ChunkCoordIntPair> changedOrNewOreChunks = new HashSet<>();
    private final Set<ChunkCoordIntPair> changedOrNewUndergroundFluids = new HashSet<>();
    public final int dimensionId;

    public DimensionCache(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public ByteBuffer saveOreChunks() {
        if(changedOrNewOreChunks.isEmpty() == false) {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(changedOrNewOreChunks.size() * (2 * Integer.BYTES + Short.BYTES));
            changedOrNewOreChunks.stream().map(oreChunks::get).forEach(oreVeinPosition -> {
                byteBuffer.putInt(oreVeinPosition.chunkX);
                byteBuffer.putInt(oreVeinPosition.chunkZ);
                short veinTypeId = VeinTypeCaching.getVeinTypeId(oreVeinPosition.veinType);
                if(oreVeinPosition.isDepleted()) {
                    veinTypeId |= 0x8000;
                }
                byteBuffer.putShort(veinTypeId);
            });
            changedOrNewOreChunks.clear();
            byteBuffer.flip();
            return byteBuffer;
        }
        return null;
    }

    public ByteBuffer saveUndergroundFluids() {
        if(changedOrNewUndergroundFluids.isEmpty() == false) {
            final ByteBuffer byteBuffer = ByteBuffer.allocate(changedOrNewUndergroundFluids.size() * (Long.BYTES + Integer.BYTES * (1 + VP.undergroundFluidSizeChunkX * VP.undergroundFluidSizeChunkZ)));
            changedOrNewUndergroundFluids.stream().map(undergroundFluids::get).forEach(undergroundFluidPosition -> {
                byteBuffer.putInt(undergroundFluidPosition.chunkX);
                byteBuffer.putInt(undergroundFluidPosition.chunkZ);
                byteBuffer.putInt(undergroundFluidPosition.fluid.getID());
                for(int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
                    for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                        byteBuffer.putInt(undergroundFluidPosition.chunks[offsetChunkX][offsetChunkZ]);
                    }
                }
            });
            changedOrNewUndergroundFluids.clear();
            byteBuffer.flip();
            return byteBuffer;
        }
        return null;
    }

    public void loadCache(ByteBuffer oreChunksBuffer, ByteBuffer undergroundFluidsBuffer) {
        if(oreChunksBuffer != null) {
            while (oreChunksBuffer.remaining() >= Integer.BYTES * 2 + Short.BYTES) {
                final int chunkX = oreChunksBuffer.getInt();
                final int chunkZ = oreChunksBuffer.getInt();
                final short veinTypeId = oreChunksBuffer.getShort();
                final boolean depleted = (veinTypeId & 0x8000) > 0;
                final VeinType veinType = VeinTypeCaching.getVeinType((short)(veinTypeId & 0x7FFF));
                oreChunks.put(getOreVeinKey(chunkX, chunkZ), new OreVeinPosition(dimensionId, chunkX, chunkZ, veinType, depleted));
            }
        }
        if(undergroundFluidsBuffer != null) {
            while (undergroundFluidsBuffer.remaining() >= Integer.BYTES * (3 + VP.undergroundFluidSizeChunkX * VP.undergroundFluidSizeChunkZ)) {
                final int chunkX = undergroundFluidsBuffer.getInt();
                final int chunkZ = undergroundFluidsBuffer.getInt();
                final Fluid fluid = FluidRegistry.getFluid(undergroundFluidsBuffer.getInt());
                final int[][] chunks = new int[VP.undergroundFluidSizeChunkX][VP.undergroundFluidSizeChunkZ];
                for(int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
                    for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                        chunks[offsetChunkX][offsetChunkZ] = undergroundFluidsBuffer.getInt();
                    }
                }
                undergroundFluids.put(getOreVeinKey(chunkX, chunkZ), new UndergroundFluidPosition(dimensionId, chunkX, chunkZ, fluid, chunks));
            }
        }
    }

    private ChunkCoordIntPair getOreVeinKey(int chunkX, int chunkZ) {
        return new ChunkCoordIntPair(Utils.mapToCenterOreChunkCoord(chunkX), Utils.mapToCenterOreChunkCoord(chunkZ));
    }

    public UpdateResult putOreVein(final OreVeinPosition oreVeinPosition) {
        final ChunkCoordIntPair key = getOreVeinKey(oreVeinPosition.chunkX, oreVeinPosition.chunkZ);
        if(oreChunks.containsKey(key) == false) {
            oreChunks.put(key, oreVeinPosition);
            changedOrNewOreChunks.add(key);
            return UpdateResult.New;
        }
        final OreVeinPosition storedOreVeinPosition = oreChunks.get(key);
        if(storedOreVeinPosition.veinType != oreVeinPosition.veinType) {
            oreChunks.put(key, oreVeinPosition.joinDepletedState(storedOreVeinPosition));
            changedOrNewOreChunks.add(key);
            return UpdateResult.New;
        }
        return UpdateResult.AlreadyKnown;
    }

    public void toggleOreVein(int chunkX, int chunkZ) {
        final ChunkCoordIntPair key = getOreVeinKey(chunkX, chunkZ);
        if(oreChunks.containsKey(key)) {
            oreChunks.get(key).toggleDepleted();
            changedOrNewOreChunks.add(key);
        }
    }

    public OreVeinPosition getOreVein(int chunkX, int chunkZ) {
        final ChunkCoordIntPair key = getOreVeinKey(chunkX, chunkZ);
        return oreChunks.getOrDefault(key, new OreVeinPosition(dimensionId, chunkX, chunkZ, VeinType.NO_VEIN, true));
    }

    private ChunkCoordIntPair getUndergroundFluidKey(int chunkX, int chunkZ) {
        return new ChunkCoordIntPair(Utils.mapToCornerUndergroundFluidChunkCoord(chunkX), Utils.mapToCornerUndergroundFluidChunkCoord(chunkZ));
    }

    public UpdateResult putUndergroundFluid(final UndergroundFluidPosition undergroundFluid) {
        final ChunkCoordIntPair key = getUndergroundFluidKey(undergroundFluid.chunkX, undergroundFluid.chunkZ);
        if(undergroundFluids.containsKey(key) == false) {
            changedOrNewUndergroundFluids.add(key);
            undergroundFluids.put(key, undergroundFluid);
            return UpdateResult.New;
        }
        else if(undergroundFluids.get(key).equals(undergroundFluid) == false) {
            changedOrNewUndergroundFluids.add(key);
            undergroundFluids.put(key, undergroundFluid);
            return UpdateResult.Updated;
        }
        return UpdateResult.AlreadyKnown;
    }

    public UndergroundFluidPosition getUndergroundFluid(int chunkX, int chunkZ) {
        final ChunkCoordIntPair key = getUndergroundFluidKey(chunkX, chunkZ);
        return undergroundFluids.getOrDefault(key, UndergroundFluidPosition.getNotProspected(dimensionId, chunkX, chunkZ));
    }

    public Collection<OreVeinPosition> getAllOreVeins() {
        return oreChunks.values();
    }

    public Collection<UndergroundFluidPosition> getAllUndergroundFluids() {
        return undergroundFluids.values();
    }
}
