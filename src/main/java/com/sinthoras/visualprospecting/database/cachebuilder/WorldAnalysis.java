package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.VP;
import io.xol.enklume.MinecraftWorld;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

public class WorldAnalysis {

    private final MinecraftWorld world;

    public WorldAnalysis(File worldDirectory) throws IOException {
            world = new MinecraftWorld(worldDirectory);
    }

    public void cacheVeins() throws IOException, DataFormatException {
        VP.info("Starting to parse world save to cache GT vein locations. This might take some time...");
        VP.serverCache.reset();
        final List<Integer> dimensionIds = world.getDimensionIds();
        AnalysisProgressTracker.setNumberOfDimensions(dimensionIds.size());
        for(int dimensionId : dimensionIds) {
            final DimensionAnalysis dimension = new DimensionAnalysis(dimensionId);
            dimension.processMinecraftWorld(world);
            AnalysisProgressTracker.dimensionProcessed();
        }
        AnalysisProgressTracker.processingFinished();
    }
}
