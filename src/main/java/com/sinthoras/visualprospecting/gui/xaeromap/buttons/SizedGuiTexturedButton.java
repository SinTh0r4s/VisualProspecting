package com.sinthoras.visualprospecting.gui.xaeromap.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiTexturedButton;

import java.util.function.Consumer;

public class SizedGuiTexturedButton extends GuiTexturedButton {
	public SizedGuiTexturedButton(int x, int y, int w, int h, int textureX, int textureY, int textureW, int textureH, ResourceLocation texture, Consumer<GuiButton> action, CursorBox tooltip) {
		super(x, y, w, h, textureX, textureY, textureW, textureH, texture, action, tooltip);
	}

	// don't like how much code i'm copying here but its the only sane way to allow non-256x256 textures i can think of
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
      int iconX = this.xPosition + this.width / 2 - this.textureW / 2;
      int iconY = this.yPosition + this.height / 2 - this.textureH / 2;
      if (this.enabled) {
         if (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {
            --iconY;
            GL11.glColor4f(0.9F, 0.9F, 0.9F, 1.0F);
         } else {
            GL11.glColor4f(0.9882F, 0.9882F, 0.9882F, 1.0F);
         }
      } else {
         GL11.glColor4f(0.25F, 0.25F, 0.25F, 1.0F);
      }

      Gui.func_146110_a(iconX, iconY, this.textureX, this.textureY, this.textureW, this.textureH, this.textureW, this.textureH);
   }
}
