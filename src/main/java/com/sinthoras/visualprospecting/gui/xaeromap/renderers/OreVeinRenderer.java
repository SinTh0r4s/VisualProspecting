package com.sinthoras.visualprospecting.gui.xaeromap.renderers;

import com.sinthoras.visualprospecting.gui.model.layers.OreVeinLayerManager;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.gui.model.locations.OreVeinLocation;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.OreVeinRenderStep;

import java.util.ArrayList;
import java.util.List;

public class OreVeinRenderer extends InteractableLayerRenderer {
	public static OreVeinRenderer instance = new OreVeinRenderer();

	public OreVeinRenderer() {
		super(OreVeinLayerManager.instance);
	}

	@Override
	protected List<OreVeinRenderStep> generateRenderSteps(List<? extends ILocationProvider> visibleElements) {
		final List<OreVeinRenderStep> renderSteps = new ArrayList<>();
        visibleElements.stream()
                .map(element -> (OreVeinLocation) element)
                .forEach(location -> renderSteps.add(new OreVeinRenderStep(location)));
        return renderSteps;
	}

	@Override
	public void doDoubleClick() {
		if (hovered != null) {
			if (hovered.getLocationProvider().isActiveAsWaypoint()) {
				manager.clearActiveWaypoint();
			} else {
				manager.setActiveWaypoint(hovered.getLocationProvider().toWaypoint());
			}
		}
	}
}
