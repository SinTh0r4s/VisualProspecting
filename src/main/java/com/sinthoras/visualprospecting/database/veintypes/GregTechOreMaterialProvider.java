package com.sinthoras.visualprospecting.database.veintypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import net.minecraft.util.IIcon;

public class GregTechOreMaterialProvider implements IOreMaterialProvider {

    private final Materials material;

    public GregTechOreMaterialProvider(Materials material) {
        this.material = material;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return material.mIconSet.mTextures[OrePrefixes.ore.mTextureIndex].getIcon();
    }

    @Override
    public int getColor() {
        return (material.mRGBa[0] << 16) | (material.mRGBa[1]) << 8 | material.mRGBa[2];
    }
}
