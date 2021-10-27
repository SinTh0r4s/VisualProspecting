package com.sinthoras.visualprospecting;

import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.ServerCache;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.gui.journeymap.MapState;
import com.sinthoras.visualprospecting.gui.journeymap.buttons.LayerButton;
import com.sinthoras.visualprospecting.gui.journeymap.layers.InformationLayer;
import com.sinthoras.visualprospecting.gui.journeymap.layers.OreVeinLayer;
import com.sinthoras.visualprospecting.gui.journeymap.layers.UndergroundFluidLayer;
import com.sinthoras.visualprospecting.network.ProspectingNotification;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isJourneyMapInstalled;

@SuppressWarnings("unused")
public class VisualProspecting_API {

    @SideOnly(Side.CLIENT)
    public static class LogicalClient {

        public static void registerLayerButton(LayerButton button) {
            if (isJourneyMapInstalled()) {
                MapState.instance.buttons.add(button);
            }
        }

        public static void registerLayer(InformationLayer layer) {
            if (isJourneyMapInstalled()) {
                MapState.instance.layers.add(layer);
            }
        }

        public static void openJourneyForOreVeinsMapAt(int blockX, int blockZ) {
            if (isJourneyMapInstalled()) {
                OreVeinLayer.instance.activateLayer();
                MapState.instance.openJourneyMapAt(blockX, blockZ);
            }
        }

        public static void openJourneyForOreVeinsMapAt(int blockX, int blockZ, int zoom) {
            if (isJourneyMapInstalled()) {
                OreVeinLayer.instance.activateLayer();
                MapState.instance.openJourneyMapAt(blockX, blockZ, zoom);
            }
        }

        public static void openJourneyMapForUndergroundFluidsAt(int blockX, int blockZ) {
            if (isJourneyMapInstalled()) {
                UndergroundFluidLayer.instance.activateLayer();
                MapState.instance.openJourneyMapAt(blockX, blockZ);
            }
        }

        public static void openJourneyMapForUndergroundFluidsAt(int blockX, int blockZ, int zoom) {
            if (isJourneyMapInstalled()) {
                UndergroundFluidLayer.instance.activateLayer();
                MapState.instance.openJourneyMapAt(blockX, blockZ, zoom);
            }
        }

        // This mechanic is limited to blocks the player can touch
        public static void triggerProspectingForOreBlock(EntityPlayer player, World world, int blockX, int blockY, int blockZ) {
            ClientCache.instance.onOreInteracted(world, blockX, blockY, blockZ, player);
        }

        public static OreVeinPosition getOreVein(int dimensionId, int blockX, int blockZ) {
            return ClientCache.instance.getOreVein(dimensionId, Utils.coordBlockToChunk(blockX), Utils.coordBlockToChunk(blockZ));
        }

        public static UndergroundFluidPosition getUndergroundFluid(int dimensionId, int blockX, int blockZ) {
            return ClientCache.instance.getUndergroundFluid(dimensionId, Utils.coordBlockToChunk(blockX), Utils.coordBlockToChunk(blockZ));
        }

        public static void setOreVeinDepleted(int dimensionId, int blockX, int blockZ) {
            final OreVeinPosition oreVeinPosition = ClientCache.instance.getOreVein(dimensionId, Utils.coordBlockToChunk(blockX), Utils.coordBlockToChunk(blockZ));
            if(oreVeinPosition.isDepleted() == false) {
                oreVeinPosition.toggleDepleted();
            }
            ClientCache.instance.putOreVeins(Collections.singletonList(oreVeinPosition));
        }

        public static void toggleOreVeinDepleted(OreVeinPosition oreVeinPosition) {
            oreVeinPosition = ClientCache.instance.getOreVein(oreVeinPosition.dimensionId, oreVeinPosition.chunkX, oreVeinPosition.chunkZ);
            oreVeinPosition.toggleDepleted();
            ClientCache.instance.putOreVeins(Collections.singletonList(oreVeinPosition));
        }

        public static void putProspectionResults(List<OreVeinPosition> oreVeins, List<UndergroundFluidPosition> undergroundFluids) {
            ClientCache.instance.putOreVeins(oreVeins);
            ClientCache.instance.putUndergroundFluids(undergroundFluids);
        }
    }

    public static class LogicalServer {

        public static OreVeinPosition getOreVein(int dimensionId, int blockX, int blockZ) {
            return ServerCache.instance.getOreVein(dimensionId, Utils.mapToCenterOreChunkCoord(blockX), Utils.mapToCenterOreChunkCoord(blockZ));
        }

        public static UndergroundFluidPosition getUndergroundFluid(World world, int blockX, int blockZ) {
            return prospectUndergroundFluidsWithingRadius(world, blockX, blockZ, 0).get(0);
        }

        public static void notifyOreGeneration(int dimensionId, int blockX, int blockZ, final String oreVeinName) {
            ServerCache.instance.notifyOreVeinGeneration(dimensionId, blockX, blockZ, oreVeinName);
        }

        public static void sendProspectionResultsToClient(EntityPlayerMP player, List<OreVeinPosition> oreVeins, List<UndergroundFluidPosition> undergroundFluids) {
            // Skip networking if in single player
            if(Utils.isLogicalClient()) {
                ClientCache.instance.putOreVeins(oreVeins);
                ClientCache.instance.putUndergroundFluids(undergroundFluids);
            }
            else {
                VP.network.sendTo(new ProspectingNotification(oreVeins, undergroundFluids), player);
            }
        }

        public static List<OreVeinPosition> prospectOreVeinsWithinRadius(int dimensionId, int blockX, int blockZ, int blockRadius) {
            return ServerCache.instance.prospectOreBlockRadius(dimensionId, blockX, blockZ, blockRadius);
        }

        public static List<UndergroundFluidPosition> prospectUndergroundFluidsWithingRadius(World world, int blockX, int blockZ, int blockRadius) {
            return ServerCache.instance.prospectUndergroundFluidBlockRadius(world, blockX, blockZ, blockRadius);
        }
    }
}
