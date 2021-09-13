package com.sinthoras.visualprospecting;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class VPConfig {
    private static class Defaults {
        public static final boolean enableProspecting = true;
        public static final int veinLocalizationDiameter = 8;
        public static final int veinIdentificationDiameter = 8;
        public static final int veinIdentificationHeightUpDown = 5;
    }

    private static class Categories {
        public static final String general = "general";
    }

    public static boolean enableProspecting;
    public static int veinLocalizationDiameter;
    public static int veinIdentificationDiameter;
    public static int veinIdentificationHeightUpDown;

    public static void syncronizeConfiguration(java.io.File configurationFile) {
        Configuration configuration = new Configuration(configurationFile);
        configuration.load();

        Property enableProspectingProperty = configuration.get(Categories.general, "enableProspecting",
                Defaults.enableProspecting, "You may want to disable prospecting for low-performance clients.");
        enableProspecting = enableProspectingProperty.getBoolean();

        Property veinLocalizationDiameterProperty = configuration.get(Categories.general, "veinLocalizationRadius",
                Defaults.veinLocalizationDiameter, "What a diameter is to be checked in an ore chunk to decide " +
                        "weather the found ore is located there or not.");
        veinLocalizationDiameter = veinLocalizationDiameterProperty.getInt();

        Property veinIdentificationDiameterProperty = configuration.get(Categories.general, "veinIdentificationDiameter",
                Defaults.veinIdentificationDiameter, "What a diameter is to be used in an ore chunk to find all " +
                        "available ore metas. The vein will be identified based on those found metas.");
        veinIdentificationDiameter = veinIdentificationDiameterProperty.getInt();

        Property veinIdentificationHeightUpDownProperty = configuration.get(Categories.general, "veinIdentificationHeightUpDown",
                Defaults.veinIdentificationHeightUpDown, "What height will be looked up and down when " +
                        "prospecting is looking for all vein metas.");
        veinIdentificationHeightUpDown = veinIdentificationHeightUpDownProperty.getInt();
    }
}
