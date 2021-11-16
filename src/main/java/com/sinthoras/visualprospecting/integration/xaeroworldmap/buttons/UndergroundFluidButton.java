package com.sinthoras.visualprospecting.integration.xaeroworldmap.buttons;

import com.sinthoras.visualprospecting.integration.model.buttons.UndergroundFluidButtonManager;

public class UndergroundFluidButton extends LayerButton {

    public static final UndergroundFluidButton instance = new UndergroundFluidButton();

    public UndergroundFluidButton() {
        super(UndergroundFluidButtonManager.instance);
    }
}
