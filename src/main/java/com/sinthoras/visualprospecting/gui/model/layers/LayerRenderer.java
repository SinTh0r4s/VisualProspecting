package com.sinthoras.visualprospecting.gui.model.layers;

import com.sinthoras.visualprospecting.gui.model.SupportedMods;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;

import java.util.List;

public abstract class LayerRenderer {

    public LayerRenderer(LayerManager manager, SupportedMods map) {
        manager.registerLayerRenderer(map, this);
    }

    public abstract void updateVisibleElements(List<? extends ILocationProvider> visibleElements);
}
