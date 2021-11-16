package com.sinthoras.visualprospecting.integration.journeymap.render;

import com.sinthoras.visualprospecting.integration.journeymap.drawsteps.ClickableDrawStep;
import com.sinthoras.visualprospecting.integration.model.layers.WaypointProviderManager;
import com.sinthoras.visualprospecting.integration.model.locations.ILocationProvider;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class WaypointProviderLayerRenderer extends LayerRenderer {

    private final WaypointProviderManager manager;

    private List<ClickableDrawStep> drawSteps = new ArrayList<>();
    private List<ClickableDrawStep> drawStepsReversed = new ArrayList<>();

    private ClickableDrawStep hoveredDrawStep = null;

    public WaypointProviderLayerRenderer(WaypointProviderManager manager) {
        super(manager);
        this.manager = manager;
    }

    @Override
    public List<ClickableDrawStep> getDrawStepsCachedForInteraction() {
        return drawSteps;
    }

    @Override
    public List<ClickableDrawStep> getDrawStepsCachedForRendering() {
        return drawStepsReversed;
    }

    @Override
    public void updateVisibleElements(List<? extends ILocationProvider> visibleElements) {
        drawSteps = (List<ClickableDrawStep>) mapLocationProviderToDrawStep(visibleElements);
        drawStepsReversed = new ArrayList<>(drawSteps);
        Collections.reverse(drawStepsReversed);
    }

    protected abstract List<? extends ClickableDrawStep> mapLocationProviderToDrawStep(List<? extends ILocationProvider> visibleElements);

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
                if(hoveredDrawStep.getLocationProvider().isActiveAsWaypoint()) {
                    manager.clearActiveWaypoint();
                }
                else {
                    manager.setActiveWaypoint(hoveredDrawStep.getLocationProvider().toWaypoint());
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
        if(manager.isLayerActive() && hoveredDrawStep != null) {
            hoveredDrawStep.onActionKeyPressed();
            manager.forceRefresh();
        }
    }

    public boolean isLayerActive() {
        return manager.isLayerActive();
    }
}
