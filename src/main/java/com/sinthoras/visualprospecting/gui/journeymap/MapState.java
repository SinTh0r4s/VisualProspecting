package com.sinthoras.visualprospecting.gui.journeymap;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.NodeList;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import journeymap.client.model.Waypoint;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.Utils.isTCNodeTrackerInstalled;

public class MapState {
    public static final MapState instance = new MapState();

    private final List<OreVeinDrawStep> oreChunkDrawSteps = new ArrayList<>();
    private int oldMinOreChunkX = 0;
    private int oldMaxOreChunkX = 0;
    private int oldMinOreChunkZ = 0;
    private int oldMaxOreChunkZ = 0;

    private final List<UndergroundFluidChunkDrawStep> undergroundFluidChunksDrawSteps = new ArrayList<>();
    private final List<UndergroundFluidDrawStep> undergroundFluidsDrawSteps = new ArrayList<>();
    private int oldMinUndergroundFluidX = 0;
    private int oldMaxUndergroundFluidX = 0;
    private int oldMinUndergroundFluidZ = 0;
    private int oldMaxUndergroundFluidZ = 0;

    private final List<ThaumcraftNodeDrawStep> thaumcraftNodesDrawSteps = new ArrayList<>();
    private int oldMinBlockX = 0;
    private int oldMinBlockZ = 0;
    private int oldMaxBlockX = 0;
    private int oldMaxBlockZ = 0;

    private Waypoint activeOreVeinPosition = null;
    private Waypoint activeAuraNode = null;
    private int oldMouseX = 0;
    private int oldMouseY = 0;
    private long timeLastClick = 0;

    public boolean drawOreVeins = true;
    public boolean drawUndergroundFluids = false;
    public boolean drawThaumcraftNodes = false;

    public void refresh() {
        oldMinOreChunkX = 0;
        oldMaxOreChunkX = 0;
        oldMinOreChunkZ = 0;
        oldMaxOreChunkZ = 0;
        oldMinUndergroundFluidX = 0;
        oldMaxUndergroundFluidX = 0;
        oldMinUndergroundFluidZ = 0;
        oldMaxUndergroundFluidZ = 0;
        oldMinBlockX = 0;
        oldMinBlockZ = 0;
        oldMaxBlockX = 0;
        oldMaxBlockZ = 0;
        oldMouseX = 0;
        oldMouseY = 0;
        timeLastClick = 0;
    }

    public List<OreVeinDrawStep> getOreVeinDrawSteps(final GridRenderer gridRenderer) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int radiusBlockX = minecraft.displayWidth >> (1 + gridRenderer.getZoom());
        final int radiusBlockZ = minecraft.displayHeight >> (1 + gridRenderer.getZoom());
        final int minOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(centerBlockX - radiusBlockX));
        final int minOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(centerBlockZ - radiusBlockZ));
        final int maxOreChunkX = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(centerBlockX + radiusBlockX));
        final int maxOreChunkZ = Utils.mapToCenterOreChunkCoord(Utils.coordBlockToChunk(centerBlockZ + radiusBlockZ));

        if (minOreChunkX != oldMinOreChunkX || maxOreChunkX != oldMaxOreChunkX || minOreChunkZ != oldMinOreChunkZ || maxOreChunkZ != oldMaxOreChunkZ) {
            oldMinOreChunkX = minOreChunkX;
            oldMaxOreChunkX = maxOreChunkX;
            oldMinOreChunkZ = minOreChunkZ;
            oldMaxOreChunkZ = maxOreChunkZ;
            oreChunkDrawSteps.clear();
            for (int chunkX = minOreChunkX; chunkX <= maxOreChunkX; chunkX = Utils.mapToCenterOreChunkCoord(chunkX + 3)) {
                for (int chunkZ = minOreChunkZ; chunkZ <= maxOreChunkZ; chunkZ = Utils.mapToCenterOreChunkCoord(chunkZ + 3)) {
                    final OreVeinPosition oreVeinPosition = VP.clientCache.getOreVein(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (oreVeinPosition.veinType != VeinType.NO_VEIN) {
                        oreChunkDrawSteps.add(new OreVeinDrawStep(oreVeinPosition));
                    }
                }
            }
        }
        return oreChunkDrawSteps;
    }

    private void updateUndergroundFluidRelatedDrawSteps(final GridRenderer gridRenderer) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int radiusBlockX = minecraft.displayWidth >> (1 + gridRenderer.getZoom());
        final int radiusBlockZ = minecraft.displayHeight >> (1 + gridRenderer.getZoom());
        final int minUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(centerBlockX - radiusBlockX));
        final int minUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(centerBlockZ - radiusBlockZ));
        final int maxUndergroundFluidX = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(centerBlockX + radiusBlockX));
        final int maxUndergroundFluidZ = Utils.mapToCornerUndergroundFluidChunkCoord(Utils.coordBlockToChunk(centerBlockZ + radiusBlockZ));
        if (minUndergroundFluidX != oldMinUndergroundFluidX || maxUndergroundFluidX != oldMaxUndergroundFluidX || minUndergroundFluidZ != oldMinUndergroundFluidZ || maxUndergroundFluidZ != oldMaxUndergroundFluidZ) {
            oldMinUndergroundFluidX = minUndergroundFluidX;
            oldMaxUndergroundFluidX = maxUndergroundFluidX;
            oldMinUndergroundFluidZ = minUndergroundFluidZ;
            oldMaxUndergroundFluidZ = maxUndergroundFluidZ;

            undergroundFluidChunksDrawSteps.clear();
            for (int chunkX = minUndergroundFluidX; chunkX <= maxUndergroundFluidX; chunkX += VP.undergroundFluidSizeChunkX) {
                for (int chunkZ = minUndergroundFluidZ; chunkZ <= maxUndergroundFluidZ; chunkZ += VP.undergroundFluidSizeChunkZ) {
                    final UndergroundFluidPosition undergroundFluid = VP.clientCache.getUndergroundFluid(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (undergroundFluid.isProspected()) {
                        final int minAmountInField = undergroundFluid.getMinProduction();
                        final int maxAmountInField = undergroundFluid.getMaxProduction();
                        for (int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
                            for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                                undergroundFluidChunksDrawSteps.add(new UndergroundFluidChunkDrawStep(chunkX + offsetChunkX, chunkZ + offsetChunkZ, undergroundFluid.fluid, undergroundFluid.chunks[offsetChunkX][offsetChunkZ], minAmountInField, maxAmountInField));
                            }
                        }
                    }
                }
            }

            undergroundFluidsDrawSteps.clear();
            for (int chunkX = minUndergroundFluidX; chunkX <= maxUndergroundFluidX; chunkX += VP.undergroundFluidSizeChunkX) {
                for (int chunkZ = minUndergroundFluidZ; chunkZ <= maxUndergroundFluidZ; chunkZ += VP.undergroundFluidSizeChunkZ) {
                    final UndergroundFluidPosition undergroundFluid = VP.clientCache.getUndergroundFluid(minecraft.thePlayer.dimension, chunkX, chunkZ);
                    if (undergroundFluid.isProspected()) {
                        undergroundFluidsDrawSteps.add(new UndergroundFluidDrawStep(undergroundFluid));
                    }
                }
            }
        }
    }

    public List<UndergroundFluidChunkDrawStep> getUndergroundFluidChunksDrawSteps(final GridRenderer gridRenderer) {
        updateUndergroundFluidRelatedDrawSteps(gridRenderer);
        return undergroundFluidChunksDrawSteps;
    }

    public List<UndergroundFluidDrawStep> getUndergroundFluidsDrawSteps(final GridRenderer gridRenderer) {
        updateUndergroundFluidRelatedDrawSteps(gridRenderer);
        return undergroundFluidsDrawSteps;
    }

    public List<ThaumcraftNodeDrawStep> getThaumcraftNodesDrawSteps(final GridRenderer gridRenderer) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final int centerBlockX = (int) Math.round(gridRenderer.getCenterBlockX());
        final int centerBlockZ = (int) Math.round(gridRenderer.getCenterBlockZ());
        final int radiusBlockX = minecraft.displayWidth >> (1 + gridRenderer.getZoom());
        final int radiusBlockZ = minecraft.displayHeight >> (1 + gridRenderer.getZoom());
        final int minBlockX = centerBlockX - radiusBlockX;
        final int minBlockZ = centerBlockZ - radiusBlockZ;
        final int maxBlockX = centerBlockX + radiusBlockX;
        final int maxBlockZ = centerBlockZ + radiusBlockZ;
        if(minBlockX != oldMinBlockX || minBlockZ != oldMinBlockZ || maxBlockX != oldMaxBlockX || maxBlockZ != oldMaxBlockZ) {
            oldMinBlockX = minBlockX;
            oldMinBlockZ = minBlockZ;
            oldMaxBlockX = maxBlockX;
            oldMaxBlockZ = maxBlockZ;
            thaumcraftNodesDrawSteps.clear();
            for (NodeList node : TCNodeTracker.nodelist) {
                if(node.dim == minecraft.thePlayer.dimension
                        && node.x >= minBlockX && node.x <= maxBlockX
                        && node.z >= minBlockZ && node.z <= maxBlockZ) {
                    thaumcraftNodesDrawSteps.add(new ThaumcraftNodeDrawStep(node));
                }
            }
        }

        return thaumcraftNodesDrawSteps;
    }

    public void onDeletePressed() {
        if(drawOreVeins) {
            oreChunkDrawSteps.removeIf(OreVeinDrawStep::onDeletePressed);
        }
        else if(drawThaumcraftNodes && isTCNodeTrackerInstalled()) {
            thaumcraftNodesDrawSteps.removeIf(ThaumcraftNodeDrawStep::onDeletePressed);
        }
    }

    public boolean onMapClicked(int mouseButton, int mouseX, int mouseY, double blockSize) {
        if(mouseButton != 0) {
            return false;
        }
        final long timestamp = System.currentTimeMillis();
        final boolean isDoubleClick = mouseX == oldMouseX && mouseY == oldMouseY && timestamp - timeLastClick < 500;
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        timeLastClick = timestamp;

        boolean objectHit = false;
        // If no double click: Just check if click hit a DrawStep and return boolean. Remove isDoubleClick from mouseClick call
        if(drawOreVeins) {
            activeOreVeinPosition = null;
            for (OreVeinDrawStep oreVeinDrawStep : oreChunkDrawSteps) {
                if (oreVeinDrawStep.onMouseClick(mouseX, mouseY, blockSize, isDoubleClick)) {
                    activeOreVeinPosition = oreVeinDrawStep.toWaypoint();
                    objectHit = true;
                }
            }
        }
        if(drawThaumcraftNodes && isTCNodeTrackerInstalled()) {
            for(ThaumcraftNodeDrawStep thaumcraftNodeDrawStep : thaumcraftNodesDrawSteps) {
                if(thaumcraftNodeDrawStep.onMouseClick(mouseX, mouseY, isDoubleClick)) {
                    activeAuraNode = thaumcraftNodeDrawStep.toWaypoint();
                    objectHit = true;
                }
            }
        }
        return objectHit;
    }

    public void disableWaypoint() {
        for(OreVeinDrawStep oreVeinDrawStep : oreChunkDrawSteps) {
            oreVeinDrawStep.disableWaypoint();
        }
        for(ThaumcraftNodeDrawStep thaumcraftNodeDrawStep : thaumcraftNodesDrawSteps) {
            thaumcraftNodeDrawStep.disableWaypoint();
        }
    }

    public Waypoint getActiveOreVein() {
        return activeOreVeinPosition;
    }

    public Waypoint getActiveAuraNode() {
        return activeAuraNode;
    }

    public void setActiveAuraNode(final Waypoint activeAuraNode) {
        this.activeAuraNode = activeAuraNode;
    }

    public void resetActiveNode() {
        activeAuraNode = null;
    }
}
