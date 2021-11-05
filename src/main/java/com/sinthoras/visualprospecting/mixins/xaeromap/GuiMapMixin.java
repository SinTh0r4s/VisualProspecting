package com.sinthoras.visualprospecting.mixins.xaeromap;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.gui.DrawUtils;
import com.sinthoras.visualprospecting.gui.xaeromap.Buttons;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiTexturedButton;
import xaero.map.gui.ScreenBase;

import java.awt.geom.Point2D;
import java.util.ArrayList;

@Mixin(value = GuiMap.class, remap = false)
public abstract class GuiMapMixin extends ScreenBase {

	protected GuiMapMixin(GuiScreen parent, GuiScreen escape) {
		super(parent, escape);
	}

	@Shadow public abstract void drawArrowOnMap(double x, double z, float angle, double sc);

	@Shadow private double cameraX;

	@Shadow private double cameraZ;

	@Shadow private double scale;

	@Shadow protected abstract void setColourBuffer(float r, float g, float b, float a);

	@Shadow public abstract void addGuiButton(GuiButton b);

	@Inject(method = "drawScreen",
			at = @At(value = "INVOKE",
					target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
					ordinal = 1,
					shift = At.Shift.AFTER
			), slice = @Slice(
					from = @At(value = "INVOKE",
							target = "Lorg/lwjgl/opengl/GL14;glBlendFuncSeparate(IIII)V"
					),
					to = @At(value = "INVOKE",
							target = "Lxaero/map/mods/SupportXaeroMinimap;renderWaypoints(Lnet/minecraft/client/gui/GuiScreen;DDIIDDDDLjava/util/regex/Pattern;Ljava/util/regex/Pattern;FLxaero/map/mods/gui/Waypoint;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/ScaledResolution;)Lxaero/map/mods/gui/Waypoint;"
					)
			)
	)
	private void injectDraw(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
		final int minOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk((int) (cameraX - (double)(mc.displayWidth / 2) / scale)));
		final int minOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk((int) (cameraZ - (double)(mc.displayHeight / 2) / scale)));
		final int maxOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk((int) (cameraX + (double)(mc.displayWidth / 2) / scale)));
		final int maxOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk((int) (cameraZ + (double)(mc.displayHeight / 2) / scale)));
		final int playerDimensionId = Minecraft.getMinecraft().thePlayer.dimension;

		for (int chunkX = minOreChunkX; chunkX <= maxOreChunkX; chunkX = Utils.mapToCenterOreChunkCoord(chunkX + 3)) {
			for (int chunkZ = minOreChunkZ; chunkZ <= maxOreChunkZ; chunkZ = Utils.mapToCenterOreChunkCoord(chunkZ + 3)) {
				final OreVeinPosition oreVeinPosition = ClientCache.instance.getOreVein(playerDimensionId, chunkX, chunkZ);
				if (oreVeinPosition.veinType != VeinType.NO_VEIN) {
					final ResourceLocation depletedTextureLocation = new ResourceLocation(Tags.MODID, "textures/depleted.png");
					double iconSize = 32;
					final double iconSizeHalf = iconSize / 2;
					final double scaleForGui = Math.max(1, scale);
					final Point2D.Double blockAsPixel = new Point2D.Double(oreVeinPosition.getBlockX(), oreVeinPosition.getBlockZ());
					final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() - cameraX, blockAsPixel.getY() - cameraZ);

					double iconX = pixel.getX() * scaleForGui - iconSizeHalf;
					double iconY = pixel.getY() * scaleForGui - iconSizeHalf;

					final IIcon blockIcon = Blocks.stone.getIcon(0, 0);
					GL11.glPushMatrix();
					GL11.glScaled(1 / scaleForGui, 1 / scaleForGui, 1);
					DrawUtils.drawQuad(blockIcon, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255);

					DrawUtils.drawQuad(getIconFromPrimaryOre(oreVeinPosition), iconX, iconY, iconSize, iconSize, getColor(oreVeinPosition), 255);

					if(!oreVeinPosition.veinType.isHighlighted() || oreVeinPosition.isDepleted()) {
						//DrawUtil.drawRectangle(iconX, iconY, iconSize, iconSize, 0x000000, 150);
						DrawUtils.drawGradientRect(iconX, iconY, iconX + iconSize, iconY + iconSize, zLevel, 0x96000000, 0x96000000);
						if(oreVeinPosition.isDepleted()) {
							DrawUtils.drawQuad(depletedTextureLocation, iconX, iconY, iconSize, iconSize, 0xFFFFFF, 255);
						}
					}

					if(scale >= Utils.journeyMapScaleToLinear(Config.minZoomLevelForOreLabel) && !oreVeinPosition.isDepleted()) {
						final int fontColor = oreVeinPosition.veinType.isHighlighted() ? 0xFFFFFF : 0x7F7F7F;
						//DrawUtil.drawLabel(I18n.format(oreVeinPosition.veinType.name), pixel.getX(), pixel.getY() - iconSize, DrawUtil.HAlign.Center, DrawUtil.VAlign.Middle, 0, 180, fontColor, 255, fontScale, false, rotation);
						//GL11.glTranslated(pixel.getX() * scale - (((int)pixel.getX()) * scale), pixel.getY() * scale - (((int) pixel.getY()) * scale), 0);
						double textX = pixel.getX() * scaleForGui;
						double textY = pixel.getY() * scaleForGui - iconSizeHalf - mc.fontRenderer.FONT_HEIGHT - 5;
						int intTextX = (int)Math.floor(textX);
						int intTextY = (int)Math.floor(textY);
						double dTextX = textX - (double)intTextX;
						double dTextY = textY - (double)intTextY;
						GL11.glTranslated(dTextX, dTextY, 0.0D);
						drawCenteredString(mc.fontRenderer, I18n.format(oreVeinPosition.veinType.name), intTextX, intTextY, fontColor);
					}

					GL11.glPopMatrix();
				}
			}
		}

	}

	@Inject(method = "initGui",
			at = @At(value = "INVOKE",
					target = "Lorg/lwjgl/input/Keyboard;enableRepeatEvents(Z)V"
			)
	)
	private void injectInitButtons(CallbackInfo ci) {
		Buttons.oreVeinButton = new GuiTexturedButton(0, height - 20, 20, 20, 0, 0, 16, 16,
				Buttons.xTextures, Buttons::onOreVeinButton, new CursorBox("visualprospecting.button.orevein"));
		addGuiButton(Buttons.oreVeinButton);
		Buttons.undergroundFluidButton = new GuiTexturedButton(0, height - 40, 20, 20, 16, 0, 16, 16,
				Buttons.xTextures, Buttons::onUndergroundFluidButton, new CursorBox("visualprospecting.button.undergroundfluid"));
		addGuiButton(Buttons.undergroundFluidButton);
		if (Utils.isTCNodeTrackerInstalled()) {
			Buttons.thaumcraftNodeButton = new GuiTexturedButton(0, height - 60, 20, 20, 32, 0, 16, 16,
					Buttons.xTextures, Buttons::onThaumcraftNodeButton, new CursorBox("visualprospecting.button.nodes"));
			addGuiButton(Buttons.thaumcraftNodeButton);
		}
	}

	@Unique private IIcon getIconFromPrimaryOre(OreVeinPosition oreVeinPosition) {
		Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
		return aMaterial.mIconSet.mTextures[OrePrefixes.ore.mTextureIndex].getIcon();
	}

	@Unique private int getColor(OreVeinPosition oreVeinPosition) {
		Materials aMaterial = GregTech_API.sGeneratedMaterials[oreVeinPosition.veinType.primaryOreMeta];
		return (aMaterial.mRGBa[0] << 16) | (aMaterial.mRGBa[1]) << 8 | aMaterial.mRGBa[2];
	}
}
