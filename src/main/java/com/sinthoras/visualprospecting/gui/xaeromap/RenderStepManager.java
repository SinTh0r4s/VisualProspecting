package com.sinthoras.visualprospecting.gui.xaeromap;

import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.InteractableRenderStep;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.OreVeinRenderStep;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.RenderStep;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderStepManager {

	public static List<RenderStep> renderSteps = new ArrayList<>();
	public static InteractableRenderStep hovered = null;
	public static double mouseXForRender;
	public static double mouseYForRender;

	public static void render(GuiScreen gui, double cameraX, double cameraZ, double scale) {
		renderSteps.clear();

		if (Buttons.oreVeinsEnabled) {
			final int minOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk((int) (cameraX - (double)(gui.mc.displayWidth / 2) / scale)));
			final int minOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk((int) (cameraZ - (double)(gui.mc.displayHeight / 2) / scale)));
			final int maxOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk((int) (cameraX + (double)(gui.mc.displayWidth / 2) / scale)));
			final int maxOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk((int) (cameraZ + (double)(gui.mc.displayHeight / 2) / scale)));
			final int playerDimensionId = Minecraft.getMinecraft().thePlayer.dimension;

			for (int chunkX = minOreChunkX; chunkX <= maxOreChunkX; chunkX = Utils.mapToCenterOreChunkCoord(chunkX + 3)) {
				for (int chunkZ = minOreChunkZ; chunkZ <= maxOreChunkZ; chunkZ = Utils.mapToCenterOreChunkCoord(chunkZ + 3)) {
					final OreVeinPosition oreVeinPosition = ClientCache.instance.getOreVein(playerDimensionId, chunkX, chunkZ);
					if (oreVeinPosition.veinType != VeinType.NO_VEIN) {
						renderSteps.add(new OreVeinRenderStep(oreVeinPosition));
					}
				}
			}
		}

		if (Buttons.undergroundFluidsEnabled) {
			//todo
		}

		if (Buttons.thaumcraftNodesEnabled) {
			//todo
		}

		for (RenderStep step : renderSteps) {
			step.draw(gui, cameraX, cameraZ, scale);
		}
	}

	public static void updateHovered(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
		mouseXForRender = mouseX;
		mouseYForRender = mouseY;
		for (RenderStep step : renderSteps) {
			if (step instanceof InteractableRenderStep && ((InteractableRenderStep) step).isMouseOver(mouseX, mouseY, cameraX, cameraZ, scale)) {
				hovered = (InteractableRenderStep) step;
				return;
			}
		}
		hovered = null;
	}

	public static void drawTooltip(GuiScreen gui, double cameraX, double cameraZ, double scale, int scaleAdj) {
		if (hovered != null) {
			hovered.drawTooltip(gui, mouseXForRender, mouseYForRender, cameraX, cameraZ, scale, scaleAdj);
		}
	}

	public static void doActionKeyPress() {
		if (hovered != null) {
			hovered.onActionButton();
		}
	}

	public static void doDoubleClick() {
		if (hovered != null) {
			hovered.onDoubleClick();
		}
	}
}
