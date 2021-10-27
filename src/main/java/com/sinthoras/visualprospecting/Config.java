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
        public static final int minZoomLevelForOreLabel = 1;
        public static final int minZoomLevelForUndergroundFluidDetails = 2;
        public static final int uploadBandwidthBytes = 2000000;
    }

    private static class Categories {
        public static final String general = "general";
        public static final String network = "network";
    }

    public static final int uploadPacketsPerSecond = 10;

    public static boolean enableProspecting = Defaults.enableProspecting;
    public static int cacheGenerationLogUpdateMinTime = Defaults.cacheGenerationLogUpdateMinTime;
    public static boolean recacheVeins = Defaults.recacheVeins;
    public static int minDelayBetweenVeinRequests = Defaults.minDelayBetweenVeinRequests;
    public static int minZoomLevelForOreLabel = Defaults.minZoomLevelForOreLabel;
    public static int minZoomLevelForUndergroundFluidDetails = Defaults.minZoomLevelForUndergroundFluidDetails;
    public static double uploadBandwidthBytes = Defaults.uploadBandwidthBytes;
    public static int uploadSizePerPacket = (int)(uploadBandwidthBytes / uploadPacketsPerSecond);


    public static void syncronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        configuration.load();

        Property enableProspectingProperty = configuration.get(Categories.general, "enableProspecting",
                Defaults.enableProspecting, "[CLIENT] You may want to disable prospecting for low-performance clients.");
        enableProspecting = enableProspectingProperty.getBoolean();

        Property cacheGenerationLogUpdateMinTimeProperty = configuration.get(Categories.general, "cacheGenerationLogUpdateMinTime",
                Defaults.cacheGenerationLogUpdateMinTime, "[SERVER] Minimum between log updates to show progress when " +
                        "caching save files. This happens only ONCE!");
        cacheGenerationLogUpdateMinTime = cacheGenerationLogUpdateMinTimeProperty.getInt();

        Property minDelayBetweenVeinRequestsProperty = configuration.get(Categories.network, "minDelayBetweenVeinRequests",
                Defaults.minDelayBetweenVeinRequests, "[CLIENT + SERVER] Anti spam mechanic: What is the minimum delay (in milliseconds)" +
                        " a player is allowed to request ore vein information.");
        minDelayBetweenVeinRequests = minDelayBetweenVeinRequestsProperty.getInt();

        Property minZoomLevelProperty = configuration.get(Categories.general, "minZoomLevelForOreLabel", Defaults.minZoomLevelForOreLabel,
                "[CLIENT] Minimum zoom level at which ore veins labels are displayed. Zoom starts at 0 and increments linearly.");
        minZoomLevelForOreLabel = minZoomLevelProperty.getInt();

        Property minZoomLevelForUndergroundFluidDetailsProperty = configuration.get(Categories.general, "minZoomLevelForUndergroundFluidDetails",
                Defaults.minZoomLevelForUndergroundFluidDetails, "[CLIENT] Minimum zoom level at which underground fluid details are displayed. Zoom starts at 0 and increments linearly.");
        minZoomLevelForUndergroundFluidDetails = minZoomLevelForUndergroundFluidDetailsProperty.getInt();

        Property uploadBandwidthProperty = configuration.get(Categories.network, "uploadBandwidth", Defaults.uploadBandwidthBytes,
                "[CLIENT + SERVER] Limit the bandwidth (in B/s) a client is allowed to transmit when uploading its prospection data." +
                        " If exceeded, the client will be kicked!");
        uploadBandwidthBytes = uploadBandwidthProperty.getDouble();
        uploadSizePerPacket = (int)(uploadBandwidthBytes / uploadPacketsPerSecond);

        Property recacheVeinsProperty = configuration.get(Categories.general, "recacheVeins", Defaults.recacheVeins,
                "[SERVER] Redo GT ore vein caching if set to True. Will automatically be set back to False the next " +
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
