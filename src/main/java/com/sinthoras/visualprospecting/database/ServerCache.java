package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerCache extends WorldCache {

    protected File getStorageDirectory() {
        return Utils.getSubDirectory(Tags.SERVER_DIR);
    }

    public DimensionCache.UpdateResult putOreVein(int dimensionId, int chunkX, int chunkZ, final VeinType veinType) {
        return super.putOreVein(dimensionId, chunkX, chunkZ, veinType);
    }

    public List<OreVeinPosition> prospectOreChunks(int dimensionId, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ) {
        minChunkX = Utils.mapToCenterOreChunkCoord(minChunkX);
        minChunkZ = Utils.mapToCenterOreChunkCoord(minChunkZ);
        maxChunkX = Utils.mapToCenterOreChunkCoord(maxChunkX);
        maxChunkZ = Utils.mapToCenterOreChunkCoord(maxChunkZ);

        List<OreVeinPosition> oreVeinPositions = new ArrayList<>();
        for(int chunkX = minChunkX; chunkX <= maxChunkX; chunkX += 3) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ += 3) {
                final VeinType veinType = getOreVein(dimensionId, chunkX, chunkZ);
                if (veinType != VeinType.NO_VEIN) {
                    oreVeinPositions.add(new OreVeinPosition(chunkX, chunkZ, veinType));
                }
            }
        }
        return oreVeinPositions;
    }

    public List<OreVeinPosition> prospectOreBlocks(int dimensionId, int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        return prospectOreChunks(dimensionId,
                Utils.coordBlockToChunk(minBlockX),
                Utils.coordBlockToChunk(minBlockZ),
                Utils.coordBlockToChunk(maxBlockX),
                Utils.coordBlockToChunk(maxBlockZ));
    }

    public List<OreVeinPosition> prospectOreBlockRadius(int dimensionId, int blockX, int blockZ, int blockRadius) {
        return prospectOreBlocks(dimensionId, blockX - blockRadius, blockZ - blockRadius, blockX + blockRadius, blockZ + blockRadius);
    }

    public List<OilFieldPosition> prospectOilBlockRadius(World world, int blockX, int blockZ, int oilFieldRadius) {
        final int minChunkX = Utils.mapToCornerOilFieldChunkCoord(Utils.coordBlockToChunk(blockX)) - VP.oilFieldSizeChunkX * oilFieldRadius;
        final int minChunkZ = Utils.mapToCornerOilFieldChunkCoord(Utils.coordBlockToChunk(blockZ)) - VP.oilFieldSizeChunkZ * oilFieldRadius;

        List<OilFieldPosition> foundOilFields = new ArrayList<>((2 * oilFieldRadius + 1) * (2 * oilFieldRadius + 1));

        for(int oilFieldX = 0; oilFieldX < 2 * oilFieldRadius + 1; oilFieldX++) {
            for (int oilFieldZ = 0; oilFieldZ < 2 * oilFieldRadius + 1; oilFieldZ++) {
                final int chunkX = minChunkX + oilFieldX * VP.oilFieldSizeChunkX;
                final int chunkZ = minChunkZ + oilFieldZ * VP.oilFieldSizeChunkZ;
                final int[][] chunks = new int[VP.oilFieldSizeChunkX][VP.oilFieldSizeChunkZ];
                Fluid oil = null;
                for (int offsetChunkX = 0; offsetChunkX < VP.oilFieldSizeChunkX; offsetChunkX++) {
                    for (int offsetChunkZ = 0; offsetChunkZ < VP.oilFieldSizeChunkZ; offsetChunkZ++) {
                        final FluidStack prospectedOil = Utils.prospectOil(world, chunkX + offsetChunkX, chunkZ + offsetChunkZ);
                        if (prospectedOil != null) {
                            oil = prospectedOil.getFluid();
                            chunks[offsetChunkX][offsetChunkZ] = prospectedOil.amount;
                        }
                    }
                }
                if (oil != null) {
                    foundOilFields.add(new OilFieldPosition(chunkX, chunkZ, new OilField(oil, chunks)));
                }
            }
        }
        return foundOilFields;
    }
}
