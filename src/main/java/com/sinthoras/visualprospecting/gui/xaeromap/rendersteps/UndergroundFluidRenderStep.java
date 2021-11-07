package com.sinthoras.visualprospecting.gui.xaeromap.rendersteps;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.gui.DrawUtils;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class UndergroundFluidRenderStep implements RenderStep{

	private final UndergroundFluidPosition undergroundFluidPosition;

	public UndergroundFluidRenderStep(UndergroundFluidPosition undergroundFluid) {
		undergroundFluidPosition = undergroundFluid;
	}

	@Override
	public void draw(GuiScreen gui, double cameraX, double cameraZ, double scale) {
		final int maxAmountInField = undergroundFluidPosition.getMaxProduction();
		// < 0.5 scale is when scaling issues show up
        if (maxAmountInField > 0 && scale >= 0.5) {
        	GL11.glPushMatrix();
			GL11.glTranslated(undergroundFluidPosition.getBlockX() - cameraX, undergroundFluidPosition.getBlockZ() - cameraZ, 0);

            final int borderColor = undergroundFluidPosition.fluid.getColor() | 0xCC000000;
            final double lenX = VP.undergroundFluidSizeChunkX * VP.chunkWidth;
            final double lenZ = VP.undergroundFluidSizeChunkZ * VP.chunkDepth;
            DrawUtils.drawGradientRect(0, 0, lenX, 2, 0, borderColor, borderColor);
			DrawUtils.drawGradientRect(lenX, 0, lenX + 2, lenZ, 0, borderColor, borderColor);
			DrawUtils.drawGradientRect(2, lenZ, lenX + 2, lenZ + 2, 0, borderColor, borderColor);
			DrawUtils.drawGradientRect(0, 2, 2, lenZ + 2, 0, borderColor, borderColor);

			// min scale that journeymap can go to
			if (scale >= 1) {
				GL11.glScaled(1 / scale, 1 / scale, 1);
				final String label = undergroundFluidPosition.getMinProduction() + "L - " + maxAmountInField + "L  " + undergroundFluidPosition.fluid.getLocalizedName();
				DrawUtils.drawSimpleLabel(gui, label, VP.chunkWidth * scale, 0, 0xFFFFFFFF, 0xB4000000, false);
			}

            GL11.glPopMatrix();
        }
	}
}
