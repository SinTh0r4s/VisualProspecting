package com.sinthoras.visualprospecting.gui.xaeromap.renderers;

import com.sinthoras.visualprospecting.gui.model.layers.ThaumcraftNodeLayerManager;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.gui.model.locations.ThaumcraftNodeLocation;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.ThaumcraftNodeRenderStep;

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
