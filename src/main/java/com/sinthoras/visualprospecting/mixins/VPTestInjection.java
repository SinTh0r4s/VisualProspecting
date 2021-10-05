package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VPVeinTypeCaching.class)
public class VPTestInjection {
    @Inject(method = {"run"}, at = @At("HEAD"), remap = false)
    public void test(CallbackInfo ci) {
        VP.info("HELLO WORLD! - EXCITING!");
    }
}
