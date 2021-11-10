package com.sinthoras.visualprospecting.gui.model.buttons;

import com.sinthoras.visualprospecting.gui.model.MapState;
import com.sinthoras.visualprospecting.gui.model.SupportedMods;

import java.util.EnumMap;
import java.util.Map;

public class ButtonManager {

    private final String buttonTextKey;
    private final String iconName;

    private Map<SupportedMods, LayerButton> buttons = new EnumMap<>(SupportedMods.class);
    private boolean isActive = false;

    /*
    Provide textures in assets/journeymap/icon/theme/Vault/icon/<iconName>.png
    and assets/journeymap/icon/theme/Victorian/icon/<iconName>.png for JourneyMap
    and provide a texture in assets/xaeroworldmap/textures/<iconName>.png for XaeroWorldMap.
     */
    public ButtonManager(String buttonTextKey, String iconName) {
        this.buttonTextKey = buttonTextKey;
        this.iconName = iconName;
    }

    public void registerButton(SupportedMods map, LayerButton layerButton) {
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

    public boolean isActive() {
        return isActive;
    }

    public void activate() {
        MapState.instance.buttons.forEach(ButtonManager::deactivate);
        isActive = true;
        updateState(true);
    }

    public void deactivate() {
        isActive = false;
        updateState(false);
    }

    public void toggle() {
        if(isActive) {
            deactivate();
        }
        else {
            activate();
        }
    }
}