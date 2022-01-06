package com.sinthoras.visualprospecting.integration.gregtech;

import gregtech.GT_Mod;
import gregtech.api.objects.GT_UO_Dimension;
import gregtech.api.objects.GT_UO_Fluid;
import gregtech.api.objects.XSTR;
import gregtech.common.GT_UndergroundOil;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

import static gregtech.api.objects.XSTR.XSTR_INSTANCE;
import static gregtech.common.GT_Proxy.*;
import static gregtech.common.GT_Proxy.GTOIL;
import static gregtech.common.GT_UndergroundOil.undergroundOil;

public class UndergroundFluidsWrapper {

    private final static boolean isGTNHGregTechUndergroundFluids;
    private final static float MODE_READ_ONLY = -1;

    static {
        boolean foundMethod;
        try {
            GT_UndergroundOil.class.getDeclaredMethod("undergroundOil", World.class, int.class, int.class, float.class);
            foundMethod = true;
        }
        catch (NoSuchMethodException e) {
            foundMethod = false;
        }
        isGTNHGregTechUndergroundFluids = foundMethod;
    }

    // GTNH's GregTech5-Unofficial was rewritten and requires different access
    public static FluidStack prospectFluid(World world, int chunkX, int chunkZ) {
        if(isGTNHGregTechUndergroundFluids) {
            return undergroundOil(world, chunkX, chunkZ, MODE_READ_ONLY);
        }
        else {
            return vanillaProspectFluid(world, chunkX, chunkZ);
        }
    }

    // Rewrite from GT_UndergroundOil.undergroundOil(Chunk chunk, float readOrDrainCoefficient),
    // because there is no reason to require a chunk to be loaded
    private static FluidStack vanillaProspectFluid(World world, int chunkX, int chunkZ) {
        final ChunkCoordIntPair chunkCoordinate = new ChunkCoordIntPair(chunkX, chunkZ);
        int dimensionId = world.provider.dimensionId;
        GT_UO_Dimension dimension = GT_Mod.gregtechproxy.mUndergroundOil.GetDimension(dimensionId);
        if (dimension == null) {
            return null;
        }

        Map<ChunkCoordIntPair, int[]> chunkData = dimensionWiseChunkData.computeIfAbsent(dimensionId, k -> new HashMap<>(1024));

        int[] tInts = chunkData.get(chunkCoordinate);

        if (tInts == null) {
            tInts = getDefaultChunkDataOnCreation();
        }
        else if (tInts[GTOIL] == 0) {
            return new FluidStack(FluidRegistry.getFluid(tInts[GTOILFLUID]), 0);
        }

        final XSTR tRandom = new XSTR(world.getSeed() + dimensionId * 2L + (chunkX >> 3) + 8267L * (chunkZ >> 3));

        GT_UO_Fluid uoFluid = dimension.getRandomFluid(tRandom);

        FluidStack fluidInChunk;

        if (uoFluid == null || uoFluid.getFluid() == null) {
            tInts[GTOILFLUID] = Integer.MAX_VALUE;//null fluid pointer... kind of
            tInts[GTOIL] = 0;
            chunkData.put(chunkCoordinate, tInts);//update hash map
            return null;
        }
        else {
            if (tInts[GTOILFLUID] == uoFluid.getFluid().getID()) {//if stored fluid matches uoFluid
                fluidInChunk = new FluidStack(uoFluid.getFluid(), tInts[GTOIL]);
            }
            else {
                fluidInChunk = new FluidStack(uoFluid.getFluid(), uoFluid.getRandomAmount(tRandom));
                fluidInChunk.amount = (int) ((float) fluidInChunk.amount * (0.75f + (XSTR_INSTANCE.nextFloat() / 2f)));//Randomly change amounts by +/- 25%
            }
            tInts[GTOIL] = fluidInChunk.amount;
            tInts[GTOILFLUID] = fluidInChunk.getFluidID();
        }

        if (fluidInChunk.amount <= GT_UndergroundOil.DIVIDER) {
            fluidInChunk.amount = 0;//return informative stack
            tInts[GTOIL] = 0;//so in next access it will stop way above
        }
        else {
            fluidInChunk.amount = fluidInChunk.amount / GT_UndergroundOil.DIVIDER;//give moderate extraction speed
        }

        chunkData.put(chunkCoordinate, tInts);//update hash map
        return fluidInChunk;
    }
}
