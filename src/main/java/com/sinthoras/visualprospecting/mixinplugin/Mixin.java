package com.sinthoras.visualprospecting.mixinplugin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import java.util.Arrays;
import java.util.List;

import static com.sinthoras.visualprospecting.mixinplugin.TargetedMod.*;

public enum Mixin {

    //
    // IMPORTANT: Do not make any references to any mod from this file. This file is loaded quite early on and if
    // you refer to other mods you load them as well. The consequence is: You can't inject any previously loaded classes!
    // Exception: Tags.java, as long as it is used for Strings only!
    //

    WorldGenContainerMixin("bartworks.WorldGenContainerMixin", BARTWORKS),

    GT_Worldgenerator_SpaceMixin("galacticgreg.GT_Worldgenerator_SpaceMixin", GALACTICGREG),

    GT_Block_Ores_AbstractMixin("gregtech.GT_Block_Ores_AbstractMixin", GREGTECH),
    GT_MetaTileEntity_AdvSeismicProspectorMixin("gregtech.GT_MetaTileEntity_AdvSeismicProspectorMixin", GREGTECH),
    GT_MetaTileEntity_ScannerMixin("gregtech.GT_MetaTileEntity_ScannerMixin", GREGTECH),
    GT_WorldGenContainerMixin("gregtech.WorldGenContainerMixin", GREGTECH),

    FullscreenMixin("journeymap.FullscreenMixin", Side.CLIENT, JOURNEYMAP),
    FullscreenActionsMixin("journeymap.FullscreenActionsMixin", Side.CLIENT, JOURNEYMAP),
    RenderWaypointBeaconMixin("journeymap.RenderWaypointBeaconMixin", Side.CLIENT, JOURNEYMAP),
    WaypointManagerMixin("journeymap.WaypointManagerMixin", Side.CLIENT, JOURNEYMAP),

    GuiMainMixin("tcnodetracker.GuiMainMixin", Side.CLIENT, TCNODETRACKER),

    GuiMapMixin("xaerosworldmap.GuiMapMixin", XAEROWORLDMAP),
    WaypointsIngameRendererMixin("xaerosworldmap.xaerosminimap.WaypointsIngameRendererMixin", XAEROWORLDMAP, XAEROMINIMAP),

    ItemEditableBookMixin("minecraft.ItemEditableBookMixin", VANILLA);

    public final String mixinClass;
    public final List<TargetedMod> targetedMods;
    private final Side side;

    Mixin(String mixinClass, Side side, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = side;
    }

    Mixin(String mixinClass, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = Side.BOTH;
    }

    public boolean shouldLoad(List<TargetedMod> loadedMods) {
        return (side == Side.BOTH
                || side == Side.SERVER && FMLLaunchHandler.side().isServer()
                || side == Side.CLIENT && FMLLaunchHandler.side().isClient())
                && loadedMods.containsAll(targetedMods);
    }
}

enum Side {
    BOTH,
    CLIENT,
    SERVER;
}
