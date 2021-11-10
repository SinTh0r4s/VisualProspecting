package com.sinthoras.visualprospecting.gui.journeymap.render;

import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.ClickableDrawStep;
import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.ThaumcraftNodeDrawStep;
import com.sinthoras.visualprospecting.gui.model.layers.ThaumcraftNodeLayerManager;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.gui.model.locations.ThaumcraftNodeLocation;

import java.util.ArrayList;
import java.util.List;

public class ThaumcraftNodeRenderer extends WaypointProviderLayerRenderer {

    public static ThaumcraftNodeRenderer instance = new ThaumcraftNodeRenderer();

    public ThaumcraftNodeRenderer() {
        super(ThaumcraftNodeLayerManager.instance);
    }

    @Override
    public List<? extends ClickableDrawStep> mapLocationProviderToDrawStep(List<? extends ILocationProvider> visibleElements) {
        final List<ThaumcraftNodeDrawStep> drawSteps = new ArrayList<>();
        visibleElements.stream()
                .map(element -> (ThaumcraftNodeLocation) element)
                .forEach(location -> drawSteps.add(new ThaumcraftNodeDrawStep(location)));
        return drawSteps;
    }
}
