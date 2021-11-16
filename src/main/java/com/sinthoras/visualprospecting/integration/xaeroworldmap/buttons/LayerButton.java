package com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons;

import com.sinthoras.visualprospecting.integration.model.SupportedMods;
import com.sinthoras.visualprospecting.integration.model.buttons.ButtonManager;
import net.minecraft.util.ResourceLocation;

public class LayerButton extends com.sinthoras.visualprospecting.integration.model.buttons.LayerButton {

    private final ButtonManager manager;
    private SizedGuiTexturedButton button;

    public final ResourceLocation textureLocation;

    public LayerButton(ButtonManager manager) {
        super(manager, SupportedMods.XaeroWorldMap);
        this.manager = manager;
        textureLocation = new ResourceLocation("xaeroworldmap", "textures/" + getIconName() + ".png");
    }

    @Override
    public void updateState(boolean active) {
        if (button != null) {
            button.setActive(active);
        }
    }

    public void setButton(SizedGuiTexturedButton button) {
        this.button = button;
        updateState(manager.isActive());
    }

    public String getButtonTextKey() {
        return manager.getButtonTextKey();
    }

    public String getIconName() {
        return manager.getIconName();
    }

    public void toggle() {
        manager.toggle();
    }
}
