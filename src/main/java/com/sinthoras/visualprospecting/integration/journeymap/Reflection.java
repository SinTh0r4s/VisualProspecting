package com.sinthoras.visualprospecting.integration.journeymap;

import com.sinthoras.visualprospecting.VP;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.fullscreen.Fullscreen;

import java.lang.reflect.Field;

public class Reflection {

    private static Field gridRenderer;

    static {
        try {
            gridRenderer = Fullscreen.class.getDeclaredField("gridRenderer");
            gridRenderer.setAccessible(true);
        }
        catch (Exception e) {
            VP.error("Failed to access private fields in JourneyMap!");
            e.printStackTrace();
        }
    }

    public static GridRenderer getJourneyMapGridRenderer() {
        try {
            return (GridRenderer) gridRenderer.get(null);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
