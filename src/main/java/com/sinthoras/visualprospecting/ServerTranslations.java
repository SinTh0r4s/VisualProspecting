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
            case "fluid.gas_natural_gas":
                return "Natural Gas";
            case "fluid.liquid_light_oil":
                return "Light Oil";
            case "fluid.liquid_medium_oil":
                return "Raw Oil";
            case "fluid.liquid_heavy_oil":
                return "Heavy Oil";
            case "fluid.oil":
                return "Oil";
            case "fluid.drillingfluid":
                return "Drilling Fluid";
            case "fluid.helium-3":
                return "Helium-3";
            case "fluid.saltwater":
                return "Saltwater";
            case "fluid.molten.iron":
                return "Molten Iron";
            case "fluid.molten.lead":
                return "Molten Lead";
            case "fluid.sulfuricacid":
                return "Sulfuric Acid";
            case "fluid.carbondioxide":
                return "Carbondioxide";
            case "fluid.chlorobenzene":
                return "Chlorobenzene";
            case "fluid.liquid_extra_heavy_oil":
                return "Extra Heavy Oil";
            case "fluid.ic2distilledwater":
                return "Distilled Water";
            case "fluid.oxygen":
                return "Oxygen";
            case "fluid.liquidair":
                return "Liquid Air";
            case "fluid.methane":
                return "Methane";
            case "fluid.ethane":
                return "Ethane";
            case "fluid.liquid_hydricsulfur":
                return "Liquid Hydric Sulfur";
            case "fluid.carbonmonoxide":
                return "Carbonmonoxide";
            case "fluid.nitrogen":
                return "Nitrogen";
            case "fluid.ethylene":
                return "Ethylene";
            case "fluid.deuterium":
                return "Deuterium";
            case "fluid.fluorine":
                return "Fluorine";
            case "fluid.hydrofluoricacid_gt5u":
                return "Hydrofluoric Acid";
            case "fluid.molten.copper":
                return "Molten Copper";
            case "fluid.unknowwater":
                return "Unknowwater";
            case "fluid.molten.tin":
                return "Molten Tin";
            case "fluid.hydrogen":
                return "Hydrogen";
            case "fluid.lava":
                return "Lava";
            default:
                return fluid.getUnlocalizedName();
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
            case "ore.mix.ross128.Thorianit":
                return "Thorianit";
            case "ore.mix.ross128.carbon":
                return "Graphite";
            case "ore.mix.ross128.bismuth":
                return "Bismuth";
            case "ore.mix.ross128.TurmalinAlkali":
                return "Olenit";
            case "ore.mix.ross128.Roquesit":
                return "Roquesit";
            case "ore.mix.ross128.Tungstate":
                return "Scheelite";
            case "ore.mix.ross128.CopperSulfits":
                return "Djurleit";
            case "ore.mix.ross128.Forsterit":
                return "Forsterit";
            case "ore.mix.ross128.Hedenbergit":
                return "Hedenbergit";
            case "ore.mix.ross128.RedZircon":
                return "Red Zircon";
            case "ore.mix.ross128ba.tib":
                return "Tiberium";
            case "ore.mix.ross128ba.Tungstate":
                return "Scheelite";
            case "ore.mix.ross128ba.bart":
                return "BArTiMaEuSNeK";
            case "ore.mix.ross128ba.TurmalinAlkali":
                return "Olenit";
            case "ore.mix.ross128ba.Amethyst":
                return "Amethyst";
            case "ore.mix.ross128ba.CopperSulfits":
                return "Djurleit";
            case "ore.mix.ross128ba.RedZircon":
                return "Red Zircon";
            case "ore.mix.ross128ba.Fluorspar":
                return "Fluorspa";
            default:
                return veinType.name;
        }
    }
}
