package com.sinthoras.visualprospecting;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Config {

    private static class Defaults {

        public static final boolean enableProspecting = true;
        public static final int veinSearchDiameter = 8;
        public static final int veinIdentificationMaxUpDown = 10;
        public static final int cacheGenerationLogUpdateMinTime = 5;
        public static final boolean recacheVeins = false;
        public static final int minDelayBetweenVeinRequests = 2000;
        public static final int minZoomLevel = 1;
        public static int blockIdOffset = 17020;
    }

    private static class Categories {
        public static final String general = "general";
    }

    public static boolean enableProspecting = Defaults.enableProspecting;
    public static int veinSearchDiameter = Defaults.veinSearchDiameter;
    public static int veinIdentificationMaxUpDown = Defaults.veinIdentificationMaxUpDown;
    public static int cacheGenerationLogUpdateMinTime = Defaults.cacheGenerationLogUpdateMinTime;
    public static boolean recacheVeins = Defaults.recacheVeins;
    public static int minDelayBetweenVeinRequests = Defaults.minDelayBetweenVeinRequests;
    public static int minZoomLevel = Defaults.minZoomLevel;
    public static int blockIdOffset = Defaults.blockIdOffset;

    public static void syncronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        configuration.load();

        Property enableProspectingProperty = configuration.get(Categories.general, "enableProspecting",
                Defaults.enableProspecting, "You may want to disable prospecting for low-performance clients.");
        enableProspecting = enableProspectingProperty.getBoolean();

        Property veinSearchDiameterProperty = configuration.get(Categories.general, "veinSearchDiameter",
                Defaults.veinSearchDiameter, "Search diameter to find and identify ore veins. The larger the diameter, " +
                        "the lower it takes, but the lower is the chance to miss/missidentify a vein.");
        veinSearchDiameter = veinSearchDiameterProperty.getInt();

        Property veinIdentificationMaxUpDownProperty = configuration.get(Categories.general, "veinIdentificationMaxUpDown",
                Defaults.veinIdentificationMaxUpDown, "What height will be looked up and down when " +
                        "prospecting is looking for all vein metas. This is a upper limit. Will terminate earlier if possible!");
        veinIdentificationMaxUpDown = veinIdentificationMaxUpDownProperty.getInt();

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

        Property blockIdOffsetProperty = configuration.get(Categories.general, "blockIdOffset", Defaults.blockIdOffset,
                "Offset of blockIds for GregTech block registration");
        blockIdOffset = blockIdOffsetProperty.getInt();

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
