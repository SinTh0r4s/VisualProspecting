package com.sinthoras.visualprospecting.database.veintypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;


public interface IOreMaterialProvider {

    @SideOnly(Side.CLIENT)
    IIcon getIcon();

    int getColor();
}
