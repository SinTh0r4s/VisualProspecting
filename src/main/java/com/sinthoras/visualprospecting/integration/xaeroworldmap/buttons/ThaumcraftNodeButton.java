package com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons;

import com.sinthoras.visualprospecting.integration.model.buttons.ThaumcraftNodeButtonManager;

public class ThaumcraftNodeButton extends LayerButton {

    public static final ThaumcraftNodeButton instance = new ThaumcraftNodeButton();

    public ThaumcraftNodeButton() {
        super(ThaumcraftNodeButtonManager.instance);
    }
}
