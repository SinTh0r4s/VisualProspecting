package com.sinthoras.visualprospecting;

import cpw.mods.fml.common.Loader;

public class VPUtils {

    public static boolean isBartworksInstalled() {
        return Loader.isModLoaded("bartworks");
    }
}
