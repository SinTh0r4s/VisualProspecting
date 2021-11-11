package com.sinthoras.visualprospecting.gui.journeymap.render;

import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.UndergroundFluidChunkDrawStep;
import com.sinthoras.visualprospecting.gui.model.layers.UndergroundFluidChunkLayerManager;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.gui.model.locations.UndergroundFluidChunkLocation;
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
