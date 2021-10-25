package com.sinthoras.visualprospecting.gui.journeymap.drawsteps;

import journeymap.client.model.Waypoint;
import journeymap.client.render.draw.DrawStep;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;

public interface ClickableDrawStep extends DrawStep {

    public List<String> getTooltip();

    public void drawTooltip(FontRenderer fontRenderer, int mouseX, int mouseY, int displayWidth, int displayHeight);

    public boolean isMouseOver(int scaledMouseX, int scaledMouseY);

    public void onActionKeyPressed();

    public boolean isWaypoint(Waypoint waypoint);

    public Waypoint toWaypoint();
}
