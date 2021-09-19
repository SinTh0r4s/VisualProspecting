package com.sinthoras.visualprospecting.database.cachebuilder;

import io.xol.enklume.nbt.*;

import java.util.HashSet;

public class VPChunk {
    public final int chunkX;
    public final int chunkZ;
    private final HashSet<Short> ores = new HashSet<>();

    public VPChunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public void processMinecraftChunk(final NBTCompound chunkRoot) {
        for (final NBTNamed te : ((NBTList) chunkRoot.getTag("Level.TileEntities")).elements) {
            final NBTCompound tileEntity = (NBTCompound) te;
            final NBTString id = (NBTString) tileEntity.getTag("id");
            final NBTShort meta = (NBTShort) tileEntity.getTag("m");
            // Filter out small ores. They start from 16000+
            if (id != null && id.data.equals("GT_TileEntity_Ores") && meta.data < 16000) {
                final short metaData = (short)(meta.data % 1000); // Filter out block type
                ores.add(metaData);
            }
        }
    }

    public HashSet<Short> getOres() {
        return ores;
    }
}