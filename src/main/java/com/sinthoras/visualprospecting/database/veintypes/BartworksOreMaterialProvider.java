package com.sinthoras.visualprospecting.database.veintypes;

import com.github.bartimaeusnek.bartworks.system.material.Werkstoff;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.OrePrefixes;
import net.minecraft.util.IIcon;

import static com.github.bartimaeusnek.bartworks.util.BW_ColorUtil.getColorFromRGBArray;

public class BartworksOreMaterialProvider implements IOreMaterialProvider {

    private final Werkstoff material;

    public BartworksOreMaterialProvider(Werkstoff material) {
        this.material = material;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return material.getTexSet().mTextures[OrePrefixes.ore.mTextureIndex].getIcon();
    }

    @Override
    public int getColor() {
        return getColorFromRGBArray(material.getRGBA());
    }
}
