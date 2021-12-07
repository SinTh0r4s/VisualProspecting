package com.sinthoras.visualprospecting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinthoras.visualprospecting.hooks.HooksClient;

import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;
import gregtech.api.objects.GT_UO_Dimension;
import gregtech.api.objects.GT_UO_Fluid;
import gregtech.api.objects.XSTR;
import gregtech.common.GT_UndergroundOil;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gregtech.api.objects.XSTR.XSTR_INSTANCE;
import static gregtech.common.GT_Proxy.*;

public class Utils {

    public static boolean isDevelopmentEnvironment() {
        return (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    public static boolean isBartworksInstalled() {
        return Loader.isModLoaded("bartworks");
    }

    public static boolean isNEIInstalled() {
        return Loader.isModLoaded("NotEnoughItems");
    }

    public static boolean isTCNodeTrackerInstalled() {
        return Loader.isModLoaded("tcnodetracker");
    }

    public static boolean isJourneyMapInstalled() {
        return Loader.isModLoaded("journeymap");
    }

    public static boolean isXaerosWorldMapInstalled() {
        return Loader.isModLoaded("XaeroWorldMap");
    }
    
    public static boolean isVoxelMapInstalled() {
        try {
            // If a LiteLoader mod is present cannot be checked by calling Loader#isModLoaded.
            // Instead, we check if the VoxelMap main class is present.
            Class.forName("com.thevoxelbox.voxelmap.litemod.LiteModVoxelMap");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int coordBlockToChunk(int blockCoord) {
        return blockCoord < 0 ? -((-blockCoord - 1) >> 4) - 1 : blockCoord >> 4;
    }

    public static int coordChunkToBlock(int chunkCoord) {
        return chunkCoord < 0 ? -((-chunkCoord) << 4) : chunkCoord << 4;
    }

    public static long chunkCoordsToKey(int chunkX, int chunkZ) {
        return (((long)chunkX) << 32) | (chunkZ & 0xffffffffL);
    }

    public static int mapToCenterOreChunkCoord(final int chunkCoord) {
        if(chunkCoord >= 0) {
            return chunkCoord - (chunkCoord % 3) + 1;
        }
        else {
            return chunkCoord - (chunkCoord % 3) - 1;
        }
    }

    public static int mapToCornerUndergroundFluidChunkCoord(final int chunkCoord) {
        return chunkCoord & 0xFFFFFFF8;
    }

    public static double journeyMapScaleToLinear(final int jzoom) {
        return Math.pow(2, jzoom);
    }

    public static boolean isSmallOreId(short metaData) {
        return metaData >= VP.gregTechSmallOreMinimumMeta;
    }

    public static short oreIdToMaterialId(short metaData) {
        return (short)(metaData % 1000);
    }

    public static boolean isLogicalClient() {
        return VPMod.proxy instanceof HooksClient;
    }

    public static File getMinecraftDirectory() {
        if(isLogicalClient()) {
            return Minecraft.getMinecraft().mcDataDir;
        }
        else {
            return new File(".");
        }
    }

    public static File getSubDirectory(final String subdirectory) {
        return new File(getMinecraftDirectory(), subdirectory);
    }

    public static void deleteDirectoryRecursively(final File targetDirectory) {
        try {
            Files.walk(targetDirectory.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ByteBuffer readFileToBuffer(File file) {
        if(file.exists() == false) {
            return null;
        }
        try
        {
            final FileInputStream inputStream = new FileInputStream(file);
            final FileChannel inputChannel = inputStream.getChannel();
            final ByteBuffer buffer = ByteBuffer.allocate((int) inputChannel.size());

            inputChannel.read(buffer);
            buffer.flip();

            inputChannel.close();
            inputStream.close();

            return buffer;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Short> readFileToMap(File file) {
        if(file.exists() == false) {
            return new HashMap<>();
        }
        try {
            final Gson gson = new Gson();
            final Reader reader = Files.newBufferedReader(file.toPath());
            final Map<String, Short> map = gson.fromJson(reader, new TypeToken<Map<String, Short>>() { }.getType());
            reader.close();
            return map;
        }
        catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static void writeMapToFile(File file, Map<String, Short> map) {
        try {
            if(file.exists()) {
                file.delete();
            }
            final Gson gson = new Gson();
            final Writer writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.CREATE_NEW);
            gson.toJson(map, new TypeToken<Map<String, Short>>() { }.getType(), writer);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendToFile(File file, ByteBuffer byteBuffer) {
        try {
            if(file.exists() == false) {
                file.createNewFile();
            }
            final FileOutputStream outputStream = new FileOutputStream(file, true);
            final FileChannel outputChannel = outputStream.getChannel();

            outputChannel.write(byteBuffer);

            outputChannel.close();
            outputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, ByteBuffer> getDIMFiles(File directory) {
        try {
            final List<Integer> dimensionIds = Files.walk(directory.toPath(), 1)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("DIM"))
                    .map(dimensionFolder -> Integer.parseInt(dimensionFolder.getFileName().toString().substring(3)))
                    .collect(Collectors.toList());
            final Map<Integer, ByteBuffer> dimensionFiles = new HashMap<>();
            for (int dimensionId : dimensionIds) {
                ByteBuffer buffer = readFileToBuffer(new File(directory.toPath() + "/DIM" + dimensionId));
                if (buffer != null) {
                    dimensionFiles.put(dimensionId, buffer);
                }
            }
            return dimensionFiles;

        }
        catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
