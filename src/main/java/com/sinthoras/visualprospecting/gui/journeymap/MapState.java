package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.gui.journeymap.buttons.LayerButton;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.OreVeinButton;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.ThaumcraftNodeButton;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.UndergroundFluidButton;
import com.sinthoras.visualprospecting.gui.journeymap.layers.*;
import journeymap.client.render.map.GridRenderer;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isTCNodeTrackerInstalled;
import static com.sinthoras.visualprospecting.gui.journeymap.Reflection.getJourneyMapGridRenderer;

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

    public void openJourneyMapAt(int blockX, int blockZ) {
        final GridRenderer gridRenderer = getJourneyMapGridRenderer();
        assert gridRenderer != null;

        gridRenderer.center(gridRenderer.getMapType(), blockX, blockZ, gridRenderer.getZoom());
    }

    public void openJourneyMapAt(int blockX, int blockZ, int zoom) {
        final GridRenderer gridRenderer = getJourneyMapGridRenderer();
        assert gridRenderer != null;

        gridRenderer.center(gridRenderer.getMapType(), blockX, blockZ, zoom);
    }
}
