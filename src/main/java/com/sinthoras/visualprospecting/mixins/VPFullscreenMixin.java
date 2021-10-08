package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.gui.VPMapState;
import journeymap.client.log.StatTimer;
import journeymap.client.ui.fullscreen.Fullscreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Fullscreen.class)
public class VPFullscreenMixin {

    @Inject(method = "drawMap",
            at = @At(value = "INVOKE", target = "Ljourneymap/client/model/MapState;getDrawWaypointSteps()Ljava/util/List;"),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void onBeforeDrawWaypoints(CallbackInfo callbackInfo, boolean refreshReady, StatTimer timer, int xOffset, int yOffset, float drawScale) {
        VPMapState.onDraw(xOffset, yOffset, drawScale, getMapFontScale(), 0.0D);
    }

    @Shadow(remap = false)
    private int getMapFontScale() {
        throw new IllegalStateException("Mixin failed to shadow getMapFontScale()");
    }
}
