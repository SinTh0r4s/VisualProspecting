package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.render.map.GridRenderer;

import java.awt.geom.Point2D;

public class UndergroundFluidDrawStep implements DrawStep {

    private final UndergroundFluidPosition undergroundFluidPosition;

    public UndergroundFluidDrawStep(UndergroundFluidPosition undergroundFluidPosition) {
        this.undergroundFluidPosition = undergroundFluidPosition;
    }

    @Override
    public void draw(double xOffset, double yOffset, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        final int maxAmountInField = undergroundFluidPosition.getMaxProduction();
        if (maxAmountInField > 0) {
            double blockSize = Math.pow(2, gridRenderer.getZoom());
            final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(undergroundFluidPosition.getBlockX(), undergroundFluidPosition.getBlockZ());
            final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + xOffset, blockAsPixel.getY() + yOffset);

            final int borderColor = undergroundFluidPosition.fluid.getColor();
            final int borderAlpha = 204;
            DrawUtil.drawRectangle(pixel.getX(), pixel.getY(), VP.undergroundFluidSizeChunkX * VP.chunkWidth * blockSize, 2 * blockSize, borderColor, borderAlpha);
            DrawUtil.drawRectangle(pixel.getX() + VP.undergroundFluidSizeChunkX * VP.chunkWidth * blockSize, pixel.getY(), 2 * blockSize, VP.undergroundFluidSizeChunkZ * VP.chunkDepth * blockSize, borderColor, borderAlpha);
            DrawUtil.drawRectangle(pixel.getX() + 2 * blockSize, pixel.getY() + VP.undergroundFluidSizeChunkZ * VP.chunkDepth * blockSize, VP.undergroundFluidSizeChunkX * VP.chunkWidth * blockSize, 2 * blockSize, borderColor, borderAlpha);
            DrawUtil.drawRectangle(pixel.getX(), pixel.getY() + 2 * blockSize, 2 * blockSize, VP.undergroundFluidSizeChunkZ * VP.chunkDepth * blockSize, borderColor, borderAlpha);

            final String label = undergroundFluidPosition.getMinProduction() + "L - " + maxAmountInField + "L  " + Utils.getEnglishLocalization(undergroundFluidPosition.fluid);
            DrawUtil.drawLabel(label, pixel.getX() + VP.chunkWidth * blockSize, pixel.getY(), DrawUtil.HAlign.Right, DrawUtil.VAlign.Below, 0, 180, 0x00FFFFFF, 255, fontScale, false, rotation);
        }
    }
}
