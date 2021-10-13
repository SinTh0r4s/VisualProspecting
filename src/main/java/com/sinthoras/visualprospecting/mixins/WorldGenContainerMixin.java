package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import gregtech.api.objects.XSTR;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;
import gregtech.common.GT_Worldgenerator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(value = GT_Worldgenerator.WorldGenContainer.class, remap = false)
public class WorldGenContainerMixin {

    @Final
    @Shadow(remap = false)
    public int mX;

    @Final
    @Shadow(remap = false)
    public int mZ;

    @Final
    @Shadow(remap = false)
    public World mWorld;

    @Inject(method = "worldGenFindVein",
            at = @At(value = "INVOKE", target = "Ljava/util/Hashtable;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            remap = false,
            require = 1,
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onOreVeinPlacedFailedA(int oreseedX, int oreseedZ, CallbackInfo callbackInfo, long oreveinSeed, XSTR oreveinRNG, int oreveinPercentageRoll, int noOrePlacedCount, String tDimensionName) {
        VP.serverCache.putOreVein(mWorld.provider.dimensionId, mX, mZ, VeinType.NO_VEIN);
    }

    @Inject(method = "worldGenFindVein",
            at = @At(value = "INVOKE", target = "Ljava/util/Hashtable;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            remap = false,
            require = 1,
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onOreVeinPlacedFailedB(int oreseedX, int oreseedZ, CallbackInfo ci, long oreveinSeed, XSTR oreveinRNG, int oreveinPercentageRoll, int noOrePlacedCount, String tDimensionName, int placementAttempts, boolean oreveinFound, int i) {
        VP.serverCache.putOreVein(mWorld.provider.dimensionId, mX, mZ, VeinType.NO_VEIN);
    }

    @Inject(method = "worldGenFindVein",
            at = @At(value = "INVOKE", target = "Ljava/util/Hashtable;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            remap = false,
            require = 2,
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onOreVeinPlacedSuccess(int oreseedX, int oreseedZ, CallbackInfo callbackInfo, long oreveinSeed, XSTR oreveinRNG, int oreveinPercentageRoll, int noOrePlacedCount, String tDimensionName, int placementAttempts, boolean oreveinFound, int i, int tRandomWeight, Iterator var13, GT_Worldgen_GT_Ore_Layer tWorldGen, int placementResult) {
        VP.serverCache.putOreVein(mWorld.provider.dimensionId, mX, mZ, VeinTypeCaching.getVeinType(tWorldGen.mWorldGenName));
    }

}
