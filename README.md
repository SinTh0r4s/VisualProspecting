# GregTech5U Add-On: VisualProspecting (under development, API subject to change!)
### by SinTh0r4s

This mod is intended for player convenience, but may also be used as API, since it provides the location of all GT ore veins in a cache. VisualProspecting tracks all GT Ore Veins a player has found and visualizes them in JourneyMap (if installed).

VisualProspecting tracks all ores that a player interacted with, by right or by left click. It also integrates prospecting data from _GT Advanced Prospector_.

Further, it adds a craftable _Prospectors Log_ that allows sharing prospected locations between players.

This mod is tailored to _GregTech: New Horizons 2_, but feel free to use it however you like. Even though this mod is build against the custom GT5U from GT:NH, it should still work fine with other GT5U versions.


### Add Visual Prospecting as API

You would have a great idea for a new prospecting feature? You may use VPs database as a starting point to save yourself a ton of work. Just add these following changes to your ```build.gradle``` and you are ready to develop.

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

You need to determine whether your code is executed on the logical client or logical server. Dependent on your answer you need to use the according database: The client database only knows about ore veins the player has already prospected, while the server database will know about all veins. You may add or request the ore vein for a chunk:
```
VP.serverVeinCache.putVeinType(int dimensionId, int chunkX, int chunkZ, VPVeinType veinType);
VP.serverVeinCache.getVeinType(int dimensionId, int chunkX, int chunkZ);

VP.clientVeinCache.putVeinType(int dimensionId, List<VPServerCache.VPProspectionResult> prospectionResults);
VP.clientVeinCache.getVeinType(int dimensionId, int chunkX, int chunkZ);
```
You may also use more sophisticated methods to prospect whole areas at once. Take a look at ``VPServerCache``.
Please keep in mind that chunk coordinates are block coordinates divided by 16! When in doubt you may fall back on:
```
int chunkX = VPUtils.coordBlockToChunk(blockX);
```
```
// blockZ is the lowest block coordinate in a chunk. If you want 
// to iterate over all blocks in that particular chunk you need
// to add [0, ... 15] to it
int blockZ = VPUtils.coordChunkToBlock(chunkZ);
```

Whenever you detect a new ore vein you need to add custom network payloads and request the information from the logical server yourself. Please do your best to disallow a logical client from querying the complete server database as it would lead to potential abuse. So, please check if the player is allowed to prospect a dimension and location.

If you simply want to notify a logical client from the logical server you may send a ``VPProspectingNotification`` to the logical client. It will be handled from the client.

Thank you and happy coding,\
SinTh0r4s

### Warnings

If you look closely, you will find a block of warnings regarding Mixins: ``Injection warning: LVT in gregtech/common/GT_Worldgenerator$WorldGenContainer::worldGenFindVein(II)V has incompatible[...]``. These can be safely ignored.