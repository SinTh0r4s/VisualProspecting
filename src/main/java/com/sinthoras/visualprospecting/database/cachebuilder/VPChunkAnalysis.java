package com.sinthoras.visualprospecting.database.cachebuilder;

import io.xol.enklume.nbt.*;

import java.util.HashSet;

public class VPChunkAnalysis {
    public final int chunkX;
    public final int chunkZ;
    private final HashSet<Short> ores = new HashSet<>();
    private int minVeinBlockY = 256;

    public VPChunkAnalysis(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    // Helpful read: https://minecraft.fandom.com/wiki/Chunk_format
    public void processMinecraftChunk(final NBTCompound chunkRoot) {
        for (final NBTNamed te : ((NBTList) chunkRoot.getTag("Level.TileEntities")).elements) {
            final NBTCompound tileEntity = (NBTCompound) te;
            final NBTString id = (NBTString) tileEntity.getTag("id");
            final NBTShort meta = (NBTShort) tileEntity.getTag("m");
            final NBTInt blockY = (NBTInt) tileEntity.getTag("y");
            // Filter out small ores. They start from 16000+
            if (id != null && id.data.equals("GT_TileEntity_Ores") && meta.data < 16000) {
                final short metaData = (short)(meta.data % 1000); // Filter out block type
                ores.add(metaData);
                if(minVeinBlockY > blockY.data)
                    minVeinBlockY = blockY.data;
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