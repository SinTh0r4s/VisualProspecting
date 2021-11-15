package com.sinthoras.visualprospecting.gui.xaeromap.rendersteps;

import com.sinthoras.visualprospecting.gui.model.locations.IWaypointAndLocationProvider;
import net.minecraft.client.gui.GuiScreen;

public interface InteractableRenderStep extends RenderStep {

    boolean isMouseOver(double mouseX, double mouseY, double cameraX, double cameraZ, double scale);

    void drawTooltip(GuiScreen gui, double mouseX, double mouseY, double cameraX, double cameraZ, double scale, int scaleAdj);

    void onActionButton();

    IWaypointAndLocationProvider getLocationProvider();

}
