# GregTech5U Add-On: VisualProspecting
### For Minecraft 1.7.10

This mod is intended for player convenience, but may also be used as API, since it provides the location of all GT ore veins in a cache. VisualProspecting tracks all GT Ore Veins a player has found and visualizes them in JourneyMap (optional, if installed).

VisualProspecting tracks all ores that a player interacted with, by right or by left click. It also integrates prospecting data from GTs _Advanced Seismic Prospector_.

This mod is tailored to _GregTech: New Horizons 2_, but feel free to use it however you like. Even though this mod is build against the custom GT5U from GT:NH, it should still work fine with other GT5U versions.

![Underground fluids in JourneyMap overlay](https://i.ibb.co/crPhR1X/2021-10-12-15-45-25.png) \
_Underground fluids in JourneyMap overlay._

![GregTech ore veins in JourneyMap overlay](https://i.ibb.co/cg7gH0P/2021-10-13-16-32-06.png) \
_GregTech ore veins in JourneyMap overlay._

### Dependencies

#### Required Mods:
 - Minecraft Forge
    - Injected class: [_ItemEditableBook_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/ItemEditableBookMixin.java)
 - GregTech5-Unofficial
    - Injected classes: [_GT_Block_Ores_Abstract_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/GT_Block_Ores_AbstractMixin.java), [_GT_MetaTileEntity_AdvSeismicProspector_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/GT_MetaTileEntity_AdvSeismicProspectorMixin.java), [_GT_MetaTileEntity_Scanner_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/GT_MetaTileEntity_ScannerMixin.java), [_GT_Worldgenerator.WorldGenContainer_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/WorldGenContainerMixin.java)
 - SpongeMixin
 - [Enklumne](https://github.com/Hugobros3/Enklume)
    - Ensure you build it with added
      ```
      sourceCompatibility = 1.8
      targetCompatibility = 1.8
      ```
      in `build.gradle` or use [my fork](https://github.com/SinTh0r4s/Enklume).
#### Optional Mods:
 - JourneyMap: Visualizes prospected ore veins and oil fields on custom overlay, that can be toggled on and off.
    - Injected class: [_Fullscreen_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/FullscreenMixin.java)
 - NEI: Ores on JourneyMap are highlighted according to NEI search if active (double click on search field).

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
VP.serverCache.getVeinType(int dimensionId, int chunkX, int chunkZ);
VP.serverCache.putVeinType(int dimensionId, int chunkX, int chunkZ, VPVeinType veinType);

VP.clientCache.getVeinType(int dimensionId, int chunkX, int chunkZ);
VP.clientCache.putOreVeins(int dimensionId, List<OreVeinPosition> oreVeinPositions);

VP.clientCache.putUndergroundFluids(int dimensionId, List<UndergroundFluidPosition> undergroundFluids);
VP.clientCache.getUndergroundFluid(int dimensionId, int chunkX, int chunkZ);
```
The logical server does not store underground fluid information, because GregTech has its own database for it. Instead, it provides a wrapper to access said GT database. You may also use more sophisticated methods to prospect whole areas at once. Take a look at exposed methods in `ServerCache`.

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
    final List<UndergroundFluidPosition> foundUndergroundFluids = VP.serverCache.prospectUndergroundFluidBlockRadius(world, blockX, blockZ, VP.undergroundFluidChunkProspectingBlockRadius);

    // Skip networking if in single player
    if(Utils.isLogicalClient()) {
        VP.clientCache.putOreVeins(dimensionId, foundOreVeins);
        VP.clientCache.putUndergroundFluids(dimensionId, foundUndergroundFluids);
    }
    else {
        VP.network.sendTo(new ProspectingNotification(dimensionId, foundOreVeins, foundUndergroundFluids), entityPlayer);
    }
}
```

Thank you and happy coding,\
SinTh0r4s

### Warnings

If you look closely, you will find a block of warnings regarding Mixins: ``Injection warning: LVT in gregtech/common/GT_Worldgenerator$WorldGenContainer::worldGenFindVein(II)V has incompatible[...]``. These can be safely ignored.