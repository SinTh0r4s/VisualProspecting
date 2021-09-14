package com.sinthoras.visualprospecting;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class VPConfig {
    private static class Defaults {
        public static final boolean enableProspecting = true;
        public static final int veinSearchDiameter = 8;
        public static final int veinIdentificationHeightUpDown = 5;
    }

    private static class Categories {
        public static final String general = "general";
    }

    public static boolean enableProspecting;
    public static int veinSearchDiameter;
    public static int veinIdentificationHeightUpDown;

    public static void syncronizeConfiguration(java.io.File configurationFile) {
        Configuration configuration = new Configuration(configurationFile);
        configuration.load();

        Property enableProspectingProperty = configuration.get(Categories.general, "enableProspecting",
                Defaults.enableProspecting, "You may want to disable prospecting for low-performance clients.");
        enableProspecting = enableProspectingProperty.getBoolean();

        Property veinSearchDiameterProperty = configuration.get(Categories.general, "veinSearchDiameter",
                Defaults.veinSearchDiameter, "Search diameter to find and identify ore veins. The larger the diameter, " +
                        "the lower it takes, but the lower is the chance to miss/missidentify a vein.");
        veinSearchDiameter = veinSearchDiameterProperty.getInt();

        Property veinIdentificationHeightUpDownProperty = configuration.get(Categories.general, "veinIdentificationHeightUpDown",
                Defaults.veinIdentificationHeightUpDown, "What height will be looked up and down when " +
                        "prospecting is looking for all vein metas.");
        veinIdentificationHeightUpDown = veinIdentificationHeightUpDownProperty.getInt();
    }
}
