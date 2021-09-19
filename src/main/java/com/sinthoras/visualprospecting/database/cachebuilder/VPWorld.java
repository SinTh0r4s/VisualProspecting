package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.VPCacheWorld;
import io.xol.enklume.MinecraftWorld;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VPWorld {

    private final MinecraftWorld world;

    public VPWorld(File worldDirectory) throws IOException {
            world = new MinecraftWorld(worldDirectory);
    }

    public void cacheVeins() throws IOException {
        VP.info("Starting to parse world save to cache GT vein locations. This might take some time...");
        VPCacheWorld.reset();
        final List<Integer> dimensionIds = world.getDimensions();
        VPProgressTracker.setNumberOfDimensions(dimensionIds.size());
        for(int dimensionId : dimensionIds) {
            final VPDimension dimension = new VPDimension(dimensionId);
            dimension.processMinecraftWorld(world);
            VPProgressTracker.dimensionProcessed();
        }
    }
}
