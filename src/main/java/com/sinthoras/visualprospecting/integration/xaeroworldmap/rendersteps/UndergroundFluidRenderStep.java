package com.sinthoras.visualprospecting.integration.xaeroworldmap.rendersteps;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.integration.DrawUtils;
import com.sinthoras.visualprospecting.integration.model.locations.UndergroundFluidLocation;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class UndergroundFluidRenderStep implements RenderStep {

    private final UndergroundFluidLocation undergroundFluidLocation;

    public UndergroundFluidRenderStep(UndergroundFluidLocation undergroundFluid) {
        undergroundFluidLocation = undergroundFluid;
    }

    @Override
    public void draw(GuiScreen gui, double cameraX, double cameraZ, double scale) {
        final int maxAmountInField = undergroundFluidLocation.getMaxProduction();
        // < 0.5 scale is when scaling issues show up
        if (maxAmountInField > 0 && scale >= 0.5) {
            GL11.glPushMatrix();
            GL11.glTranslated(undergroundFluidLocation.getBlockX() - 0.5 - cameraX, undergroundFluidLocation.getBlockZ() - 0.5 - cameraZ, 0);

            final int borderColor = undergroundFluidLocation.getFluid().getColor() | 0xCC000000;
            final double lenX = VP.undergroundFluidSizeChunkX * VP.chunkWidth;
            final double lenZ = VP.undergroundFluidSizeChunkZ * VP.chunkDepth;
            DrawUtils.drawGradientRect(0, 0, lenX, 2, 0, borderColor, borderColor);
            DrawUtils.drawGradientRect(lenX, 0, lenX + 2, lenZ, 0, borderColor, borderColor);
            DrawUtils.drawGradientRect(2, lenZ, lenX + 2, lenZ + 2, 0, borderColor, borderColor);
            DrawUtils.drawGradientRect(0, 2, 2, lenZ + 2, 0, borderColor, borderColor);

            // min scale that journeymap can go to
            if (scale >= 1) {
                GL11.glScaled(1 / scale, 1 / scale, 1);
                final String label = undergroundFluidLocation.getMinProduction() + "L - " + maxAmountInField + "L  " + undergroundFluidLocation.getFluid().getLocalizedName();
                DrawUtils.drawSimpleLabel(gui, label, VP.chunkWidth * scale, 0, 0xFFFFFFFF, 0xB4000000, false);
            }

            GL11.glPopMatrix();
        }
    }
}
