package com.sinthoras.visualprospecting.integration.xaeroworldmap;

import com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons.LayerButton;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons.OreVeinButton;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons.ThaumcraftNodeButton;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons.UndergroundFluidButton;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.renderers.*;
import com.sinthoras.visualprospecting.integration.xaerominimap.waypoints.OreVeinWaypointManager;
import com.sinthoras.visualprospecting.integration.xaerominimap.waypoints.ThaumcraftNodeWaypointManager;
import com.sinthoras.visualprospecting.integration.xaerominimap.waypoints.WaypointManager;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isTCNodeTrackerInstalled;

public class XaeroWorldMapState {
    public static XaeroWorldMapState instance = new XaeroWorldMapState();

    public final List<LayerButton> buttons = new ArrayList<>();
    public final List<LayerRenderer> renderers = new ArrayList<>();

    public XaeroWorldMapState() {
        buttons.add(OreVeinButton.instance);
        renderers.add(OreVeinRenderer.instance);

        buttons.add(UndergroundFluidButton.instance);
        renderers.add(UndergroundFluidChunkRenderer.instance);
        renderers.add(UndergroundFluidRenderer.instance);

        if (isTCNodeTrackerInstalled()) {
            buttons.add(ThaumcraftNodeButton.instance);
            renderers.add(ThaumcraftNodeRenderer.instance);
        }
    }
}
