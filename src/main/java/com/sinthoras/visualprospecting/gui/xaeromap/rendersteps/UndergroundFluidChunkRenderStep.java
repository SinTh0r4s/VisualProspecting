package com.sinthoras.visualprospecting.gui.xaeromap.rendersteps;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.gui.DrawUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

public class UndergroundFluidChunkRenderStep implements RenderStep{
	private final int blockX;
	private final int blockZ;
	private final Fluid fluid;
	private final int fluidAmount;
	private final int maxAmountInField;
	private final int minAmountInField;

	public UndergroundFluidChunkRenderStep(int chunkX, int chunkZ, Fluid fluid, int fluidAmount, int minAmountInField, int maxAmountInField) {
		blockX = Utils.coordChunkToBlock(chunkX);
		blockZ = Utils.coordChunkToBlock(chunkZ);
		this.fluid = fluid;
		this.fluidAmount = fluidAmount;
		this.maxAmountInField = maxAmountInField;
		this.minAmountInField = minAmountInField;
	}

	private String getFluidAmountFormatted() {
		if(fluidAmount >= 1000) {
			return (fluidAmount / 1000) + "kL";
		}
		return fluidAmount + "L";
	}

	@Override
	public void draw(GuiScreen gui, double cameraX, double cameraZ, double scale) {
        if (fluidAmount > 0 && scale >= Utils.journeyMapScaleToLinear(Config.minZoomLevelForUndergroundFluidDetails)) {
			GL11.glPushMatrix();
			GL11.glTranslated(blockX - cameraX, blockZ - cameraZ, 0);

			float alpha = ((float)(fluidAmount - minAmountInField)) / (maxAmountInField - minAmountInField + 1);
            alpha *= alpha * 204;
            int fluidColor = fluid.getColor() | (((int) alpha) << 24);
			DrawUtils.drawGradientRect(0, 0, VP.chunkWidth, VP.chunkDepth, 0, fluidColor, fluidColor);

            if(fluidAmount >= maxAmountInField) {
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
