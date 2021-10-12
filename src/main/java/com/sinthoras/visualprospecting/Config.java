package com.sinthoras.visualprospecting;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Config {

    private static class Defaults {

        public static final boolean enableProspecting = true;
        public static final int cacheGenerationLogUpdateMinTime = 5;
        public static final boolean recacheVeins = false;
        public static final int minDelayBetweenVeinRequests = 2000;
        public static final int minZoomLevel = 1;
    }

    private static class Categories {
        public static final String general = "general";
    }

    public static boolean enableProspecting = Defaults.enableProspecting;
    public static int cacheGenerationLogUpdateMinTime = Defaults.cacheGenerationLogUpdateMinTime;
    public static boolean recacheVeins = Defaults.recacheVeins;
    public static int minDelayBetweenVeinRequests = Defaults.minDelayBetweenVeinRequests;
    public static int minZoomLevel = Defaults.minZoomLevel;

    public static void syncronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        configuration.load();

        Property enableProspectingProperty = configuration.get(Categories.general, "enableProspecting",
                Defaults.enableProspecting, "You may want to disable prospecting for low-performance clients.");
        enableProspecting = enableProspectingProperty.getBoolean();

        Property cacheGenerationLogUpdateMinTimeProperty = configuration.get(Categories.general, "cacheGenerationLogUpdateMinTime",
                Defaults.cacheGenerationLogUpdateMinTime, "Minimum between log updates to show progress when " +
                        "caching save files. This happens only ONCE!");
        cacheGenerationLogUpdateMinTime = cacheGenerationLogUpdateMinTimeProperty.getInt();

        Property minDelayBetweenVeinRequestsProperty = configuration.get(Categories.general, "minDelayBetweenVeinRequests",
                Defaults.minDelayBetweenVeinRequests, "Anti spam mechanic: What is the minimum delay (in milliseconds)" +
                        " a player is allowed to request ore vein information.");
        minDelayBetweenVeinRequests = minDelayBetweenVeinRequestsProperty.getInt();

        Property minZoomLevelProperty = configuration.get(Categories.general, "minZoomLevel", Defaults.minZoomLevel,
                "Sets the minimum zoom level at which ore veins are displayed. Zoom starts at 0 and increments linearly.");
        minZoomLevel = minZoomLevelProperty.getInt();

        Property recacheVeinsProperty = configuration.get(Categories.general, "recacheVeins", Defaults.recacheVeins,
                "Redo GT ore vein caching if set to True. Will automatically be set back to False the next " +
                        "time the game is started.");
        recacheVeins = recacheVeinsProperty.getBoolean();
        if(recacheVeins) {
            recacheVeinsProperty.set(false);
        }

        if(configuration.hasChanged()) {
            configuration.save();
        }
    }
}
