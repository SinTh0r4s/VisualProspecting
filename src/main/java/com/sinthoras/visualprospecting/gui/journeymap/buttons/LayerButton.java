package com.sinthoras.visualprospecting.gui.journeymap.buttons;

public class LayerButton {

    private final String buttonTextKey;
    private final String iconName;

    private boolean layerActive = false;

    public LayerButton(String buttonTextKey, String iconName) {
        this.buttonTextKey = buttonTextKey;
        this.iconName = iconName;
    }

    public boolean isLayerActive() {
        return layerActive;
    }

    public void setLayerActive(boolean layerActive) {
        this.layerActive = layerActive;
    }

    public String getButtonTextKey() {
        return buttonTextKey;
    }

    public String getIconName() {
        return iconName;
    }
}
