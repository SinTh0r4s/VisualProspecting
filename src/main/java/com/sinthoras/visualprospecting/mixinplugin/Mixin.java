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

    WorldGenContainerMixin("bartworks.WorldGenContainerMixin", false, BARTWORKS),

    GT_Worldgenerator_SpaceMixin("galacticgreg.GT_Worldgenerator_SpaceMixin", false, GALACTICGREG),

    GT_Block_Ores_AbstractMixin("gregtech.GT_Block_Ores_AbstractMixin", false, GREGTECH),
    GT_MetaTileEntity_AdvSeismicProspectorMixin("gregtech.GT_MetaTileEntity_AdvSeismicProspectorMixin", false, GREGTECH),
    GT_MetaTileEntity_ScannerMixin("gregtech.GT_MetaTileEntity_ScannerMixin", false, GREGTECH),
    GT_WorldGenContainerMixin("gregtech.WorldGenContainerMixin", false, GREGTECH),

    FullscreenMixin("journeymap.FullscreenMixin", true, JOURNEYMAP),
    FullscreenActionsMixin("journeymap.FullscreenActionsMixin", true, JOURNEYMAP),
    RenderWaypointBeaconMixin("journeymap.RenderWaypointBeaconMixin", true, JOURNEYMAP),
    WaypointManagerMixin("journeymap.WaypointManagerMixin", true, JOURNEYMAP),

    GuiMainMixin("journeymap.tcnodetracker.GuiMainMixin", true, JOURNEYMAP, TCNODETRACKER);

    public final String mixinClass;
    public final List<TargetedMod> targetedMods;
    // Injecting into @SideOnly(Side.Client) classes will crash the server. Flag them as clientSideOnly!
    public final boolean clientSideOnly;

    Mixin(String mixinClass, boolean clientSideOnly, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.clientSideOnly = clientSideOnly;
    }
}
