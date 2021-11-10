package com.sinthoras.visualprospecting.gui.model.locations;

import com.dyonovan.tcnodetracker.lib.NodeList;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.gui.model.waypoints.Waypoint;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileNode;

public class ThaumcraftNodeLocation implements IWaypointAndLocationProvider {

    private static final String deleteHint = EnumChatFormatting.DARK_GRAY + I18n.format("visualprospecting.node.deletehint", Keyboard.getKeyName(VP.keyAction.getKeyCode()));;
    private static final String activeWaypointHint = EnumChatFormatting.GOLD + I18n.format("visualprospecting.iswaypoint");
    private static final String title = EnumChatFormatting.BOLD + I18n.format("tile.blockAiry.0.name");

    private final NodeList node;
    private final TileNode nodeTile;
    private final String description;

    private boolean isActiveAsWaypoint;

    public ThaumcraftNodeLocation(NodeList node) {
        this.node = node;

        nodeTile = new TileNode();
        final AspectList aspectList = new AspectList();
        for(String aspectTag : node.aspect.keySet()) {
            aspectList.add(Aspect.getAspect(aspectTag), node.aspect.get(aspectTag));
        }
        nodeTile.setAspects(aspectList);
        switch(node.type) {
            case "NORMAL":
                nodeTile.setNodeType(NodeType.NORMAL);
                break;
            case "UNSTABLE":
                nodeTile.setNodeType(NodeType.UNSTABLE);
                break;
            case "DARK":
                nodeTile.setNodeType(NodeType.DARK);
                break;
            case "TAINTED":
                nodeTile.setNodeType(NodeType.TAINTED);
                break;
            case "PURE":
                nodeTile.setNodeType(NodeType.PURE);
                break;
            case "HUNGRY":
                nodeTile.setNodeType(NodeType.HUNGRY);
                break;
        }
        nodeTile.blockType = ConfigBlocks.blockAiry;
        nodeTile.blockMetadata = 0;

        description = node.mod.equals("BLANK") ? EnumChatFormatting.GRAY + I18n.format("nodetype." + node.type + ".name")
                : EnumChatFormatting.GRAY + I18n.format("nodetype." + node.type + ".name") + ", " + I18n.format("nodemod." + node.mod + ".name");
    }

    @Override
    public double getBlockX() {
        return node.x + 0.5;
    }

    public double getBlockY() {
        return node.y + 0.5;
    }

    @Override
    public double getBlockZ() {
        return node.z + 0.5;
    }

    @Override
    public int getDimensionId() {
        return node.dim;
    }

    @Override
    public Waypoint toWaypoint() {
        return new Waypoint(node.x, node.y, node.z, node.dim,
                I18n.format("visualprospecting.tracked", I18n.format("tile.blockAiry.0.name")),
                nodeTile.targetColor.getRGB());
    }

    @Override
    public boolean isActiveAsWaypoint() {
        return isActiveAsWaypoint;
    }

    @Override
    public void onWaypointCleared() {
        isActiveAsWaypoint = false;
    }

    @Override
    public void onWaypointUpdated(Waypoint waypoint) {
        isActiveAsWaypoint = waypoint.dimensionId == node.dim
                && waypoint.blockX == node.x
                && waypoint.blockY == node.y
                && waypoint.blockZ== node.z;
    }

    public boolean belongsToNode(NodeList other) {
        return node.x == other.x && node.y == other.y && node.z == other.z && node.dim == other.dim;
    }

    public Aspect getStrongestAspect() {
        return nodeTile.getAspects().getAspectsSortedAmount()[0];
    }

    public AspectList getAspects() {
        return nodeTile.getAspects();
    }

    public String getDescription() {
        return description;
    }

    public String getDeleteHint() {
        return deleteHint;
    }

    public String getActiveWaypointHint() {
        return activeWaypointHint;
    }

    public String getTitle() {
        return title;
    }
}
