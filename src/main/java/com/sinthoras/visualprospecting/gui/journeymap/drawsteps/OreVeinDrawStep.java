package com.sinthoras.visualprospecting.gui.journeymap.drawsteps;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.gui.journeymap.DrawUtils;
import com.sinthoras.visualprospecting.gui.journeymap.layers.OreVeinLayer;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import journeymap.client.model.Waypoint;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OreVeinDrawStep implements ClickableDrawStep {

    private static final ResourceLocation depletedTextureLocation = new ResourceLocation(Tags.MODID, "textures/depleted.png");

    private final OreVeinPosition oreVeinPosition;
    private double iconX;
    private double iconY;
    private double iconSize;


    public OreVeinDrawStep(final OreVeinPosition oreVeinPosition) {
        this.oreVeinPosition = oreVeinPosition;
    }

    public void drawTooltip(FontRenderer fontRenderer, int mouseX, int mouseY, int displayWidth, int displayHeight) {

    }

    @Override
    public void draw(double draggedPixelX, double draggedPixelY, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        iconSize = 32 * fontScale;
        final double iconSizeHalf = iconSize / 2;
        final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(oreVeinPosition.getBlockX(), oreVeinPosition.getBlockZ());
        final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + draggedPixelX, blockAsPixel.getY() + draggedPixelY);


        if(gridRenderer.getZoom() >= Config.minZoomLevelForOreLabel && oreVeinPosition.isDepleted() == false) {
            final int fontColor = oreVeinPosition.veinType.isHighlighted() ? 0xFFFFFF : 0x7F7F7F;
            DrawUtil.drawLabel(I18n.format(oreVeinPosition.veinType.name), pixel.getX(), pixel.getY() - iconSize, DrawUtil.HAlign.Center, DrawUtil.VAlign.Middle, 0, 180, fontColor, 255, fontScale, false, rotation);
        }

        iconX = pixel.getX() - iconSizeHalf;
        iconY = pixel.getY() - iconSizeHalf;
        final IIcon blockIcon = Blocks.stone.getIcon(0, 0);
        DrawUtils.drawQuad(blockIcon, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255);

        DrawUtils.drawQuad(getIconFromPrimaryOre(), iconX, iconY, iconSize, iconSize, getColor(), 255);

        if(oreVeinPosition.veinType.isHighlighted() == false || oreVeinPosition.isDepleted()) {
            DrawUtil.drawRectangle(iconX, iconY, iconSize, iconSize, 0x000000, 150);
            if(oreVeinPosition.isDepleted()) {
                DrawUtils.drawQuad(depletedTextureLocation, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255);
            }
        }

        if(isWaypoint(OreVeinLayer.instance.getActiveWaypoint())) {
            final double thickness = iconSize / 8;
            final int borderAlpha = 204;
            final int color = 0xFFD700;
            DrawUtil.drawRectangle(iconX - thickness, iconY - thickness, iconSize + thickness, thickness, color, borderAlpha);
            DrawUtil.drawRectangle(iconX + iconSize, iconY - thickness, thickness, iconSize + thickness, color, borderAlpha);
            DrawUtil.drawRectangle(iconX, iconY + iconSize, iconSize + thickness, thickness, color, borderAlpha);
            DrawUtil.drawRectangle(iconX - thickness, iconY, thickness, iconSize + thickness, color, borderAlpha);
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= iconX && mouseX <= iconX + iconSize && mouseY >= iconY && mouseY <= iconY + iconSize;
    }

    public List<String> getTooltip() {
        final List<String> tooltip = new ArrayList<>();
        if(oreVeinPosition.isDepleted()) {
            tooltip.add(EnumChatFormatting.RED + I18n.format("visualprospecting.depleted"));
        }
        if(isWaypoint(OreVeinLayer.instance.getActiveWaypoint())) {
            tooltip.add(EnumChatFormatting.GOLD + I18n.format("visualprospecting.iswaypoint"));
        }
        tooltip.add(EnumChatFormatting.WHITE + I18n.format(oreVeinPosition.veinType.name));
        if(oreVeinPosition.isDepleted() == false) {
            tooltip.addAll(oreVeinPosition.veinType.getOreMaterialNames().stream().map(materialName -> EnumChatFormatting.GRAY + materialName).collect(Collectors.toList()));
        }
        tooltip.add(EnumChatFormatting.DARK_GRAY + I18n.format("visualprospecting.node.deletehint", Keyboard.getKeyName(VP.keyAction.getKeyCode())));
        return tooltip;
    }

    public void onActionKeyPressed() {
        ClientCache.instance.toggleOreVein(oreVeinPosition.dimensionId, oreVeinPosition.chunkX, oreVeinPosition.chunkZ);
    }

    private int getColor() {
        Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
        return (aMaterial.mRGBa[0] << 16) | (aMaterial.mRGBa[1]) << 8 | aMaterial.mRGBa[2];
    }

    private IIcon getIconFromPrimaryOre() {
        Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
        return aMaterial.mIconSet.mTextures[OrePrefixes.ore.mTextureIndex].getIcon();
    }

    public Waypoint toWaypoint() {
        return new Waypoint(I18n.format("visualprospecting.tracked", I18n.format(oreVeinPosition.veinType.name)),
                oreVeinPosition.getBlockX(),
                65,
                oreVeinPosition.getBlockZ(),
                new Color(getColor()),
                Waypoint.Type.Normal,
                oreVeinPosition.dimensionId);
    }

    public boolean isWaypoint(Waypoint waypoint) {
        return waypoint != null
                && waypoint.getDimensions().contains(oreVeinPosition.dimensionId)
                && waypoint.getX() == oreVeinPosition.getBlockX()
                && waypoint.getZ() == oreVeinPosition.getBlockZ();
    }
}
