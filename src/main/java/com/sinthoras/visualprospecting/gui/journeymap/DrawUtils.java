package com.sinthoras.visualprospecting.gui.journeymap;

import journeymap.client.cartography.RGB;
import journeymap.client.forge.helper.ForgeHelper;
import journeymap.client.forge.helper.IRenderHelper;
import journeymap.client.render.draw.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

public class DrawUtils {

    public static void drawGradientRect(int minPixelX, int minPixelY, int maxPixelX, int maxPixelY, int colorA, int colorB)
    {
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
        tessellator.addVertex(maxPixelX, minPixelY, 300);
        tessellator.addVertex(minPixelX, minPixelY, 300);

        alpha = (colorB >> 24 & 255) / 255.0f;
        red = (colorB >> 16 & 255) / 255.0f;
        green = (colorB >> 8 & 255) / 255.0f;
        blue = (colorB & 255) / 255.0f;
        tessellator.setColorRGBA_F(red, green, blue, alpha);
        tessellator.addVertex(minPixelX, maxPixelY, 300);
        tessellator.addVertex(maxPixelX, maxPixelY, 300);

        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawQuad(ResourceLocation texture, double x, double y, double width, double height, int color, float alpha) {
        final IRenderHelper renderHelper = ForgeHelper.INSTANCE.getRenderHelper();

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
    }

    public static void drawQuad(IIcon icon, double x, double y, double width, double height, int color, float alpha) {
        IRenderHelper renderHelper = ForgeHelper.INSTANCE.getRenderHelper();

        renderHelper.glEnableBlend();
        renderHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

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
    }

    public static void drawAspect(double centerPixelX, double centerPixelY, double pixelSize, Aspect aspect, int amount) {
        final int textureSize = 16;

        GL11.glPushMatrix();
        final double scale = pixelSize / textureSize;
        GL11.glScaled(scale, scale, scale);
        UtilsFX.drawTag((centerPixelX - pixelSize / 2) / scale, (centerPixelY - pixelSize / 2) / scale, aspect, amount, 0, 0.01, GL11.GL_ONE_MINUS_SRC_ALPHA, 1.0F, false);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
