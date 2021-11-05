package com.sinthoras.visualprospecting.gui.xaeromap;

import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.VP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class Buttons {
	public static GuiButton oreVeinButton;
	public static GuiButton undergroundFluidButton;
	public static GuiButton thaumcraftNodeButton;
	// yes it needs to be exactly 256x256 to render properly. dont ask why
	public static ResourceLocation xTextures = new ResourceLocation(Tags.MODID, "textures/xtextures.png");

	public static void onOreVeinButton(GuiButton button) {
		VP.info("ore vein button");
	}

	public static void onUndergroundFluidButton(GuiButton button) {
		VP.info("underground fluid button");
	}

	public static void onThaumcraftNodeButton(GuiButton button) {
		VP.info("tc node button");
	}

}
