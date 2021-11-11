package com.sinthoras.visualprospecting.gui.journeymap.render;

import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.UndergroundFluidDrawStep;
import com.sinthoras.visualprospecting.gui.model.layers.UndergroundFluidLayerManager;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.gui.model.locations.UndergroundFluidLocation;
import journeymap.client.render.draw.DrawStep;

import java.util.ArrayList;
import java.util.List;

public class UndergroundFluidRenderer extends LayerRenderer {

    public static final UndergroundFluidRenderer instance = new UndergroundFluidRenderer();

    public UndergroundFluidRenderer() {
        super(UndergroundFluidLayerManager.instance);
    }

    @Override
    public List<? extends DrawStep> mapLocationProviderToDrawStep(List<? extends ILocationProvider> visibleElements) {
        final List<UndergroundFluidDrawStep> drawSteps = new ArrayList<>();
        visibleElements.stream()
                .map(element -> (UndergroundFluidLocation) element)
                .forEach(location -> drawSteps.add(new UndergroundFluidDrawStep(location)));
        return drawSteps;
    }
}
