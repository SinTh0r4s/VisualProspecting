package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.VPOilField;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class VPMapState {
    private final List<VPOreVeinDrawStep> oreChunkDrawSteps = new ArrayList<>();
    private int oldMinOreChunkX = 0;
    private int oldMaxOreChunkX = 0;
    private int oldMinOreChunkZ = 0;
    private int oldMaxOreChunkZ = 0;
    private final List<VPOilFieldDrawStep> oilFieldDrawSteps = new ArrayList<>();
    private int oldMinOilFieldX = 0;
    private int oldMaxOilFieldX = 0;
    private int oldMinOilFieldZ = 0;
    private int oldMaxOilFieldZ = 0;

    public boolean drawOreVeins = true;
    public boolean drawOilFields = true;

    public List<VPOreVeinDrawStep> getOreVeinDrawSteps(final GridRenderer gridRenderer) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int radiusBlockX = minecraft.displayWidth >> (1 + gridRenderer.getZoom());
        final int radiusBlockZ = minecraft.displayHeight >> (1 + gridRenderer.getZoom());
        final int minOreChunkX = VPUtils.mapToCenterOreChunkCoord(VPUtils.coordBlockToChunk(centerBlockX - radiusBlockX));
        final int minOreChunkZ = VPUtils.mapToCenterOreChunkCoord(VPUtils.coordBlockToChunk(centerBlockZ - radiusBlockZ));
        final int maxOreChunkX = VPUtils.mapToCenterOreChunkCoord(VPUtils.coordBlockToChunk(centerBlockX + radiusBlockX));
        final int maxOreChunkZ = VPUtils.mapToCenterOreChunkCoord(VPUtils.coordBlockToChunk(centerBlockZ + radiusBlockZ));

        if (minOreChunkX != oldMinOreChunkX || maxOreChunkX != oldMaxOreChunkX || minOreChunkZ != oldMinOreChunkZ || maxOreChunkZ != oldMaxOreChunkZ) {
            oldMinOreChunkX = minOreChunkX;
            oldMaxOreChunkX = maxOreChunkX;
            oldMinOreChunkZ = minOreChunkZ;
            oldMaxOreChunkZ = maxOreChunkZ;
            oreChunkDrawSteps.clear();
            for (int chunkX = minOreChunkX; chunkX <= maxOreChunkX; chunkX += VP.oreVeinSizeChunkX) {
                for (int chunkZ = minOreChunkZ; chunkZ <= maxOreChunkZ; chunkZ += VP.oreVeinSizeChunkZ) {
                    final VPVeinType veinType = VP.clientCache.getOreVein(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (veinType != VPVeinType.NO_VEIN) {
                        oreChunkDrawSteps.add(new VPOreVeinDrawStep(veinType, chunkX, chunkZ));
                    }
                }
            }
        }
        return oreChunkDrawSteps;
    }

    public List<VPOilFieldDrawStep> getOilFieldDrawSteps(final GridRenderer gridRenderer) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int radiusBlockX = minecraft.displayWidth >> (1 + gridRenderer.getZoom());
        final int radiusBlockZ = minecraft.displayHeight >> (1 + gridRenderer.getZoom());
        final int minOilFieldX = VPUtils.mapToCornerOilFieldChunkCoord(VPUtils.coordBlockToChunk(centerBlockX - radiusBlockX));
        final int minOilFieldZ = VPUtils.mapToCornerOilFieldChunkCoord(VPUtils.coordBlockToChunk(centerBlockZ - radiusBlockZ));
        final int maxOilFieldX = VPUtils.mapToCornerOilFieldChunkCoord(VPUtils.coordBlockToChunk(centerBlockX + radiusBlockX));
        final int maxOilFieldZ = VPUtils.mapToCornerOilFieldChunkCoord(VPUtils.coordBlockToChunk(centerBlockZ + radiusBlockZ));
        if (minOilFieldX != oldMinOilFieldX || maxOilFieldX != oldMaxOilFieldX || minOilFieldZ != oldMinOilFieldZ || maxOilFieldZ != oldMaxOilFieldZ) {
            oldMinOilFieldX = minOilFieldX;
            oldMaxOilFieldX = maxOilFieldX;
            oldMinOilFieldZ = minOilFieldZ;
            oldMaxOilFieldZ = maxOilFieldZ;
            oilFieldDrawSteps.clear();
            for (int chunkX = minOilFieldX; chunkX <= maxOilFieldX; chunkX += VP.oilFieldSizeChunkX) {
                for (int chunkZ = minOilFieldZ; chunkZ <= maxOilFieldZ; chunkZ += VP.oilFieldSizeChunkZ) {
                    final VPOilField oilField = VP.clientCache.getOilField(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (oilField != VPOilField.NOT_PROSPECTED) {
                        final int minAmountInField = oilField.getMinProduction();
                        final int maxAmountInField = oilField.getMaxProduction();
                        for (int offsetChunkX = 0; offsetChunkX < VP.oilFieldSizeChunkX; offsetChunkX++) {
                            for (int offsetChunkZ = 0; offsetChunkZ < VP.oilFieldSizeChunkZ; offsetChunkZ++) {
                                oilFieldDrawSteps.add(new VPOilFieldDrawStep(chunkX + offsetChunkX, chunkZ + offsetChunkZ, oilField.oil, oilField.chunks[offsetChunkX][offsetChunkZ], minAmountInField, maxAmountInField));
                            }
                        }
                    }
                }
            }
        }
        return oilFieldDrawSteps;
    }
}
