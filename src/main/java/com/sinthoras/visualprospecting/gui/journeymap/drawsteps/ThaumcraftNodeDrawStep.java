package com.sinthoras.visualprospecting.gui.journeymap.drawsteps;

import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.gui.DrawUtils;
import com.sinthoras.visualprospecting.gui.model.layers.ThaumcraftNodeLayerManager;
import com.sinthoras.visualprospecting.gui.model.locations.IWaypointAndLocationProvider;
import com.sinthoras.visualprospecting.gui.model.locations.ThaumcraftNodeLocation;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

import java.awt.geom.Point2D;
import java.util.List;

public class ThaumcraftNodeDrawStep implements ClickableDrawStep {

    private static final ResourceLocation markedTextureLocation = new ResourceLocation(Tags.MODID, "textures/node_marked.png");
    private static final ResourceLocation unmarkedTextureLocation = new ResourceLocation(Tags.MODID, "textures/node_unmarked.png");

    private final ThaumcraftNodeLocation thaumcraftNodeLocation;

    private double centerPixelX = 0;
    private double centerPixelY = 0;
    private double clickableRadiusPixelSquared = 0;

    public ThaumcraftNodeDrawStep(ThaumcraftNodeLocation thaumcraftNodeLocation) {
        this.thaumcraftNodeLocation = thaumcraftNodeLocation;
    }

    @Override
    public void draw(double draggedPixelX, double draggedPixelY, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        final double borderSize = 44 * fontScale;
        final double borderSizeHalf = borderSize / 2;
        final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(thaumcraftNodeLocation.getBlockX(), thaumcraftNodeLocation.getBlockZ());
        final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + draggedPixelX, blockAsPixel.getY() + draggedPixelY);
        centerPixelX = pixel.getX();
        centerPixelY = pixel.getY();
        clickableRadiusPixelSquared = borderSizeHalf * borderSizeHalf;

        final int alpha = 204;
        DrawUtils.drawQuad(thaumcraftNodeLocation.isActiveAsWaypoint() ? markedTextureLocation : unmarkedTextureLocation, pixel.getX() - borderSizeHalf, pixel.getY() - borderSizeHalf, borderSize, borderSize, 0xFFFFFF, alpha);

        final int aspectPixelDiameter = 32;
        DrawUtils.drawAspect(pixel.getX(), pixel.getY(), aspectPixelDiameter, thaumcraftNodeLocation.getStrongestAspect(), 0);
    }

    public List<String> getTooltip() {
        return null;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        final double deltaX = mouseX - centerPixelX;
        final double deltaY = mouseY - centerPixelY;
        return deltaX * deltaX + deltaY * deltaY <= clickableRadiusPixelSquared;
    }

    public void onActionKeyPressed() {
        ThaumcraftNodeLayerManager.instance.deleteNode(thaumcraftNodeLocation);
    }

    @Override
    public IWaypointAndLocationProvider getLocationProvider() {
        return thaumcraftNodeLocation;
    }

    public void drawTooltip(FontRenderer fontRenderer, int mouseX, int mouseY, int displayWidth, int displayHeight) {
        final boolean isWaypoint = thaumcraftNodeLocation.isActiveAsWaypoint();
        final String activeWaypointHint = thaumcraftNodeLocation.getActiveWaypointHint();
        final String title = thaumcraftNodeLocation.getTitle();
        final String nodeDescription = thaumcraftNodeLocation.getDescription();
        final String deleteHint = thaumcraftNodeLocation.getDeleteHint();

        int maxTextWidth = Math.max(Math.max(fontRenderer.getStringWidth(title), fontRenderer.getStringWidth(nodeDescription)), fontRenderer.getStringWidth(deleteHint));
        if(isWaypoint) {
            maxTextWidth = Math.max(maxTextWidth, fontRenderer.getStringWidth(activeWaypointHint));
        }
        if(fontRenderer.getBidiFlag()) {
            maxTextWidth = (int) Math.ceil(maxTextWidth * 1.25f);
        }

        final int aspectRows = (thaumcraftNodeLocation.getAspects().size() + 4) / 5;  // Equivalent to Math.ceil(size / 5)
        final int aspectColumns = Math.min(thaumcraftNodeLocation.getAspects().size(), 5);

        int pixelX = mouseX + 12;
        int pixelY = mouseY - 12;
        final int tooltipHeight = (isWaypoint ? 44 : 32) + aspectRows * 16;
        final int tooltipWidth = Math.max(aspectColumns * 16, maxTextWidth);
        if(pixelX + tooltipWidth > displayWidth) {
            pixelX -= 28 + tooltipWidth;
        }
        if(pixelY + tooltipHeight + 6 > displayHeight) {
            pixelY = displayHeight - tooltipHeight - 6;
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // Draw background
        final int backgroundColor = 0xF0100010;
        DrawUtils.drawGradientRect(pixelX - 3, pixelY - 4, pixelX + tooltipWidth + 3, pixelY - 3, backgroundColor, backgroundColor);
        DrawUtils.drawGradientRect(pixelX - 3, pixelY + tooltipHeight + 3, pixelX + tooltipWidth + 3, pixelY + tooltipHeight + 4, backgroundColor, backgroundColor);
        DrawUtils.drawGradientRect(pixelX - 3, pixelY - 3, pixelX + tooltipWidth + 3, pixelY + tooltipHeight + 3, backgroundColor, backgroundColor);
        DrawUtils.drawGradientRect(pixelX - 4, pixelY - 3, pixelX - 3, pixelY + tooltipHeight + 3, backgroundColor, backgroundColor);
        DrawUtils.drawGradientRect(pixelX + tooltipWidth + 3, pixelY - 3, pixelX + tooltipWidth + 4, pixelY + tooltipHeight + 3, backgroundColor, backgroundColor);

        int verdunGreen = 0x505000FF;
        int borderColor = 0x5028007F;
        DrawUtils.drawGradientRect(pixelX - 3, pixelY - 3 + 1, pixelX - 3 + 1, pixelY + tooltipHeight + 3 - 1, verdunGreen, borderColor);
        DrawUtils.drawGradientRect(pixelX + tooltipWidth + 2, pixelY - 3 + 1, pixelX + tooltipWidth + 3, pixelY + tooltipHeight + 3 - 1, verdunGreen, borderColor);
        DrawUtils.drawGradientRect(pixelX - 3, pixelY - 3, pixelX + tooltipWidth + 3, pixelY - 3 + 1, verdunGreen, verdunGreen);
        DrawUtils.drawGradientRect(pixelX - 3, pixelY + tooltipHeight + 2, pixelX + tooltipWidth + 3, pixelY + tooltipHeight + 3, borderColor, borderColor);

        // Draw text
        int offset = 0;
        if(fontRenderer.getBidiFlag()) {
            if(isWaypoint) {
                final int asWaypointWidth = (int) Math.ceil(fontRenderer.getStringWidth(activeWaypointHint) * 1.1f);
                fontRenderer.drawString(title, pixelX + tooltipWidth - asWaypointWidth, pixelY, 0xFFFFFFFF);
                offset += 12;
            }
            final int titleWidth = (int) Math.ceil(fontRenderer.getStringWidth(title) * 1.1f);
            fontRenderer.drawString(title, pixelX + tooltipWidth - titleWidth, pixelY + offset, 0xFFFFFFFF);
            offset += 12;
            final int nodeDescriptonWidth = (int) Math.ceil(fontRenderer.getStringWidth(nodeDescription) * 1.1f);
            fontRenderer.drawString(nodeDescription, pixelX + nodeDescriptonWidth - titleWidth, pixelY + offset, 0xFFFFFFFF);

            final int deleteHintWidth = (int) Math.ceil(fontRenderer.getStringWidth(deleteHint) * 1.1f);
            fontRenderer.drawString(deleteHint, pixelX + tooltipWidth - deleteHintWidth, pixelY + aspectRows * 16 + offset + 12, 0xFFFFFFFF);
        }
        else {
            if(isWaypoint) {
                fontRenderer.drawString(activeWaypointHint, pixelX, pixelY, 0xFFFFFFFF);
                offset += 12;
            }
            fontRenderer.drawString(title, pixelX, pixelY + offset, 0xFFFFFFFF);
            offset += 12;
            fontRenderer.drawString(nodeDescription, pixelX, pixelY + offset, 0xFFFFFFFF);

            fontRenderer.drawString(deleteHint, pixelX, pixelY + aspectRows * 16 + offset + 12, 0xFFFFFFFF);
        }

        // Draw aspects
        int aspectX = 0;
        int aspectY = 0;


        for(Aspect aspect : thaumcraftNodeLocation.getAspects().getAspectsSortedAmount()) {
            GL11.glPushMatrix();
            UtilsFX.drawTag(pixelX + aspectX * 16, pixelY + aspectY * 16 + offset + 10, aspect, thaumcraftNodeLocation.getAspects().getAmount(aspect), 0, 0.01, 1, 1, false);
            GL11.glPopMatrix();
            ++aspectX;
            if(aspectX >= 5) {
                aspectX = 0;
                ++aspectY;
            }
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }
}
