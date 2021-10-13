package com.sinthoras.visualprospecting;

import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.ServerCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class VP {

    public static SimpleNetworkWrapper network;

    public static ServerCache serverCache = new ServerCache();
    public static ClientCache clientCache = new ClientCache();

    private static Logger LOG = LogManager.getLogger(Tags.MODID);
    public static final int gregTechSmallOreMinimumMeta = 16000;
    public static final int minecraftWorldHeight = 256;
    public static final int chunksPerRegionFileX = 32;
    public static final int chunksPerRegionFileZ = 32;
    public static final int oreVeinSizeChunkX = 3;
    public static final int oreVeinSizeChunkZ = 3;
    public static final int undergroundFluidSizeChunkX = 8;
    public static final int undergroundFluidSizeChunkZ = 8;
    public static final int chunkWidth = 16;
    public static final int chunkHeight = 16;
    public static final int chunkDepth = 16;
    public static final int undergroundFluidChunkProspectingBlockRadius = undergroundFluidSizeChunkX * chunkWidth;


    public static void debug(String message) {
        VP.LOG.debug(formatMessage(message));
    }

    public static void info(String message) {
        VP.LOG.info(formatMessage(message));
    }

    public static void warn(String message) {
        VP.LOG.warn(formatMessage(message));
    }

    public static void error(String message) {
        VP.LOG.error(formatMessage(message));
    }

    private static String formatMessage(String message) {
        return "[" + Tags.VISUALPROSPECTING + "] " + message;
    }
}
