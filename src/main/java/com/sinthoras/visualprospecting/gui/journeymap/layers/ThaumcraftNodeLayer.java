package com.sinthoras.visualprospecting.gui.journeymap.layers;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.NodeList;
import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.ClickableDrawStep;
import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.ThaumcraftNodeDrawStep;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.ThaumcraftNodeButton;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class ThaumcraftNodeLayer extends WaypointProviderLayer {

    public final static ThaumcraftNodeLayer instance = new ThaumcraftNodeLayer();

    private int oldMinBlockX = 0;
    private int oldMinBlockZ = 0;
    private int oldMaxBlockX = 0;
    private int oldMaxBlockZ = 0;

    public ThaumcraftNodeLayer() {
        super(ThaumcraftNodeButton.instance);
    }

    @Override
    protected boolean needsRegenerateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        if(minBlockX != oldMinBlockX || minBlockZ != oldMinBlockZ || maxBlockX != oldMaxBlockX || maxBlockZ != oldMaxBlockZ) {
            oldMinBlockX = minBlockX;
            oldMinBlockZ = minBlockZ;
            oldMaxBlockX = maxBlockX;
            oldMaxBlockZ = maxBlockZ;
            return true;
        }
        return false;
    }

    @Override
    protected List<ClickableDrawStep> generateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        final int playerDimensionId = Minecraft.getMinecraft().thePlayer.dimension;

        ArrayList<ClickableDrawStep> thaumcraftNodesDrawSteps = new ArrayList<>();

        for (NodeList node : TCNodeTracker.nodelist) {
            if(node.dim == playerDimensionId
                    && node.x >= minBlockX && node.x <= maxBlockX
                    && node.z >= minBlockZ && node.z <= maxBlockZ) {
                thaumcraftNodesDrawSteps.add(new ThaumcraftNodeDrawStep(node));
            }
        }

        return thaumcraftNodesDrawSteps;
    }
}
