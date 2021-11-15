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

    private static final int BUTTON_WIDTH = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    protected boolean active;

    public SizedGuiTexturedButton(int x, int y, ResourceLocation texture, Consumer<GuiButton> action, CursorBox tooltip) {
        super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, 0, 0, ICON_WIDTH, ICON_HEIGHT, texture, action, tooltip);
        active = false;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(texture);
        int iconX = xPosition + width / 2 - textureW / 2;
        int iconY = yPosition + height / 2 - textureH / 2;
        if (enabled) {
            if (active) {
                if (isMouseOver(mouseX, mouseY)) {
                    iconY--;
                    GL11.glColor4f(0.5F, 0.9F, 0.5F, 1.0F);
                }
                else {
                    GL11.glColor4f(0.5882F, 0.9882F, 0.5882F, 1.0F);
                }
            }
            else {
                if (isMouseOver(mouseX, mouseY)) {
                    iconY--;
                    GL11.glColor4f(0.9F, 0.9F, 0.9F, 1.0F);
                } else {
                    GL11.glColor4f(0.9882F, 0.9882F, 0.9882F, 1.0F);
                }
            }
        } else {
            GL11.glColor4f(0.25F, 0.25F, 0.25F, 1.0F);
        }

        Gui.func_146110_a(iconX, iconY, textureX, textureY, textureW, textureH, textureW, textureH);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
    }

    public void toggle() {
        active = !active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
