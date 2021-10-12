package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.Tags;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;

import java.util.UUID;

public class WorldIdHandler extends WorldSavedData {

    private static WorldIdHandler instance;
    private String worldId;

    public WorldIdHandler() {
        super(Tags.MODID);
    }

    public WorldIdHandler(String name) {
        super(name);
    }

    public static void load(WorldServer world) {
        instance = (WorldIdHandler) world.mapStorage.loadData(WorldIdHandler.class, Tags.MODID);
        if (instance == null) {
            instance = new WorldIdHandler(Tags.MODID);
            instance.worldId = world.func_73046_m().getFolderName() + "_" + UUID.randomUUID();
            world.mapStorage.setData(Tags.MODID, instance);
            instance.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        worldId = compound.getString(Tags.worldId);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setString(Tags.worldId, worldId);
    }

    public static String getWorldId() {
        return instance.worldId;
    }
}
