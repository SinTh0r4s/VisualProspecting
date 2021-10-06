package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.VPUtils;
import net.minecraft.world.World;

import java.io.File;

public class VPServerCache extends VPWorldCache{

    public boolean loadVeinCache(World world) {
        final File worldCacheDirectory = new File(VPUtils.getSubDirectory(VPTags.SERVER_DIR), world.getWorldInfo().getWorldName());
        return super.loadVeinCache(worldCacheDirectory);
    }
}
