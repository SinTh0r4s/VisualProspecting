package com.sinthoras.visualprospecting.gui.journeymap.buttons;

public class ThaumcraftNodeButton extends LayerButton {

    public final static ThaumcraftNodeButton instance = new ThaumcraftNodeButton("visualprospecting.button.nodes", "nodes");

    public ThaumcraftNodeButton(String buttonTextKey, String iconName) {
        super(buttonTextKey, iconName);
    }
}
