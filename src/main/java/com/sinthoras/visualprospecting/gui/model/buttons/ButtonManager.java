package com.sinthoras.visualprospecting.gui.model.buttons;

import com.sinthoras.visualprospecting.gui.model.SupportedMap;
import com.sinthoras.visualprospecting.gui.model.buttons.LayerButton;

import java.util.EnumMap;
import java.util.Map;

public class ButtonManager {

    private final String buttonTextKey;
    private final String iconName;

    private Map<SupportedMap, LayerButton> buttons = new EnumMap<>(SupportedMap.class);

    /*
    Provide textures in assets/journeymap/icon/theme/Vault/icon/<iconName>.png
    and assets/journeymap/icon/theme/Victorian/icon/<iconName>.png for JourneyMap
    and provide a texture in assets/xaeroworldmap/textures/<iconName>.png for XaeroWorldMap.
     */
    public ButtonManager(String buttonTextKey, String iconName) {
        this.buttonTextKey = buttonTextKey;
        this.iconName = iconName;
    }

    public void registerButton(SupportedMap map, LayerButton layerButton) {
        buttons.put(map, layerButton);
    }

    public void updateState(boolean active) {
        buttons.values().forEach(button -> button.updateState(active));
    }

    public boolean containsButton(LayerButton button) {
        return buttons.containsValue(button);
    }

    public String getButtonTextKey() {
        return buttonTextKey;
    }

    public String getIconName() {
        return iconName;
    }
}