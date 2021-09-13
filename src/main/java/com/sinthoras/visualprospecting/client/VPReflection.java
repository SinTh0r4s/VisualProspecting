package com.sinthoras.visualprospecting.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.VPUtils.isBartworksInstalled;

public class VPReflection {

    private static Class bw_OreLayer;
    private static Field veinName;
    private static Field veinPrimaryOreMeta;
    private static Field veinSecondaryOreMeta;
    private static Field veinInBetweenOreMeta;
    private static Field veinSporadicOreMeta;
    private static Field veinSize;

    static {
        if(isBartworksInstalled()) {
            try {
                bw_OreLayer = Class.forName("com.github.bartimaeusnek.bartworks.system.oregen.BW_OreLayer");
                veinName = bw_OreLayer.getSuperclass().getDeclaredField("mWorldGenName");
                veinPrimaryOreMeta = bw_OreLayer.getDeclaredField("mPrimaryMeta");
                veinSecondaryOreMeta = bw_OreLayer.getDeclaredField("mSecondaryMeta");
                veinInBetweenOreMeta = bw_OreLayer.getDeclaredField("mBetweenMeta");
                veinSporadicOreMeta = bw_OreLayer.getDeclaredField("mSporadicMeta");
                veinSize = bw_OreLayer.getDeclaredField("mSize");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Object> getBWOreVeins() {
        try {
            return (List<Object>)bw_OreLayer.getField("sList").get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String getBWOreVeinName(Object vein) {
        try {
            return (String)veinName.get(vein);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "error!";
        }
    }

    public static short getBWOreVeinPrimaryMeta(Object vein) {
        try {
            return (short)veinPrimaryOreMeta.getInt(vein);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static short getBWOreVeinSecondaryMeta(Object vein) {
        try {
            return (short)veinSecondaryOreMeta.getInt(vein);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static short getBWOreVeinInBetweenMeta(Object vein) {
        try {
            return (short)veinInBetweenOreMeta.getInt(vein);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static short getBWOreVeinSporadicMeta(Object vein) {
        try {
            return (short)veinSporadicOreMeta.getInt(vein);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getBWOreVeinSize(Object vein) {
        try {
            return veinSize.getInt(vein);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
