package com.sinthoras.visualprospecting.gui.journeymap.drawsteps;

import com.sinthoras.visualprospecting.gui.model.locations.IWaypointAndLocationProvider;
import journeymap.client.render.draw.DrawStep;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;

public interface ClickableDrawStep extends DrawStep {

    List<String> getTooltip();

    void drawTooltip(FontRenderer fontRenderer, int mouseX, int mouseY, int displayWidth, int displayHeight);

    boolean isMouseOver(int mouseX, int mouseY);

    void onActionKeyPressed();

    IWaypointAndLocationProvider getLocationProvider();
}
