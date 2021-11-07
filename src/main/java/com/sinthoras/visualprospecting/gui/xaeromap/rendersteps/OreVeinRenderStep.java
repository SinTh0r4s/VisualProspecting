package com.sinthoras.visualprospecting.gui.xaeromap.rendersteps;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.gui.DrawUtils;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OreVeinRenderStep implements InteractableRenderStep {

	private final OreVeinPosition oreVeinPosition;
	private final ResourceLocation depletedTextureLocation = new ResourceLocation(Tags.MODID, "textures/depleted.png");
	private final IIcon blockStoneIcon = Blocks.stone.getIcon(0, 0);
	private final int iconSize = 32;
	private double iconX;
	private double iconY;

	public OreVeinRenderStep(OreVeinPosition veinPosition) {
		oreVeinPosition = veinPosition;
	}

	@Override
	public void draw(GuiScreen gui, double cameraX, double cameraZ, double scale) {
		final int iconSizeHalf = iconSize / 2;
		final double scaleForGui = Math.max(1.0D, scale);
		this.iconX = (oreVeinPosition.getBlockX() - cameraX) * scaleForGui - iconSizeHalf;
		this.iconY = (oreVeinPosition.getBlockZ() - cameraZ) * scaleForGui - iconSizeHalf;

		GL11.glPushMatrix();
		GL11.glTranslated(oreVeinPosition.getBlockX() - cameraX, oreVeinPosition.getBlockZ() - cameraZ, 0);
		GL11.glScaled(1 / scaleForGui, 1 / scaleForGui, 1);
		DrawUtils.drawQuad(blockStoneIcon, -iconSizeHalf, -iconSizeHalf, iconSize, iconSize, 0xFFFFFF, 255);

		DrawUtils.drawQuad(getIconFromPrimaryOre(), -iconSizeHalf, -iconSizeHalf, iconSize, iconSize, getColor(), 255);

		if(!oreVeinPosition.veinType.isHighlighted() || oreVeinPosition.isDepleted()) {
			DrawUtils.drawGradientRect(-iconSizeHalf, -iconSizeHalf, iconSizeHalf, iconSizeHalf, 0, 0x96000000, 0x96000000);
			if(oreVeinPosition.isDepleted()) {
				DrawUtils.drawQuad(depletedTextureLocation, -iconSizeHalf, -iconSizeHalf, iconSize, iconSize, 0xFFFFFF, 255);
			}
		}

		if(scale >= Utils.journeyMapScaleToLinear(Config.minZoomLevelForOreLabel) && !oreVeinPosition.isDepleted()) {
			final int fontColor = oreVeinPosition.veinType.isHighlighted() ? 0xFFFFFF : 0x7F7F7F;
			String text = I18n.format(oreVeinPosition.veinType.name);
			DrawUtils.drawSimpleLabel(gui, text, 0, -iconSizeHalf - gui.mc.fontRenderer.FONT_HEIGHT - 5, fontColor, 0xB4000000, true);
		}

		GL11.glPopMatrix();
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
		final double scaleForGui = Math.max(1.0D, scale);
		mouseX = (mouseX - cameraX) * scaleForGui;
		mouseY = (mouseY - cameraZ) * scaleForGui;
		return mouseX >= iconX && mouseY >= iconY && mouseX <= iconX + iconSize && mouseY <= iconY + iconSize;
	}

	@Override
	public void drawTooltip(GuiScreen gui, double mouseX, double mouseY, double cameraX, double cameraZ, double scale, int scaleAdj) {
		//too much magic
		mouseX = (mouseX - cameraX) * scale + gui.mc.displayWidth / 2.0;
		mouseY = (mouseY - cameraZ) * scale + gui.mc.displayHeight / 2.0;

		final List<String> tooltip = new ArrayList<>();
		if(oreVeinPosition.isDepleted()) {
			tooltip.add(EnumChatFormatting.RED + I18n.format("visualprospecting.depleted"));
		}
		/*if(isWaypoint(OreVeinLayer.instance.getActiveWaypoint())) {
			tooltip.add(EnumChatFormatting.GOLD + I18n.format("visualprospecting.iswaypoint"));
		}*/
		tooltip.add(EnumChatFormatting.WHITE + I18n.format(oreVeinPosition.veinType.name));
		if(!oreVeinPosition.isDepleted()) {
			tooltip.addAll(oreVeinPosition.veinType.getOreMaterialNames().stream().map(materialName -> EnumChatFormatting.GRAY + materialName).collect(Collectors.toList()));
		}
		tooltip.add(EnumChatFormatting.DARK_GRAY + I18n.format("visualprospecting.node.deletehint", Keyboard.getKeyName(VP.keyAction.getKeyCode())));

		GL11.glPushMatrix();

		DrawUtils.drawSimpleTooltip(gui, tooltip, mouseX / scaleAdj + 6, mouseY / scaleAdj - 12, 0xFFFFFFFF, 0x86000000);

		GL11.glPopMatrix();
	}

	@Override
	public void onDoubleClick() {
		VP.info("got double click!");
	}

	@Override
	public void onActionButton() {
		ClientCache.instance.toggleOreVein(oreVeinPosition.dimensionId, oreVeinPosition.chunkX, oreVeinPosition.chunkZ);
	}

	private IIcon getIconFromPrimaryOre() {
		Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
		return aMaterial.mIconSet.mTextures[OrePrefixes.ore.mTextureIndex].getIcon();
	}

	private int getColor() {
		Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
		return (aMaterial.mRGBa[0] << 16) | (aMaterial.mRGBa[1]) << 8 | aMaterial.mRGBa[2];
	}
}
