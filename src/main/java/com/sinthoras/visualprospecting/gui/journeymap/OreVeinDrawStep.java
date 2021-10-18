package com.sinthoras.visualprospecting.gui.journeymap;

import com.sinthoras.visualprospecting.Config;
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
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OreVeinDrawStep implements DrawStep {

    private final OreVeinPosition oreVeinPosition;
    private double iconX;
    private double iconY;
    private double iconSize;


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
        drawQuad(blockIcon, iconX, iconY, iconSize, iconSize, 0.0, 0xFFFFFF, 255, false, GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, false);

        Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
        final int color = (aMaterial.mRGBa[0] << 16) | (aMaterial.mRGBa[1]) << 8 | aMaterial.mRGBa[2];
        final IIcon oreIcon = aMaterial.mIconSet.mTextures[OrePrefixes.ore.mTextureIndex].getIcon();
        drawQuad(oreIcon, iconX, iconY, iconSize, iconSize, 0.0, color, 255, true, GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, false);

        if(oreVeinPosition.veinType.isHighlighted() == false) {
            DrawUtil.drawRectangle(iconX, iconY, iconSize, iconSize, 0x000000, 150);
        }
    }

    public boolean mouseOver(int mouseX, int mouseY) {
        return mouseX >= iconX && mouseX <= iconX + iconSize && mouseY >= iconY && mouseY <= iconY + iconSize;
    }

    public List<String> getTooltip() {
        final ArrayList<String> list = new ArrayList();
        list.add(oreVeinPosition.veinType.getNameReadable() + " Vein");
        list.addAll(oreVeinPosition.veinType.getOreMaterials().stream().filter(Objects::nonNull).map(material -> EnumChatFormatting.GRAY + material.mLocalizedName + " ore").collect(Collectors.toList()));
        return list;
    }

    public static void drawQuad(IIcon icon, double x, double y, double width, double height, double rotation, Integer color, float alpha, boolean blend, int glBlendSfactor, int glBlendDFactor, boolean clampTexture) {
        IRenderHelper renderHelper = ForgeHelper.INSTANCE.getRenderHelper();

        GL11.glPushMatrix();

        try {
            if (blend) {
                renderHelper.glEnableBlend();
                renderHelper.glBlendFunc(glBlendSfactor, glBlendDFactor, 1, 0);
            }

            renderHelper.glEnableTexture2D();
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            if (blend && color != null) {
                float[] c = RGB.floats(color);
                renderHelper.glColor4f(c[0], c[1], c[2], alpha);
            }
            else {
                renderHelper.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            }

            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            int texEdgeBehavior = clampTexture ? GL11.GL_NEAREST : GL11.GL_REPEAT;
            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, texEdgeBehavior);
            renderHelper.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, texEdgeBehavior);
            if (rotation != 0.0D) {
                double transX = x + width / 2.0D;
                double transY = y + height / 2.0D;
                GL11.glTranslated(transX, transY, 0.0D);
                GL11.glRotated(rotation, 0.0D, 0.0D, 1.0D);
                GL11.glTranslated(-transX, -transY, 0.0D);
            }
            renderHelper.startDrawingQuads(false);
            renderHelper.addVertexWithUV(x, y + height, DrawUtil.zLevel, icon.getMinU(), icon.getMaxV());
            renderHelper.addVertexWithUV(x + width, y + height, DrawUtil.zLevel, icon.getMaxU(), icon.getMaxV());
            renderHelper.addVertexWithUV(x + width, y, DrawUtil.zLevel, icon.getMaxU(), icon.getMinV());
            renderHelper.addVertexWithUV(x, y, DrawUtil.zLevel, icon.getMinU(), icon.getMinV());
            renderHelper.draw();
            if (blend) {
                renderHelper.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (glBlendSfactor != GL11.GL_SRC_ALPHA || glBlendDFactor != GL11.GL_ONE_MINUS_SRC_ALPHA) {
                    renderHelper.glEnableBlend();
                    renderHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                }
            }
        }
        finally {
            GL11.glPopMatrix();
        }

    }
}
