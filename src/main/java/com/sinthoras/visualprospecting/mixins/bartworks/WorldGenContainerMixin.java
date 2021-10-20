package com.sinthoras.visualprospecting.mixins.bartworks;

import com.github.bartimaeusnek.bartworks.system.oregen.BW_OreLayer;
import com.github.bartimaeusnek.bartworks.system.oregen.BW_WordGenerator;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(BW_WordGenerator.WorldGenContainer.class)
public class WorldGenContainerMixin {

    @Redirect(method = "run",
            at = @At(value = "INVOKE",
                    target = "Lcom/github/bartimaeusnek/bartworks/system/oregen/BW_OreLayer;executeWorldgen(Lnet/minecraft/world/World;Ljava/util/Random;Ljava/lang/String;IIILnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)Z"),
            remap = false,
            require = 1)
    private boolean onOreVeinGenerationAttempt(BW_OreLayer worldGen, World world, Random random, String biome, int dimensionType, int blockX, int blockZ, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        final boolean oreVeinPlaced = worldGen.executeWorldgen(world, random, biome, Integer.MIN_VALUE, blockX, blockZ, chunkGenerator, chunkProvider);
        if(oreVeinPlaced) {
            VP.serverCache.notifyOreVeinGeneration(world.provider.dimensionId, Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(blockX)), Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(blockZ)), worldGen.mWorldGenName);
        }
        return oreVeinPlaced;
    }
}
