package com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons;

import com.sinthoras.visualprospecting.integration.model.buttons.OreVeinButtonManager;

public class OreVeinButton extends LayerButton {

    public static final OreVeinButton instance = new OreVeinButton();

    public OreVeinButton() {
        super(OreVeinButtonManager.instance);
    }
}
