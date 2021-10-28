package com.sinthoras.visualprospecting;

import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fluids.Fluid;

// Backup translations for server side lookups only
public class ServerTranslations {

    public static String getEnglishLocalization(Fluid fluid) {
        if(MinecraftServer.getServer().isSinglePlayer()) {
            return fluid.getLocalizedName();
        }

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

    /*
    Copy the relevant content into a file 'data.dat' and run the following python script to generate the switch-case:

    with open("data.dat") as f:
    lines = f.readlines()
    for line in lines:
        if line is not "\n":
            key, value = line.split("=")
            value = value[:-1]
            print("case \"" + key + "\":")
            print("    return \"" + value + "\";")
     */
    public static String getEnglishLocalization(VeinType veinType) {
        if(MinecraftServer.getServer().isSinglePlayer()) {
            return I18n.format(veinType.name);
        }

        switch (veinType.name) {
            case "ore.mix.naquadah":
                return "Naquadah";
            case "ore.mix.lignite":
                return "Lignite";
            case "ore.mix.coal":
                return "Coal";
            case "ore.mix.magnetite":
                return "Magnetite";
            case "ore.mix.gold":
                return "Gold";
            case "ore.mix.iron":
                return "Iron";
            case "ore.mix.cassiterite":
                return "Cassiterite";
            case "ore.mix.tetrahedrite":
                return "Tetrahedrite";
            case "ore.mix.netherquartz":
                return "Nether Quartz";
            case "ore.mix.sulfur":
                return "Sulfur";
            case "ore.mix.copper":
                return "Copper";
            case "ore.mix.bauxite":
                return "Bauxite";
            case "ore.mix.salts":
                return "Salts";
            case "ore.mix.redstone":
                return "Redstone";
            case "ore.mix.soapstone":
                return "Soapstone";
            case "ore.mix.nickel":
                return "Nickel";
            case "ore.mix.platinum":
                return "Platinum";
            case "ore.mix.pitchblende":
                return "Pitchblende";
            case "ore.mix.monazite":
                return "Monazite";
            case "ore.mix.molybdenum":
                return "Molybdenum";
            case "ore.mix.tungstate":
                return "Tungstate";
            case "ore.mix.sapphire":
                return "Sapphire";
            case "ore.mix.manganese":
                return "Manganese";
            case "ore.mix.quartz":
                return "Quartz";
            case "ore.mix.diamond":
                return "Diamond";
            case "ore.mix.olivine":
                return "Olivine";
            case "ore.mix.apatite":
                return "Apatite";
            case "ore.mix.galena":
                return "Galena";
            case "ore.mix.lapis":
                return "Lapis";
            case "ore.mix.beryllium":
                return "Beryllium";
            case "ore.mix.uranium":
                return "Uranium";
            case "ore.mix.oilsand":
                return "Oilsands";
            case "ore.mix.neutronium":
                return "Neutronium";
            case "ore.mix.aquaignis":
                return "Aqua and Ignis";
            case "ore.mix.terraaer":
                return "Terra and Aer";
            case "ore.mix.perditioordo":
                return "Perdito and Ordo";
            case "ore.mix.coppertin":
                return "Vermiculite";
            case "ore.mix.titaniumchrome":
                return "Ilmenite";
            case "ore.mix.mineralsand":
                return "Mineralsand";
            case "ore.mix.garnettin":
                return "Garnettin";
            case "ore.mix.kaolinitezeolite":
                return "Kaolinite";
            case "ore.mix.mica":
                return "Mica";
            case "ore.mix.dolomite":
                return "Dolomite";
            case "ore.mix.platinumchrome":
                return "Palladium";
            case "ore.mix.iridiummytryl":
                return "Iridium";
            case "ore.mix.osmium":
                return "Osmium";
            case "ore.mix.saltpeterelectrotine":
                return "Electrotine";
            case "ore.mix.desh":
                return "Desh";
            case "ore.mix.draconium":
                return "Draconium";
            case "ore.mix.quantium":
                return "Quantum";
            case "ore.mix.callistoice":
                return "Callisto Ice";
            case "ore.mix.mytryl":
                return "Mithril";
            case "ore.mix.ledox":
                return "Ledox";
            case "ore.mix.oriharukon":
                return "Oriharukon";
            case "ore.mix.blackplutonium":
                return "Black Plutonium";
            case "ore.mix.infusedgold":
                return "Infused Gold";
            case "ore.mix.niobium":
                return "Niobium";
            case "ore.mix.tungstenirons":
                return "Tungsten";
            case "ore.mix.uraniumgtnh":
                return "Thorium";
            case "ore.mix.vanadiumgold":
                return "Vanadium";
            case "ore.mix.netherstar":
                return "NetherStar";
            case "ore.mix.garnet":
                return "Garnet";
            case "ore.mix.rareearth":
                return "Rare Earths";
            case "ore.mix.richnuclear":
                return "Plutonium";
            case "ore.mix.heavypentele":
                return "Arsenic";
            case "ore.mix.europa":
                return "Magnesite";
            case "ore.mix.europacore":
                return "Chrome";
            case "ore.mix.secondlanthanid":
                return "Samarium";
            case "ore.mix.quartzspace":
                return "Quartz";
            case "ore.mix.rutile":
                return "Rutile";
            case "ore.mix.tfgalena":
                return "Cryolite";
            case "ore.mix.luvtantalite":
                return "Pyrolusit";
            default:
                return veinType.name;
        }
    }
}
