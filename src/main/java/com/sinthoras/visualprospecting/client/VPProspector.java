package com.sinthoras.visualprospecting.client;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.client.database.VPVeinCaching;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;


public class VPProspector
{
    @SideOnly(Side.CLIENT)
    public static void prospectPotentialNewVein(World world, int blockX, int blockY, int blockZ, short oreMeta)
    {
        final int chunkX = VPUtils.coordBlockToChunk(blockX);
        final int chunkZ = VPUtils.coordBlockToChunk(blockZ);
        final int oreChunkX = toOreChunkCenter(chunkX);
        final int oreChunkZ = toOreChunkCenter(chunkZ);

        //TODO: if vein in center or surroundings contains meta -> abort ASAP

        // Check closest ore chunk center for corresponding vein
        if(world.getChunkProvider().chunkExists(oreChunkX, oreChunkZ)) {
            VP.info("Checking closest chunk");
            if (chunkContainsOre(world, oreChunkX, oreChunkZ, oreMeta, blockY)) {
                // identify vein
                // add to prospected
                return;
            }
        }
        if(VPVeinCaching.largeVeinOres.contains(oreMeta)) {
            // Check neighboring ore chunk centers for large veins
            final int oreChunkOffsetX = (chunkX % 3) - 1;
            final int oreChunkOffsetZ = (chunkZ % 3) - 1;

            int[] offsetX;
            int[] offsetZ;

            if(oreChunkOffsetX == -1 && oreChunkOffsetZ == -1) {
                offsetX = new int[] {-3, -3, 0};
                offsetZ = new int[] {0, -3, -3};
            }
            else if(oreChunkOffsetX == 0 && oreChunkOffsetZ == -1) {
                offsetX = new int[] {-3, 0, 3};
                offsetZ = new int[] {-3, -3, -3};
            }
            else if(oreChunkOffsetX == 1 && oreChunkOffsetZ == -1) {
                offsetX = new int[] {0, 3, 3};
                offsetZ = new int[] {-3, -3, 0};
            }
            else if(oreChunkOffsetX == 1 && oreChunkOffsetZ == 0) {
                offsetX = new int[] {3, 3, 3};
                offsetZ = new int[] {-3, 0, 3};
            }
            else if(oreChunkOffsetX == 1 && oreChunkOffsetZ == 1) {
                offsetX = new int[] {3, 3, 0};
                offsetZ = new int[] {0, 3, 3};
            }
            else if(oreChunkOffsetX == 0 && oreChunkOffsetZ == 1) {
                offsetX = new int[] {3, 0, -3};
                offsetZ = new int[] {3, 3, 3};
            }
            else if(oreChunkOffsetX == -1 && oreChunkOffsetZ == 1) {
                offsetX = new int[] {0, -3, -3};
                offsetZ = new int[] {3, 3, 0};
            }
            else if(oreChunkOffsetX == -1 && oreChunkOffsetZ == 0) {
                offsetX = new int[] {-3, -3, -3};
                offsetZ = new int[] {3, 0, -3};
            }
            else
                return;

            if(world.getChunkProvider().chunkExists(oreChunkX + offsetX1, oreChunkZ + offsetZ1)) {
                VP.info("Checking secondary chunk 1");
                if (chunkContainsOre(world, oreChunkX + offsetX1, oreChunkZ + offsetZ1, oreMeta, blockY)) {
                    // identify vein
                    // add to prospected
                    return;
                }
            }
        }
    }

    private static boolean chunkContainsOre(World world, int chunkX, int chunkZ, short oreMeta, int blockY)
    {
        final Chunk chunk = world.provider.worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
        // TODO: reduce number of checked blocks as much as possible. Search area of 8x8 SHOULD be enough
        for(int blockX=0;blockX<VP.chunkWidth;blockX++)
            for(int blockZ=0;blockZ<P.chunkDepth;blockZ++) {
                final Block block = chunk.getBlock(blockX, blockY, blockZ);
                if(block instanceof GT_Block_Ores_Abstract) {
                    final TileEntity tileEntity = chunk.getTileEntityUnsafe(blockX, blockY, blockZ);
                    if (tileEntity instanceof GT_TileEntity_Ores)
                        return oreMeta == ((GT_TileEntity_Ores) tileEntity).mMetaData;

                }
            }
        return false;
    }

    private static int toOreChunkCenter(int chunkXorZ)
    {
        final int remainder = chunkXorZ % 3;
        if(remainder == 0)
            return chunkXorZ + 1;
        if(remainder == 1)
            return chunkXorZ;
        return  chunkXorZ - 1;
    }
}
