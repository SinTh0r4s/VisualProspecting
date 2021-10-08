package com.sinthoras.visualprospecting.gui.journeymap;

import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.fullscreen.Fullscreen;

import java.lang.reflect.Field;

public class VPReflection {

    private static Field gridRenderer;

    static {
        try {
            gridRenderer = Fullscreen.class.getDeclaredField("gridRenderer");
            gridRenderer.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GridRenderer getJourneyMapGridRenderer() {
        try {
            return (GridRenderer) gridRenderer.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
