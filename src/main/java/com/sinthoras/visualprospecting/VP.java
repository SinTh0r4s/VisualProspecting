package com.sinthoras.visualprospecting;

import com.sinthoras.visualprospecting.database.VPClientCache;
import com.sinthoras.visualprospecting.database.VPServerCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class VP {

    public static SimpleNetworkWrapper network;

    public static VPServerCache serverVeinCache = new VPServerCache();
    public static VPClientCache clientVeinCache = new VPClientCache();

    private static Logger LOG = LogManager.getLogger(VPTags.MODID);
    public static final int chunkWidth = 16;
    public static final int chunkHeight = 16;
    public static final int chunkDepth = 16;
    public static final int gregTechSmallOreMinimumMeta = 16000;


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
        return "[" + VPTags.VISUALPROSPECTING + "] " + message;
    }
}
