package com.sinthoras.visualprospecting.item;

import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.task.SnapshotDownloadTask;
import com.sinthoras.visualprospecting.task.SnapshotUploadTask;
import gregtech.api.GregTech_API;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.util.List;

public class ProspectorsLog extends Item {

    public static ProspectorsLog instance;

    public ProspectorsLog() {
        maxStackSize = 1;
        setUnlocalizedName("visualprospecting.prospectorslog");
        setCreativeTab(GregTech_API.TAB_GREGTECH);
        setTextureName(Tags.MODID + ":prospectorslog");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
        if(isFilledLog(item) == false) {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.setString(Tags.PROSPECTORSLOG_AUTHOR, player.getDisplayName());
            compound.setString(Tags.PROSPECTORSLOG_AUTHOR_ID, player.getPersistentID().toString());
            item.setTagCompound(compound);
            if (world.isRemote) {
                VP.taskManager.addTask(new SnapshotUploadTask());
            }
            else {
                final int random = VP.randomGeneration.nextInt(100);
                if(random < 10) {
                    final String localizationKey = "visualprospecting.prospectorslog.creation.fail" + (random / 2);
                    final IChatComponent notification = new ChatComponentTranslation(localizationKey);
                    notification.getChatStyle().setItalic(true);
                    notification.getChatStyle().setColor(EnumChatFormatting.GRAY);
                    player.addChatMessage(notification);
                    player.destroyCurrentEquippedItem();
                }
            }
        }
        else if(world.isRemote == false){
            final NBTTagCompound compound = item.getTagCompound();
            final String authorUuid = compound.getString(Tags.PROSPECTORSLOG_AUTHOR_ID);
            if(authorUuid.equals(player.getPersistentID().toString()) == false) {
                final int random = VP.randomGeneration.nextInt(VP.transferCache.isClientDataAvailable(authorUuid) ? 100 : 10);
                if(random < 10) {
                    final String localizationKey = "visualprospecting.prospectorslog.reading.fail" + (random / 2);
                    final IChatComponent notification = new ChatComponentTranslation(localizationKey);
                    notification.getChatStyle().setItalic(true);
                    notification.getChatStyle().setColor(EnumChatFormatting.GRAY);
                    player.addChatMessage(notification);
                    player.destroyCurrentEquippedItem();
                }
                else {
                    final IChatComponent notification = new ChatComponentTranslation("visualprospecting.prospectorslog.reading.begin");
                    notification.getChatStyle().setItalic(true);
                    notification.getChatStyle().setColor(EnumChatFormatting.GRAY);
                    player.addChatMessage(notification);
                    VP.taskManager.addTask(new SnapshotDownloadTask(authorUuid, (EntityPlayerMP) player));
                }
            }
        }
        return item;
    }

    @Override
    public String getItemStackDisplayName(ItemStack item) {
        return I18n.format("visualprospecting.prospectorslog.name");
    }

    @Override
    public void addInformation(ItemStack item, EntityPlayer player, List infoList, boolean ignored) {
        if(isFilledLog(item)) {
            final NBTTagCompound compound = item.getTagCompound();
            infoList.add(I18n.format("visualprospecting.prospectorslog.author", compound.getString(Tags.PROSPECTORSLOG_AUTHOR)));
        }
        else {
            infoList.add(I18n.format("visualprospecting.prospectorslog.empty"));
        }
    }

    private boolean isFilledLog(ItemStack item) {
        final NBTTagCompound compound = item.getTagCompound();
        return compound != null && compound.hasKey(Tags.PROSPECTORSLOG_AUTHOR) && compound.hasKey(Tags.PROSPECTORSLOG_AUTHOR_ID);
    }
}
