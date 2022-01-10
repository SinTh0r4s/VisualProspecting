package com.sinthoras.visualprospecting.mixins.journeymap;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.integration.journeymap.JourneyMapState;
import com.sinthoras.visualprospecting.integration.journeymap.buttons.LayerButton;
import com.sinthoras.visualprospecting.integration.journeymap.render.LayerRenderer;
import com.sinthoras.visualprospecting.integration.journeymap.render.WaypointProviderLayerRenderer;
import com.sinthoras.visualprospecting.integration.model.MapState;
import com.sinthoras.visualprospecting.integration.model.layers.LayerManager;
import journeymap.client.Constants;
import journeymap.client.io.ThemeFileHandler;
import journeymap.client.log.LogFormatter;
import journeymap.client.log.StatTimer;
import journeymap.client.model.BlockCoordIntPair;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.UIManager;
import journeymap.client.ui.component.Button;
import journeymap.client.ui.component.ButtonList;
import journeymap.client.ui.component.JmUI;
import journeymap.client.ui.fullscreen.Fullscreen;
import journeymap.client.ui.fullscreen.MapChat;
import journeymap.client.ui.fullscreen.layer.LayerDelegate;
import journeymap.client.ui.theme.Theme;
import journeymap.client.ui.theme.ThemeButton;
import journeymap.client.ui.theme.ThemeToggle;
import journeymap.client.ui.theme.ThemeToolbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = Fullscreen.class, remap = false)
public abstract class FullscreenMixin extends JmUI {

    private int oldMouseX = 0;
    private int oldMouseY = 0;
    private long timeLastClick = 0;

    @Final
    @Shadow(remap = false)
    static GridRenderer gridRenderer;

    @Shadow(remap = false)
    ThemeToolbar mapTypeToolbar;

    @Shadow(remap = false)
    ThemeButton buttonCaves;

    @Shadow(remap = false)
    ThemeButton buttonNight;

    @Shadow(remap = false)
    ThemeButton buttonDay;

    @Shadow(remap = false)
    StatTimer drawScreenTimer;

    @Shadow(remap = false)
    MapChat chat;

    @Final
    @Shadow(remap = false)
    LayerDelegate layerDelegate;

    @Shadow(remap = false)
    boolean firstLayoutPass;

    @Shadow(remap = false)
    int mx;

    @Shadow(remap = false)
    int my;

    public FullscreenMixin() {
        super("");
    }

    @Inject(method = "<init>*", at = @At("RETURN"), remap = false, require = 1)
    private void init(CallbackInfo callbackInfo) {
        MapState.instance.layers.forEach(LayerManager::forceRefresh);
    }

    @Shadow(remap = false)
    private int getMapFontScale() {
        throw new IllegalStateException("Mixin failed to shadow getMapFontScale()");
    }

    @Shadow(remap = false)
    void drawMap() {
        throw new IllegalStateException("Mixin failed to shadow drawMap()");
    }

    @Inject(method = "<init>*", at = @At("RETURN"), remap = false, require = 1)
    private void onConstructed(CallbackInfo callbackInfo) {
        MapState.instance.layers.forEach(LayerManager::onOpenMap);
    }

    @Inject(method = "drawMap",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/client/model/MapState;getDrawWaypointSteps()Ljava/util/List;"),
            remap = false,
            require = 1,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onBeforeDrawJourneyMapWaypoints(CallbackInfo callbackInfo, boolean refreshReady, StatTimer timer, int xOffset, int yOffset, float drawScale) {
        final int fontScale = getMapFontScale();
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int widthBlocks = minecraft.displayWidth >> gridRenderer.getZoom();
        final int heightBlocks = minecraft.displayHeight >> gridRenderer.getZoom();
        for(LayerManager layerManager : MapState.instance.layers) {
            if(layerManager.isLayerActive()) {
                layerManager.recacheFullscreenMap(centerBlockX, centerBlockZ, widthBlocks, heightBlocks);
            }
        }

        for(LayerRenderer layerRenderer : JourneyMapState.instance.renderers) {
            if(layerRenderer.isLayerActive()) {
                gridRenderer.draw(layerRenderer.getDrawStepsCachedForRendering(), xOffset, yOffset, drawScale, fontScale, 0.0);
            }
        }
    }

    @Redirect(method = "initButtons",
            at = @At(value = "FIELD",
                    target = "Ljourneymap/client/ui/fullscreen/Fullscreen;mapTypeToolbar:Ljourneymap/client/ui/theme/ThemeToolbar;",
                    opcode = Opcodes.PUTFIELD),
            remap = false,
            require = 1)
    private void OnCreateMapTypeToolbar(Fullscreen owner, ThemeToolbar value) {
        final Theme theme = ThemeFileHandler.getCurrentTheme();
        final ButtonList buttonList = new ButtonList();

        for(LayerButton layerButton : JourneyMapState.instance.buttons) {
            final ThemeToggle button = new ThemeToggle(theme, layerButton.getButtonTextKey(), layerButton.getIconName());
            layerButton.setButton(button);
            button.setToggled(layerButton.isActive(), false);
            button.addToggleListener((unused, toggled) -> {
                layerButton.toggle();
                return true;
            });
            buttonList.add(button);
        }

        buttonList.add(buttonCaves);
        buttonList.add(buttonNight);
        buttonList.add(buttonDay);
        mapTypeToolbar = new ThemeToolbar(theme, buttonList);
    }

    @Override
    public void func_73863_a(int width, int height, float f) {
        try {
            func_146278_c(0);
            drawMap();
            drawScreenTimer.start();
            layoutButtons();
            List<String> tooltip = null;
            if (firstLayoutPass) {
                layoutButtons();
                firstLayoutPass = false;
            }
            else {
                for(int k = 0; k < buttonList.size(); ++k) {
                    GuiButton guibutton = (GuiButton) buttonList.get(k);
                    guibutton.drawButton(mc, width, height);
                    if (tooltip == null && guibutton instanceof Button) {
                        Button button = (Button)guibutton;
                        if (button.mouseOver(mx, my)) {
                            tooltip = button.getTooltip();
                        }
                    }
                }
            }

            final int scaledMouseX = (mx * mc.displayWidth) / this.width;
            final int scaledMouseY = (my * mc.displayHeight) / this.height;
            for(LayerRenderer layer : JourneyMapState.instance.renderers) {
                if (layer instanceof WaypointProviderLayerRenderer) {
                    final WaypointProviderLayerRenderer waypointProviderLayer = (WaypointProviderLayerRenderer) layer;
                    waypointProviderLayer.onMouseMove(scaledMouseX, scaledMouseY);
                }
            }

            if(tooltip == null) {
                for(LayerRenderer layer : JourneyMapState.instance.renderers) {
                    if (layer instanceof WaypointProviderLayerRenderer) {
                        final WaypointProviderLayerRenderer waypointProviderLayer = (WaypointProviderLayerRenderer) layer;
                        if (waypointProviderLayer.isLayerActive()) {
                            tooltip = waypointProviderLayer.getTextTooltip();
                        }
                    }
                }
            }

            if (chat != null) {
                chat.func_73863_a(width, height, f);
            }

            if (tooltip != null && !tooltip.isEmpty()) {
                drawHoveringText(tooltip, mx, my, getFontRenderer());
                RenderHelper.disableStandardItemLighting();
            }
            else {
                for(LayerRenderer layer : JourneyMapState.instance.renderers) {
                    if (layer instanceof WaypointProviderLayerRenderer) {
                        final WaypointProviderLayerRenderer waypointProviderLayer = (WaypointProviderLayerRenderer) layer;
                        if (waypointProviderLayer.isLayerActive()) {
                            waypointProviderLayer.drawCustomTooltip(getFontRenderer(), mx, my, this.width, this.height);
                        }
                    }
                }
            }


        }
        catch (Throwable var11) {
            logger.log(Level.ERROR, "Unexpected exception in jm.fullscreen.drawScreen(): " + LogFormatter.toString(var11));
            UIManager.getInstance().closeAll();
        }
        finally {
            drawScreenTimer.stop();
        }

    }

    @Inject(method = "func_73869_a",
            at = @At(value = "HEAD"),
            remap = false,
            require = 1,
            cancellable = true)
    private void onKeyPress(CallbackInfo callbackInfo) {
        if((chat == null || chat.isHidden()) && Constants.isPressed(VP.keyAction)) {
            for(LayerRenderer layer : JourneyMapState.instance.renderers) {
                if (layer instanceof WaypointProviderLayerRenderer) {
                    ((WaypointProviderLayerRenderer) layer).onActionKeyPressed();
                }
            }
            callbackInfo.cancel();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (chat != null && !chat.isHidden()) {
            chat.func_73864_a(mouseX, mouseY, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.isMouseOverButton(mouseX, mouseY)) {
            final int scaledMouseX = mx * mc.displayWidth / width;
            final int scaledMouseY = my * mc.displayHeight / height;
            final double blockSize = Math.pow(2.0D, gridRenderer.getZoom());
            if(onMapClicked(mouseButton, scaledMouseX, scaledMouseY, blockSize) == false) {
                BlockCoordIntPair blockCoord = gridRenderer.getBlockUnderMouse(Mouse.getEventX(), Mouse.getEventY(), mc.displayWidth, mc.displayHeight);
                layerDelegate.onMouseClicked(mc, Mouse.getEventX(), Mouse.getEventY(), gridRenderer.getWidth(), gridRenderer.getHeight(), blockCoord, mouseButton);
            }
        }
    }

    private boolean onMapClicked(int mouseButton, int mouseX, int mouseY, double blockSize) {
        final long timestamp = System.currentTimeMillis();
        final boolean isDoubleClick = mouseX == oldMouseX && mouseY == oldMouseY && timestamp - timeLastClick < 500;
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        timeLastClick = isDoubleClick ? 0 : timestamp;

        if(mouseButton != 0) {
            return false;
        }

        boolean layerHit = false;
        for(LayerRenderer layer : JourneyMapState.instance.renderers) {
            if (layer instanceof WaypointProviderLayerRenderer) {
                ((WaypointProviderLayerRenderer) layer).onMouseMove(mouseX, mouseY);
                layerHit |= ((WaypointProviderLayerRenderer) layer).onMouseAction(isDoubleClick);
            }
        }
        return layerHit;
    }
}
