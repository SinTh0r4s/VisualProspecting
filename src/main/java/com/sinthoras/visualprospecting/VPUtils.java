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
}
