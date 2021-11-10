package com.sinthoras.visualprospecting.gui.model.locations;

import com.sinthoras.visualprospecting.Utils;
import net.minecraftforge.fluids.Fluid;

public class UndergroundFluidChunkLocation implements ILocationProvider {

    private final int blockX;
    private final int blockZ;
    private final int dimensionId;
    private final Fluid fluid;
    private final int fluidAmount;
    private final int maxAmountInField;
    private final int minAmountInField;

    public UndergroundFluidChunkLocation(int chunkX, int chunkZ, int dimensionId, Fluid fluid, int fluidAmount, int minAmountInField, int maxAmountInField) {
        blockX = Utils.coordChunkToBlock(chunkX);
        blockZ = Utils.coordChunkToBlock(chunkZ);
        this.dimensionId = dimensionId;
        this.fluid = fluid;
        this.fluidAmount = fluidAmount;
        this.maxAmountInField = maxAmountInField;
        this.minAmountInField = minAmountInField;
    }

    public int getBlockX() {
        return blockX;
    }

    public int getBlockZ() {
        return blockZ;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public String getFluidAmountFormatted() {
        if(fluidAmount >= 1000) {
            return (fluidAmount / 1000) + "kL";
        }
        return fluidAmount + "L";
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public int getMaxAmountInField() {
        return maxAmountInField;
    }

    public int getMinAmountInField() {
        return minAmountInField;
    }
}
