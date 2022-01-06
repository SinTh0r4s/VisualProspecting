package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.text.DecimalFormat;

public class AnalysisProgressTracker {

    private static int numberOfDimensions = 0;
    private static int dimensionsProcessed = 0;
    private static int numberOfRegionFiles = 0;
    private static int regionFilesProcessed = 0;
    private static long lastLogUpdate = 0;
    private static long timestampMS = 0;

    public static synchronized void setNumberOfDimensions(int numberOfDimensions) {
        timestampMS = System.currentTimeMillis();
        AnalysisProgressTracker.numberOfDimensions = numberOfDimensions;
        dimensionsProcessed = 0;
        updateLog();
    }

    public static synchronized void dimensionProcessed() {
        dimensionsProcessed++;
        updateLog();
    }

    public static synchronized void announceFastDimension(int dimensionId) {
        final String message = "Processing dimension with id " + dimensionId + " with fast scanning.";
        VP.info(message);
        if(Utils.isLogicalClient()) {
            MinecraftServer.getServer().userMessage = message;
        }
    }

    public static synchronized void announceSlowDimension(int dimensionId) {
        final String message = "Processing dimension with id " + dimensionId + " with slow (safe) scanning.";
        VP.info(message);
        if(Utils.isLogicalClient()) {
            MinecraftServer.getServer().userMessage = message;
        }
    }

    public static synchronized void setNumberOfRegionFiles(int numberOfRegionFiles) {
        AnalysisProgressTracker.numberOfRegionFiles = numberOfRegionFiles;
        regionFilesProcessed = 0;
        updateLog();
    }

    public static synchronized void regionFileProcessed() {
        regionFilesProcessed++;
        updateLog();
    }

    private static synchronized void updateLog() {
        long timestamp = System.currentTimeMillis();
        if(timestamp - (Config.cacheGenerationLogUpdateMinTime * 1000) > lastLogUpdate) {
            lastLogUpdate = timestamp;
            final String message = "Caching GT ore generation meta data - Dimension ("
                    + (dimensionsProcessed + 1) + "/" + numberOfDimensions + ")  "
                    + (numberOfRegionFiles == 0 ? 0 : ((regionFilesProcessed * 100) / numberOfRegionFiles)) + "%";
            VP.info(message);
            if(Utils.isLogicalClient()) {
                MinecraftServer.getServer().userMessage = message + "%";  // Escape % for String.format
            }

        }
    }

    public static synchronized void processingFinished() {
        final long elapsedTimeMS = System.currentTimeMillis() - timestampMS;
        DecimalFormat format = new DecimalFormat();
        format.setMinimumFractionDigits(1);
        format.setMaximumFractionDigits(1);
        final String message = "Parsing complete! Thank you for your patience.  - Duration: " + format.format(elapsedTimeMS / 1000) + "sec";
        VP.info(message);
        if(Utils.isLogicalClient()) {
            MinecraftServer.getServer().userMessage = message;
        }
    }

    public static synchronized void notifyCorruptFile(File regionFile) {
        final String message = "Encountered corrupt/malformed/modified save file: " + regionFile;
        VP.info(message);
        if(Utils.isLogicalClient()) {
            MinecraftServer.getServer().userMessage = message;
        }
    }
}
