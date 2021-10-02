package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.VPCacheWorld;
import io.xol.enklume.MinecraftWorld;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

public class VPWorldAnalysis {

    private final MinecraftWorld world;

    public VPWorldAnalysis(File worldDirectory) throws IOException {
            world = new MinecraftWorld(worldDirectory);
    }

    public void cacheVeins() throws IOException, DataFormatException {
        VP.info("Starting to parse world save to cache GT vein locations. This might take some time...");
        VPCacheWorld.reset();
        final List<Integer> dimensionIds = world.getDimensionIds();
        VPAnalysisProgressTracker.setNumberOfDimensions(dimensionIds.size());
        for(int dimensionId : dimensionIds) {
            final VPDimensionAnalysis dimension = new VPDimensionAnalysis(dimensionId);
            dimension.processMinecraftWorld(world);
            VPAnalysisProgressTracker.dimensionProcessed();
        }
        VPAnalysisProgressTracker.processingFinished();
    }
}
