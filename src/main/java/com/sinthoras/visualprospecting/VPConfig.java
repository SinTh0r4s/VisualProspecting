package com.sinthoras.visualprospecting;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class VPConfig {
    private static class Defaults {
        public static final boolean enableProspecting = true;
        public static final int veinSearchDiameter = 8;
        public static final int veinIdentificationMaxUpDown = 10;
        public static final int cacheGenerationLogUpdateMinTime = 5;
        public static final boolean recacheVeins = false;
    }

    private static class Categories {
        public static final String general = "general";
    }

    public static boolean enableProspecting;
    public static int veinSearchDiameter;
    public static int veinIdentificationMaxUpDown;
    public static int cacheGenerationLogUpdateMinTime;
    public static boolean recacheVeins;

    public static void syncronizeConfiguration() {
        Configuration configuration = new Configuration(VP.configFile);
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
