package com.sinthoras.visualprospecting.integration.xaeroworldmap.renderers;

import com.sinthoras.visualprospecting.integration.model.layers.ThaumcraftNodeLayerManager;
import com.sinthoras.visualprospecting.integration.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.integration.model.locations.ThaumcraftNodeLocation;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.rendersteps.ThaumcraftNodeRenderStep;

import java.util.ArrayList;
import java.util.List;

public class ThaumcraftNodeRenderer extends InteractableLayerRenderer {
    public static ThaumcraftNodeRenderer instance = new ThaumcraftNodeRenderer();

    public ThaumcraftNodeRenderer() {
        super(ThaumcraftNodeLayerManager.instance);
    }

    @Override
    protected List<ThaumcraftNodeRenderStep> generateRenderSteps(List<? extends ILocationProvider> visibleElements) {
        final List<ThaumcraftNodeRenderStep> renderSteps = new ArrayList<>();
        visibleElements.stream()
                .map(element -> (ThaumcraftNodeLocation) element)
                .forEach(location -> renderSteps.add(new ThaumcraftNodeRenderStep(location)));
        return renderSteps;
    }
}
