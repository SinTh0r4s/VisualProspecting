package com.sinthoras.visualprospecting.integration.xaeroworldmap.renderers;

import com.sinthoras.visualprospecting.integration.model.layers.UndergroundFluidLayerManager;
import com.sinthoras.visualprospecting.integration.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.integration.model.locations.UndergroundFluidLocation;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.rendersteps.UndergroundFluidRenderStep;

import java.util.ArrayList;
import java.util.List;

public class UndergroundFluidRenderer extends LayerRenderer {
    public static UndergroundFluidRenderer instance = new UndergroundFluidRenderer();

    public UndergroundFluidRenderer() {
        super(UndergroundFluidLayerManager.instance);
    }

    @Override
    protected List<UndergroundFluidRenderStep> generateRenderSteps(List<? extends ILocationProvider> visibleElements) {
        final List<UndergroundFluidRenderStep> renderSteps = new ArrayList<>();
        visibleElements.stream()
                .map(element -> (UndergroundFluidLocation) element)
                .forEach(location -> renderSteps.add(new UndergroundFluidRenderStep(location)));
        return renderSteps;
    }
}
