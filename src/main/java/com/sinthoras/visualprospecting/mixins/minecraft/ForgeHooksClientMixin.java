package com.sinthoras.visualprospecting.mixins.minecraft;

import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(value = ForgeHooksClient.class, remap = false)
public class ForgeHooksClientMixin {

    // this is only a mixin because it needs to run before the minecraft window is created
    @Inject(method = "createDisplay",
            at = @At("HEAD")
    )
    private static void enableStencilBuffer(CallbackInfo ci) {
        // give me my stencil buffer, forge.
        System.setProperty("forge.forceDisplayStencil", "true");
    }

}
