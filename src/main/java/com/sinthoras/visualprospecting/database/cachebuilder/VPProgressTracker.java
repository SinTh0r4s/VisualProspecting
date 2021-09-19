package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPConfig;

public class VPProgressTracker {

    private static int numberOfDimensions = 0;
    private static int dimensionsProcessed = 0;
    private static int numberOfRegionFiles = 0;
    private static int regionFilesProcessed = 0;
    private static long lastLogUpdate = 0;

    public static void setNumberOfDimensions(int numberOfDimensions) {
        VPProgressTracker.numberOfDimensions = numberOfDimensions;
        dimensionsProcessed = 0;
        updateLog();
    }

    public static void dimensionProcessed() {
        dimensionsProcessed++;
        updateLog();
    }

    public static void setNumberOfRegionFiles(int numberOfRegionFiles) {
        VPProgressTracker.numberOfRegionFiles = numberOfRegionFiles;
        regionFilesProcessed = 0;
        updateLog();
    }

    public static void regionFileProcessed() {
        regionFilesProcessed++;
        updateLog();
    }

    private static void updateLog() {
        long timestamp = System.currentTimeMillis();
        if(timestamp - (VPConfig.cacheGenerationLogUpdateMinTime * 1000) > lastLogUpdate) {
            lastLogUpdate = timestamp;
            VP.info("Caching GT ore generation meta data - Dimension ("
                    + (dimensionsProcessed + 1) + "/" + numberOfDimensions + ")  "
                    + (numberOfRegionFiles == 0 ? 0 : ((regionFilesProcessed * 100) / (numberOfRegionFiles * 100))) + "%");
        }
    }
}
