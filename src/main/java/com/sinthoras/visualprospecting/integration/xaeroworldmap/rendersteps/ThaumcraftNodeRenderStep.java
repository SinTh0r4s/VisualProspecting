package com.sinthoras.visualprospecting.integration.xaeroworldmap.rendersteps;

import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.integration.DrawUtils;
import com.sinthoras.visualprospecting.integration.model.layers.ThaumcraftNodeLayerManager;
import com.sinthoras.visualprospecting.integration.model.locations.IWaypointAndLocationProvider;
import com.sinthoras.visualprospecting.integration.model.locations.ThaumcraftNodeLocation;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

import javax.annotation.Nullable;

public class ThaumcraftNodeRenderStep implements InteractableRenderStep {

    private static final ResourceLocation markedTextureLocation = new ResourceLocation(Tags.MODID, "textures/node_marked.png");
    private static final ResourceLocation unmarkedTextureLocation = new ResourceLocation(Tags.MODID, "textures/node_unmarked.png");

    private final ThaumcraftNodeLocation thaumcraftNodeLocation;

    private double centerPixelX = 0;
    private double centerPixelY = 0;
    private double clickableRadiusPixelSquared = 0;

    public ThaumcraftNodeRenderStep(ThaumcraftNodeLocation thaumcraftNodeLocation) {
        this.thaumcraftNodeLocation = thaumcraftNodeLocation;
    }

    @Override
    public void draw(@Nullable GuiScreen gui, double cameraX, double cameraZ, double scale) {
        final double borderSize = 44;
        final double borderSizeHalf = borderSize / 2;
        final double scaleForGui = Math.max(0.5, scale);
        centerPixelX = (thaumcraftNodeLocation.getBlockX() - cameraX) * scaleForGui;
        centerPixelY = (thaumcraftNodeLocation.getBlockZ() - cameraZ) * scaleForGui;
        clickableRadiusPixelSquared = borderSizeHalf * borderSizeHalf;

        GL11.glPushMatrix();
        GL11.glTranslated(thaumcraftNodeLocation.getBlockX() - cameraX, thaumcraftNodeLocation.getBlockZ() - cameraZ, 0);
        GL11.glScaled(1 / scaleForGui, 1 / scaleForGui, 1);

        final int alpha = 204;
        DrawUtils.drawQuad(thaumcraftNodeLocation.isActiveAsWaypoint() ? markedTextureLocation : unmarkedTextureLocation, -borderSizeHalf, -borderSizeHalf, borderSize, borderSize, 0xFFFFFF, alpha);

        final int aspectPixelDiameter = 32;
        DrawUtils.drawAspect(0, 0, aspectPixelDiameter, thaumcraftNodeLocation.getStrongestAspect(), 0);

        GL11.glPopMatrix();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY, double scale) {
        final double scaleForGui = Math.max(0.5, scale);
        final double deltaX = mouseX * scaleForGui - centerPixelX;
        final double deltaY = mouseY * scaleForGui - centerPixelY;
        return deltaX * deltaX + deltaY * deltaY <= clickableRadiusPixelSquared;
    }

    @Override
    public void drawTooltip(GuiScreen gui, double mouseX, double mouseY, double scale, int scaleAdj) {

        GL11.glPushMatrix();

        mouseX = (mouseX * scale + (gui.mc.displayWidth >> 1)) / scaleAdj;
        mouseY = (mouseY * scale + (gui.mc.displayHeight >> 1)) / scaleAdj;

        final boolean isWaypoint = thaumcraftNodeLocation.isActiveAsWaypoint();
        final String asWaypoint = thaumcraftNodeLocation.getActiveWaypointHint();
        final String title = thaumcraftNodeLocation.getTitle();
        final String nodeDescription = thaumcraftNodeLocation.getDescription();
        final String deleteHint = thaumcraftNodeLocation.getDeleteHint();

        int maxTextWidth = Math.max(Math.max(gui.mc.fontRenderer.getStringWidth(title), gui.mc.fontRenderer.getStringWidth(nodeDescription)), gui.mc.fontRenderer.getStringWidth(deleteHint));
        if (isWaypoint) {
            maxTextWidth = Math.max(maxTextWidth, gui.mc.fontRenderer.getStringWidth(asWaypoint));
        }
        if (gui.mc.fontRenderer.getBidiFlag()) {
            maxTextWidth = (int) Math.ceil(maxTextWidth * 1.25f);
        }

        final int aspectRows = (thaumcraftNodeLocation.getAspects().size() + 4) / 5;  // Equivalent to Math.ceil(size / 5)
        final int aspectColumns = Math.min(thaumcraftNodeLocation.getAspects().size(), 5);

        int pixelX = (int) (mouseX + 12);
        int pixelY = (int) (mouseY - 12);
        final int tooltipHeight = (isWaypoint ? 44 : 32) + aspectRows * 16;
        final int tooltipWidth = Math.max(aspectColumns * 16, maxTextWidth);
        if (pixelX + tooltipWidth > gui.mc.displayWidth) {
            pixelX -= 28 + tooltipWidth;
        }
        if (pixelY + tooltipHeight + 6 > gui.mc.displayHeight) {
            pixelY = gui.mc.displayHeight - tooltipHeight - 6;
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // Draw background
        final int backgroundColor = 0xF0100010;
        DrawUtils.drawGradientRect(pixelX - 3, pixelY - 4, pixelX + tooltipWidth + 3, pixelY - 3, backgroundColor, backgroundColor);
        DrawUtils.drawGradientRect(pixelX - 3, pixelY + tooltipHeight + 3, pixelX + tooltipWidth + 3, pixelY + tooltipHeight + 4, backgroundColor, backgroundColor);
        DrawUtils.drawGradientRect(pixelX - 3, pixelY - 3, pixelX + tooltipWidth + 3, pixelY + tooltipHeight + 3, backgroundColor, backgroundColor);
        DrawUtils.drawGradientRect(pixelX - 4, pixelY - 3, pixelX - 3, pixelY + tooltipHeight + 3, backgroundColor, backgroundColor);
        DrawUtils.drawGradientRect(pixelX + tooltipWidth + 3, pixelY - 3, pixelX + tooltipWidth + 4, pixelY + tooltipHeight + 3, backgroundColor, backgroundColor);

        // Draw text
        int offset = 0;
        if (gui.mc.fontRenderer.getBidiFlag()) {
            if (isWaypoint) {
                final int asWaypointWidth = (int) Math.ceil(gui.mc.fontRenderer.getStringWidth(asWaypoint) * 1.1f);
                gui.mc.fontRenderer.drawString(title, pixelX + tooltipWidth - asWaypointWidth, pixelY, 0xFFFFFFFF);
                offset += 12;
            }
            final int titleWidth = (int) Math.ceil(gui.mc.fontRenderer.getStringWidth(title) * 1.1f);
            gui.mc.fontRenderer.drawString(title, pixelX + tooltipWidth - titleWidth, pixelY + offset, 0xFFFFFFFF);
            offset += 12;
            final int nodeDescriptonWidth = (int) Math.ceil(gui.mc.fontRenderer.getStringWidth(nodeDescription) * 1.1f);
            gui.mc.fontRenderer.drawString(nodeDescription, pixelX + nodeDescriptonWidth - titleWidth, pixelY + offset, 0xFFFFFFFF);

            final int deleteHintWidth = (int) Math.ceil(gui.mc.fontRenderer.getStringWidth(deleteHint) * 1.1f);
            gui.mc.fontRenderer.drawString(deleteHint, pixelX + tooltipWidth - deleteHintWidth, pixelY + aspectRows * 16 + offset + 12, 0xFFFFFFFF);
        } else {
            if (isWaypoint) {
                gui.mc.fontRenderer.drawString(asWaypoint, pixelX, pixelY, 0xFFFFFFFF);
                offset += 12;
            }
            gui.mc.fontRenderer.drawString(title, pixelX, pixelY + offset, 0xFFFFFFFF);
            offset += 12;
            gui.mc.fontRenderer.drawString(nodeDescription, pixelX, pixelY + offset, 0xFFFFFFFF);

            gui.mc.fontRenderer.drawString(deleteHint, pixelX, pixelY + aspectRows * 16 + offset + 12, 0xFFFFFFFF);
        }

        // Draw aspects
        int aspectX = 0;
        int aspectY = 0;


        for (Aspect aspect : thaumcraftNodeLocation.getAspects().getAspectsSortedAmount()) {
            GL11.glPushMatrix();
            UtilsFX.drawTag(pixelX + aspectX * 16, pixelY + aspectY * 16 + offset + 10, aspect, thaumcraftNodeLocation.getAspects().getAmount(aspect), 0, 0.01, 1, 1, false);
            GL11.glPopMatrix();
            ++aspectX;
            if (aspectX >= 5) {
                aspectX = 0;
                ++aspectY;
            }
        }

        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }

    @Override
    public void onActionButton() {
        ThaumcraftNodeLayerManager.instance.deleteNode(thaumcraftNodeLocation);
    }

    @Override
    public IWaypointAndLocationProvider getLocationProvider() {
        return thaumcraftNodeLocation;
    }
}
