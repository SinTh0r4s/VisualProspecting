package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import journeymap.client.JourneymapClient;
import journeymap.common.Journeymap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JourneymapClient.class)
public class VPJourneymapClientMixin {

    @Inject(method = "startMapping", at = @At(value = "RETURN"), remap = false, require = 1)
    public void onStartMapping(CallbackInfo callbackInfo) {
        final JourneymapClient journeymapClient = (JourneymapClient) Journeymap.proxy;
        // In case FileIO for JourneyMap failed
        if(journeymapClient.isMapping() == false)
            return;

        final String worldId = journeymapClient.getCurrentWorldId();
        VP.clientVeinCache.loadVeinCache(worldId == null? "" : worldId);
    }

    @Inject(method = "stopMapping", at = @At(value = "RETURN"), remap = false, require = 1)
    public void onStopMapping(CallbackInfo callbackInfo) {
        VP.clientVeinCache.saveVeinCache();
    }
}
