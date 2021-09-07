package com.sinthoras.visualprospecting.client;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.client.database.VPVeinCaching;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;


public class VPProspector
{
    @SideOnly(Side.CLIENT)
    public static void prospectPotentialNewVein(World world, int blockX, int blockY, int blockZ)
    {
        VP.info("clicked\n");

        VPVeinCaching.getVeinTypes();
    }
}
