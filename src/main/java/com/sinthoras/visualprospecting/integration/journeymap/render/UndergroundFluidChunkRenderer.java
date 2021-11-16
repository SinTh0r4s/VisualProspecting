package com.sinthoras.visualprospecting.integration.journeymap.render;

import com.sinthoras.visualprospecting.integration.journeymap.drawsteps.UndergroundFluidChunkDrawStep;
import com.sinthoras.visualprospecting.integration.model.layers.UndergroundFluidChunkLayerManager;
import com.sinthoras.visualprospecting.integration.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.integration.model.locations.UndergroundFluidChunkLocation;
import journeymap.client.render.draw.DrawStep;

import java.util.ArrayList;
import java.util.List;

public class UndergroundFluidChunkRenderer extends LayerRenderer {

    public static final UndergroundFluidChunkRenderer instance = new UndergroundFluidChunkRenderer();

    public UndergroundFluidChunkRenderer() {
        super(UndergroundFluidChunkLayerManager.instance);
    }

    @Override
    public List<? extends DrawStep> mapLocationProviderToDrawStep(List<? extends ILocationProvider> visibleElements) {
        final List<UndergroundFluidChunkDrawStep> drawSteps = new ArrayList<>();
        visibleElements.stream()
                .map(element -> (UndergroundFluidChunkLocation) element)
                .forEach(location -> drawSteps.add(new UndergroundFluidChunkDrawStep(location)));
        return drawSteps;
    }
}
