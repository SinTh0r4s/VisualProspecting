package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VPServerCache extends VPWorldCache {

    protected File getStorageDirectory() {
        return VPUtils.getSubDirectory(VPTags.SERVER_DIR);
    }

    public VPDimensionCache.UpdateResult putOreVein(int dimensionId, int chunkX, int chunkZ, final VPVeinType veinType) {
        return super.putOreVein(dimensionId, chunkX, chunkZ, veinType);
    }

    public List<VPOreVeinPosition> prospectChunks(int dimensionId, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ) {
        minChunkX = VPUtils.mapToCenterOreChunkCoord(minChunkX);
        minChunkZ = VPUtils.mapToCenterOreChunkCoord(minChunkZ);
        maxChunkX = VPUtils.mapToCenterOreChunkCoord(maxChunkX);
        maxChunkZ = VPUtils.mapToCenterOreChunkCoord(maxChunkZ);

        List<VPOreVeinPosition> prospectionResult = new ArrayList<>();
        for(int chunkX = minChunkX;chunkX <= maxChunkX;chunkX+=3)
            for(int chunkZ = minChunkZ;chunkZ <= maxChunkZ;chunkZ+=3) {
                final VPVeinType veinType = getOreVein(dimensionId, chunkX, chunkZ);
                if(veinType != VPVeinType.NO_VEIN) {
                    prospectionResult.add(new VPOreVeinPosition(chunkX, chunkZ, veinType));
                }
            }
        return prospectionResult;
    }

    public List<VPOreVeinPosition> prospectBlocks(int dimensionId, int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        return prospectChunks(dimensionId,
                VPUtils.coordBlockToChunk(minBlockX),
                VPUtils.coordBlockToChunk(minBlockZ),
                VPUtils.coordBlockToChunk(maxBlockX),
                VPUtils.coordBlockToChunk(maxBlockZ));
    }

    public List<VPOreVeinPosition> prospectBlockRadius(int dimensionId, int blockX, int blockZ, int blockRadius) {
        return prospectBlocks(dimensionId, blockX - blockRadius, blockZ - blockRadius, blockX + blockRadius, blockZ + blockRadius);
    }
}
