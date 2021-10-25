package com.sinthoras.visualprospecting.gui.journeymap.layers;

import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.ClickableDrawStep;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.LayerButton;
import journeymap.client.model.Waypoint;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class WaypointProviderLayer extends InformationLayer {

    private List<ClickableDrawStep> drawSteps = new ArrayList<>();
    private List<ClickableDrawStep> drawStepsReversed = new ArrayList<>();

    private ClickableDrawStep hoveredDrawStep = null;
    private Waypoint activeWaypoint = null;


    public WaypointProviderLayer(LayerButton layerButton) {
        super(layerButton);
    }

    public Waypoint getActiveWaypoint() {
        return activeWaypoint;
    }

    public void setActiveWaypoint(Waypoint waypoint) {
        activeWaypoint = waypoint;
    }

    public void clearActiveWaypoint() {
        activeWaypoint = null;
    }

    protected abstract List<ClickableDrawStep> generateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ);

    @Override
    public void recacheDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        if(forceRefresh || needsRegenerateDrawSteps(minBlockX, minBlockZ, maxBlockX, maxBlockZ)) {
            drawSteps = generateDrawSteps(minBlockX, minBlockZ, maxBlockX, maxBlockZ);
            drawStepsReversed = new ArrayList<>(drawSteps);
            Collections.reverse(drawStepsReversed);
            forceRefresh = false;
        }
    }

    @Override
    public List<ClickableDrawStep> getDrawStepsCachedForInteraction() {
        return drawSteps;
    }

    public List<ClickableDrawStep> getDrawStepsCachedForRendering() {
        return drawStepsReversed;
    }

    public void onMouseMove(int mouseX, int mouseY) {
        hoveredDrawStep = null;
        for (ClickableDrawStep drawStep : getDrawStepsCachedForInteraction()) {
            if (drawStep.isMouseOver(mouseX, mouseY)) {
                hoveredDrawStep = drawStep;
                return;
            }
        }
    }

    public boolean onMouseAction(boolean isDoubleClick) {
        if(hoveredDrawStep != null) {
            if(isDoubleClick) {
                if(hoveredDrawStep.isWaypoint(activeWaypoint)) {
                    activeWaypoint = null;
                }
                else {
                    activeWaypoint = hoveredDrawStep.toWaypoint();
                }
            }
            return true;
        }
        return false;
    }

    public List<String> getTextTooltip() {
        if(hoveredDrawStep != null) {
            return hoveredDrawStep.getTooltip();
        }
        return null;
    }

    public void drawCustomTooltip(FontRenderer fontRenderer, int mouseX, int mouseY, int displayWidth, int displayHeight) {
        if(hoveredDrawStep != null) {
            hoveredDrawStep.drawTooltip(fontRenderer, mouseX, mouseY, displayWidth, displayHeight);
        }
    }

    public void onActionKeyPressed() {
        if(isLayerActive() && hoveredDrawStep != null) {
            hoveredDrawStep.onActionKeyPressed();
            forceRefresh();
        }
    }
}
