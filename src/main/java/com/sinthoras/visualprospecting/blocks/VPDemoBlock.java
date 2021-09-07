package com.sinthoras.visualprospecting.blocks;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.client.VPProspector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class VPDemoBlock extends Block {

    // This TEMPORARY block exists to trigger behaviour that is to be moved onto ores!

    public static final String NAME = "DemoBlock";

    public VPDemoBlock() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setBlockName(NAME);
    }

    public boolean onBlockActivated(World world, int blockX, int blockY, int blockZ, EntityPlayer player, int side, float offsetBlockX, float offsetBlockY, float offsetBlockZ)
    {
        if(!world.provider.worldObj.isRemote)
        {
            VPProspector.prospectPotentialNewVein(world, blockX, blockY, blockZ);
        }
        return super.onBlockActivated(world, blockX, blockY, blockZ, player, side, offsetBlockX, offsetBlockY, offsetBlockZ);
    }

    public void onBlockClicked(World world, int blockX, int blockY, int blockZ, EntityPlayer player)
    {
        if(!world.provider.worldObj.isRemote)
        {
            VPProspector.prospectPotentialNewVein(world, blockX, blockY, blockZ);
        }
        super.onBlockClicked(world, blockX, blockY, blockZ, player);
    }
}
