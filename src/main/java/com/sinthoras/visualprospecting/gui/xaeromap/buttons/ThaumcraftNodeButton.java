package com.sinthoras.visualprospecting.gui.xaeromap.buttons;

import com.sinthoras.visualprospecting.gui.model.buttons.ThaumcraftNodeButtonManager;

public class ThaumcraftNodeButton extends LayerButton {

    public static final ThaumcraftNodeButton instance = new ThaumcraftNodeButton();

    public ThaumcraftNodeButton() {
        super(ThaumcraftNodeButtonManager.instance);
    }
}
