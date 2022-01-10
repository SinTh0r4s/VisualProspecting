package com.sinthoras.visualprospecting.mixins.journeymap;

import com.sinthoras.visualprospecting.integration.journeymap.JourneyMapState;
import com.sinthoras.visualprospecting.integration.journeymap.render.LayerRenderer;
import com.sinthoras.visualprospecting.integration.model.MapState;
import com.sinthoras.visualprospecting.integration.model.layers.LayerManager;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.minimap.DisplayVars;
import journeymap.client.ui.minimap.MiniMap;
import journeymap.client.ui.minimap.Shape;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.geom.Point2D;
import java.lang.reflect.Field;

@Mixin(MiniMap.class)
public abstract class MiniMapMixin {

    private static Field drawScale;
    private static Field fontScale;
    private static Field shape;
    private static Field minimapWidth;

    static {
        try {
            drawScale = DisplayVars.class.getDeclaredField("drawScale");
            drawScale.setAccessible(true);
            fontScale = DisplayVars.class.getDeclaredField("fontScale");
            fontScale.setAccessible(true);
            shape = DisplayVars.class.getDeclaredField("shape");
            shape.setAccessible(true);
            minimapWidth = DisplayVars.class.getDeclaredField("minimapWidth");
            minimapWidth.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private static float getDrawScale(DisplayVars displayVars) {
        try {
            return drawScale.getFloat(displayVars);
        }
        catch (IllegalAccessException e) {
            return 0.0f;
        }
    }

    private static double getFontScale(DisplayVars displayVars) {
        try {
            return fontScale.getDouble(displayVars);
        }
        catch (IllegalAccessException e) {
            return 0.0;
        }
    }

    private static Shape getShape(DisplayVars displayVars) {
        try {
            return (Shape) shape.get(displayVars);
        }
        catch (IllegalAccessException e) {
            return Shape.Circle;
        }
    }

    private static int getMinimapWidth(DisplayVars displayVars) {
        try {
            return minimapWidth.getInt(displayVars);
        }
        catch (IllegalAccessException e) {
            return 1;
        }
    }

    @Final
    @Shadow(remap = false)
    private static GridRenderer gridRenderer;

    @Final
    @Shadow(remap = false)
    private Minecraft mc;

    @Shadow(remap = false)
    private DisplayVars dv;

    @Inject(method = "drawOnMapWaypoints",
            at = @At(value = "HEAD"),
            remap = false,
            require = 1,
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onBeforeDrawWaypoints(double rotation, CallbackInfo callbackInfo) {
        for(LayerManager layerManager : MapState.instance.layers) {
            if (layerManager.isLayerActive()) {
                if (getShape(dv) == Shape.Circle) {
                    layerManager.recacheMiniMap((int) mc.thePlayer.posX, (int) mc.thePlayer.posZ, getMinimapWidth(dv));
                }
                else {
                    layerManager.recacheMiniMap((int) mc.thePlayer.posX, (int) mc.thePlayer.posZ, gridRenderer.getWidth(), gridRenderer.getHeight());
                }
            }
        }

        for(LayerRenderer layerRenderer : JourneyMapState.instance.renderers) {
            if(layerRenderer.isLayerActive()) {
                for (DrawStep drawStep : layerRenderer.getDrawStepsCachedForRendering()) {
                    drawStep.draw(0.0D, 0.0D, gridRenderer, getDrawScale(dv), getFontScale(dv), rotation);
                }
            }
        }
    }
}
