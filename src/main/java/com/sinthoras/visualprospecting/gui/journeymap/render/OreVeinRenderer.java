package com.sinthoras.visualprospecting.gui.journeymap.render;

import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.ClickableDrawStep;
import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.OreVeinDrawStep;
import com.sinthoras.visualprospecting.gui.model.layers.OreVeinLayerManager;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.gui.model.locations.OreVeinLocation;

import java.util.ArrayList;
import java.util.List;

public class OreVeinRenderer extends WaypointProviderLayerRenderer {

    public static final OreVeinRenderer instance = new OreVeinRenderer();

    public OreVeinRenderer() {
        super(OreVeinLayerManager.instance);
    }

    @Override
    public List<? extends ClickableDrawStep> mapLocationProviderToDrawStep(List<? extends ILocationProvider> visibleElements) {
        final List<OreVeinDrawStep> drawSteps = new ArrayList<>();
        visibleElements.stream()
                .map(element -> (OreVeinLocation) element)
                .forEach(location -> drawSteps.add(new OreVeinDrawStep(location)));
        return drawSteps;
    }
}
