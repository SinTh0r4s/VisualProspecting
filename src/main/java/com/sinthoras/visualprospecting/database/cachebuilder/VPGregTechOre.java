package com.sinthoras.visualprospecting.database.cachebuilder;

import io.xol.enklume.nbt.NBTCompound;
import io.xol.enklume.nbt.NBTInt;
import io.xol.enklume.nbt.NBTShort;
import io.xol.enklume.nbt.NBTString;

public class VPGregTechOre {

    public final boolean isValidGTOre;
    public final short metaData;
    public final int blockY;

    public VPGregTechOre(NBTCompound tileEntity) {
        final NBTString tagId = (NBTString) tileEntity.getTag("id");
        final NBTShort tagMeta = (NBTShort) tileEntity.getTag("m");
        final NBTInt tagBlockY = (NBTInt) tileEntity.getTag("y");

        if (tagId != null && tagId.getText().equals("GT_TileEntity_Ores") && tagBlockY != null
                // Filter out small ores. They start from 16000+
                && tagMeta != null && tagMeta.data < 16000) {
            metaData = (short)(tagMeta.data % 1000); // Filter out stone variants
            isValidGTOre = true;
            blockY = tagBlockY.data;
        }
        else {
            isValidGTOre = false;
            metaData = 0;
            blockY = 0;
        }
    }
}
