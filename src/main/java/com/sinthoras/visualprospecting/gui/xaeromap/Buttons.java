package com.sinthoras.visualprospecting.gui.xaeromap;

import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.VP;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class Buttons {
	public static GuiButton oreVeinButton;
	public static boolean oreVeinsEnabled;
	public static GuiButton undergroundFluidButton;
	public static boolean undergroundFluidsEnabled;
	public static GuiButton thaumcraftNodeButton;
	public static boolean thaumcraftNodesEnabled;
	// yes it needs to be exactly 256x256 to render properly. dont ask why
	public static ResourceLocation xTextures = new ResourceLocation(Tags.MODID, "textures/xtextures.png");

	public static void onOreVeinButton(GuiButton button) {
		VP.info("ore vein button");
		oreVeinsEnabled = !oreVeinsEnabled;
		if (oreVeinsEnabled) {
			undergroundFluidsEnabled = false;
			thaumcraftNodesEnabled = false;
		}
	}

	public static void onUndergroundFluidButton(GuiButton button) {
		VP.info("underground fluid button");
		undergroundFluidsEnabled = !undergroundFluidsEnabled;
		if (undergroundFluidsEnabled) {
			oreVeinsEnabled = false;
			thaumcraftNodesEnabled = false;
		}
	}

	public static void onThaumcraftNodeButton(GuiButton button) {
		VP.info("tc node button");
		thaumcraftNodesEnabled = !thaumcraftNodesEnabled;
		if (thaumcraftNodesEnabled) {
			oreVeinsEnabled = false;
			undergroundFluidsEnabled = false;
		}
	}

}
