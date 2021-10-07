package com.sinthoras.visualprospecting.gui;

import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.map.GridRenderer;

import static com.sinthoras.visualprospecting.gui.VPReflection.getJourneyMapGridRenderer;

public class VPOreVeinDrawStep implements DrawStep {

    @Override
    public void draw(double v, double v1, GridRenderer gridRenderer, float v2, double v3, double v4) {

    }

    public static void onDraw(double xOffset, double yOffset, float drawScale, double fontScale, double rotation) {
        final GridRenderer gridRenderer = getJourneyMapGridRenderer();
    }
}
