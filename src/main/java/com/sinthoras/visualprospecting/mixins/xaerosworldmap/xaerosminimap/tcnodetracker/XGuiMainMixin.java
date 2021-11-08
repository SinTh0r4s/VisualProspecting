package com.sinthoras.visualprospecting.mixins.xaerosworldmap.xaerosminimap.tcnodetracker;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.gui.GuiMain;
import com.dyonovan.tcnodetracker.lib.AspectLoc;
import com.sinthoras.visualprospecting.gui.xaeromap.FakeWaypointManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(value = GuiMain.class)
public class XGuiMainMixin {

    @Shadow(remap = false) private int low;

    @Inject(method = "actionPerformed",
            at = @At(value = "FIELD",
                    target = "Lcom/dyonovan/tcnodetracker/TCNodeTracker;zMarker:I",
                    opcode = Opcodes.PUTSTATIC,
                    shift = At.Shift.AFTER,
                    remap = false
            ),
            require = 1,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onWaypointSet(GuiButton button, CallbackInfo callbackInfo, int i) {
        final AspectLoc aspect = GuiMain.aspectList.get(low + i);
        /*ThaumcraftNodeLayer.instance.setActiveWaypoint(new Waypoint(I18n.format("visualprospecting.tracked", I18n.format("tile.blockAiry.0.name")),
                aspect.x,
                aspect.y,
                aspect.z,
                new Color(0xFFFFFF),
                Waypoint.Type.Normal,
                aspect.dimID));*/
        FakeWaypointManager.toggleWaypoint(FakeWaypointManager.TC_NODES_WAYPOINT, aspect.x, aspect.y, aspect.z,
                I18n.format("visualprospecting.tracked", I18n.format("tile.blockAiry.0.name")), "@", 15, aspect.dimID);
        if (FakeWaypointManager.hasWaypoint(FakeWaypointManager.TC_NODES_WAYPOINT)) {
            TCNodeTracker.xMarker = aspect.x;
            TCNodeTracker.yMarker = aspect.y;
            TCNodeTracker.zMarker = aspect.z;
        }
        else {
            TCNodeTracker.yMarker = -1;
        }
    }

    @Inject(method = "actionPerformed",
            at = @At(value = "FIELD",
                    target = "Lcom/dyonovan/tcnodetracker/TCNodeTracker;doGui:Z",
                    opcode = Opcodes.PUTSTATIC,
                    ordinal = 0,
                    remap = false
            ),
            require = 1)
    private void onWaypointClear(CallbackInfo callbackInfo) {
        //ThaumcraftNodeLayer.instance.clearActiveWaypoint();
        FakeWaypointManager.removeWaypoint(FakeWaypointManager.TC_NODES_WAYPOINT);
        TCNodeTracker.yMarker = -1;
    }

    @Inject(method = "actionPerformed",
            at = @At(value = "FIELD",
                    target = "Lcom/dyonovan/tcnodetracker/TCNodeTracker;doGui:Z",
                    opcode = Opcodes.PUTSTATIC,
                    ordinal = 1,
                    remap = false
            ),
            require = 1,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onWaypointDelete(GuiButton button, CallbackInfo callbackInfo, int i, int k, int j) {
        //ThaumcraftNodeLayer.instance.clearActiveWaypoint();
        FakeWaypointManager.removeWaypoint(FakeWaypointManager.TC_NODES_WAYPOINT);
        TCNodeTracker.yMarker = -1;
    }
}
