package com.sinthoras.visualprospecting.client;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPConfig;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashSet;

@Deprecated
public class VPProspector {
    @SideOnly(Side.CLIENT)
    public static void prospectPotentialNewVein(World world, int blockX, int blockY, int blockZ, short oreMeta) {
        final int chunkX = VPUtils.coordBlockToChunk(blockX);
        final int chunkZ = VPUtils.coordBlockToChunk(blockZ);
        final int oreChunkX = toOreChunkCenter(chunkX);
        final int oreChunkZ = toOreChunkCenter(chunkZ);

        //TODO: if vein in center or surroundings contains meta -> abort ASAP

        // Check closest ore chunk center for corresponding vein
        if(world.getChunkProvider().chunkExists(oreChunkX, oreChunkZ)) {
            VP.info("Checking closest chunk");
            if (chunkContainsOre(world, oreChunkX, oreChunkZ, oreMeta, blockY)) {
                VP.info("Found ORE!");
                final VPVeinType veinType = identifyVein(world, oreChunkX, oreChunkZ, blockY);
                // add to prospected
                return;
            }
        }
        if(VPVeinTypeCaching.largeVeinOres.contains(oreMeta)) {
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

            for(int secondaryOreChunkId = 0;secondaryOreChunkId < offsetX.length;secondaryOreChunkId++) {
                final int secondaryOreChunkX = oreChunkX + offsetX[secondaryOreChunkId];
                final int secondaryOreChunkZ = oreChunkZ + offsetZ[secondaryOreChunkId];
                if (world.getChunkProvider().chunkExists(secondaryOreChunkX, secondaryOreChunkZ)) {
                    VP.info("Checking secondary chunk " + secondaryOreChunkId);
                    if (chunkContainsOre(world, secondaryOreChunkX, secondaryOreChunkZ, oreMeta, blockY)) {
                        VP.info("Found ORE!");
                        final VPVeinType veinType = identifyVein(world, secondaryOreChunkX, secondaryOreChunkZ, blockY);
                        // add to prospected
                        return;
                    }
                }
            }
        }
    }

    private static boolean chunkContainsOre(World world, int chunkX, int chunkZ, short oreMeta, int blockY) {
        final Chunk chunk = world.provider.worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
        for(int blockX=0;blockX<VPConfig.veinSearchDiameter;blockX++)
            for(int blockZ=0;blockZ<VPConfig.veinSearchDiameter;blockZ++) {
                final Block block = chunk.getBlock(blockX, blockY, blockZ);
                if(block instanceof GT_Block_Ores_Abstract) {
                    final TileEntity tileEntity = chunk.getTileEntityUnsafe(blockX, blockY, blockZ);
                    if (tileEntity instanceof GT_TileEntity_Ores
                            && oreMeta == ((GT_TileEntity_Ores) tileEntity).mMetaData)
                        return true;
                }
            }
        return false;
    }

    private static int toOreChunkCenter(int chunkXorZ) {
        final int remainder = chunkXorZ % 3;
        if(remainder == 0)
            return chunkXorZ + 1;
        if(remainder == 1)
            return chunkXorZ;
        return  chunkXorZ - 1;
    }

    private static boolean sampleYLevel(Chunk chunk, HashSet<Short> foundOres, int levelY) {
        boolean foundOre = false;
        for (int blockX = 0; blockX < VPConfig.veinSearchDiameter; blockX++)
            for (int blockZ = 0; blockZ < VPConfig.veinSearchDiameter; blockZ++) {
                final Block block = chunk.getBlock(blockX, levelY, blockZ);
                if (block instanceof GT_Block_Ores_Abstract) {
                    final TileEntity tileEntity = chunk.getTileEntityUnsafe(blockX, levelY, blockZ);
                    if (tileEntity instanceof GT_TileEntity_Ores) {
                        final short meta = ((GT_TileEntity_Ores) tileEntity).getMetaData();
                        if (meta != 0 && meta < VP.gregTechSmallOreMinimumMeta) {
                            foundOres.add(meta);
                            foundOre = true;
                        }
                    }
                }
            }
        return foundOre;
    }

    private static VPVeinType identifyVein(World world, int chunkX, int chunkZ, int foundY) {
        final Chunk chunk = world.provider.worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
        final int minY = Math.max(0, foundY - VPConfig.veinIdentificationMaxUpDown);
        final int maxY = Math.min(255, foundY + VPConfig.veinIdentificationMaxUpDown);

        HashSet<Short> foundOres = new HashSet<>();

        int oreLessLayers = 0;
        for(int blockY=foundY;blockY<maxY;blockY++) {
            if(!sampleYLevel(chunk, foundOres, foundY))
                oreLessLayers++;
            if(oreLessLayers >= 1 || foundOres.size() >= 4)
                break;
        }

        oreLessLayers = 0;
        for(int blockY=Math.max(foundY-1, 0);blockY>minY;blockY--) {
            if(!sampleYLevel(chunk, foundOres, foundY))
                oreLessLayers++;
            if(oreLessLayers >= 1 || foundOres.size() >= 4)
                break;
        }

        final ArrayList<VPVeinType> possibleMatches = new ArrayList<>();
        for(final VPVeinType veinType : VPVeinTypeCaching.veinTypes)
            if(veinType.matches(foundOres)) {
                VP.info("Found possibly matching vein: " + veinType.name);
                possibleMatches.add(veinType);
            }
        if(possibleMatches.size() == 1) {
            final VPVeinType veinType = possibleMatches.get(0);
            VP.info("Found matching vein: " + veinType.name);
            return veinType;
        }

        return null;
    }
}
