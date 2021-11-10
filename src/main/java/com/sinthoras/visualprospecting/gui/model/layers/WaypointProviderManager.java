package com.sinthoras.visualprospecting.gui.model.layers;

import com.sinthoras.visualprospecting.gui.model.SupportedMap;
import com.sinthoras.visualprospecting.gui.model.waypoints.Waypoint;
import com.sinthoras.visualprospecting.gui.model.buttons.ButtonManager;
import com.sinthoras.visualprospecting.gui.model.locations.IWaypointAndLocationProvider;
import com.sinthoras.visualprospecting.gui.model.waypoints.WaypointManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class WaypointProviderManager extends LayerManager {

    private List<? extends IWaypointAndLocationProvider> visibleElements = new ArrayList<>();
    private Map<SupportedMap, WaypointManager> waypointManagers = new EnumMap<>(SupportedMap.class);

    private Waypoint activeWaypoint = null;

    public WaypointProviderManager(ButtonManager buttonManager) {
        super(buttonManager);
    }

    public void setActiveWaypoint(Waypoint waypoint) {
        activeWaypoint = waypoint;
        visibleElements.forEach(element -> element.onWaypointUpdated(waypoint));
        waypointManagers.values().forEach(translator -> translator.updateActiveWaypoint(waypoint));
    }

    public void clearActiveWaypoint() {
        activeWaypoint = null;
        visibleElements.forEach(IWaypointAndLocationProvider::onWaypointCleared);
        waypointManagers.values().forEach(WaypointManager::clearActiveWaypoint);
    }

    public boolean hasActiveWaypoint() {
        return activeWaypoint != null;
    }

    public void registerWaypointManager(SupportedMap map, WaypointManager waypointManager) {
        waypointManagers.put(map, waypointManager);
    }

    public WaypointManager getWaypointManager(SupportedMap map) {
        return waypointManagers.get(map);
    }

    protected abstract List<? extends IWaypointAndLocationProvider> generateVisibleElements(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ);

    public void recacheVisibleElements(int centerBlockX, int centerBlockZ, int widthBlocks, int heightBlocks) {
        final int radiusBlockX = (widthBlocks + 1) >> 1;
        final int radiusBlockZ = (heightBlocks + 1) >> 1;

        final int minBlockX = centerBlockX - radiusBlockX;
        final int minBlockZ = centerBlockZ - radiusBlockZ;
        final int maxBlockX = centerBlockX + radiusBlockX;
        final int maxBlockZ = centerBlockZ + radiusBlockZ;

        if(forceRefresh || needsRegenerateVisibleElements(minBlockX, minBlockZ, maxBlockX, maxBlockZ)) {
            visibleElements = generateVisibleElements(minBlockX, minBlockZ, maxBlockX, maxBlockZ);
            layerRenderer.values().forEach(layer -> layer.updateVisibleElements(visibleElements));
            forceRefresh = false;
        }
    }
}
