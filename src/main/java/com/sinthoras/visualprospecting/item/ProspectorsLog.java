package com.sinthoras.visualprospecting.item;

import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.task.SnapshotTask;
import gregtech.api.GregTech_API;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ProspectorsLog extends Item {

    public static ProspectorsLog instance;

    public ProspectorsLog() {
        maxStackSize = 1;
        setUnlocalizedName("visualprospecting.prospectorslog");
        setCreativeTab(GregTech_API.TAB_GREGTECH);
    }

    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int blockX, int blockY, int blockZ, int side, float offsetX, float offsetY, float offsetZ) {
        if(isFilledLog(item) == false) {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.setString(Tags.PROSPECTORSLOG_AUTHOR, player.getDisplayName());
            compound.setString(Tags.PROSPECTORSLOG_AUTHOR_ID, player.getUniqueID().toString());
            item.setTagCompound(compound);
            if (world.isRemote) {
                VP.taskManager.addTask(new SnapshotTask());
            }
            return true;
        }
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack item) {
        final NBTTagCompound compound = item.getTagCompound();
        if(isFilledLog(item)) {
            return I18n.format("visualprospecting.prospectorslog.owned", compound.getString(Tags.PROSPECTORSLOG_AUTHOR));
        }
        return I18n.format("visualprospecting.prospectorslog.empty");
    }

    private boolean isFilledLog(ItemStack item) {
        final NBTTagCompound compound = item.getTagCompound();
        return compound != null && compound.hasKey(Tags.PROSPECTORSLOG_AUTHOR) && compound.hasKey(Tags.PROSPECTORSLOG_AUTHOR_ID);
    }
}
