package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.OilField;
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
    private final List<OilChunkDrawStep> oilChunkDrawSteps = new ArrayList<>();
    private final List<OilFieldDrawStep> oilFieldDrawSteps = new ArrayList<>();
    private int oldMinOilFieldX = 0;
    private int oldMaxOilFieldX = 0;
    private int oldMinOilFieldZ = 0;
    private int oldMaxOilFieldZ = 0;

    public boolean drawOreVeins = true;
    public boolean drawOilFields = true;

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
            for (int chunkX = minOreChunkX; chunkX <= maxOreChunkX; chunkX += VP.oreVeinSizeChunkX) {
                for (int chunkZ = minOreChunkZ; chunkZ <= maxOreChunkZ; chunkZ += VP.oreVeinSizeChunkZ) {
                    final VeinType veinType = VP.clientCache.getOreVein(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (veinType != VeinType.NO_VEIN) {
                        oreChunkDrawSteps.add(new OreVeinDrawStep(veinType, chunkX, chunkZ));
                    }
                }
            }
        }
        return oreChunkDrawSteps;
    }

    private void updateOilRelatedDrawSteps(final GridRenderer gridRenderer) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int radiusBlockX = minecraft.displayWidth >> (1 + gridRenderer.getZoom());
        final int radiusBlockZ = minecraft.displayHeight >> (1 + gridRenderer.getZoom());
        final int minOilFieldX = Utils.mapToCornerOilFieldChunkCoord(Utils.coordBlockToChunk(centerBlockX - radiusBlockX));
        final int minOilFieldZ = Utils.mapToCornerOilFieldChunkCoord(Utils.coordBlockToChunk(centerBlockZ - radiusBlockZ));
        final int maxOilFieldX = Utils.mapToCornerOilFieldChunkCoord(Utils.coordBlockToChunk(centerBlockX + radiusBlockX));
        final int maxOilFieldZ = Utils.mapToCornerOilFieldChunkCoord(Utils.coordBlockToChunk(centerBlockZ + radiusBlockZ));
        if (minOilFieldX != oldMinOilFieldX || maxOilFieldX != oldMaxOilFieldX || minOilFieldZ != oldMinOilFieldZ || maxOilFieldZ != oldMaxOilFieldZ) {
            oldMinOilFieldX = minOilFieldX;
            oldMaxOilFieldX = maxOilFieldX;
            oldMinOilFieldZ = minOilFieldZ;
            oldMaxOilFieldZ = maxOilFieldZ;

            oilChunkDrawSteps.clear();
            for (int chunkX = minOilFieldX; chunkX <= maxOilFieldX; chunkX += VP.oilFieldSizeChunkX) {
                for (int chunkZ = minOilFieldZ; chunkZ <= maxOilFieldZ; chunkZ += VP.oilFieldSizeChunkZ) {
                    final OilField oilField = VP.clientCache.getOilField(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (oilField != OilField.NOT_PROSPECTED) {
                        final int minAmountInField = oilField.getMinProduction();
                        final int maxAmountInField = oilField.getMaxProduction();
                        for (int offsetChunkX = 0; offsetChunkX < VP.oilFieldSizeChunkX; offsetChunkX++) {
                            for (int offsetChunkZ = 0; offsetChunkZ < VP.oilFieldSizeChunkZ; offsetChunkZ++) {
                                oilChunkDrawSteps.add(new OilChunkDrawStep(chunkX + offsetChunkX, chunkZ + offsetChunkZ, oilField.oil, oilField.chunks[offsetChunkX][offsetChunkZ], minAmountInField, maxAmountInField));
                            }
                        }
                    }
                }
            }

            oilFieldDrawSteps.clear();
            for (int chunkX = minOilFieldX; chunkX <= maxOilFieldX; chunkX += VP.oilFieldSizeChunkX) {
                for (int chunkZ = minOilFieldZ; chunkZ <= maxOilFieldZ; chunkZ += VP.oilFieldSizeChunkZ) {
                    final OilField oilField = VP.clientCache.getOilField(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (oilField != OilField.NOT_PROSPECTED) {
                        oilFieldDrawSteps.add(new OilFieldDrawStep(chunkX, chunkZ, oilField.oil, oilField.getMinProduction(), oilField.getMaxProduction()));
                    }
                }
            }
        }
    }

    public List<OilChunkDrawStep> getOilChunkDrawSteps(final GridRenderer gridRenderer) {
        updateOilRelatedDrawSteps(gridRenderer);
        return oilChunkDrawSteps;
    }

    public List<OilFieldDrawStep> getOilFieldDrawSteps(final GridRenderer gridRenderer) {
        updateOilRelatedDrawSteps(gridRenderer);
        return oilFieldDrawSteps;
    }
}
