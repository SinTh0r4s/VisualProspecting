# GregTech5U Add-On: VisualProspecting

[![](https://jitpack.io/v/SinTh0r4s/VisualProspecting.svg)](https://jitpack.io/#SinTh0r4s/VisualProspecting)
[![Build and test](https://github.com/SinTh0r4s/VisualProspecting/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/SinTh0r4s/VisualProspecting/actions/workflows/build-and-test.yml)

### For Minecraft 1.7.10

This mod is intended for player convenience, but may also be used as API, since it provides the location of all GT ore veins in a cache. VisualProspecting tracks all GT Ore Veins a player has found and visualizes them in JourneyMap and/or XaeroWorldMap (optional, if installed). It also visualizes tracked Thaumcraft aura nodes if TCNodeTracker if installed. VoxelMap will add waypoints for prospected ore veins and fluids.

VisualProspecting tracks all ores that a player interacted with, by right or by left click. It also integrates prospecting data from GTs _Advanced Seismic Prospector_, although only books that are created after this mod was added will provide integration.
You may share your findings with other players by crafting a _Prospector's Log_.

This mod is tailored to _GregTech: New Horizons 2_, but feel free to use it however you like. Even though this mod is build against the custom GT5U from GT:NH, it should still work fine with other GT5U versions.

![Underground fluids in JourneyMap overlay](https://i.ibb.co/crPhR1X/2021-10-12-15-45-25.png) \
_Underground fluids in JourneyMap overlay._

<details>
 <summary>Other Maps</summary>

![Underground fluids in XaeroWorldMap overlay](https://i.ibb.co/d5Fw1px/2021-11-15-20-48-04.png) \
_Underground fluids in XaeroWorldMap overlay_
</details>


![GregTech ore veins in XaeroWorldMap overlay](https://i.ibb.co/DGqQZ8g/2021-11-15-20-47-47.png) \
_GregTech ore veins in XaeroWorldMap overlay. You may double-click an ore vein to toggle it as waypoint._

<details>
 <summary>Other Maps</summary>

![GregTech ore veins in JourneyMap overlay](https://i.ibb.co/G5KLGjQ/2021-10-20-01-16-57.png) \
_GregTech ore veins in JourneyMap overlay_
</details>


![Thaumcraft aura nodes in JourneyMap overlay](https://i.ibb.co/WDk41qd/2021-10-25-15-01-11.png) \
_Thaumcraft aura nodes in JourneyMap overlay. You may double-click an aura node to toggle it as waypoint._

<details>
 <summary>Other Maps</summary>

![Thaumcraft aura nodes in XaeroWorldMap overlay](https://i.ibb.co/njQ14RK/2021-11-15-20-48-12.png) \
_Thaumcraft aura nodes in XaeroWorldMap overlay_
</details>

### Reset Progress

You may use JourneyMap's Actions Menu to achieve this or type `/visualprospectingresetprogress` in chat. Beware, there are no backups! Please use at your own risk.

### Other Maps

Does VisualProspecting run with other maps? - I runs just fine, but it has no visualization or GUI integration. If you like to add integration into other maps yourself, feel free to contact me or open a Pull Request.
 - [TheLastKumquat](https://github.com/kumquat-ir) integrated XaeroWorldMap and XaeroMiniMap
 - [glowredman](https://github.com/glowredman) integrated VoxelMap

### Dependencies

#### Required Mods:
 - Minecraft Forge
    - Injected class: [_ItemEditableBook_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/minecraft/ItemEditableBookMixin.java)
 - [GregTech5-Unofficial](https://github.com/GTNewHorizons/GT5-Unofficial)
    - Injected classes: [_GT_Block_Ores_Abstract_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/gregtech/GT_Block_Ores_AbstractMixin.java), [_GT_MetaTileEntity_AdvSeismicProspector_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/gregtech/GT_MetaTileEntity_AdvSeismicProspectorMixin.java), [_GT_MetaTileEntity_Scanner_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/gregtech/GT_MetaTileEntity_ScannerMixin.java), [_GT_Worldgenerator.WorldGenContainer_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/gregtech/WorldGenContainerMixin.java)
 - [SpongeMixins](https://github.com/GTNewHorizons/SpongeMixins)
 - [Enklumne](https://github.com/Hugobros3/Enklume) _by Hugobros3_
    - Automatically shipped. No manual handling is required.
#### Optional Mods:
 - [JourneyMap](https://www.curseforge.com/minecraft/mc-mods/journeymap/files/2367915): Visualizes prospected ore veins, oil fields and thaumcraft nodes on custom overlay, that can be toggled on and off. Visualizes active ore veins and thaumcraft nodes as waypoints.
    - Injected classes: [_Fullscreen_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/FullscreenMixin.java), [_FullscreenActions_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/FullscreenActionsMixin.java), [_RenderWaypointBeacon_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/RenderWaypointBeaconMixin.java), [_WaypointManager_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/WaypointManagerMixin.java), [_MiniMap_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/MiniMapMixin.java)
 - [XaeroWorldMap](https://www.curseforge.com/minecraft/mc-mods/xaeros-world-map): Visualizes prospected ore veins, oil fields and thaumcraft nodes on custom overlay, that can be toggled on and off.
    - Injected class: [_GuiMap_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/xaerosworldmap/GuiMapMixin.java)
 - [XaeroMiniMap](https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap): Visualizes active ore veins and thaumcraft nodes as waypoints.
    - Injected class: [_WaypointsIngameRenderer_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/xaerosminimap/WaypointsIngameRendererMixin.java)
 - [TCNodeTracker](https://github.com/GTNewHorizons/TCNodeTracker): Provides tracked aura nodes to maps for visualization.
    - Injected class: [_GuiMain_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/tcnodetracker/GuiMainMixin.java)
 - [NEI](https://github.com/GTNewHorizons/NotEnoughItems): Ores on JourneyMap are highlighted according to NEI search if active (double click on search field).
 - [GalacticGreg](https://github.com/GTNewHorizons/GalacticGregGT5): Injects a notification call into ore vein generation.
    - Injected class: [_GT_Worldgenerator_Space_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/galacticgreg/GT_Worldgenerator_SpaceMixin.java)
 - [Bartworks](https://github.com/GTNewHorizons/bartworks): Injects a notification call into ore vein generation.
    - Injected class: [_BW_WordGenerator.WorldGenContainer_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/bartworks/WorldGenContainerMixin.java)
 - [IFU](https://github.com/GTNewHorizons/IFU): Injects a notification call to add found ore veins by the ore finder wand.
    - Injected class: [_ItemOreFinderTool_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/ifu/ItemOreFinderToolMixin.java)
 - [VoxelMap](https://www.curseforge.com/minecraft/mc-mods/voxelmap/files/2462146): Automatically adds waypoints for prospected ore veins and fluids.

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
    compile("com.github.SinTh0r4s:VisualProspecting:1.0.10b")  // Adapt 1.0.10b to targeted release
}
```

In case you do not require any Thaumcraft integration it is recommended to disable it. This will increase your start time of Minecraft in dev:
```
dependencies {
    compile("com.github.SinTh0r4s:VisualProspecting:1.0.10b") {  // Adapt 1.0.10b to targeted release
        exclude module: "TCNodeTracker"
    }
}
```

GregTech, JourneyMap and their respective dependencies will be loaded automatically. You are ready to start now.


### Usage as API

#### GT Ore Database

All database access is channeled through the classes [`ServerCache`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/database/ServerCache.java) and [`ClientCache`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/database/ClientCache.java). Database use is split up into logical sides.
You need to determine whether your code is executed on the logical client or logical server. Dependent on your answer you need to use the according database: The client database only knows about ore veins the player has already prospected, while the server database will know about all veins. [`VisualProspecting_API`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/VisualProspecting_API.java) helps you to clarify which side you are working on. You may add or request the ore vein for a chunk:
```
VisualProspecting_API.LogicalServer.getOreVein(int dimensionId, int chunkX, int chunkZ);
VisualProspecting_API.LogicalServer.getUndergroundFluid(World world, int blockX, int blockZ);


VisualProspecting_API.LogicalClient.getOreVein(int dimensionId, int chunkX, int chunkZ);
VisualProspecting_API.LogicalClient.getUndergroundFluid(int dimensionId, int blockX, int blockZ);
VisualProspecting_API.LogicalClient.setOreVeinDepleted(int dimensionId, int blockX, int blockZ);
VisualProspecting_API.LogicalClient.putProspectionResults(List<OreVeinPosition> oreVeins, List<UndergroundFluidPosition> undergroundFluids);
```
The logical server does not store underground fluid information, because GregTech has its own database for it. Instead, it provides a wrapper to access said GT database. You may also use more sophisticated methods to prospect whole areas at once. Take a look at exposed methods in [`VisualProspecting_API`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/VisualProspecting_API.java) or directly in [`ServerCache`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/database/ServerCache.java) and [`ClientCache`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/database/ClientCache.java).

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

If you simply want to notify a logical client from the logical server you may send a [`ProspectingNotification`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/network/ProspectingNotification.java) to the logical client. It will be handled from the client. For example:
```
final World world;
final int blockX;
final int blockZ;
final int blockRadius;
final EntityPlayerMP entityPlayer;

if(world.isRemote == false) {
    final List<OreVeinPosition> foundOreVeins = VisualProspecting_API.LogicalServer.prospectOreVeinsWithinRadius(world.provider.dimensionId, blockX, blockZ, blockRadius);
    final List<UndergroundFluidPosition> foundUndergroundFluids = VisualProspecting_API.LogicalServer.prospectUndergroundFluidsWithingRadius(world, blockX, blockZ, VP.undergroundFluidChunkProspectingBlockRadius);

    VisualProspecting_API.LogicalServer.sendProspectionResultsToClient(entityPlayer, foundOreVeins, foundUndergroundFluids);
}
```

#### JourneyMap Custom Layer

VisualProspecting provides a light-weight API for custom and interactive layers. This API will keep all maps as optional mod at runtime and not crash you game if it is missing. You may instantiate [`ButtonManager`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/model/buttons/ButtonManager.java) to create your own logical button. Follow it up with an instance of [`LayerButton`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/journeymap/buttons/LayerButton.java) and register both in the [`VisualProspecting_API`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/VisualProspecting_API.java):

```
ButtonManager buttonManager = new ButtonManager("translation.key", "iconName");
LayerButton layerButton = new LayerButton(buttonManager);

VisualProspecting_API.LogicalClient.registerCustomButtonManager(buttonManager);
VisualProspecting_API.LogicalClient.registerJourneyMapButton(layerButton);
```

If you start the game now, you will see a new button in the menu!

First, you will implement [`ILocationProvider`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/model/locations/ILocationProvider.java). This class is a container and will provide all information required to display your item on screen. It won't do any rendering.

```
class MyLocation implements ILocationProvider {

    public int getDimensionId() {
        return 0; // overworld
    }
    
    public int getBlockX() {
        return 0;
    }
    
    public int getBlockY() {
        return 65;
    }
    
    public int getBlockZ() {
        return 0;
    }
    
    public String getLabel() {
        return "Hello Minecraft";
    }
}
```

Next up, you'll extend [`LayerManager`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/model/layers/LayerManager.java) and implement the abstract function to generate cached List of your [`ILocationProvider`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/model/locations/ILocationProvider.java) implementation. You should only add whatever items are visible to this list. There are more methods to override, that will assist you with that. Take a look!

```
class MyLayerManager extends LayerManager {

    public static final MyLayerManager instance = new MyLayerManager();

    public MyLayerManager() {
        super(buttonManager);
    }
    
    protected List<? extends ILocationProvider> generateVisibleElements(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        return Collections.singletonList(new MyLocation);
    }
}
```

You have finished the logical implementation of your custom map layer. Congratulations! Now it is time for the visual integration into the map. This example is provided for JourneyMap, but you might as well take a look at the other possibilities. Since you already implemented the button as a first step, you will need to follow it up with an implementation of `DrawStep`, an interface from JourneyMap. This class will receive an instance of `MyLocation` and perform the actual rendering.

```
class MyDrawStep implements DrawStep {

    private final MyLocation myLocation;
    
    public MyDrawStep(MyLocation myLocation) {
        this.myLocation = myLocation;
    }
    
    @Override
    public void draw(double draggedPixelX, double draggedPixelY, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        final double blockSize = Math.pow(2, gridRenderer.getZoom());
        final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(myLocation.getBlockX(), myLocation.getBlockZ());
        final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + draggedPixelX, blockAsPixel.getY() + draggedPixelY);
        
        DrawUtil.drawLabel(myLocation.getText(), pixel.getX(), pixel.getY(), DrawUtil.HAlign.Center, DrawUtil.VAlign.Middle, 0, 180, 0x00FFFFFF, 255, fontScale, false, rotation);
    }
}
```

Continue with your own implementation of [`LayerRenderer`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/journeymap/render/LayerRenderer.java). This class will cache all `DrawStep`s and provide it to JourneyMap whenever it is time to render.

```
class MyLayerRenderer extends LayerRenderer {

    public MyLayerRenderer() {
        // You may skip MyLayerManager and use an existing ButtonManager like "OreVeinLayerManager.instance"
        // Your custom layer will toggle with whatever button you specify here
        super(MyLayerManager.instance);
    }
    
    @Override
    public List<? extends ClickableDrawStep> mapLocationProviderToDrawStep(List<? extends ILocationProvider> visibleElements) {
        final List<MyDrawStep> drawSteps = new ArrayList<>();
        visibleElements.stream()
                .map(element -> (MyLocation) element)
                .forEach(location -> drawSteps.add(new MyDrawStep(location)));
        return drawSteps;
    }

}
```

That's already it! Now you need to register some of these classes. Forge's postInit is a good place for it:

```
VisualProspecting_API.LogicalClient.registerCustomLayer(MyLayerManager.instance);
VisualProspecting_API.LogicalClient.registerJourneyMapRenderer(new MyLayerRenderer());
```

Now you need to launch Minecraft, teleport to the right sport (`/tp 0 80 0`) and open JourneyMap.

If you want to extend support for your layer in other maps as well you already have half the work done. You just need to add implementations for the layer and element renderer. \
For interactive layers you may take a look at extensions/implementations of [`WaypointProviderManager`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/model/layers/WaypointProviderManager.java) and [`ClickableDrawStep`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/journeymap/drawsteps/ClickableDrawStep.java) to get a head start.

Thank you and happy coding,\
SinTh0r4s
