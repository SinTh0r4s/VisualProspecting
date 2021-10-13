package com.sinthoras.visualprospecting.database.veintypes;

import com.sinthoras.visualprospecting.VP;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isBartworksInstalled;
import static com.sinthoras.visualprospecting.Utils.isNEIInstalled;

public class Reflection {

    private static Field sList;
    private static Field veinName;
    private static Field veinPrimaryOreMeta;
    private static Field veinSecondaryOreMeta;
    private static Field veinInBetweenOreMeta;
    private static Field veinSporadicOreMeta;
    private static Field veinSize;

    private static Method searchInventories;
    private static Method getSearchExpression;

    static {
        if(isBartworksInstalled()) {
            try {
                final Class<?> BW_OreLayer = Class.forName("com.github.bartimaeusnek.bartworks.system.oregen.BW_OreLayer");
                sList = BW_OreLayer.getField("sList");
                veinName = BW_OreLayer.getSuperclass().getDeclaredField("mWorldGenName");
                veinPrimaryOreMeta = BW_OreLayer.getDeclaredField("mPrimaryMeta");
                veinSecondaryOreMeta = BW_OreLayer.getDeclaredField("mSecondaryMeta");
                veinInBetweenOreMeta = BW_OreLayer.getDeclaredField("mBetweenMeta");
                veinSporadicOreMeta = BW_OreLayer.getDeclaredField("mSporadicMeta");
                veinSize = BW_OreLayer.getDeclaredField("mSize");
            }
            catch (Exception e) {
                VP.error("Failed to integrate Bartworks ore veins!");
                e.printStackTrace();
            }
        }
        if(isNEIInstalled()) {
            try {
                final Class<?> SearchField = Class.forName("codechicken.nei.SearchField");
                searchInventories = SearchField.getDeclaredMethod("searchInventories");
                final Class<?> NEIClientConfig = Class.forName("codechicken.nei.NEIClientConfig");
                getSearchExpression = NEIClientConfig.getDeclaredMethod("getSearchExpression");
            }
            catch (ClassNotFoundException | NoSuchMethodException e) {
                VP.error("Failed to integrate NEI search!");
                e.printStackTrace();
            }
        }
    }

    public static boolean getNEISearchActive() {
        try {
            return (boolean) searchInventories.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getNEISearchString() {
        try {
            return (String) getSearchExpression.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static List<Object> getBWOreVeins() {
        try {
            return (List<Object>) sList.get(null);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String getBWOreVeinName(Object vein) {
        try {
            return (String)veinName.get(vein);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return "error!";
        }
    }

    public static short getBWOreVeinPrimaryMeta(Object vein) {
        try {
            return (short)veinPrimaryOreMeta.getInt(vein);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static short getBWOreVeinSecondaryMeta(Object vein) {
        try {
            return (short)veinSecondaryOreMeta.getInt(vein);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static short getBWOreVeinInBetweenMeta(Object vein) {
        try {
            return (short)veinInBetweenOreMeta.getInt(vein);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static short getBWOreVeinSporadicMeta(Object vein) {
        try {
            return (short)veinSporadicOreMeta.getInt(vein);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getBWOreVeinSize(Object vein) {
        try {
            return veinSize.getInt(vein);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
