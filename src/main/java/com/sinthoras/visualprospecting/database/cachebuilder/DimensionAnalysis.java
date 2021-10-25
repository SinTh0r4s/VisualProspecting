package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.ServerCache;
import io.xol.enklume.MinecraftRegion;
import io.xol.enklume.MinecraftWorld;
import io.xol.enklume.nbt.NBTCompound;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

public class DimensionAnalysis {

    public final int dimensionId;
    private final Map<Long, DetailedChunkAnalysis> chunksForSecondIdentificationPass = new HashMap<>();

    public DimensionAnalysis(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public void processMinecraftWorld(MinecraftWorld world) throws IOException, DataFormatException {
        final Map<Long, Integer> veinBlockY = new HashMap<>();
        final List<File> regionFiles = world.getAllRegionFiles(dimensionId);
        AnalysisProgressTracker.setNumberOfRegionFiles(regionFiles.size());
        for (File regionFile : regionFiles) {
            final String[] parts = regionFile.getName().split("\\.");
            final int regionChunkX = Integer.parseInt(parts[1]) << 5;
            final int regionChunkZ = Integer.parseInt(parts[2]) << 5;
            final MinecraftRegion region = new MinecraftRegion(regionFile);
            for (int localChunkX = 0; localChunkX < VP.chunksPerRegionFileX; localChunkX++) {
                for (int localChunkZ = 0; localChunkZ < VP.chunksPerRegionFileZ; localChunkZ++) {
                    final int chunkX = regionChunkX + localChunkX;
                    final int chunkZ = regionChunkZ + localChunkZ;

                    // Only process ore chunks
                    if (chunkX == Utils.mapToCenterOreChunkCoord(chunkX) && chunkZ == Utils.mapToCenterOreChunkCoord(chunkZ)) {
                        // Helpful read about 'root' structure: https://minecraft.fandom.com/wiki/Chunk_format
                        final NBTCompound root = region.getChunk(localChunkX, localChunkZ).getRootTag();

                        // root == null occurs when a chunk is not yet generated
                        if (root != null) {
                            final ChunkAnalysis chunk = new ChunkAnalysis();
                            chunk.processMinecraftChunk(root);

                            if (chunk.matchesSingleVein()) {
                                ServerCache.instance.notifyOreVeinGeneration(dimensionId, chunkX, chunkZ, chunk.getMatchedVein());
                                veinBlockY.put(Utils.chunkCoordsToKey(chunkX, chunkZ), chunk.getVeinBlockY());
                            } else {
                                final DetailedChunkAnalysis detailedChunk = new DetailedChunkAnalysis(dimensionId, chunkX, chunkZ);
                                detailedChunk.processMinecraftChunk(root);
                                chunksForSecondIdentificationPass.put(Utils.chunkCoordsToKey(chunkX, chunkZ), detailedChunk);
                            }
                        }
                    }
                }
            }
            region.close();
            AnalysisProgressTracker.regionFileProcessed();
        }

        for(long key : chunksForSecondIdentificationPass.keySet()) {
            final DetailedChunkAnalysis chunk = chunksForSecondIdentificationPass.get(key);
            chunk.cleanUpWithNeighbors(veinBlockY);
            ServerCache.instance.notifyOreVeinGeneration(dimensionId, chunk.chunkX, chunk.chunkZ, chunk.getMatchedVein());
        }
    }
}
