package com.sinthoras.visualprospecting.integration.model.layers;

import com.sinthoras.visualprospecting.integration.model.SupportedMods;
import com.sinthoras.visualprospecting.integration.model.locations.ILocationProvider;

import java.util.List;

public abstract class LayerRenderer {

    public LayerRenderer(LayerManager manager, SupportedMods map) {
        manager.registerLayerRenderer(map, this);
    }

    public abstract void updateVisibleElements(List<? extends ILocationProvider> visibleElements);
}
