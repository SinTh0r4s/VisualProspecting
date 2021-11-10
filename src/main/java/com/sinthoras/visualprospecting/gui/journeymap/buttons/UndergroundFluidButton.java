package com.sinthoras.visualprospecting.gui.journeymap.buttons;

import com.sinthoras.visualprospecting.gui.model.buttons.UndergroundFluidButtonManager;

public class UndergroundFluidButton extends LayerButton {

    public static final UndergroundFluidButton instance = new UndergroundFluidButton();

    public UndergroundFluidButton() {
        super(UndergroundFluidButtonManager.instance);
    }
}
