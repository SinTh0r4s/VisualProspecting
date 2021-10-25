package com.sinthoras.visualprospecting.gui.journeymap.drawsteps;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.NodeList;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.gui.journeymap.DrawUtils;
import com.sinthoras.visualprospecting.gui.journeymap.MapState;
import com.sinthoras.visualprospecting.gui.journeymap.layers.ThaumcraftNodeLayer;
import journeymap.client.model.Waypoint;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.tile.ItemNodeRenderer;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileNode;

import java.awt.geom.Point2D;
import java.util.List;

public class ThaumcraftNodeDrawStep implements ClickableDrawStep {

    private static final ResourceLocation markedTextureLocation = new ResourceLocation(Tags.MODID, "textures/node_marked.png");
    private static final ResourceLocation unmarkedTextureLocation = new ResourceLocation(Tags.MODID, "textures/node_unmarked.png");

    private final NodeList node;
    private final TileNode nodeTile;
    private double centerPixelX = 0;
    private double centerPixelY = 0;
    private double clickableRadiusPixelSquared = 0;
    private boolean mouseOver = false;

    public ThaumcraftNodeDrawStep(NodeList node) {
        this.node = node;

        nodeTile = new TileNode();
        final AspectList aspectList = new AspectList();
        for(String aspectTag : node.aspect.keySet()) {
            aspectList.add(Aspect.getAspect(aspectTag), node.aspect.get(aspectTag));
        }
        nodeTile.setAspects(aspectList);
        switch(node.type) {
            case "NORMAL":
                nodeTile.setNodeType(NodeType.NORMAL);
                break;
            case "UNSTABLE":
                nodeTile.setNodeType(NodeType.UNSTABLE);
                break;
            case "DARK":
                nodeTile.setNodeType(NodeType.DARK);
                break;
            case "TAINTED":
                nodeTile.setNodeType(NodeType.TAINTED);
                break;
            case "PURE":
                nodeTile.setNodeType(NodeType.PURE);
                break;
            case "HUNGRY":
                nodeTile.setNodeType(NodeType.HUNGRY);
                break;
        }
        nodeTile.blockType = ConfigBlocks.blockAiry;
        nodeTile.blockMetadata = 0;
    }

    @Override
    public void draw(double draggedPixelX, double draggedPixelY, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        final double borderSize = 48 * fontScale;
        final double borderSizeHalf = borderSize / 2;
        final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(node.x, node.z);
        final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + draggedPixelX, blockAsPixel.getY() + draggedPixelY);
        centerPixelX = pixel.getX();
        centerPixelY = pixel.getY();
        clickableRadiusPixelSquared = borderSizeHalf * borderSizeHalf;

        final int alpha = 204;
        DrawUtils.drawQuad(isWaypoint(ThaumcraftNodeLayer.instance.getActiveWaypoint()) ? markedTextureLocation : unmarkedTextureLocation, pixel.getX() - borderSizeHalf, pixel.getY() - borderSizeHalf, borderSize, borderSize, 0xFFFFFF, alpha);

        GL11.glPushMatrix();
        GL11.glTranslated(pixel.getX(), pixel.getY(), 0);
        final double scale = 64 * fontScale;
        GL11.glScaled(scale, scale, scale);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        ItemNodeRenderer.renderItemNode(nodeTile);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        ItemNodeRenderer.renderItemNode(nodeTile);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        ItemNodeRenderer.renderItemNode(nodeTile);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    public List<String> getTooltip() {
        return null;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        final double deltaX = mouseX - centerPixelX;
        final double deltaY = mouseY - centerPixelY;
        mouseOver = deltaX * deltaX + deltaY * deltaY <= clickableRadiusPixelSquared;
        return mouseOver;
    }

    public void onActionKeyPressed() {
        TCNodeTracker.nodelist.removeIf(entry -> entry.x == node.x && entry.y == node.y && entry.z == node.z);
    }

    public boolean isWaypoint(Waypoint waypoint) {
        return waypoint != null
                && waypoint.getDimensions().contains(node.dim)
                && waypoint.getX() == node.x
                && waypoint.getY() == node.y
                && waypoint.getZ() == node.z;
    }

    public Waypoint toWaypoint() {
        return new Waypoint(I18n.format("visualprospecting.tracked", I18n.format("tile.blockAiry.0.name")),
                node.x,
                node.y,
                node.z,
                nodeTile.targetColor,
                Waypoint.Type.Normal,
                node.dim);
    }

    public void drawTooltip(FontRenderer fontRenderer, int mouseX, int mouseY, int displayWidth, int displayHeight) {
        final boolean isWaypoint = isWaypoint(ThaumcraftNodeLayer.instance.getActiveWaypoint());
        final String asWaypoint = EnumChatFormatting.GOLD + I18n.format("visualprospecting.iswaypoint");
        final String title = EnumChatFormatting.BOLD + I18n.format("tile.blockAiry.0.name");
        final String nodeDescription = node.mod.equals("BLANK") ? EnumChatFormatting.GRAY + I18n.format("nodetype." + node.type + ".name")
                : EnumChatFormatting.GRAY + I18n.format("nodetype." + node.type + ".name") + ", " + I18n.format("nodemod." + node.mod + ".name");
        final String deleteHint = EnumChatFormatting.DARK_GRAY + I18n.format("visualprospecting.node.deletehint", Keyboard.getKeyName(VP.keyAction.getKeyCode()));

        int maxTextWidth = Math.max(Math.max(fontRenderer.getStringWidth(title), fontRenderer.getStringWidth(nodeDescription)), fontRenderer.getStringWidth(deleteHint));
        if(isWaypoint) {
            maxTextWidth = Math.max(maxTextWidth, fontRenderer.getStringWidth(asWaypoint));
        }
        if(fontRenderer.getBidiFlag()) {
            maxTextWidth = (int) Math.ceil(maxTextWidth * 1.25f);
        }

        final int aspectRows = (nodeTile.getAspects().size() + 4) / 5;  // Equivalent to Math.ceil(size / 5)
        final int aspectColumns = Math.min(nodeTile.getAspects().size(), 5);

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
                final int asWaypointWidth = (int) Math.ceil(fontRenderer.getStringWidth(asWaypoint) * 1.1f);
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
                fontRenderer.drawString(asWaypoint, pixelX, pixelY, 0xFFFFFFFF);
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


        for(Aspect aspect : nodeTile.getAspects().getAspectsSortedAmount()) {
            GL11.glPushMatrix();
            UtilsFX.drawTag(pixelX + aspectX * 16, pixelY + aspectY * 16 + offset + 10, aspect, nodeTile.getAspects().getAmount(aspect), 0, 0.01, 1, 1, false);
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
