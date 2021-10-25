package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.gui.journeymap.buttons.LayerButton;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.OreVeinButton;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.ThaumcraftNodeButton;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.UndergroundFluidButton;
import com.sinthoras.visualprospecting.gui.journeymap.layers.*;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isTCNodeTrackerInstalled;

public class MapState {
    public static final MapState instance = new MapState();

    public final List<LayerButton> buttons = new ArrayList<>();
    public final List<InformationLayer> layers = new ArrayList<>();

    public MapState() {
        if(isTCNodeTrackerInstalled()) {
            buttons.add(ThaumcraftNodeButton.instance);
            layers.add(ThaumcraftNodeLayer.instance);
        }

        buttons.add(UndergroundFluidButton.instance);
        layers.add(UndergroundFluidChunkLayer.instance);
        layers.add(UndergroundFluidLayer.instance);

        buttons.add(OreVeinButton.instance);
        layers.add(OreVeinLayer.instance);
    }
}
