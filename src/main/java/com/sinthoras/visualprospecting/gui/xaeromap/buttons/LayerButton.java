package com.sinthoras.visualprospecting.gui.xaeromap.buttons;

import com.sinthoras.visualprospecting.gui.model.SupportedMods;
import com.sinthoras.visualprospecting.gui.model.buttons.ButtonManager;

public class LayerButton extends com.sinthoras.visualprospecting.gui.model.buttons.LayerButton {

	private final ButtonManager manager;

	public LayerButton(ButtonManager manager) {
		super(manager, SupportedMods.XaeroMap);
		this.manager = manager;
	}

	@Override
	public void updateState(boolean active) {

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
