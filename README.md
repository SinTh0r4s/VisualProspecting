# GregTech5U Add-On: VisualProspecting
### by SinTh0r4s

This mod is intended for player convenience, but may also be used as API, since it provides the location of all GT ore veins in a cache. VisualProspecting tracks all GT Ore Veins a player has found and visualizes them in JourneyMap (optional, if installed).

VisualProspecting tracks all ores that a player interacted with, by right or by left click. It also integrates prospecting data from GTs _Advanced Seismic Prospector_.

This mod is tailored to _GregTech: New Horizons 2_, but feel free to use it however you like. Even though this mod is build against the custom GT5U from GT:NH, it should still work fine with other GT5U versions.


### Add Visual Prospecting as API

You would have a great idea for a new prospecting feature? You may use VPs database as a starting point to save yourself a ton of work. Just add these following changes to your `build.gradle` and you are ready to develop.

Add jitpack to your repositories:
```
repositories {
    maven {
        url = "https://jitpack.io"
    }
}
```

Add Visual Prospecting in your dependencies:
```
dependencies {
    compile("com.github.SinTh0r4s:VisualProspecting:master-SNAPSHOT")
}
```

GregTech, JourneyMap and their respective dependencies will be loaded automatically. You are ready to start now.


### Usage as API

All database access is channeled through the classes `ServerCache` and `ClientCache`. Database use is split up into logical sides.
You need to determine whether your code is executed on the logical client or logical server. Dependent on your answer you need to use the according database: The client database only knows about ore veins the player has already prospected, while the server database will know about all veins. You may add or request the ore vein for a chunk:
```
VP.clientCache.getVeinType(int dimensionId, int chunkX, int chunkZ);
VP.clientCache.putVeinType(int dimensionId, int chunkX, int chunkZ, VPVeinType veinType);

VP.clientCache.getVeinType(int dimensionId, int chunkX, int chunkZ);
VP.clientCache.putOreVeins(int dimensionId, List<OreVeinPosition> oreVeinPositions);

VP.clientCache.putOilFields(int dimensionId, List<OilFieldPosition> oilFields);
VP.clientCache.getOilField(int dimensionId, int chunkX, int chunkZ);
```
The logical server does not store oil field information, because GregTech has its own database for it. Instead, it provides a wrapper to access said GT database. You may also use more sophisticated methods to prospect whole areas at once. Take a look at exposed methods in `ServerCache`.

Please keep in mind that chunk coordinates are block coordinates divided by 16! When in doubt you may fall back on:
```
int chunkX = Utils.coordBlockToChunk(blockX);
```
```
// blockZ is the lowest block coordinate in a chunk. If you want 
// to iterate over all blocks in that particular chunk you need
// to add [0, ... 15] to it
int blockZ = Utils.coordChunkToBlock(chunkZ);
```

Whenever you detect a new ore vein you need to add custom network payloads and request the information from the logical server yourself. Please do your best to disallow a logical client from querying the complete server database as it would lead to potential abuse. So, please check if the player is allowed to prospect a dimension and location.

If you simply want to notify a logical client from the logical server you may send a ``ProspectingNotification`` to the logical client. It will be handled from the client. For example:
```
final World world;
final int blockX;
final int blockZ;
final int blockRadius;
final EntityPlayerMP entityPlayer;

if(world.isRemote == false) {
    final List<OreVeinPosition> foundOreVeins = VP.serverCache.prospectOreBlockRadius(world.provider.dimensionId, blockX, blockZ, blockRadius);
    final List<OilFieldPosition> foundOilFields = VP.serverCache.prospectOilBlockRadius(world, blockX, blockZ, VP.oilChunkProspectingBlockRadius);

    // Skip networking if in single player
    if(Utils.isLogicalClient()) {
        VP.clientCache.putOreVeins(dimensionId, foundOreVeins);
        VP.clientCache.putOilFields(dimensionId, foundOilFields);
    }
    else {
        VP.network.sendTo(new ProspectingNotification(dimensionId, foundOreVeins, foundOilFields), entityPlayer);
    }
}
```

Thank you and happy coding,\
SinTh0r4s

### Warnings

If you look closely, you will find a block of warnings regarding Mixins: ``Injection warning: LVT in gregtech/common/GT_Worldgenerator$WorldGenContainer::worldGenFindVein(II)V has incompatible[...]``. These can be safely ignored.