package com.sinthoras.visualprospecting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinthoras.visualprospecting.hooks.VPHooksClient;
import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VPUtils {

    public static boolean isBartworksInstalled() {
        return Loader.isModLoaded("bartworks");
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

    public static int nonNegativeModulo(final int value, final int divisor) {
        final int rest = value % divisor;
        if(rest < 0)
            return rest + divisor;
        return rest;
    }

    public static int mapToCenterOreChunkCoord(final int chunkCoord) {
        return chunkCoord - nonNegativeModulo(chunkCoord - 1, 3) + 1;
    }

    public static boolean isLogicalClient() {
        return VPMod.proxy instanceof VPHooksClient;
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

    public static ByteBuffer readFileToBuffer(File file) {
        if(file.exists() == false)
            return null;
        try
        {
            final FileInputStream inputStream = new FileInputStream(file);
            final FileChannel inputChannel = inputStream.getChannel();
            final ByteBuffer buffer = ByteBuffer.allocate((int) inputChannel.size());

            inputChannel.read(buffer);
            buffer.flip();

            inputChannel.close();
            inputStream.close();

            if(buffer.limit() % 10 == 0) {
                return buffer;
            }
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Short> readFileToMap(File file) {
        if(file.exists() == false)
            return new HashMap<>();
        try {
            final Gson gson = new Gson();
            final Reader reader = Files.newBufferedReader(file.toPath());
            final Map<String, Short> map = gson.fromJson(reader, new TypeToken<Map<String, Short>>() { }.getType());
            reader.close();
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static void writeMapToFile(File file, Map<String, Short> map) {
        try {
            if(file.exists())
                file.delete();
            final Gson gson = new Gson();
            final Writer writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.CREATE_NEW);
            gson.toJson(map, new TypeToken<Map<String, Short>>() { }.getType(), writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendToFile(File file, ByteBuffer byteBuffer) {
        try {
            if(file.exists() == false)
                file.createNewFile();
            final FileOutputStream outputStream = new FileOutputStream(file, true);
            final FileChannel outputChannel = outputStream.getChannel();

            outputChannel.write(byteBuffer);

            outputChannel.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Integer, ByteBuffer> getDIMFiles(File directory) {
        try {
            final List<Integer> dimensionIds = Files.walk(directory.toPath(), 1)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("DIM"))
                    .map(dimensionFolder -> Integer.parseInt(dimensionFolder.getFileName().toString().substring(3)))
                    .collect(Collectors.toList());
            final HashMap<Integer, ByteBuffer> dimensionFiles = new HashMap<>();
            for (int dimensionId : dimensionIds) {
                ByteBuffer buffer = readFileToBuffer(new File(directory.toPath() + "/DIM" + dimensionId));
                if (buffer != null)
                    dimensionFiles.put(dimensionId, buffer);
            }
            return dimensionFiles;

        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
