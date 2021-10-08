package com.sinthoras.visualprospecting.gui;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.gui.VPReflection.getJourneyMapGridRenderer;

public class VPMapState {
    private static final List<VPOreVeinDrawStep> drawSteps = new ArrayList<>();
    private static int oldMinChunkX = 0;
    private static int oldMaxChunkX = 0;
    private static int oldMinChunkZ = 0;
    private static int oldMaxChunkZ = 0;

    public static List<VPOreVeinDrawStep> getOreVeinDrawSteps(final GridRenderer gridRenderer) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int radiusBlockX = minecraft.displayWidth >> (1 + gridRenderer.getZoom());
        final int radiusBlockZ = minecraft.displayHeight >> (1 + gridRenderer.getZoom());
        final int minChunkX = VPUtils.mapToCenterOreChunkCoord(VPUtils.coordBlockToChunk(centerBlockX - radiusBlockX));
        final int minChunkZ = VPUtils.mapToCenterOreChunkCoord(VPUtils.coordBlockToChunk(centerBlockZ - radiusBlockZ));
        final int maxChunkX = VPUtils.mapToCenterOreChunkCoord(VPUtils.coordBlockToChunk(centerBlockX + radiusBlockX));
        final int maxChunkZ = VPUtils.mapToCenterOreChunkCoord(VPUtils.coordBlockToChunk(centerBlockZ + radiusBlockZ));

        if (minChunkX != oldMinChunkX || maxChunkX != oldMaxChunkX || minChunkZ != oldMinChunkZ || maxChunkZ != oldMaxChunkZ) {
            oldMinChunkX = minChunkX;
            oldMaxChunkX = maxChunkX;
            oldMinChunkZ = minChunkZ;
            oldMaxChunkZ = maxChunkZ;
            drawSteps.clear();
            for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX += 3)
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ += 3) {
                    final VPVeinType veinType = VP.clientVeinCache.getVeinType(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (veinType != VPVeinType.NO_VEIN) {
                        drawSteps.add(new VPOreVeinDrawStep(veinType, chunkX, chunkZ));
                    }
                }
        }
        return drawSteps;
    }

    public static void onDraw(double xOffset, double yOffset, float drawScale, double fontScale, double rotation) {
        final GridRenderer gridRenderer = getJourneyMapGridRenderer();
        assert (gridRenderer != null);

        gridRenderer.draw(getOreVeinDrawSteps(gridRenderer), xOffset, yOffset, drawScale, fontScale, rotation);
    }
}
