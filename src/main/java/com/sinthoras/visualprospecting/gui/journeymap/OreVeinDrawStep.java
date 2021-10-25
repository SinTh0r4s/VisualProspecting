package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import journeymap.client.cartography.RGB;
import journeymap.client.forge.helper.ForgeHelper;
import journeymap.client.forge.helper.IRenderHelper;
import journeymap.client.model.Waypoint;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OreVeinDrawStep implements DrawStep {

    private static final ResourceLocation depletedTextureLocation = new ResourceLocation(Tags.MODID, "textures/depleted.png");

    private final OreVeinPosition oreVeinPosition;
    private double iconX;
    private double iconY;
    private double iconSize;
    private boolean mouseOver = false;


    public OreVeinDrawStep(final OreVeinPosition oreVeinPosition) {
        this.oreVeinPosition = oreVeinPosition;
    }

    @Override
    public void draw(double xOffset, double yOffset, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        iconSize = 32 * fontScale;
        final double iconSizeHalf = iconSize / 2;
        final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(oreVeinPosition.getBlockX(), oreVeinPosition.getBlockZ());
        final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + xOffset, blockAsPixel.getY() + yOffset);


        if(gridRenderer.getZoom() >= Config.minZoomLevelForOreLabel && oreVeinPosition.isDepleted() == false) {
            final int fontColor = oreVeinPosition.veinType.isHighlighted() ? 0xFFFFFF : 0x7F7F7F;
            DrawUtil.drawLabel(oreVeinPosition.veinType.getNameReadable(), pixel.getX(), pixel.getY() - iconSize, DrawUtil.HAlign.Center, DrawUtil.VAlign.Middle, 0, 180, fontColor, 255, fontScale, false, rotation);
        }

        iconX = pixel.getX() - iconSizeHalf;
        iconY = pixel.getY() - iconSizeHalf;
        final IIcon blockIcon = Blocks.stone.getIcon(0, 0);
        drawQuad(blockIcon, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255, false);

        drawQuad(getIconFromPrimaryOre(), iconX, iconY, iconSize, iconSize, getColor(), 255, true);

        if(oreVeinPosition.veinType.isHighlighted() == false || oreVeinPosition.isDepleted()) {
            DrawUtil.drawRectangle(iconX, iconY, iconSize, iconSize, 0x000000, 150);
            if(oreVeinPosition.isDepleted()) {
                drawQuad(depletedTextureLocation, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255);
            }
        }

        if(oreVeinPosition.isAsWaypointActive()) {
            final double thickness = iconSize / 8;
            final int borderAlpha = 204;
            final int color = 0xFFD700;
            DrawUtil.drawRectangle(iconX - thickness, iconY - thickness, iconSize + thickness, thickness, color, borderAlpha);
            DrawUtil.drawRectangle(iconX + iconSize, iconY - thickness, thickness, iconSize + thickness, color, borderAlpha);
            DrawUtil.drawRectangle(iconX, iconY + iconSize, iconSize + thickness, thickness, color, borderAlpha);
            DrawUtil.drawRectangle(iconX - thickness, iconY, thickness, iconSize + thickness, color, borderAlpha);
        }
    }

    public boolean mouseOver(int mouseX, int mouseY) {
        mouseOver = mouseX >= iconX && mouseX <= iconX + iconSize && mouseY >= iconY && mouseY <= iconY + iconSize;
        return mouseOver;
    }

    public List<String> getTooltip() {
        final List<String> tooltop = new ArrayList<>();
        if(oreVeinPosition.isDepleted()) {
            tooltop.add(EnumChatFormatting.RED + I18n.format("visualprospecting.depleted"));
        }
        if(oreVeinPosition.isAsWaypointActive()) {
            tooltop.add(EnumChatFormatting.GOLD + I18n.format("visualprospecting.iswaypoint"));
        }
        tooltop.add(EnumChatFormatting.WHITE + oreVeinPosition.veinType.getNameReadable());
        if(oreVeinPosition.isDepleted() == false) {
            tooltop.addAll(oreVeinPosition.veinType.getOreMaterialNames().stream().map(materialName -> EnumChatFormatting.GRAY + materialName).collect(Collectors.toList()));
        }
        tooltop.add(EnumChatFormatting.DARK_GRAY + I18n.format("visualprospecting.node.deletehint", Keyboard.getKeyName(VP.keyDelete.getKeyCode())));
        return tooltop;
    }

    public boolean onDeletePressed() {
        if(mouseOver) {
            VP.clientCache.toggleOreVein(oreVeinPosition.dimensionId, oreVeinPosition.chunkX, oreVeinPosition.chunkZ);
        }
        return false;
    }

    private int getColor() {
        Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
        return (aMaterial.mRGBa[0] << 16) | (aMaterial.mRGBa[1]) << 8 | aMaterial.mRGBa[2];
    }

    private IIcon getIconFromPrimaryOre() {
        Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
        return aMaterial.mIconSet.mTextures[OrePrefixes.ore.mTextureIndex].getIcon();
    }

    public boolean onMouseClick(int mouseX, int mouseY, double blockSize, boolean isDoubleClick) {
        final boolean clickMouseOver = mouseX >= iconX - blockSize && mouseX <= iconX + iconSize + blockSize && mouseY >= iconY - blockSize && mouseY <= iconY + iconSize + blockSize;
        if(isDoubleClick) {
            oreVeinPosition.triggerAsWaypointActive(clickMouseOver);
        }
        return clickMouseOver;
    }

    public void disableWaypoint() {
        oreVeinPosition.triggerAsWaypointActive(false);
    }

    public Waypoint toWaypoint() {
        return new Waypoint(I18n.format("visualprospecting.tracked", oreVeinPosition.veinType.getNameReadable()),
                oreVeinPosition.getBlockX(),
                65,
                oreVeinPosition.getBlockZ(),
                new Color(getColor()),
                Waypoint.Type.Normal,
                oreVeinPosition.dimensionId);
    }

    public static void drawQuad(ResourceLocation texture, double x, double y, double width, double height, int color, float alpha) {
        final IRenderHelper renderHelper = ForgeHelper.INSTANCE.getRenderHelper();

        GL11.glPushMatrix();
        try {
            renderHelper.glEnableBlend();
            renderHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            renderHelper.glEnableTexture2D();
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

            float[] c = RGB.floats(color);
            renderHelper.glColor4f(c[0], c[1], c[2], alpha);

            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

            renderHelper.startDrawingQuads(false);
            renderHelper.addVertexWithUV(x, y + height, DrawUtil.zLevel, 0.0, 1.0);
            renderHelper.addVertexWithUV(x + width, y + height, DrawUtil.zLevel, 1.0, 1.0);
            renderHelper.addVertexWithUV(x + width, y, DrawUtil.zLevel, 1.0, 0.0);
            renderHelper.addVertexWithUV(x, y, DrawUtil.zLevel, 0.0, 0.0);
            renderHelper.draw();

            renderHelper.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        finally {
            GL11.glPopMatrix();
        }
    }

    public static void drawQuad(IIcon icon, double x, double y, double width, double height, int color, float alpha, boolean blend) {
        IRenderHelper renderHelper = ForgeHelper.INSTANCE.getRenderHelper();
        GL11.glPushMatrix();
        try {
            if (blend) {
                renderHelper.glEnableBlend();
                renderHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            }

            renderHelper.glEnableTexture2D();
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

            float[] c = RGB.floats(color);
            renderHelper.glColor4f(c[0], c[1], c[2], alpha);

            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

            renderHelper.startDrawingQuads(false);
            renderHelper.addVertexWithUV(x, y + height, DrawUtil.zLevel, icon.getMinU(), icon.getMaxV());
            renderHelper.addVertexWithUV(x + width, y + height, DrawUtil.zLevel, icon.getMaxU(), icon.getMaxV());
            renderHelper.addVertexWithUV(x + width, y, DrawUtil.zLevel, icon.getMaxU(), icon.getMinV());
            renderHelper.addVertexWithUV(x, y, DrawUtil.zLevel, icon.getMinU(), icon.getMinV());
            renderHelper.draw();
            if (blend) {
                renderHelper.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        finally {
            GL11.glPopMatrix();
        }
    }
}
