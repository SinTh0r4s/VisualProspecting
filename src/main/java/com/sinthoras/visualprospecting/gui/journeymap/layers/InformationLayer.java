package com.sinthoras.visualprospecting.gui.journeymap.layers;

import com.sinthoras.visualprospecting.gui.journeymap.buttons.LayerButton;
import journeymap.client.render.draw.DrawStep;

import java.util.ArrayList;
import java.util.List;

public abstract class InformationLayer {

    private final LayerButton layerButton;

    protected boolean forceRefresh = false;
    private List<? extends DrawStep> drawSteps = new ArrayList<>();

    public InformationLayer(LayerButton layerButton) {
        this.layerButton = layerButton;
    }

    public boolean isLayerActive() {
        return layerButton.isLayerActive();
    }

    public void forceRefresh() {
        forceRefresh = true;
    }

    public void onOpenMap() {

    }

    protected abstract List<? extends DrawStep> generateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ);

    protected boolean needsRegenerateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        return true;
    }

    public void recacheDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        if(forceRefresh || needsRegenerateDrawSteps(minBlockX, minBlockZ, maxBlockX, maxBlockZ)) {
            drawSteps = generateDrawSteps(minBlockX, minBlockZ, maxBlockX, maxBlockZ);
            forceRefresh = false;
        }
    }

    public List<? extends DrawStep> getDrawStepsCachedForInteraction() {
        return drawSteps;
    }

    public List<? extends DrawStep> getDrawStepsCachedForRendering() {
        return drawSteps;
    }
}
