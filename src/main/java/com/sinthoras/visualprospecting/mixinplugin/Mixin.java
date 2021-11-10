package com.sinthoras.visualprospecting.mixinplugin;

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

    FullscreenMixin("journeymap.FullscreenMixin", JOURNEYMAP),
    FullscreenActionsMixin("journeymap.FullscreenActionsMixin", JOURNEYMAP),
    RenderWaypointBeaconMixin("journeymap.RenderWaypointBeaconMixin", JOURNEYMAP),
    WaypointManagerMixin("journeymap.WaypointManagerMixin", JOURNEYMAP),

    GuiMainMixin("tcnodetracker.GuiMainMixin", TCNODETRACKER),

    ItemEditableBookMixin("minecraft.ItemEditableBookMixin", VANILLA);

    public final String mixinClass;
    public final List<TargetedMod> targetedMods;

    Mixin(String mixinClass, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
    }
}
