package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

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

    public List<VPOreVeinPosition> prospectOreChunks(int dimensionId, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ) {
        minChunkX = VPUtils.mapToCenterOreChunkCoord(minChunkX);
        minChunkZ = VPUtils.mapToCenterOreChunkCoord(minChunkZ);
        maxChunkX = VPUtils.mapToCenterOreChunkCoord(maxChunkX);
        maxChunkZ = VPUtils.mapToCenterOreChunkCoord(maxChunkZ);

        List<VPOreVeinPosition> oreVeinPositions = new ArrayList<>();
        for(int chunkX = minChunkX;chunkX <= maxChunkX;chunkX+=3)
            for(int chunkZ = minChunkZ;chunkZ <= maxChunkZ;chunkZ+=3) {
                final VPVeinType veinType = getOreVein(dimensionId, chunkX, chunkZ);
                if(veinType != VPVeinType.NO_VEIN) {
                    oreVeinPositions.add(new VPOreVeinPosition(chunkX, chunkZ, veinType));
                }
            }
        return oreVeinPositions;
    }

    public List<VPOreVeinPosition> prospectOreBlocks(int dimensionId, int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        return prospectOreChunks(dimensionId,
                VPUtils.coordBlockToChunk(minBlockX),
                VPUtils.coordBlockToChunk(minBlockZ),
                VPUtils.coordBlockToChunk(maxBlockX),
                VPUtils.coordBlockToChunk(maxBlockZ));
    }

    public List<VPOreVeinPosition> prospectOreBlockRadius(int dimensionId, int blockX, int blockZ, int blockRadius) {
        return prospectOreBlocks(dimensionId, blockX - blockRadius, blockZ - blockRadius, blockX + blockRadius, blockZ + blockRadius);
    }

    public List<VPOilFieldPosition> prospectOilBlockRadius(World world, int blockX, int blockZ, int oilFieldRadius) {
        final int minChunkX = VPUtils.mapToCornerOilFieldChunkCoord(VPUtils.coordBlockToChunk(blockX)) - VP.oilFieldSizeChunkX * oilFieldRadius;
        final int minChunkZ = VPUtils.mapToCornerOilFieldChunkCoord(VPUtils.coordBlockToChunk(blockZ)) - VP.oilFieldSizeChunkZ * oilFieldRadius;

        List<VPOilFieldPosition> foundOilFields = new ArrayList<>((2 * oilFieldRadius + 1) * (2 * oilFieldRadius + 1));

        for(int oilFieldX = 0;oilFieldX < 2*oilFieldRadius+1; oilFieldX++)
            for(int oilFieldZ = 0;oilFieldZ < 2*oilFieldRadius+1; oilFieldZ++) {
                final int chunkX = minChunkX + oilFieldX * VP.oilFieldSizeChunkX;
                final int chunkZ = minChunkZ + oilFieldZ * VP.oilFieldSizeChunkZ;
                final int[][] chunks = new int[VP.oilFieldSizeChunkX][VP.oilFieldSizeChunkZ];
                Fluid oil = null;
                for(int offsetChunkX = 0;offsetChunkX<VP.oilFieldSizeChunkX;offsetChunkX++)
                    for(int offsetChunkZ = 0;offsetChunkZ<VP.oilFieldSizeChunkZ;offsetChunkZ++) {
                        final FluidStack prospectedOil = VPUtils.prospectOil(world, chunkX + offsetChunkX, chunkZ + offsetChunkZ);
                        if(prospectedOil != null) {
                            oil = prospectedOil.getFluid();
                            chunks[offsetChunkX][offsetChunkZ] = prospectedOil.amount;
                        }
                    }
                if(oil != null) {
                    foundOilFields.add(new VPOilFieldPosition(chunkX, chunkZ, new VPOilField(oil, chunks)));
                }
            }

        return foundOilFields;
    }
}
