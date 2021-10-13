package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.render.map.GridRenderer;
import net.minecraftforge.fluids.Fluid;

import java.awt.geom.Point2D;

public class UndergroundFluidChunkDrawStep implements DrawStep {

    private final int blockX;
    private final int blockZ;
    private final Fluid fluid;
    private final int fluidAmount;
    private final int maxAmountInField;
    private final int minAmountInField;

    public UndergroundFluidChunkDrawStep(int chunkX, int chunkZ, Fluid fluid, int fluidAmount, int minAmountInField, int maxAmountInField) {
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
    public void draw(double xOffset, double yOffset, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        final int zoom = gridRenderer.getZoom();
        if (fluidAmount > 0 && zoom >= Config.minZoomLevelForUndergroundFluidDetails) {
            double blockSize = Math.pow(2, zoom);
            final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(blockX, blockZ);
            final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + xOffset, blockAsPixel.getY() + yOffset);
            float alpha = ((float)(fluidAmount - minAmountInField)) / (maxAmountInField - minAmountInField + 1);
            alpha *= alpha * 204;
            DrawUtil.drawRectangle(pixel.getX(), pixel.getY(), VP.chunkWidth * blockSize, VP.chunkDepth * blockSize, fluid.getColor(), (int)alpha);

            if(fluidAmount >= maxAmountInField) {
                final int borderColor = 0xFFD700;
                final int borderAlpha = 204;
                DrawUtil.drawRectangle(pixel.getX(), pixel.getY(), 15 * blockSize, blockSize, borderColor, borderAlpha);
                DrawUtil.drawRectangle(pixel.getX() + 15 * blockSize, pixel.getY(), blockSize, 15 * blockSize, borderColor, borderAlpha);
                DrawUtil.drawRectangle(pixel.getX() + 1 * blockSize, pixel.getY() + 15 * blockSize, 15 * blockSize, blockSize, borderColor, borderAlpha);
                DrawUtil.drawRectangle(pixel.getX(), pixel.getY() + 1 * blockSize, blockSize, 15 * blockSize, borderColor, borderAlpha);
            }

            DrawUtil.drawLabel(getFluidAmountFormatted(), pixel.getX() + 13 * blockSize, pixel.getY() + 13 * blockSize, DrawUtil.HAlign.Left, DrawUtil.VAlign.Above, 0, 180, 0x00FFFFFF, 255, fontScale, false, rotation);
        }
    }
}
