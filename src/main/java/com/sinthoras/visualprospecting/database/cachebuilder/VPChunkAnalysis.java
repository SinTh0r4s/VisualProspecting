package com.sinthoras.visualprospecting.database.cachebuilder;

import io.xol.enklume.nbt.*;

import java.util.HashSet;

// A slim, but faster version to identify >90% of veins
public class VPChunkAnalysis {
    public final int chunkX;
    public final int chunkZ;
    private final HashSet<Short> ores = new HashSet<>();
    private int minVeinBlockY = 256;

    public VPChunkAnalysis(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public void processMinecraftChunk(final NBTCompound chunkRoot) {
        for (final NBTNamed tileEntity : ((NBTList) chunkRoot.getTag("Level.TileEntities")).elements) {
            final VPGregTechOre gtOre = new VPGregTechOre((NBTCompound) tileEntity);
            if(gtOre.isValidGTOre) {
                ores.add(gtOre.metaData);
                if(minVeinBlockY > gtOre.blockY)
                    minVeinBlockY = gtOre.blockY;
            }
        }
    }

    public HashSet<Short> getOres() {
        return ores;
    }

    public int getVeinBlockY() {
        return minVeinBlockY;
    }
}