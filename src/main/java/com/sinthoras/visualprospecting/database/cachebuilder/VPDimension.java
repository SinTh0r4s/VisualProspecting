package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.VPCacheWorld;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import io.xol.enklume.MinecraftRegion;
import io.xol.enklume.MinecraftWorld;
import io.xol.enklume.nbt.NBTCompound;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.DataFormatException;

public class VPDimension {

    public final int dimensionId;
    private final HashMap<Long, HashSet<VPVeinType>> chunksForSecondIdentificationPass = new HashMap<>();

    public VPDimension(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public void processMinecraftWorld(MinecraftWorld world) throws IOException, DataFormatException {
        final List<File> regionFiles = world.getAllRegionFiles(dimensionId);
        VPProgressTracker.setNumberOfRegionFiles(regionFiles.size());
        for (File regionFile : regionFiles) {
            final String[] parts = regionFile.getName().split("\\.");
            final int regionChunkX = Integer.parseInt(parts[1]) << 5;
            final int regionChunkZ = Integer.parseInt(parts[2]) << 5;
            final MinecraftRegion region = new MinecraftRegion(regionFile);
            for (int localChunkX = 0; localChunkX < 32; localChunkX++)
                for (int localChunkZ = 0; localChunkZ < 32; localChunkZ++) {
                    final int chunkX = regionChunkX + localChunkX;
                    final int chunkZ = regionChunkZ + localChunkZ;

                    // Only process ore chunks
                    if(chunkX == VPUtils.mapToCenterOreChunkCoord(chunkX) && chunkZ == VPUtils.mapToCenterOreChunkCoord(chunkZ)) {
                        final NBTCompound root = region.getChunk(localChunkX, localChunkZ).getRootTag();

                        // root == null occurs when a chunk is not yet generated
                        if (root != null) {
                            final VPChunk chunk = new VPChunk(chunkX, chunkZ);
                            chunk.processMinecraftChunk(root);

                            HashSet<VPVeinType> matchingVeins = new HashSet<>();
                            for(VPVeinType veinType : VPVeinTypeCaching.veinTypes) {
                                if(veinType.partiallyMatches(chunk.getOres()))
                                    matchingVeins.add(veinType);
                            }

                            if(matchingVeins.size() == 0)
                                VPCacheWorld.putVeinType(dimensionId, chunkX, chunkZ, VPVeinType.NO_VEIN);
                            else if(matchingVeins.size() == 1)
                                VPCacheWorld.putVeinType(dimensionId, chunkX, chunkZ, matchingVeins.stream().findAny().get());
                            else {
                                chunksForSecondIdentificationPass.put(VPUtils.chunkCoordsToKey(chunkX, chunkZ), matchingVeins);
                            }
                        }
                    }
                }
            VPProgressTracker.regionFileProcessed();
        }

        // Second identification pass
        for(long key : chunksForSecondIdentificationPass.keySet()) {
            final int chunkX = (int)(key >> 32);
            final int chunkZ = (int)key;
            // TODO: identify or mark as NO_VEIN
            final HashSet<VPVeinType> matchingVeins = chunksForSecondIdentificationPass.get(key);
        }
    }
}
