package com.sinthoras.visualprospecting.gui.xaeromap.rendersteps;

import net.minecraft.client.gui.GuiScreen;

public interface RenderStep {
	void draw(GuiScreen gui, double cameraX, double cameraZ, double scale);
}
