package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPTags;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;

import java.util.UUID;

public class VPWorldIdHandler extends WorldSavedData {

    private static VPWorldIdHandler instance;
    private String worldId;

    public VPWorldIdHandler() {
        super(VPTags.MODID);
    }

    public VPWorldIdHandler(String name) {
        super(name);
    }

    public static void load(WorldServer world) {
        instance = (VPWorldIdHandler) world.mapStorage.loadData(VPWorldIdHandler.class, VPTags.MODID);
        if (instance == null) {
            instance = new VPWorldIdHandler(VPTags.MODID);
            instance.worldId = world.func_73046_m().getFolderName() + "_" + UUID.randomUUID();
            world.mapStorage.setData(VPTags.MODID, instance);
            instance.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        worldId = compound.getString(VPTags.worldId);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setString(VPTags.worldId, worldId);
    }

    public static String getWorldId() {
        return instance.worldId;
    }
}
