package com.sinthoras.visualprospecting.mixins.journeymap;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import com.sinthoras.visualprospecting.gui.journeymap.MapState;
import com.sinthoras.visualprospecting.gui.journeymap.OreVeinDrawStep;
import journeymap.client.Constants;
import journeymap.client.io.ThemeFileHandler;
import journeymap.client.log.LogFormatter;
import journeymap.client.log.StatTimer;
import journeymap.client.model.BlockCoordIntPair;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.UIManager;
import journeymap.client.ui.component.Button;
import journeymap.client.ui.component.JmUI;
import journeymap.client.ui.fullscreen.Fullscreen;
import journeymap.client.ui.fullscreen.MapChat;
import journeymap.client.ui.fullscreen.layer.LayerDelegate;
import journeymap.client.ui.theme.Theme;
import journeymap.client.ui.theme.ThemeButton;
import journeymap.client.ui.theme.ThemeToggle;
import journeymap.client.ui.theme.ThemeToolbar;
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

import static com.sinthoras.visualprospecting.Utils.isNEIInstalled;
import static com.sinthoras.visualprospecting.gui.journeymap.Reflection.getJourneyMapGridRenderer;

@Mixin(value = Fullscreen.class, remap = false)
public abstract class FullscreenMixin extends JmUI {

    private ThemeButton buttonOreVeins;
    private ThemeButton buttonUndergroundFluids;

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

    @Shadow(remap = false)
    private int getMapFontScale() {
        throw new IllegalStateException("Mixin failed to shadow getMapFontScale()");
    }

    @Shadow(remap = false)
    void drawMap() {
        throw new IllegalStateException("Mixin failed to shadow drawMap()");
    }

    @Shadow @Final private static GridRenderer gridRenderer;

    @Inject(method = "<init>*", at = @At("RETURN"), remap = false, require = 1)
    private void onConstructed(CallbackInfo callbackInfo) {
        if(isNEIInstalled()) {
            VeinTypeCaching.recalculateNEISearch();
        }
    }

    @Inject(method = "drawMap",
            at = @At(value = "INVOKE", target = "Ljourneymap/client/model/MapState;getDrawWaypointSteps()Ljava/util/List;"),
            remap = false,
            require = 1,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onBeforeDrawWaypoints(CallbackInfo callbackInfo, boolean refreshReady, StatTimer timer, int xOffset, int yOffset, float drawScale) {
        final GridRenderer gridRenderer = getJourneyMapGridRenderer();
        assert (gridRenderer != null);
        if(MapState.instance.drawUndergroundFluids) {
            gridRenderer.draw(MapState.instance.getUndergroundFluidChunksDrawSteps(gridRenderer), xOffset, yOffset, drawScale, getMapFontScale(), 0.0);
            gridRenderer.draw(MapState.instance.getUndergroundFluidsDrawSteps(gridRenderer), xOffset, yOffset, drawScale, getMapFontScale(), 0.0);
        }
        if(MapState.instance.drawOreVeins) {
            gridRenderer.draw(MapState.instance.getOreVeinDrawSteps(gridRenderer), xOffset, yOffset, drawScale, getMapFontScale(), 0.0);
        }
    }

    @Redirect(method = "initButtons",
            at = @At(value = "FIELD", target = "Ljourneymap/client/ui/fullscreen/Fullscreen;mapTypeToolbar:Ljourneymap/client/ui/theme/ThemeToolbar;", opcode = Opcodes.PUTFIELD),
            remap = false,
            require = 1)
    private void OnCreateMapTypeToolbar(Fullscreen owner, ThemeToolbar value) {
        final Theme theme = ThemeFileHandler.getCurrentTheme();

        buttonOreVeins = new ThemeToggle(theme, "visualprospecting.button.orevein", "oreveins");
        buttonOreVeins.setToggled(MapState.instance.drawOreVeins, false);
        buttonOreVeins.addToggleListener((button, toggled) -> {
            MapState.instance.drawOreVeins = toggled;
            return true;
        });

        buttonUndergroundFluids = new ThemeToggle(theme, "visualprospecting.button.undergroundfluid", "undergroundfluid");
        buttonUndergroundFluids.setToggled(MapState.instance.drawUndergroundFluids, false);
        buttonUndergroundFluids.addToggleListener((button, toggled) -> {
            MapState.instance.drawUndergroundFluids = toggled;
            return true;
        });

        mapTypeToolbar = new ThemeToolbar(theme, buttonUndergroundFluids, buttonOreVeins, buttonCaves, buttonNight, buttonDay);
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

            if(tooltip == null && MapState.instance.drawOreVeins) {
                final int scaledMouseX = (mx * mc.displayWidth) / this.width;
                final int scaledMouseY = (my * mc.displayHeight) / this.height;
                for(OreVeinDrawStep oreVein : MapState.instance.getOreVeinDrawSteps(gridRenderer)) {
                    if(oreVein.mouseOver(scaledMouseX, scaledMouseY)) {
                        tooltip = oreVein.getTooltip();
                        break;
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
        if((chat == null || chat.isHidden()) && Constants.isPressed(VP.keyDelete)) {
            MapState.instance.onToggleOreVein();
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
            final int scaledMouseX = mx * mc.displayWidth / this.width;
            final int scaledMouseY = my * mc.displayHeight / this.height;
            final double blockSize = Math.pow(2.0D, gridRenderer.getZoom());
            if(MapState.instance.onMapClicked(mouseButton, scaledMouseX, scaledMouseY, blockSize) == false) {
                BlockCoordIntPair blockCoord = gridRenderer.getBlockUnderMouse(Mouse.getEventX(), Mouse.getEventY(), mc.displayWidth, mc.displayHeight);
                layerDelegate.onMouseClicked(mc, Mouse.getEventX(), Mouse.getEventY(), gridRenderer.getWidth(), gridRenderer.getHeight(), blockCoord, mouseButton);
            }
        }
    }
}
