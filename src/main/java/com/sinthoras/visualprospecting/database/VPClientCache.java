package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPTags;
import journeymap.client.data.WorldData;
import journeymap.client.forge.helper.ForgeHelper;
import journeymap.client.io.FileHandler;
import net.minecraft.client.Minecraft;

import java.io.File;

public class VPClientCache extends VPWorldCache{

    public boolean loadVeinCache(String worldId) {
        final Minecraft minecraft = ForgeHelper.INSTANCE.getClient();

        if (worldId != null) {
            worldId = worldId.replaceAll("\\W+", "~");
        }

        final String suffix = minecraft.isSingleplayer() ? "" : worldId != null ? "_" + worldId : "";
        final File gamemodeFilder = new File(FileHandler.MinecraftDirectory, minecraft.isSingleplayer() ? VPTags.CLIENT_SP_DIR : VPTags.CLIENT_MP_DIR);
        final File worldCacheDirectory = new File(gamemodeFilder, WorldData.getWorldName(minecraft, false) + suffix);
        return super.loadVeinCache(worldCacheDirectory);
    }
}
