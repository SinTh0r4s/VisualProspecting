package com.sinthoras.visualprospecting.gui.journeymap.layers;

import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.UndergroundFluidDrawStep;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.UndergroundFluidButton;
import journeymap.client.render.draw.DrawStep;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class UndergroundFluidLayer extends InformationLayer {

    public static final UndergroundFluidLayer instance = new UndergroundFluidLayer();

    private int oldMinUndergroundFluidX = 0;
    private int oldMaxUndergroundFluidX = 0;
    private int oldMinUndergroundFluidZ = 0;
    private int oldMaxUndergroundFluidZ = 0;

    public UndergroundFluidLayer() {
        super(UndergroundFluidButton.instance);
    }

    @Override
    protected boolean needsRegenerateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        final int minUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(minBlockX));
        final int minUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(minBlockZ));
        final int maxUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(maxBlockX));
        final int maxUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(maxBlockZ));
        if (minUndergroundFluidX != oldMinUndergroundFluidX || maxUndergroundFluidX != oldMaxUndergroundFluidX || minUndergroundFluidZ != oldMinUndergroundFluidZ || maxUndergroundFluidZ != oldMaxUndergroundFluidZ) {
            oldMinUndergroundFluidX = minUndergroundFluidX;
            oldMaxUndergroundFluidX = maxUndergroundFluidX;
            oldMinUndergroundFluidZ = minUndergroundFluidZ;
            oldMaxUndergroundFluidZ = maxUndergroundFluidZ;
            return true;
        }
        return false;
    }

    @Override
    protected List<DrawStep> generateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        final int minUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(minBlockX));
        final int minUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(minBlockZ));
        final int maxUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(maxBlockX));
        final int maxUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(maxBlockZ));
        final int playerDimensionId = Minecraft.getMinecraft().thePlayer.dimension;

        ArrayList<DrawStep> undergroundFluidsDrawSteps = new ArrayList<>();

        for (int chunkX = minUndergroundFluidX; chunkX <= maxUndergroundFluidX; chunkX += VP.undergroundFluidSizeChunkX) {
            for (int chunkZ = minUndergroundFluidZ; chunkZ <= maxUndergroundFluidZ; chunkZ += VP.undergroundFluidSizeChunkZ) {
                final UndergroundFluidPosition undergroundFluid = VP.clientCache.getUndergroundFluid(playerDimensionId, chunkX, chunkZ);
                if (undergroundFluid.isProspected()) {
                    undergroundFluidsDrawSteps.add(new UndergroundFluidDrawStep(undergroundFluid));
                }
            }
        }

        return undergroundFluidsDrawSteps;
    }
}
