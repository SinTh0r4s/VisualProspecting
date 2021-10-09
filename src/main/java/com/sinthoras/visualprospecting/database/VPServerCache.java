package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VPServerCache extends VPWorldCache{
    public static class VPProspectionResult {
        public final int chunkX;
        public final int chunkZ;
        public final VPVeinType veinType;

        public VPProspectionResult(int chunkX, int chunkZ, VPVeinType veinType) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.veinType = veinType;
        }

        public int getBlockX() {
            return VPUtils.coordChunkToBlock(chunkX) + 8;
        }

        public int getBlockZ() {
            return VPUtils.coordChunkToBlock(chunkZ) + 8;
        }
    }

    protected File getStorageDirectory() {
        return VPUtils.getSubDirectory(VPTags.SERVER_DIR);
    }

    public boolean putVeinType(int dimensionId, int chunkX, int chunkZ, final VPVeinType veinType) {
        return super.putVeinType(dimensionId, chunkX, chunkZ, veinType);
    }

    public List<VPProspectionResult> prospectChunks(int dimensionId, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ) {
        minChunkX = VPUtils.mapToCenterOreChunkCoord(minChunkX);
        minChunkZ = VPUtils.mapToCenterOreChunkCoord(minChunkZ);
        maxChunkX = VPUtils.mapToCenterOreChunkCoord(maxChunkX);
        maxChunkZ = VPUtils.mapToCenterOreChunkCoord(maxChunkZ);

        List<VPProspectionResult> prospectionResult = new ArrayList<>();
        for(int chunkX = minChunkX;chunkX <= maxChunkX;chunkX+=3)
            for(int chunkZ = minChunkZ;chunkZ <= maxChunkZ;chunkZ+=3) {
                final VPVeinType veinType = getVeinType(dimensionId, chunkX, chunkZ);
                if(veinType != VPVeinType.NO_VEIN) {
                    prospectionResult.add(new VPProspectionResult(chunkX, chunkZ, veinType));
                }
            }
        return prospectionResult;
    }

    public List<VPProspectionResult> prospectBlocks(int dimensionId, int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        return prospectChunks(dimensionId,
                VPUtils.coordBlockToChunk(minBlockX),
                VPUtils.coordBlockToChunk(minBlockZ),
                VPUtils.coordBlockToChunk(maxBlockX),
                VPUtils.coordBlockToChunk(maxBlockZ));
    }

    public List<VPProspectionResult> prospectBlockRadius(int dimensionId, int blockX, int blockZ, int blockRadius) {
        return prospectBlocks(dimensionId, blockX - blockRadius, blockZ - blockRadius, blockX + blockRadius, blockZ + blockRadius);
    }
}
