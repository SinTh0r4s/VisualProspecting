package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class VPMapState {
    private final List<VPOreVeinDrawStep> drawSteps = new ArrayList<>();
    private int oldMinChunkX = 0;
    private int oldMaxChunkX = 0;
    private int oldMinChunkZ = 0;
    private int oldMaxChunkZ = 0;

    public boolean drawOreVeins = true;

    public List<VPOreVeinDrawStep> getOreVeinDrawSteps(final GridRenderer gridRenderer) {
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
                    final VPVeinType veinType = VP.clientVeinCache.getOreVein(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (veinType != VPVeinType.NO_VEIN) {
                        drawSteps.add(new VPOreVeinDrawStep(veinType, chunkX, chunkZ));
                    }
                }
        }
        return drawSteps;
    }
}
