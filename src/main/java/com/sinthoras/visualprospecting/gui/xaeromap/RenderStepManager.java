package com.sinthoras.visualprospecting.gui.xaeromap;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.NodeList;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.*;
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

		int minBlockX = (int) (cameraX - (double)(gui.mc.displayWidth / 2) / scale);
		int minBlockZ = (int) (cameraZ - (double)(gui.mc.displayHeight / 2) / scale);
		int maxBlockX = (int) (cameraX + (double)(gui.mc.displayWidth / 2) / scale);
		int maxBlockZ = (int) (cameraZ + (double)(gui.mc.displayHeight / 2) / scale);
		final int playerDimensionId = Minecraft.getMinecraft().thePlayer.dimension;

		if (Buttons.oreVeinsEnabled) {
			final int minOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(minBlockX));
			final int minOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(minBlockZ));
			final int maxOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(maxBlockX));
			final int maxOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(maxBlockZ));

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
			final int minUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(minBlockX));
			final int minUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(minBlockZ));
			final int maxUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(maxBlockX));
			final int maxUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(maxBlockZ));

			for (int chunkX = minUndergroundFluidX; chunkX <= maxUndergroundFluidX; chunkX += VP.undergroundFluidSizeChunkX) {
				for (int chunkZ = minUndergroundFluidZ; chunkZ <= maxUndergroundFluidZ; chunkZ += VP.undergroundFluidSizeChunkZ) {
					final UndergroundFluidPosition undergroundFluid = ClientCache.instance.getUndergroundFluid(playerDimensionId, chunkX, chunkZ);
					if (undergroundFluid.isProspected()) {
						final int minAmountInField = undergroundFluid.getMinProduction();
						final int maxAmountInField = undergroundFluid.getMaxProduction();
						for (int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
							for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
								renderSteps.add(new UndergroundFluidChunkRenderStep(chunkX + offsetChunkX, chunkZ + offsetChunkZ, undergroundFluid.fluid, undergroundFluid.chunks[offsetChunkX][offsetChunkZ], minAmountInField, maxAmountInField));
							}
						}
						renderSteps.add(new UndergroundFluidRenderStep(undergroundFluid));
					}
				}
			}
		}

		if (Buttons.thaumcraftNodesEnabled) {
			for (NodeList node : TCNodeTracker.nodelist) {
				if(node.dim == playerDimensionId
						&& node.x >= minBlockX && node.x <= maxBlockX
						&& node.z >= minBlockZ && node.z <= maxBlockZ) {
					renderSteps.add(new ThaumcraftNodeRenderStep(node));
				}
			}
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
