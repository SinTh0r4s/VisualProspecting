package com.sinthoras.visualprospecting.gui.xaeromap;

import com.sinthoras.visualprospecting.gui.xaeromap.buttons.LayerButton;
import com.sinthoras.visualprospecting.gui.xaeromap.buttons.OreVeinButton;
import com.sinthoras.visualprospecting.gui.xaeromap.buttons.ThaumcraftNodeButton;
import com.sinthoras.visualprospecting.gui.xaeromap.buttons.UndergroundFluidButton;
import com.sinthoras.visualprospecting.gui.xaeromap.renderers.*;
import com.sinthoras.visualprospecting.gui.xaeromap.waypoints.OreVeinWaypointManager;
import com.sinthoras.visualprospecting.gui.xaeromap.waypoints.ThaumcraftNodeWaypointManager;
import com.sinthoras.visualprospecting.gui.xaeromap.waypoints.WaypointManager;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isTCNodeTrackerInstalled;

public class XaeroMapState {
	public static XaeroMapState instance = new XaeroMapState();

	public final List<LayerButton> buttons = new ArrayList<>();
	public final List<LayerRenderer> renderers = new ArrayList<>();
	public final List<WaypointManager> waypointManagers = new ArrayList<>();

	public XaeroMapState() {
		buttons.add(OreVeinButton.instance);
		renderers.add(OreVeinRenderer.instance);
		waypointManagers.add(OreVeinWaypointManager.instance);

		buttons.add(UndergroundFluidButton.instance);
		renderers.add(UndergroundFluidChunkRenderer.instance);
		renderers.add(UndergroundFluidRenderer.instance);

		if(isTCNodeTrackerInstalled()) {
			buttons.add(ThaumcraftNodeButton.instance);
			renderers.add(ThaumcraftNodeRenderer.instance);
			waypointManagers.add(ThaumcraftNodeWaypointManager.instance);
		}
	}
}
