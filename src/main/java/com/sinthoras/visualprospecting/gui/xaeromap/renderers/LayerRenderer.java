package com.sinthoras.visualprospecting.gui.xaeromap.renderers;

import com.sinthoras.visualprospecting.gui.model.SupportedMods;
import com.sinthoras.visualprospecting.gui.model.layers.LayerManager;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.RenderStep;

import java.util.ArrayList;
import java.util.List;

public abstract class LayerRenderer extends com.sinthoras.visualprospecting.gui.model.layers.LayerRenderer {

    private final LayerManager manager;
    protected List<RenderStep> renderSteps = new ArrayList<>();

    public LayerRenderer(LayerManager manager) {
        super(manager, SupportedMods.XaeroMap);
        this.manager = manager;
    }

    public List<RenderStep> getRenderSteps() {
        return renderSteps;
    }

    public boolean isLayerActive() {
        return manager.isLayerActive();
    }

    @Override
    public void updateVisibleElements(List<? extends ILocationProvider> visibleElements) {
        //noinspection unchecked
        renderSteps = (List<RenderStep>) generateRenderSteps(visibleElements);
    }

    protected abstract List<? extends RenderStep> generateRenderSteps(List<? extends ILocationProvider> visibleElements);
}
