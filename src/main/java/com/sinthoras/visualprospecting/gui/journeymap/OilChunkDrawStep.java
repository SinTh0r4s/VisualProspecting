package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.render.map.GridRenderer;
import net.minecraftforge.fluids.Fluid;

import java.awt.geom.Point2D;

public class OilChunkDrawStep implements DrawStep {

    private final int blockX;
    private final int blockZ;
    private final Fluid oil;
    private final int oilAmount;
    private final int maxAmountInField;
    private final int minAmountInField;

    public OilChunkDrawStep(int chunkX, int chunkZ, Fluid oil, int oilAmount, int minAmountInField, int maxAmountInField) {
        blockX = Utils.coordChunkToBlock(chunkX);
        blockZ = Utils.coordChunkToBlock(chunkZ);
        this.oil = oil;
        this.oilAmount = oilAmount;
        this.maxAmountInField = maxAmountInField;
        this.minAmountInField = minAmountInField;
    }

    private String getOilAmountFormatted() {
        if(oilAmount >= 1000) {
            return (oilAmount / 1000) + "kL";
        }
        return oilAmount + "L";
    }

    @Override
    public void draw(double xOffset, double yOffset, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        if (oilAmount > 0) {
            double blockSize = Math.pow(2, gridRenderer.getZoom());
            final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(blockX, blockZ);
            final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + xOffset, blockAsPixel.getY() + yOffset);
            float alpha = ((float)(oilAmount - minAmountInField)) / (maxAmountInField - minAmountInField + 1);
            alpha *= alpha * 204;
            DrawUtil.drawRectangle(pixel.getX(), pixel.getY(), VP.chunkWidth * blockSize, VP.chunkDepth * blockSize, Utils.getMapColorForOil(oil), (int)alpha);

            if(oilAmount >= maxAmountInField) {
                final int borderColor = 0xFFD700;
                final int borderAlpha = 204;
                DrawUtil.drawRectangle(pixel.getX(), pixel.getY(), 15 * blockSize, blockSize, borderColor, borderAlpha);
                DrawUtil.drawRectangle(pixel.getX() + 15 * blockSize, pixel.getY(), blockSize, 15 * blockSize, borderColor, borderAlpha);
                DrawUtil.drawRectangle(pixel.getX() + 1 * blockSize, pixel.getY() + 15 * blockSize, 15 * blockSize, blockSize, borderColor, borderAlpha);
                DrawUtil.drawRectangle(pixel.getX(), pixel.getY() + 1 * blockSize, blockSize, 15 * blockSize, borderColor, borderAlpha);
            }

            if (gridRenderer.getZoom() >= Config.minZoomLevel) {
                DrawUtil.drawLabel(getOilAmountFormatted(), pixel.getX() + 13 * blockSize, pixel.getY() + 13 * blockSize, DrawUtil.HAlign.Left, DrawUtil.VAlign.Above, 0, 180, 0x00FFFFFF, 255, fontScale, false, rotation);
            }
        }
    }
}
