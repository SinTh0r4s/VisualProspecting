package com.sinthoras.visualprospecting.integration.xaeroworldmap.rendersteps;

import net.minecraft.client.gui.GuiScreen;

import javax.annotation.Nullable;

public interface RenderStep {
    void draw(@Nullable GuiScreen gui, double cameraX, double cameraZ, double scale);
}
