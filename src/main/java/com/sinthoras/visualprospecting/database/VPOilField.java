package com.sinthoras.visualprospecting.database;

import net.minecraftforge.fluids.Fluid;

import java.util.Arrays;

public class VPOilField {

    public static final VPOilField NOT_PROSPECTED = new VPOilField(new Fluid("no_oil"), new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}});

    public final Fluid oil;
    public final int[][] chunks;

    public VPOilField(Fluid oil, int[][] chunks) {
        this.oil = oil;
        this.chunks = chunks;
    }

    public boolean equals(VPOilField other) {
        return oil == other.oil && Arrays.deepEquals(chunks, other.chunks);
    }
}
