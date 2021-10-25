package com.sinthoras.visualprospecting.gui.journeymap.buttons;

import com.sinthoras.visualprospecting.gui.journeymap.MapState;
import journeymap.client.ui.theme.ThemeButton;

public class LayerButton {

    private final String buttonTextKey;
    private final String iconName;

    private boolean layerActive = false;
    private ThemeButton guiButton = null;

    public LayerButton(String buttonTextKey, String iconName) {
        this.buttonTextKey = buttonTextKey;
        this.iconName = iconName;
    }

    public ThemeButton getGuiButton() {
        return guiButton;
    }

    public void setGuiButton(ThemeButton guiButton) {
        this.guiButton = guiButton;
    }

    public boolean isLayerActive() {
        return layerActive;
    }

    protected void disableLayer() {
        layerActive = false;
        guiButton.setToggled(false, false);
    }

    public void setLayerActive(boolean layerActive) {
        for(LayerButton button : MapState.instance.buttons) {
            button.disableLayer();
        }
        this.layerActive = layerActive;
    }

    public String getButtonTextKey() {
        return buttonTextKey;
    }

    public String getIconName() {
        return iconName;
    }
}
