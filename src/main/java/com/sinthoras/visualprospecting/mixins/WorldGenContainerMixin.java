package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;
import gregtech.common.GT_Worldgenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(GT_Worldgenerator.WorldGenContainer.class)
public class WorldGenContainerMixin {

    // Redirect both calls to ensure that Bartworks ore veins are captured as well 
    @Redirect(method = "worldGenFindVein",
            at = @At(value = "INVOKE", target = "Lgregtech/common/GT_Worldgen_GT_Ore_Layer;executeWorldgenChunkified(Lnet/minecraft/world/World;Ljava/util/Random;Ljava/lang/String;IIIIILnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)I"),
            remap = false,
            require = 2)
    protected int onOreVeinPlaced(GT_Worldgen_GT_Ore_Layer instance, World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, int aSeedX, int aSeedZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        final int result = instance.executeWorldgenChunkified(aWorld, aRandom, aBiome, aDimensionType, aChunkX, aChunkZ, aSeedX, aSeedZ, aChunkGenerator, aChunkProvider);
        if(result == GT_Worldgen_GT_Ore_Layer.ORE_PLACED && instance.mWorldGenName.equals("NoOresInVein") == false) {
            VP.serverCache.putOreVein(
                    aWorld.provider.dimensionId,
                    Utils.coordBlockToChunk(aSeedX),
                    Utils.coordBlockToChunk(aSeedZ),
                    VeinTypeCaching.getVeinType(instance.mWorldGenName));
        }
        return result;
    }
}
