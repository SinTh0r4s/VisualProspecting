package com.sinthoras.visualprospecting.gui.model;

import com.sinthoras.visualprospecting.gui.model.buttons.*;
import com.sinthoras.visualprospecting.gui.model.layers.*;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isTCNodeTrackerInstalled;

public class MapState {
    public static final MapState instance = new MapState();

    public final List<ButtonManager> buttons = new ArrayList<>();
    public final List<LayerManager> layers = new ArrayList<>();

    public MapState() {
        if(isTCNodeTrackerInstalled()) {
            buttons.add(ThaumcraftNodeButtonManager.instance);
            layers.add(ThaumcraftNodeLayerManager.instance);
        }

        buttons.add(UndergroundFluidButtonManager.instance);
        layers.add(UndergroundFluidLayerManager.instance);
        layers.add(UndergroundFluidChunkLayerManager.instance);

        buttons.add(OreVeinButtonManager.instance);
        layers.add(OreVeinLayerManager.instance);
    }

    public void onButtonClicked(LayerButton button) {
        layers.forEach(layer -> layer.onButtonClicked(button));
    }
}
