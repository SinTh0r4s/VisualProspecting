package com.sinthoras.visualprospecting.gui.xaeromap.renderers;

import com.sinthoras.visualprospecting.gui.model.layers.UndergroundFluidChunkLayerManager;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.gui.model.locations.UndergroundFluidChunkLocation;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.UndergroundFluidChunkRenderStep;

import java.util.ArrayList;
import java.util.List;

public class UndergroundFluidChunkRenderer extends LayerRenderer {
	public static UndergroundFluidChunkRenderer instance = new UndergroundFluidChunkRenderer();

	public UndergroundFluidChunkRenderer() {
		super(UndergroundFluidChunkLayerManager.instance);
	}

	@Override
	protected List<UndergroundFluidChunkRenderStep> generateRenderSteps(List<? extends ILocationProvider> visibleElements) {
		final List<UndergroundFluidChunkRenderStep> renderSteps = new ArrayList<>();
		visibleElements.stream()
				.map(element -> (UndergroundFluidChunkLocation) element)
				.forEach(location -> renderSteps.add(new UndergroundFluidChunkRenderStep(location)));
		return renderSteps;
	}
}
