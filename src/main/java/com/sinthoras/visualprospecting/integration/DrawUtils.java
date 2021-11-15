package com.sinthoras.visualprospecting.integration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

import java.util.List;

public class DrawUtils {

    public static void drawGradientRect(double minPixelX, double minPixelY, double maxPixelX, double maxPixelY, double z, int colorA, int colorB) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        float alpha = (colorA >> 24 & 255) / 255.0f;
        float red = (colorA >> 16 & 255) / 255.0f;
        float green = (colorA >> 8 & 255) / 255.0f;
        float blue = (colorA & 255) / 255.0f;
        tessellator.setColorRGBA_F(red, green, blue, alpha);
        tessellator.addVertex(maxPixelX, minPixelY, z);
        tessellator.addVertex(minPixelX, minPixelY, z);

        alpha = (colorB >> 24 & 255) / 255.0f;
        red = (colorB >> 16 & 255) / 255.0f;
        green = (colorB >> 8 & 255) / 255.0f;
        blue = (colorB & 255) / 255.0f;
        tessellator.setColorRGBA_F(red, green, blue, alpha);
        tessellator.addVertex(minPixelX, maxPixelY, z);
        tessellator.addVertex(maxPixelX, maxPixelY, z);

        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawGradientRect(double minPixelX, double minPixelY, double maxPixelX, double maxPixelY, int colorA, int colorB) {
        drawGradientRect(minPixelX, minPixelY, maxPixelX, maxPixelY, 300, colorA, colorB);
    }

    public static void drawQuad(ResourceLocation texture, double x, double y, double width, double height, int color, float alpha) {

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        float[] c = floats(color);
        GL11.glColor4f(c[0], c[1], c[2], alpha);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0.0, 0.0, 1.0);
        tessellator.addVertexWithUV(x + width, y + height, 0.0, 1.0, 1.0);
        tessellator.addVertexWithUV(x + width, y, 0.0, 1.0, 0.0);
        tessellator.addVertexWithUV(x, y, 0.0, 0.0, 0.0);
        tessellator.draw();

    }

    public static void drawQuad(IIcon icon, double x, double y, double width, double height, int color, float alpha) {

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        float[] c = floats(color);
        GL11.glColor4f(c[0], c[1], c[2], alpha);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0.0, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y + height, 0.0, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y, 0.0, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, y, 0.0, icon.getMinU(), icon.getMinV());
        tessellator.draw();

    }

    public static void drawAspect(double centerPixelX, double centerPixelY, double pixelSize, Aspect aspect, int amount) {
        final int textureSize = 16;

        GL11.glPushMatrix();
        final double scale = pixelSize / textureSize;
        GL11.glScaled(scale, scale, scale);
        UtilsFX.drawTag((centerPixelX - pixelSize / 2) / scale, (centerPixelY - pixelSize / 2) / scale, aspect, amount, 0, 0, GL11.GL_ONE_MINUS_SRC_ALPHA, 1.0F, false);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    public static void drawSimpleLabel(GuiScreen gui, String text, double textX, double textY, int fontColor, int bgColor, boolean centered) {
        GL11.glPushMatrix();
        double dTextX = textX - (double) (int) textX;
        double dTextY = textY - (double) (int) textY;
        double textWidth = gui.mc.fontRenderer.getStringWidth(text);
        double xOffsetL = centered ? -textWidth / 2.0 - 2 : -2;
        double xOffsetR = centered ? textWidth / 2.0 + 2 : textWidth + 2;
        GL11.glTranslated(dTextX, dTextY, 0.0);
        drawGradientRect((int) textX + xOffsetL, (int) textY - 2, (int) textX + xOffsetR, (int) textY + gui.mc.fontRenderer.FONT_HEIGHT + 2, 0, bgColor, bgColor);
        if (centered)
            gui.drawCenteredString(gui.mc.fontRenderer, text, (int) textX, (int) textY, fontColor);
        else
            gui.drawString(gui.mc.fontRenderer, text, (int) textX, (int) textY, fontColor);
        GL11.glPopMatrix();
    }

    public static void drawSimpleTooltip(GuiScreen gui, List<String> text, double x, double y, int fontColor, int bgColor) {
        if (text.isEmpty()) return;

        int maxTextWidth = 0;
        for (String str : text) {
            int strWidth = gui.mc.fontRenderer.getStringWidth(str);
            if (strWidth > maxTextWidth)
                maxTextWidth = strWidth;
        }

        int boxWidth = maxTextWidth + 6;
        int boxHeight = text.size() * (gui.mc.fontRenderer.FONT_HEIGHT + 2) + 6;

        double dx = x - (double) (int) x;
        double dy = y - (double) (int) y;

        GL11.glPushMatrix();

        drawGradientRect(x, y, x + boxWidth, y + boxHeight, bgColor, bgColor);
        GL11.glTranslated(dx, dy, 301);
        for (int i = 0; i < text.size(); i++) {
            gui.drawString(gui.mc.fontRenderer, text.get(i), (int) x + 3, (int) y + 3 + i * (gui.mc.fontRenderer.FONT_HEIGHT + 2), fontColor);
        }

        GL11.glPopMatrix();
    }

    public static float[] floats(int rgb) {
        return new float[]{(float) (rgb >> 16 & 255) / 255.0F, (float) (rgb >> 8 & 255) / 255.0F, (float) (rgb & 255) / 255.0F};
    }
}
