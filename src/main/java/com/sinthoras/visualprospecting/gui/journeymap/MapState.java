package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class MapState {
    private final List<OreVeinDrawStep> oreChunkDrawSteps = new ArrayList<>();
    private int oldMinOreChunkX = 0;
    private int oldMaxOreChunkX = 0;
    private int oldMinOreChunkZ = 0;
    private int oldMaxOreChunkZ = 0;
    private final List<UndergroundFluidChunkDrawStep> undergroundFluidChunksDrawSteps = new ArrayList<>();
    private final List<UndergroundFluidDrawStep> undergroundFluidsDrawSteps = new ArrayList<>();
    private int oldMinUndergroundFluidX = 0;
    private int oldMaxUndergroundFluidX = 0;
    private int oldMinUndergroundFluidZ = 0;
    private int oldMaxUndergroundFluidZ = 0;

    public boolean drawOreVeins = true;
    public boolean drawUndergroundFluids = false;

    public List<OreVeinDrawStep> getOreVeinDrawSteps(final GridRenderer gridRenderer) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int radiusBlockX = minecraft.displayWidth >> (1 + gridRenderer.getZoom());
        final int radiusBlockZ = minecraft.displayHeight >> (1 + gridRenderer.getZoom());
        final int minOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(centerBlockX - radiusBlockX));
        final int minOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(centerBlockZ - radiusBlockZ));
        final int maxOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(centerBlockX + radiusBlockX));
        final int maxOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(centerBlockZ + radiusBlockZ));

        if (minOreChunkX != oldMinOreChunkX || maxOreChunkX != oldMaxOreChunkX || minOreChunkZ != oldMinOreChunkZ || maxOreChunkZ != oldMaxOreChunkZ) {
            oldMinOreChunkX = minOreChunkX;
            oldMaxOreChunkX = maxOreChunkX;
            oldMinOreChunkZ = minOreChunkZ;
            oldMaxOreChunkZ = maxOreChunkZ;
            oreChunkDrawSteps.clear();
            for (int chunkX = minOreChunkX; chunkX <= maxOreChunkX; chunkX = Utils.mapToCenterOreChunkCoord(chunkX + 3)) {
                for (int chunkZ = minOreChunkZ; chunkZ <= maxOreChunkZ; chunkZ = Utils.mapToCenterOreChunkCoord(chunkZ + 3)) {
                    final OreVeinPosition oreVeinPosition = VP.clientCache.getOreVein(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (oreVeinPosition.veinType != VeinType.NO_VEIN) {
                        oreChunkDrawSteps.add(new OreVeinDrawStep(oreVeinPosition));
                    }
                }
            }
        }
        return oreChunkDrawSteps;
    }

    private void updateUndergroundFluidRelatedDrawSteps(final GridRenderer gridRenderer) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int radiusBlockX = minecraft.displayWidth >> (1 + gridRenderer.getZoom());
        final int radiusBlockZ = minecraft.displayHeight >> (1 + gridRenderer.getZoom());
        final int minUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(centerBlockX - radiusBlockX));
        final int minUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(centerBlockZ - radiusBlockZ));
        final int maxUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(centerBlockX + radiusBlockX));
        final int maxUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(centerBlockZ + radiusBlockZ));
        if (minUndergroundFluidX != oldMinUndergroundFluidX || maxUndergroundFluidX != oldMaxUndergroundFluidX || minUndergroundFluidZ != oldMinUndergroundFluidZ || maxUndergroundFluidZ != oldMaxUndergroundFluidZ) {
            oldMinUndergroundFluidX = minUndergroundFluidX;
            oldMaxUndergroundFluidX = maxUndergroundFluidX;
            oldMinUndergroundFluidZ = minUndergroundFluidZ;
            oldMaxUndergroundFluidZ = maxUndergroundFluidZ;

            undergroundFluidChunksDrawSteps.clear();
            for (int chunkX = minUndergroundFluidX; chunkX <= maxUndergroundFluidX; chunkX += VP.undergroundFluidSizeChunkX) {
                for (int chunkZ = minUndergroundFluidZ; chunkZ <= maxUndergroundFluidZ; chunkZ += VP.undergroundFluidSizeChunkZ) {
                    final UndergroundFluidPosition undergroundFluid = VP.clientCache.getUndergroundFluid(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (undergroundFluid.isProspected()) {
                        final int minAmountInField = undergroundFluid.getMinProduction();
                        final int maxAmountInField = undergroundFluid.getMaxProduction();
                        for (int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
                            for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                                undergroundFluidChunksDrawSteps.add(new UndergroundFluidChunkDrawStep(chunkX + offsetChunkX, chunkZ + offsetChunkZ, undergroundFluid.fluid, undergroundFluid.chunks[offsetChunkX][offsetChunkZ], minAmountInField, maxAmountInField));
                            }
                        }
                    }
                }
            }

            undergroundFluidsDrawSteps.clear();
            for (int chunkX = minUndergroundFluidX; chunkX <= maxUndergroundFluidX; chunkX += VP.undergroundFluidSizeChunkX) {
                for (int chunkZ = minUndergroundFluidZ; chunkZ <= maxUndergroundFluidZ; chunkZ += VP.undergroundFluidSizeChunkZ) {
                    final UndergroundFluidPosition undergroundFluid = VP.clientCache.getUndergroundFluid(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (undergroundFluid.isProspected()) {
                        undergroundFluidsDrawSteps.add(new UndergroundFluidDrawStep(undergroundFluid));
                    }
                }
            }
        }
    }

    public List<UndergroundFluidChunkDrawStep> getUndergroundFluidChunksDrawSteps(final GridRenderer gridRenderer) {
        updateUndergroundFluidRelatedDrawSteps(gridRenderer);
        return undergroundFluidChunksDrawSteps;
    }

    public List<UndergroundFluidDrawStep> getUndergroundFluidsDrawSteps(final GridRenderer gridRenderer) {
        updateUndergroundFluidRelatedDrawSteps(gridRenderer);
        return undergroundFluidsDrawSteps;
    }

    public void onToggleOreVein() {
        for(OreVeinDrawStep oreVeinDrawStep : oreChunkDrawSteps) {
            oreVeinDrawStep.toggleDepletedIfMouseOver();
        }
    }
}
