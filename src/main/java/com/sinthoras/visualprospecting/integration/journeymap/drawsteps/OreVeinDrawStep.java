package com.sinthoras.visualprospecting.integration.journeymap.drawsteps;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.integration.DrawUtils;
import com.sinthoras.visualprospecting.integration.model.locations.IWaypointAndLocationProvider;
import com.sinthoras.visualprospecting.integration.model.locations.OreVeinLocation;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class OreVeinDrawStep implements ClickableDrawStep {

    private static final ResourceLocation depletedTextureLocation = new ResourceLocation(Tags.MODID, "textures/depleted.png");

    private final OreVeinLocation oreVeinLocation;

    private double iconX;
    private double iconY;
    private double iconSize;

    public OreVeinDrawStep(OreVeinLocation oreVeinLocation) {
        this.oreVeinLocation = oreVeinLocation;
    }

    @Override
    public List<String> getTooltip() {
        final List<String> tooltip = new ArrayList<>();
        if(oreVeinLocation.isDepleted()) {
            tooltip.add(oreVeinLocation.getDepletedHint());
        }
        if(oreVeinLocation.isActiveAsWaypoint()) {
            tooltip.add(oreVeinLocation.getActiveWaypointHint());
        }
        tooltip.add(oreVeinLocation.getName());
        if(oreVeinLocation.isDepleted() == false) {
            tooltip.addAll(oreVeinLocation.getMaterialNames());
        }
        tooltip.add(oreVeinLocation.getToggleDepletedHint());
        return tooltip;
    }

    @Override
    public void drawTooltip(FontRenderer fontRenderer, int mouseX, int mouseY, int displayWidth, int displayHeight) {

    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= iconX && mouseX <= iconX + iconSize && mouseY >= iconY && mouseY <= iconY + iconSize;
    }

    @Override
    public void onActionKeyPressed() {
        oreVeinLocation.toggleOreVein();
    }

    @Override
    public IWaypointAndLocationProvider getLocationProvider() {
        return oreVeinLocation;
    }

    @Override
    public void draw(double draggedPixelX, double draggedPixelY, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        iconSize = 32 * fontScale;
        final double iconSizeHalf = iconSize / 2;
        final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(oreVeinLocation.getBlockX(), oreVeinLocation.getBlockZ());
        final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + draggedPixelX, blockAsPixel.getY() + draggedPixelY);


        if(gridRenderer.getZoom() >= Config.minZoomLevelForOreLabel && oreVeinLocation.isDepleted() == false) {
            final int fontColor = oreVeinLocation.drawSearchHighlight() ? 0xFFFFFF : 0x7F7F7F;
            DrawUtil.drawLabel(oreVeinLocation.getName(), pixel.getX(), pixel.getY() - iconSize, DrawUtil.HAlign.Center, DrawUtil.VAlign.Middle, 0, 180, fontColor, 255, fontScale, false, rotation);
        }

        iconX = pixel.getX() - iconSizeHalf;
        iconY = pixel.getY() - iconSizeHalf;
        final IIcon blockIcon = Blocks.stone.getIcon(0, 0);
        DrawUtils.drawQuad(blockIcon, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255);

        DrawUtils.drawQuad(oreVeinLocation.getIconFromPrimaryOre(), iconX, iconY, iconSize, iconSize, oreVeinLocation.getColor(), 255);

        if(oreVeinLocation.drawSearchHighlight() == false || oreVeinLocation.isDepleted()) {
            DrawUtil.drawRectangle(iconX, iconY, iconSize, iconSize, 0x000000, 150);
            if(oreVeinLocation.isDepleted()) {
                DrawUtils.drawQuad(depletedTextureLocation, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255);
            }
        }

        if(oreVeinLocation.isActiveAsWaypoint()) {
            final double thickness = iconSize / 8;
            final int borderAlpha = 204;
            final int color = 0xFFD700;
            DrawUtil.drawRectangle(iconX - thickness, iconY - thickness, iconSize + thickness, thickness, color, borderAlpha);
            DrawUtil.drawRectangle(iconX + iconSize, iconY - thickness, thickness, iconSize + thickness, color, borderAlpha);
            DrawUtil.drawRectangle(iconX, iconY + iconSize, iconSize + thickness, thickness, color, borderAlpha);
            DrawUtil.drawRectangle(iconX - thickness, iconY, thickness, iconSize + thickness, color, borderAlpha);
        }
    }
}
