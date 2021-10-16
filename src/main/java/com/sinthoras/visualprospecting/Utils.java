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
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gregtech.api.objects.XSTR.XSTR_INSTANCE;
import static gregtech.common.GT_Proxy.*;
import static gregtech.common.GT_Proxy.GTOIL;

public class Utils {

    public static boolean isBartworksInstalled() {
        return Loader.isModLoaded("bartworks");
    }


    public static boolean isNEIInstalled() {
        return Loader.isModLoaded("NotEnoughItems");
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

    public static boolean isSmallOreId(short metaData) {
        return metaData >= VP.gregTechSmallOreMinimumMeta;
    }

    public static short oreIdToMaterialId(short metaData) {
        return (short)(metaData % 1000);
    }

    public static boolean isLogicalClient() {
        return VPMod.proxy instanceof HooksClient;
    }

    public static String getEnglishLocalization(Fluid fluid) {
        switch(fluid.getUnlocalizedName()) {
            case "gas_natural_gas":
                return "Natural Gas";
            case "liquid_light_oil":
                return "Light Oil";
            case "liquid_medium_oil":
                return "Raw Oil";
            case "liquid_heavy_oil":
                return "Heavy Oil";
            case "oil":
                return "Oil";
            case "helium-3":
                return "Helium-3";
            case "saltwater":
                return "Saltwater";
            case "molten.iron":
                return "Molten Iron";
            case "molten.lead":
                return "Molten Lead";
            case "sulfuricacid":
                return "Sulfuric Acid";
            case "carbondioxide":
                return "Carbondioxide";
            case "chlorobenzene":
                return "Chlorobenzene";
            case "liquid_extra_heavy_oil":
                return "Extra Heavy Oil";
            case "ic2distilledwater":
                return "Distilled Water";
            case "oxygen":
                return "Oxygen";
            case "liquidair":
                return "Liquid Air";
            case "methane":
                return "Methane";
            case "ethane":
                return "Ethane";
            case "liquid_hydricsulfur":
                return "Liquid Hydric Sulfur";
            case "carbonmonoxide":
                return "Carbonmonoxide";
            case "nitrogen":
                return "Nitrogen";
            case "ethylene":
                return "Ethylene";
            case "deuterium":
                return "Deuterium";
            case "fluorine":
                return "Fluorine";
            case "hydrofluoricacid_gt5u":
                return "Hydrofluoric Acid";
            case "molten.copper":
                return "Molten Copper";
            case "unknowwater":
                return "Unknowwater";
            case "molten.tin":
                return "Molten Tin";
            case "hydrogen":
                return "Hydrogen";
            case "lava":
                return "Lava";
            default:
                return fluid.getLocalizedName(null);
        }
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

    // Rewrite from GT_UndergroundOil.undergroundOil(Chunk chunk, float readOrDrainCoefficient),
    // because there is no reason to require a chunk to be loaded
    public static FluidStack prospectFluid(World world, int chunkX, int chunkZ) {
        final ChunkCoordIntPair chunkCoordinate = new ChunkCoordIntPair(chunkX, chunkZ);
        int dimensionId = world.provider.dimensionId;
        GT_UO_Dimension dimension = GT_Mod.gregtechproxy.mUndergroundOil.GetDimension(dimensionId);
        if (dimension == null) {
            return null;
        }

        HashMap<ChunkCoordIntPair, int[]> chunkData = dimensionWiseChunkData.computeIfAbsent(dimensionId, k -> new HashMap<>(1024));

        int[] tInts = chunkData.get(chunkCoordinate);

        if (tInts == null) {
            tInts = getDefaultChunkDataOnCreation();
        }
        else if (tInts[GTOIL] == 0) {
            return new FluidStack(FluidRegistry.getFluid(tInts[GTOILFLUID]), 0);
        }

        final XSTR tRandom = new XSTR(world.getSeed() + dimensionId * 2L + (chunkX >> 3) + 8267L * (chunkZ >> 3));

        GT_UO_Fluid uoFluid = dimension.getRandomFluid(tRandom);

        FluidStack fluidInChunk;

        if (uoFluid == null || uoFluid.getFluid() == null) {
            tInts[GTOILFLUID] = Integer.MAX_VALUE;//null fluid pointer... kind of
            tInts[GTOIL] = 0;
            chunkData.put(chunkCoordinate, tInts);//update hash map
            return null;
        }
        else {
            if (tInts[GTOILFLUID] == uoFluid.getFluid().getID()) {//if stored fluid matches uoFluid
                fluidInChunk = new FluidStack(uoFluid.getFluid(), tInts[GTOIL]);
            }
            else {
                fluidInChunk = new FluidStack(uoFluid.getFluid(), uoFluid.getRandomAmount(tRandom));
                fluidInChunk.amount = (int) ((float) fluidInChunk.amount * (0.75f + (XSTR_INSTANCE.nextFloat() / 2f)));//Randomly change amounts by +/- 25%
            }
            tInts[GTOIL] = fluidInChunk.amount;
            tInts[GTOILFLUID] = fluidInChunk.getFluidID();
        }

        if (fluidInChunk.amount <= GT_UndergroundOil.DIVIDER) {
            fluidInChunk.amount = 0;//return informative stack
            tInts[GTOIL] = 0;//so in next access it will stop way above
        }
        else {
            fluidInChunk.amount = fluidInChunk.amount / GT_UndergroundOil.DIVIDER;//give moderate extraction speed
        }

        chunkData.put(chunkCoordinate, tInts);//update hash map
        return fluidInChunk;
    }
}
