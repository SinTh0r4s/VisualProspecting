package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import journeymap.client.JourneymapClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JourneymapClient.class)
public class VPJourneymapClientMixin {

    @Inject(method = "stopMapping", at = @At(value = "RETURN"), remap = false, require = 1)
    public void onStopMapping(CallbackInfo callbackInfo) {
        VP.clientVeinCache.saveVeinCache();
    }
}
