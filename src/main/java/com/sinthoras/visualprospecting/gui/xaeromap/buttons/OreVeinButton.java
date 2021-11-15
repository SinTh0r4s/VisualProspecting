package com.sinthoras.visualprospecting.gui.xaeromap.buttons;

import com.sinthoras.visualprospecting.gui.model.buttons.OreVeinButtonManager;

public class OreVeinButton extends LayerButton {

    public static final OreVeinButton instance = new OreVeinButton();

    public OreVeinButton() {
        super(OreVeinButtonManager.instance);
    }
}
