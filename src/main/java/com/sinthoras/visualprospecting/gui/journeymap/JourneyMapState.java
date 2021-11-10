package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.gui.journeymap.buttons.LayerButton;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.OreVeinButton;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.ThaumcraftNodeButton;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.UndergroundFluidButton;
import com.sinthoras.visualprospecting.gui.journeymap.render.*;
import com.sinthoras.visualprospecting.gui.journeymap.waypoints.OreVeinWaypointManager;
import com.sinthoras.visualprospecting.gui.journeymap.waypoints.ThaumcraftNodeWaypointManager;
import com.sinthoras.visualprospecting.gui.journeymap.waypoints.WaypointManager;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isTCNodeTrackerInstalled;

public class JourneyMapState {

    public static JourneyMapState instance = new JourneyMapState();

    public final List<LayerButton> buttons = new ArrayList<>();
    public final List<LayerRenderer> renderers = new ArrayList<>();
    public final List<WaypointManager> waypointManagers = new ArrayList<>();

    public JourneyMapState() {
        if(isTCNodeTrackerInstalled()) {
            buttons.add(ThaumcraftNodeButton.instance);
            renderers.add(ThaumcraftNodeRenderer.instance);
            waypointManagers.add(ThaumcraftNodeWaypointManager.instance);
        }

        buttons.add(UndergroundFluidButton.instance);
        renderers.add(UndergroundFluidRenderer.instance);
        renderers.add(UndergroundFluidChunkRenderer.instance);

        buttons.add(OreVeinButton.instance);
        renderers.add(OreVeinRenderer.instance);
        waypointManagers.add(OreVeinWaypointManager.instance);
    }
}
