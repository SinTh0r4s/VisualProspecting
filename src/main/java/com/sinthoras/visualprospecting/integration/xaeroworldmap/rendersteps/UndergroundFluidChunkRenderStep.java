package com.sinthoras.visualprospecting.integration.xaeroworldmap.rendersteps;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.integration.DrawUtils;
import com.sinthoras.visualprospecting.integration.model.locations.UndergroundFluidChunkLocation;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class UndergroundFluidChunkRenderStep implements RenderStep {
    private final UndergroundFluidChunkLocation undergroundFluidChunkLocation;

    public UndergroundFluidChunkRenderStep(UndergroundFluidChunkLocation location) {
        undergroundFluidChunkLocation = location;
    }

    private String getFluidAmountFormatted() {
        if (undergroundFluidChunkLocation.getFluidAmount() >= 1000) {
            return (undergroundFluidChunkLocation.getFluidAmount() / 1000) + "kL";
        }
        return undergroundFluidChunkLocation.getFluidAmount() + "L";
    }

    @Override
    public void draw(GuiScreen gui, double cameraX, double cameraZ, double scale) {
        if (undergroundFluidChunkLocation.getFluidAmount() > 0 && scale >= Utils.journeyMapScaleToLinear(Config.minZoomLevelForUndergroundFluidDetails)) {
            GL11.glPushMatrix();
            GL11.glTranslated(undergroundFluidChunkLocation.getBlockX() - 0.5 - cameraX, undergroundFluidChunkLocation.getBlockZ() - 0.5 - cameraZ, 0);

            float alpha = ((float) (undergroundFluidChunkLocation.getFluidAmount() - undergroundFluidChunkLocation.getMinAmountInField())) /
                    (undergroundFluidChunkLocation.getMaxAmountInField() - undergroundFluidChunkLocation.getMinAmountInField() + 1);
            alpha *= alpha * 204;
            int fluidColor = undergroundFluidChunkLocation.getFluid().getColor() | (((int) alpha) << 24);
            DrawUtils.drawGradientRect(0, 0, VP.chunkWidth, VP.chunkDepth, 0, fluidColor, fluidColor);

            if (undergroundFluidChunkLocation.getFluidAmount() >= undergroundFluidChunkLocation.getMaxAmountInField()) {
                final int borderColor = 0xCCFFD700;
                DrawUtils.drawGradientRect(0, 0, 15, 1, 0, borderColor, borderColor);
                DrawUtils.drawGradientRect(15, 0, 16, 15, 0, borderColor, borderColor);
                DrawUtils.drawGradientRect(1, 15, 16, 16, 0, borderColor, borderColor);
                DrawUtils.drawGradientRect(0, 1, 1, 16, 0, borderColor, borderColor);
            }

            GL11.glScaled(1 / scale, 1 / scale, 1);
            DrawUtils.drawSimpleLabel(gui, getFluidAmountFormatted(), 13, 13, 0xFFFFFFFF, 0xB4000000, false);
            GL11.glPopMatrix();
        }
    }
}
