package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import io.xol.enklume.nbt.*;

import java.util.*;

// Slower, but more sophisticated approach to identify overlapping veins
public class DetailedChunkAnalysis {

    private final int dimensionId;
    public final int chunkX;
    public final int chunkZ;
    // For each height we count how often a ore (short) has occured
    private final Map<Short, Integer>[] oresPerY = new HashMap[VP.minecraftWorldHeight];

    public DetailedChunkAnalysis(int dimensionId, int chunkX, int chunkZ) {
        this.dimensionId = dimensionId;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public void processMinecraftChunk(final NBTCompound chunkRoot) {
        for (final NBTNamed tileEntity : ((NBTList) chunkRoot.getTag("Level.TileEntities")).elements) {
            final GregTechOre gtOre = new GregTechOre((NBTCompound) tileEntity);
            if(gtOre.isValidGTOre) {
                if(oresPerY[gtOre.blockY] == null) {
                    oresPerY[gtOre.blockY] = new HashMap<>();
                }
                if(oresPerY[gtOre.blockY].containsKey(gtOre.metaData) == false) {
                    oresPerY[gtOre.blockY].put(gtOre.metaData, 0);
                }
                oresPerY[gtOre.blockY].put(gtOre.metaData, oresPerY[gtOre.blockY].get(gtOre.metaData) + 1);
            }
        }
    }

    public void cleanUpWithNeighbors(final Map<Long, Integer> veinChunkY) {
        final OreVeinPosition[] neighbors = new OreVeinPosition[] {
                VP.serverCache.getOreVein(dimensionId, chunkX - 3, chunkZ + 3),
                VP.serverCache.getOreVein(dimensionId, chunkX, chunkZ + 3),
                VP.serverCache.getOreVein(dimensionId, chunkX + 3, chunkZ + 3),
                VP.serverCache.getOreVein(dimensionId, chunkX + 3, chunkZ),
                VP.serverCache.getOreVein(dimensionId, chunkX + 3, chunkZ - 3),
                VP.serverCache.getOreVein(dimensionId, chunkX, chunkZ - 3),
                VP.serverCache.getOreVein(dimensionId, chunkX - 3, chunkZ - 3),
                VP.serverCache.getOreVein(dimensionId, chunkX - 3, chunkZ)
        };
        final int[] neighborVeinBlockY = new int[] {
                veinChunkY.getOrDefault(Utils.chunkCoordsToKey(chunkX, chunkZ + 3), 0),
                veinChunkY.getOrDefault(Utils.chunkCoordsToKey(chunkX + 3, chunkZ + 3), 0),
                veinChunkY.getOrDefault(Utils.chunkCoordsToKey(chunkX + 3, chunkZ), 0),
                veinChunkY.getOrDefault(Utils.chunkCoordsToKey(chunkX + 3, chunkZ - 3), 0),
                veinChunkY.getOrDefault(Utils.chunkCoordsToKey(chunkX, chunkZ - 3), 0),
                veinChunkY.getOrDefault(Utils.chunkCoordsToKey(chunkX, chunkZ - 3), 0),
                veinChunkY.getOrDefault(Utils.chunkCoordsToKey(chunkX - 3, chunkZ - 3), 0),
                veinChunkY.getOrDefault(Utils.chunkCoordsToKey(chunkX - 3, chunkZ), 0)
        };

        // Remove all generated ores from neighbors. They could also be generated in the same chunk,
        // but that case is rare and therefore, neglected
        for(int neighborId = 0; neighborId < neighbors.length; neighborId++) {
            final VeinType neighbor = neighbors[neighborId].veinType;
            if (neighbor != null && neighbor.canOverlapIntoNeighborOreChunk()) {
                final int veinBlockY = neighborVeinBlockY[neighborId];
                for (int layerBlockY = 0; layerBlockY < VeinType.veinHeight; layerBlockY++) {
                    final int blockY = veinBlockY + layerBlockY;
                    if(blockY > 255) {
                        break;
                    }
                    if(oresPerY[blockY] != null) {
                        for (short metaData : neighbor.getOresAtLayer(layerBlockY)) {
                            oresPerY[blockY].remove(metaData);
                        }
                    }
                }
            }
        }
    }

    public VeinType getMatchedVein() {
        final Set<VeinType> matchedVeins = new HashSet<>();

        final Map<Short, Integer> allOres = new HashMap<>();
        for(Map<Short, Integer> oreLevel : oresPerY) {
            if (oreLevel != null) {
                oreLevel.forEach((metaData, numberOfBlocks) -> allOres.merge(metaData, numberOfBlocks, Integer::sum));
            }
        }

        final Optional<Short> dominantOre = allOres.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey).findFirst();
        if(dominantOre.isPresent()) {
            for(VeinType veinType : VeinTypeCaching.veinTypes) {
                if(veinType.matchesWithSpecificPrimaryOrSecondary(allOres.keySet(), dominantOre.get())) {
                    matchedVeins.add(veinType);
                }
            }
        }

        if(matchedVeins.size() == 1) {
            return matchedVeins.stream().findAny().get();
        }
        else {
            return VeinType.NO_VEIN;
        }
    }
}
