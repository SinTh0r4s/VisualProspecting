package com.sinthoras.visualprospecting.mixins.journeymap;

import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import com.sinthoras.visualprospecting.gui.journeymap.MapState;
import journeymap.client.io.ThemeFileHandler;
import journeymap.client.log.StatTimer;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.fullscreen.Fullscreen;
import journeymap.client.ui.theme.Theme;
import journeymap.client.ui.theme.ThemeButton;
import journeymap.client.ui.theme.ThemeToggle;
import journeymap.client.ui.theme.ThemeToolbar;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.sinthoras.visualprospecting.Utils.isNEIInstalled;
import static com.sinthoras.visualprospecting.gui.journeymap.Reflection.getJourneyMapGridRenderer;

@Mixin(value = Fullscreen.class, remap = false)
public class FullscreenMixin {

    private static MapState mapState = new MapState();
    private ThemeButton buttonOreVeins;
    private ThemeButton buttonUndergroundFluids;

    @Shadow(remap = false)
    private ThemeToolbar mapTypeToolbar;

    @Shadow(remap = false)
    private ThemeButton buttonCaves;

    @Shadow(remap = false)
    private ThemeButton buttonNight;

    @Shadow(remap = false)
    private ThemeButton buttonDay;

    @Shadow(remap = false)
    private int getMapFontScale() {
        throw new IllegalStateException("Mixin failed to shadow getMapFontScale()");
    }

    @Inject(method = "<init>*", at = @At("RETURN"), remap = false, require = 1)
    private void onConstructed(CallbackInfo callbackInfo) {
        if(isNEIInstalled()) {
            VeinTypeCaching.recalculateNEISearch();
        }
    }

    @Inject(method = "drawMap",
            at = @At(value = "INVOKE", target = "Ljourneymap/client/model/MapState;getDrawWaypointSteps()Ljava/util/List;"),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onBeforeDrawWaypoints(CallbackInfo callbackInfo, boolean refreshReady, StatTimer timer, int xOffset, int yOffset, float drawScale) {
        final GridRenderer gridRenderer = getJourneyMapGridRenderer();
        assert (gridRenderer != null);
        if(mapState.drawUndergroundFluids) {
            gridRenderer.draw(mapState.getUndergroundFluidChunksDrawSteps(gridRenderer), xOffset, yOffset, drawScale, getMapFontScale(), 0.0);
            gridRenderer.draw(mapState.getUndergroundFluidsDrawSteps(gridRenderer), xOffset, yOffset, drawScale, getMapFontScale(), 0.0);
        }
        if(mapState.drawOreVeins) {
            gridRenderer.draw(mapState.getOreVeinDrawSteps(gridRenderer), xOffset, yOffset, drawScale, getMapFontScale(), 0.0);
        }
    }

    @Redirect(method = "initButtons",
            at = @At(value = "FIELD", target = "Ljourneymap/client/ui/fullscreen/Fullscreen;mapTypeToolbar:Ljourneymap/client/ui/theme/ThemeToolbar;", opcode = Opcodes.PUTFIELD),
            remap = false)
    private void OnCreateMapTypeToolbar(Fullscreen owner, ThemeToolbar value) {
        final Theme theme = ThemeFileHandler.getCurrentTheme();

        buttonOreVeins = new ThemeToggle(theme, "visualprospecting.button.orevein", "oreveins");
        buttonOreVeins.setToggled(mapState.drawOreVeins, false);
        buttonOreVeins.addToggleListener((button, toggled) -> {
            mapState.drawOreVeins = toggled;
            return true;
        });

        buttonUndergroundFluids = new ThemeToggle(theme, "visualprospecting.button.undergroundfluid", "undergroundfluid");
        buttonUndergroundFluids.setToggled(mapState.drawUndergroundFluids, false);
        buttonUndergroundFluids.addToggleListener((button, toggled) -> {
            mapState.drawUndergroundFluids = toggled;
            return true;
        });

        mapTypeToolbar = new ThemeToolbar(theme, buttonUndergroundFluids, buttonOreVeins, buttonCaves, buttonNight, buttonDay);
    }




}
