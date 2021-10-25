package com.sinthoras.visualprospecting.gui.journeymap.buttons;

public class ThaumcraftNodeButton extends LayerButton {

    public static final ThaumcraftNodeButton instance = new ThaumcraftNodeButton("visualprospecting.button.nodes", "nodes");

    public ThaumcraftNodeButton(String buttonTextKey, String iconName) {
        super(buttonTextKey, iconName);
    }
}
