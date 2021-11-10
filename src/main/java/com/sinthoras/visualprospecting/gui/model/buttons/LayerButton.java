package com.sinthoras.visualprospecting.gui.model.buttons;

import com.sinthoras.visualprospecting.gui.model.SupportedMap;

public abstract class LayerButton {

    public LayerButton(ButtonManager manager, SupportedMap map) {
        manager.registerButton(map, this);
        // Grab lang key and texture information from manager in extended constructor
    }

    public abstract void updateState(boolean active);
}
