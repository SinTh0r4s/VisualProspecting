package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPTags;
import journeymap.client.data.WorldData;
import journeymap.client.io.FileHandler;
import net.minecraft.client.Minecraft;
import journeymap.client.JourneymapClient;
import journeymap.client.forge.helper.ForgeHelper;
import journeymap.common.Journeymap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(JourneymapClient.class)
public class VPJourneymapClientMixin {
    //@At(value = "RETURN", ordinal = 1)
    @Inject(method = "startMapping", at = @At(value = "RETURN"), remap = false, require = 1)
    public void onStartMapping(CallbackInfo callbackInfo) {
        final Minecraft minecraft = ForgeHelper.INSTANCE.getClient();
        final JourneymapClient journeymapClient = (JourneymapClient) Journeymap.proxy;

        // Incase FileIO for JourneyMap failed
        if(journeymapClient.isMapping() == false)
            return;

        String worldId = journeymapClient.getCurrentWorldId();
        if (worldId != null) {
            worldId = worldId.replaceAll("\\W+", "~");
        }

        final String suffix = minecraft.isSingleplayer() ? "" : worldId != null ? "_" + worldId : "";
        final File gamemodeFilder = new File(FileHandler.MinecraftDirectory, minecraft.isSingleplayer() ? VPTags.SP_DATA_DIR : VPTags.MP_DATA_DIR);
        File veinCacheFolder = new File(gamemodeFilder, WorldData.getWorldName(minecraft, false) + suffix);
        veinCacheFolder.mkdirs();

        // TODO: create or load veinCacheFile
        VP.info("START MAPPING!");
        VP.info(veinCacheFolder.toString());
    }

    @Inject(method = "stopMapping", at = @At(value = "RETURN"), remap = false, require = 1)
    public void onStopMapping(CallbackInfo callbackInfo) {
        // TODO: save files to disk and disable vein identification on interaction
        VP.info("STOP MAPPING!");
    }
}
