package com.sinthoras.visualprospecting;

import cpw.mods.fml.common.Loader;

public class VPUtils {

    public static boolean isBartworksInstalled() {
        return Loader.isModLoaded("bartworks");
    }

    public static int coordBlockToChunk(int blockCoord) {
        return blockCoord < 0 ? -((-blockCoord - 1) >> 4) - 1 : blockCoord >> 4;
    }

    public static int coordChunkToBlock(int chunkCoord) {
        return chunkCoord < 0 ? -((-chunkCoord) << 4) : chunkCoord << 4;
    }

    public static long chunkCoordsToKey(int chunkX, int chunkZ) {
        return (((long)chunkX) << 32) | (chunkZ & 0xffffffffL);
    }

    public static int nonNegativeModulo(final int value, final int divisor) {
        final int rest = value % divisor;
        if(rest < 0)
            return rest + divisor;
        return rest;
    }

    public static int mapToCenterOreChunkCoord(final int chunkCoord) {
        return chunkCoord - nonNegativeModulo(chunkCoord - 1, 3) + 1;
    }
}
