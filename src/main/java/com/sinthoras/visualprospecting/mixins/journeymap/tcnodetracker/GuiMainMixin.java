package com.sinthoras.visualprospecting.mixins.journeymap.tcnodetracker;

import com.dyonovan.tcnodetracker.gui.GuiMain;
import com.dyonovan.tcnodetracker.lib.AspectLoc;
import com.sinthoras.visualprospecting.gui.journeymap.MapState;
import com.sinthoras.visualprospecting.gui.journeymap.layers.InformationLayer;
import com.sinthoras.visualprospecting.gui.journeymap.layers.ThaumcraftNodeLayer;
import com.sinthoras.visualprospecting.gui.journeymap.layers.WaypointProviderLayer;
import journeymap.client.model.Waypoint;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;

@Mixin(GuiMain.class)
public class GuiMainMixin {

    @Shadow(remap = false)
    private int low;

    @Inject(method = "actionPerformed",
            at = @At(value = "FIELD",
                    target = "Lcom/dyonovan/tcnodetracker/TCNodeTracker;zMarker:I",
                    opcode = Opcodes.PUTSTATIC,
                    shift = At.Shift.AFTER,
                    remap = false),
            remap = true,
            require = 1,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true)
    private void onWaypointSet(GuiButton button, CallbackInfo callbackInfo, int i) {
        final AspectLoc aspect = GuiMain.aspectList.get(low + i);
        GuiMain.aspectList.clear();
        ThaumcraftNodeLayer.instance.setActiveWaypoint(new Waypoint(I18n.format("visualprospecting.tracked", I18n.format("tile.blockAiry.0.name")),
                aspect.x,
                aspect.y,
                aspect.z,
                new Color(0xFFFFFF),
                Waypoint.Type.Normal,
                aspect.dimID));
        callbackInfo.cancel();
    }

    @Inject(method = "actionPerformed",
            at = @At(value = "FIELD",
                    target = "Lcom/dyonovan/tcnodetracker/TCNodeTracker;doGui:Z",
                    opcode = Opcodes.PUTSTATIC,
                    ordinal = 0,
                    remap = false),
            remap = true,
            require = 1)
    private void onWaypointClear(CallbackInfo callbackInfo) {
        ThaumcraftNodeLayer.instance.clearActiveWaypoint();
    }

    @Inject(method = "actionPerformed",
            at = @At(value = "FIELD",
                    target = "Lcom/dyonovan/tcnodetracker/TCNodeTracker;doGui:Z",
                    opcode = Opcodes.PUTSTATIC,
                    ordinal = 1,
                    remap = false),
            remap = true,
            require = 1,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onWaypointDelete(GuiButton button, CallbackInfo callbackInfo, int i, int k, int j) {
        ThaumcraftNodeLayer.instance.clearActiveWaypoint();
    }
}
