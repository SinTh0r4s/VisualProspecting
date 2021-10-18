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


        if(gridRenderer.getZoom() >= Config.minZoomLevelForOreLabel) {
            final int fontColor = oreVeinPosition.veinType.isHighlighted() ? 0xFFFFFF : 0x7F7F7F;
            DrawUtil.drawLabel(oreVeinPosition.veinType.getNameReadable() + " Vein", pixel.getX(), pixel.getY() - iconSize, DrawUtil.HAlign.Center, DrawUtil.VAlign.Middle, 0, 180, fontColor, 255, fontScale, false, rotation);
        }

        iconX = pixel.getX() - iconSizeHalf;
        iconY = pixel.getY() - iconSizeHalf;
        final IIcon blockIcon = Blocks.stone.getIcon(0, 0);
        drawQuad(blockIcon, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255, false);

        Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
        final int color = (aMaterial.mRGBa[0] << 16) | (aMaterial.mRGBa[1]) << 8 | aMaterial.mRGBa[2];
        final IIcon oreIcon = aMaterial.mIconSet.mTextures[OrePrefixes.ore.mTextureIndex].getIcon();
        drawQuad(oreIcon, iconX, iconY, iconSize, iconSize, color, 255, true);

        if(oreVeinPosition.veinType.isHighlighted() == false || oreVeinPosition.isDepleted()) {
            DrawUtil.drawRectangle(iconX, iconY, iconSize, iconSize, 0x000000, 150);
            if(oreVeinPosition.isDepleted()) {
                drawQuad(depletedTextureLocation, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255);
            }
        }
    }

    public boolean mouseOver(int mouseX, int mouseY) {
        mouseOver = mouseX >= iconX && mouseX <= iconX + iconSize && mouseY >= iconY && mouseY <= iconY + iconSize;
        return mouseOver;
    }

    public List<String> getTooltip() {
        final ArrayList<String> list = new ArrayList();
        if(oreVeinPosition.isDepleted()) {
            list.add(EnumChatFormatting.RED + I18n.format("visualprospecting.depleted"));
        }
        list.add(EnumChatFormatting.WHITE + oreVeinPosition.veinType.getNameReadable() + " Vein");
        list.addAll(oreVeinPosition.veinType.getOreMaterialNames().stream().map(materialName -> EnumChatFormatting.GRAY + materialName).collect(Collectors.toList()));
        list.add(EnumChatFormatting.DARK_GRAY + I18n.format("visualprospecting.depleted.toggle", Keyboard.getKeyName(Keyboard.KEY_DELETE)));
        return list;
    }

    public void toggleDepletedIfMouseOver() {
        if(mouseOver) {
            VP.clientCache.toggleOreVein(oreVeinPosition.dimensionId, oreVeinPosition.chunkX, oreVeinPosition.chunkZ);
        }
    }

    public static void drawQuad(ResourceLocation texture, double x, double y, double width, double height, int color, float alpha) {
        IRenderHelper renderHelper = ForgeHelper.INSTANCE.getRenderHelper();
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
