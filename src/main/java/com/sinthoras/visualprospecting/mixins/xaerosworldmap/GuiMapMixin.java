package com.sinthoras.visualprospecting.mixins.xaerosworldmap;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.integration.model.MapState;
import com.sinthoras.visualprospecting.integration.model.layers.LayerManager;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.XaeroWorldMapState;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons.LayerButton;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons.SizedGuiTexturedButton;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.renderers.InteractableLayerRenderer;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.renderers.LayerRenderer;
import com.sinthoras.visualprospecting.integration.xaeroworldmap.rendersteps.RenderStep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xaero.map.MapProcessor;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiMap;
import xaero.map.gui.ScreenBase;
import xaero.map.misc.Misc;

@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference", "UnusedMixin"})
@Mixin(value = GuiMap.class, remap = false)
public abstract class GuiMapMixin extends ScreenBase {

    @Unique
    private int oldMouseX = 0;
    @Unique
    private int oldMouseY = 0;
    @Unique
    private long timeLastClick = 0;

    protected GuiMapMixin(GuiScreen parent, GuiScreen escape) {
        super(parent, escape);
    }

    @Shadow
    private double cameraX;

    @Shadow
    private double cameraZ;

    @Shadow
    private double scale;

    @Shadow
    public abstract void addGuiButton(GuiButton b);

    @Shadow
    private int screenScale;

    @Inject(method = "<init>",
            at = @At("RETURN")
    )
    private void injectConstruct(GuiScreen parent, GuiScreen escape, MapProcessor mapProcessor, EntityPlayer player, CallbackInfo ci) {
        MapState.instance.layers.forEach(LayerManager::onOpenMap);
    }

    // apparently mixin can read the obfuscated names even with the deobf jar loaded. weird
    // i guess all the errors mcdev shows here aren't real?
    // deobf method = "drawScreen"
    @Inject(method = "func_73863_a",
            at = @At(value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V",
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    // why is this method so long. this isnt even 1/5 of the way through and look at how many locals there are already
    private void injectPreRender(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci, Minecraft mc, long startTime, long passed, double passedScrolls,
                                 int direction, Object var12, boolean mapLoaded, boolean noWorldMapEffect, int mouseXPos, int mouseYPos, double scaleMultiplier,
                                 double oldMousePosZ, double preScale, double fboScale, double secondaryScale, double mousePosX, double mousePosZ, int mouseFromCentreX,
                                 int mouseFromCentreY, double oldMousePosX, int textureLevel, int leveledRegionShift) {
        // snap the camera to whole pixel values. works around a rendering issue but causes another when framerate is uncapped
        if (mc.gameSettings.limitFramerate >= 255 && !mc.gameSettings.enableVsync) {
            cameraX = Math.round(cameraX * scale) / scale;
            cameraZ = Math.round(cameraZ * scale) / scale;
        }

        for (LayerRenderer layer : XaeroWorldMapState.instance.renderers) {
            if (layer instanceof InteractableLayerRenderer) {
                ((InteractableLayerRenderer) layer).updateHovered(mousePosX, mousePosZ, cameraX, cameraZ, scale);
            }
        }
    }

    // deobf method = "drawScreen"
    @Inject(method = "func_73863_a",
            at = @At(value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            ), slice = @Slice(
                    from = @At(value = "INVOKE",
                            target = "Lorg/lwjgl/opengl/GL14;glBlendFuncSeparate(IIII)V"
                    ),
                    to = @At(value = "INVOKE",
                            target = "Lxaero/map/mods/SupportXaeroMinimap;renderWaypoints(Lnet/minecraft/client/gui/GuiScreen;DDIIDDDDLjava/util/regex/Pattern;Ljava/util/regex/Pattern;FLxaero/map/mods/gui/Waypoint;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/ScaledResolution;)Lxaero/map/mods/gui/Waypoint;"
                    )
            )
    )
    private void injectDraw(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        for (LayerManager layerManager : MapState.instance.layers) {
            if (layerManager.isLayerActive()) {
                // +20s are to work around precision loss from casting to int and right-shifting
                layerManager.recacheFullscreenMap((int) cameraX, (int) cameraZ, (int) (mc.displayWidth / scale) + 20, (int) (mc.displayHeight / scale) + 20);
            }
        }

        for (LayerRenderer renderer : XaeroWorldMapState.instance.renderers) {
            if (renderer.isLayerActive()) {
                for (RenderStep step : renderer.getRenderSteps()) {
                    step.draw(this, cameraX, cameraZ, scale);
                }
            }
        }
    }

    // deobf method = drawScreen
    @Inject(method = "func_73863_a",
            at = @At(value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTranslated(DDD)V"
            ),
            slice = @Slice(
                    from = @At(value = "FIELD",
                            // deobf target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;"
                            target = "Lnet/minecraft/client/Minecraft;field_71462_r:Lnet/minecraft/client/gui/GuiScreen;",
                            opcode = Opcodes.GETFIELD
                    ),
                    to = @At(value = "INVOKE",
                            target = "Lxaero/map/gui/CursorBox;drawBox(IIII)V"
                    )
            )
    )
    private void injectDrawTooltip(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        for (LayerRenderer layer : XaeroWorldMapState.instance.renderers) {
            if (layer instanceof InteractableLayerRenderer && layer.isLayerActive()) {
                ((InteractableLayerRenderer) layer).drawTooltip(this, scale, screenScale);
            }
        }
    }

    // deobf method = "initGui"
    @Inject(method = "func_73866_w_",
            at = @At(value = "INVOKE",
                    target = "Lorg/lwjgl/input/Keyboard;enableRepeatEvents(Z)V"
            )
    )
    private void injectInitButtons(CallbackInfo ci) {
        for (int i = 0; i < XaeroWorldMapState.instance.buttons.size(); i++) {
            LayerButton layerButton = XaeroWorldMapState.instance.buttons.get(i);
            SizedGuiTexturedButton button = new SizedGuiTexturedButton(0, height - 20 * (i + 1),
                    layerButton.textureLocation, (btn) -> layerButton.toggle(),
                    new CursorBox(layerButton.getButtonTextKey()));
            layerButton.setButton(button);
            addGuiButton(button);
        }
    }

    @Inject(method = "onInputPress",
            at = @At("TAIL")
    )
    private void injectListenKeypress(boolean mouse, int code, CallbackInfoReturnable<Boolean> cir) {
        if (Misc.inputMatchesKeyBinding(mouse, code, VP.keyAction)) {
            for (LayerRenderer layer : XaeroWorldMapState.instance.renderers) {
                if (layer instanceof InteractableLayerRenderer) {
                    ((InteractableLayerRenderer) layer).doActionKeyPress();
                }
            }
        }
    }

    @Inject(method = "mapClicked",
            at = @At("TAIL")
    )
    private void injectListenClick(int button, int x, int y, CallbackInfo ci) {
        if (button == 0) {
            final long timestamp = System.currentTimeMillis();
            final boolean isDoubleClick = x == oldMouseX && y == oldMouseY && timestamp - timeLastClick < 500;
            oldMouseX = x;
            oldMouseY = y;
            timeLastClick = isDoubleClick ? 0 : timestamp;

            if (isDoubleClick) {
                for (LayerRenderer layer : XaeroWorldMapState.instance.renderers) {
                    if (layer instanceof InteractableLayerRenderer && layer.isLayerActive()) {
                        ((InteractableLayerRenderer) layer).doDoubleClick();
                    }
                }
            }
        }
    }
}
