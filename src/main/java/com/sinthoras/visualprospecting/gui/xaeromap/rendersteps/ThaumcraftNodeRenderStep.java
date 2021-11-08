package com.sinthoras.visualprospecting.gui.xaeromap.rendersteps;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.NodeList;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.gui.DrawUtils;
import com.sinthoras.visualprospecting.gui.xaeromap.FakeWaypointManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;

public class ThaumcraftNodeRenderStep implements InteractableRenderStep {

	private static final ResourceLocation markedTextureLocation = new ResourceLocation(Tags.MODID, "textures/node_marked.png");
    private static final ResourceLocation unmarkedTextureLocation = new ResourceLocation(Tags.MODID, "textures/node_unmarked.png");

    private final NodeList node;
    private final AspectList aspectList;
    private double centerPixelX = 0;
    private double centerPixelY = 0;
    private double clickableRadiusPixelSquared = 0;

	public ThaumcraftNodeRenderStep(NodeList node) {
		this.node = node;
		aspectList = new AspectList();
        for(String aspectTag : node.aspect.keySet()) {
            aspectList.add(Aspect.getAspect(aspectTag), node.aspect.get(aspectTag));
        }
	}

	@Override
	public void draw(GuiScreen gui, double cameraX, double cameraZ, double scale) {
		final double borderSize = 44;
        final double borderSizeHalf = borderSize / 2;
        final double scaleForGui = Math.max(0.5D, scale);
        centerPixelX = (node.x - cameraX) * scaleForGui;
        centerPixelY = (node.z - cameraZ) * scaleForGui;
        clickableRadiusPixelSquared = borderSizeHalf * borderSizeHalf;

        GL11.glPushMatrix();
		GL11.glTranslated(node.x - cameraX, node.z - cameraZ, 0);
		GL11.glScaled(1 / scaleForGui, 1 / scaleForGui, 1);

        final int alpha = 204;
        DrawUtils.drawQuad(isWaypoint() ? markedTextureLocation : unmarkedTextureLocation, -borderSizeHalf, -borderSizeHalf, borderSize, borderSize, 0xFFFFFF, alpha);

        final int aspectPixelDiameter = 32;
        DrawUtils.drawAspect(0, 0, aspectPixelDiameter, aspectList.getAspectsSortedAmount()[0], 0);

        GL11.glPopMatrix();
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
		final double scaleForGui = Math.max(0.5D, scale);
		final double deltaX = (mouseX - cameraX) * scaleForGui - centerPixelX;
        final double deltaY = (mouseY - cameraZ) * scaleForGui - centerPixelY;
		return deltaX * deltaX + deltaY * deltaY <= clickableRadiusPixelSquared;
	}

	@Override
	public void drawTooltip(GuiScreen gui, double mouseX, double mouseY, double cameraX, double cameraZ, double scale, int scaleAdj) {

		GL11.glPushMatrix();

		mouseX = ((mouseX - cameraX) * scale + gui.mc.displayWidth / 2.0) / scaleAdj;
		mouseY = ((mouseY - cameraZ) * scale + gui.mc.displayHeight / 2.0) / scaleAdj;

		final boolean isWaypoint = isWaypoint();
        final String asWaypoint = EnumChatFormatting.GOLD + I18n.format("visualprospecting.iswaypoint");
        final String title = EnumChatFormatting.BOLD + I18n.format("tile.blockAiry.0.name");
        final String nodeDescription = node.mod.equals("BLANK") ? EnumChatFormatting.GRAY + I18n.format("nodetype." + node.type + ".name")
                : EnumChatFormatting.GRAY + I18n.format("nodetype." + node.type + ".name") + ", " + I18n.format("nodemod." + node.mod + ".name");
        final String deleteHint = EnumChatFormatting.DARK_GRAY + I18n.format("visualprospecting.node.deletehint", Keyboard.getKeyName(VP.keyAction.getKeyCode()));

        int maxTextWidth = Math.max(Math.max(gui.mc.fontRenderer.getStringWidth(title), gui.mc.fontRenderer.getStringWidth(nodeDescription)), gui.mc.fontRenderer.getStringWidth(deleteHint));
        if(isWaypoint) {
            maxTextWidth = Math.max(maxTextWidth, gui.mc.fontRenderer.getStringWidth(asWaypoint));
        }
        if(gui.mc.fontRenderer.getBidiFlag()) {
            maxTextWidth = (int) Math.ceil(maxTextWidth * 1.25f);
        }

        final int aspectRows = (aspectList.size() + 4) / 5;  // Equivalent to Math.ceil(size / 5)
        final int aspectColumns = Math.min(aspectList.size(), 5);

        int pixelX = (int) (mouseX + 12);
        int pixelY = (int) (mouseY - 12);
        final int tooltipHeight = (isWaypoint ? 44 : 32) + aspectRows * 16;
        final int tooltipWidth = Math.max(aspectColumns * 16, maxTextWidth);
        if(pixelX + tooltipWidth > gui.mc.displayWidth) {
            pixelX -= 28 + tooltipWidth;
        }
        if(pixelY + tooltipHeight + 6 > gui.mc.displayHeight) {
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
        if(gui.mc.fontRenderer.getBidiFlag()) {
            if(isWaypoint) {
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
        }
        else {
            if(isWaypoint) {
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


        for(Aspect aspect : aspectList.getAspectsSortedAmount()) {
            GL11.glPushMatrix();
            UtilsFX.drawTag(pixelX + aspectX * 16, pixelY + aspectY * 16 + offset + 10, aspect, aspectList.getAmount(aspect), 0, 0.01, 1, 1, false);
            GL11.glPopMatrix();
            ++aspectX;
            if(aspectX >= 5) {
                aspectX = 0;
                ++aspectY;
            }
        }

        GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glPopMatrix();
	}

	@Override
	public void onDoubleClick() {
		if(Utils.isXaerosMinimapInstalled()) {
			FakeWaypointManager.toggleWaypoint(FakeWaypointManager.TC_NODES_WAYPOINT, node.x, node.y, node.z,
					I18n.format("visualprospecting.tracked", I18n.format("tile.blockAiry.0.name")), "@", 15, node.dim);
			if (FakeWaypointManager.hasWaypoint(FakeWaypointManager.TC_NODES_WAYPOINT)) {
				TCNodeTracker.xMarker = node.x;
				TCNodeTracker.yMarker = node.y;
				TCNodeTracker.zMarker = node.z;
			}
			else {
				TCNodeTracker.yMarker = -1;
			}
		}
	}

	@Override
	public void onActionButton() {
		TCNodeTracker.nodelist.removeIf(entry -> entry.x == node.x && entry.y == node.y && entry.z == node.z);
	}

	private boolean isWaypoint() {
		return Utils.isXaerosMinimapInstalled() && FakeWaypointManager.isWaypointAtCoords(FakeWaypointManager.TC_NODES_WAYPOINT, node.x, node.y, node.z);
	}
}
