package com.sinthoras.visualprospecting.gui.journeymap.layers;

import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.ClickableDrawStep;
import com.sinthoras.visualprospecting.gui.journeymap.drawsteps.OreVeinDrawStep;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.OreVeinButton;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isNEIInstalled;

public class OreVeinLayer extends WaypointProviderLayer {

    public final static OreVeinLayer instance = new OreVeinLayer();

    private int oldMinOreChunkX = 0;
    private int oldMaxOreChunkX = 0;
    private int oldMinOreChunkZ = 0;
    private int oldMaxOreChunkZ = 0;

    public OreVeinLayer() {
        super(OreVeinButton.instance);
    }

    @Override
    public void onOpenMap() {
        if(isNEIInstalled()) {
            VeinTypeCaching.recalculateNEISearch();
        }
    }

    @Override
    protected boolean needsRegenerateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        final int minOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(minBlockX));
        final int minOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(minBlockZ));
        final int maxOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(maxBlockX));
        final int maxOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(maxBlockZ));

        if (minOreChunkX != oldMinOreChunkX || maxOreChunkX != oldMaxOreChunkX || minOreChunkZ != oldMinOreChunkZ || maxOreChunkZ != oldMaxOreChunkZ) {
            oldMinOreChunkX = minOreChunkX;
            oldMaxOreChunkX = maxOreChunkX;
            oldMinOreChunkZ = minOreChunkZ;
            oldMaxOreChunkZ = maxOreChunkZ;
            return true;
        }
        return false;
    }

    @Override
    protected List<ClickableDrawStep> generateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        final int minOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(minBlockX));
        final int minOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(minBlockZ));
        final int maxOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(maxBlockX));
        final int maxOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(maxBlockZ));
        final int playerDimensionId = Minecraft.getMinecraft().thePlayer.dimension;

        ArrayList<ClickableDrawStep> oreChunkDrawSteps = new ArrayList<>();

        for (int chunkX = minOreChunkX; chunkX <= maxOreChunkX; chunkX = Utils.mapToCenterOreChunkCoord(chunkX + 3)) {
            for (int chunkZ = minOreChunkZ; chunkZ <= maxOreChunkZ; chunkZ = Utils.mapToCenterOreChunkCoord(chunkZ + 3)) {
                final OreVeinPosition oreVeinPosition = VP.clientCache.getOreVein(playerDimensionId, chunkX, chunkZ);
                if (oreVeinPosition.veinType != VeinType.NO_VEIN) {
                    oreChunkDrawSteps.add(new OreVeinDrawStep(oreVeinPosition));
                }
            }
        }

        return oreChunkDrawSteps;
    }
}
