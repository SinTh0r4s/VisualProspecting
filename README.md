# GregTech5U Add-On: VisualProspecting

[![](https://jitpack.io/v/SinTh0r4s/VisualProspecting.svg)](https://jitpack.io/#SinTh0r4s/VisualProspecting)
[![](https://github.com/SinTh0r4s/VisualProspecting/actions/workflows/gradle.yml/badge.svg)](https://github.com/SinTh0r4s/VisualProspecting/actions/workflows/gradle.yml)

### For Minecraft 1.7.10

This mod is intended for player convenience, but may also be used as API, since it provides the location of all GT ore veins in a cache. VisualProspecting tracks all GT Ore Veins a player has found and visualizes them in JourneyMap (optional, if installed). It also visualizes tracked Thaumcraft aura nodes if TCNodeTracker if installed.

VisualProspecting tracks all ores that a player interacted with, by right or by left click. It also integrates prospecting data from GTs _Advanced Seismic Prospector_, although only books that are created after this mod was added will provide integration.
You may share your findings with other players by crafting a _Prospector's Log_.

This mod is tailored to _GregTech: New Horizons 2_, but feel free to use it however you like. Even though this mod is build against the custom GT5U from GT:NH, it should still work fine with other GT5U versions.

![Underground fluids in JourneyMap overlay](https://i.ibb.co/crPhR1X/2021-10-12-15-45-25.png) \
_Underground fluids in JourneyMap overlay._

![GregTech ore veins in JourneyMap overlay](https://i.ibb.co/G5KLGjQ/2021-10-20-01-16-57.png) \
_GregTech ore veins in JourneyMap overlay. You may double-click an ore vein to toggle it as waypoint._

![Thaumcraft aura nodes in JourneyMap overlay](https://i.ibb.co/WDk41qd/2021-10-25-15-01-11.png) \
_Thaumcraft aura nodes in JourneyMap overlay. You may double-click an aura node to toggle it as waypoint._

### Reset Progress

You may use JourneyMap's Actions Menu to achieve this or type `/visualprospectingresetprogress` in chat. Beware, there are no backups! Please use at your own risk.

### Other Maps

Does VisualProspecting run with other maps? - I runs just fine, but it has no visualization or GUI integration. If you like to add integration into other maps yourself, feel free to contact me or open a Pull Request.

### Dependencies

#### Required Mods:
 - Minecraft Forge
    - Injected class: [_ItemEditableBook_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/ItemEditableBookMixin.java)
 - GregTech5-Unofficial
    - Injected classes: [_GT_Block_Ores_Abstract_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/GT_Block_Ores_AbstractMixin.java), [_GT_MetaTileEntity_AdvSeismicProspector_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/GT_MetaTileEntity_AdvSeismicProspectorMixin.java), [_GT_MetaTileEntity_Scanner_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/GT_MetaTileEntity_ScannerMixin.java), [_GT_Worldgenerator.WorldGenContainer_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/WorldGenContainerMixin.java)
 - SpongeMixin
 - [Enklumne](https://github.com/Hugobros3/Enklume) _by Hugobros3_
    - Automatically shipped. No manual handling is required.
#### Optional Mods:
 - JourneyMap: Visualizes prospected ore veins and oil fields on custom overlay, that can be toggled on and off.
    - Injected classes: [_Fullscreen_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/FullscreenMixin.java), [_FullscreenActions_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/FullscreenActionsMixin.java), [_RenderWaypointBeacon_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/RenderWaypointBeaconMixin.java), [_WaypointManager_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/WaypointManagerMixin.java)
 - TCNodeTracker: Visualizes tracked aura nodes in JourneyMap. Requires JourneyMap  
    - Injected class: [_GuiMain_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/journeymap/tcnodetracker/GuiMainMixin.java)
 - NEI: Ores on JourneyMap are highlighted according to NEI search if active (double click on search field).
 - GalacticGreg: Injects a notification call into ore vein generation.
    - Injected class: [_GT_Worldgenerator_Space_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/galacticgreg/GT_Worldgenerator_SpaceMixin.java)
 - Bartworks: Injects a notification call into ore vein generation.
    - Injected class: [_BW_WordGenerator.WorldGenContainer_](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/mixins/bartworks/WorldGenContainerMixin.java)

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

VisualProspecting provides a light-weight API for custom and interactive layers in JourneyMap. This API will keep JourneyMap as optional mod at runtime and not crash you game if it is missing. You may overwrite [`LayerButton`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/journeymap/buttons/LayerButton.java) to create your own button in JourneyMap:

```
class MyLayerButton extends LayerButton {

    // You need to provide a texture in "assets\journeymap\icon\theme\Vault\icon/textureName.png"
    // and "assets\journeymap\icon\theme\Victorian\icon/textureName.png"
    public static final MyLayerButton instance = new MyLayerButton("hover.text.translation.key", "textureName");
    
    public MyLayerButton(String buttonTextKey, String iconName) {
        super(buttonTextKey, iconName);
    }
}
```

Now it is already time to implement the actual render unit. You can implement `DrawStep` to get started. `DrawStep` is an interface from JourneyMap:

```
class MyDrawStep implements DrawStep {

    private final String text;
    private final int blockX;
    private final int blockZ;
    
    public MyDrawStep(String text, int blockX, int blockZ) {
        this.text = text;
        this.blockX = blockX;
        this.blockZ = blockZ;
    }
    
    @Override
    public void draw(double draggedPixelX, double draggedPixelY, GridRenderer gridRenderer, float drawScale, double fontScale, double rotation) {
        final double blockSize = Math.pow(2, gridRenderer.getZoom());
        final Point2D.Double blockAsPixel = gridRenderer.getBlockPixelInGrid(blockX, blockZ);
        final Point2D.Double pixel = new Point2D.Double(blockAsPixel.getX() + draggedPixelX, blockAsPixel.getY() + draggedPixelY);
        
        DrawUtil.drawLabel(text, pixel.getX(), pixel.getY(), DrawUtil.HAlign.Center, DrawUtil.VAlign.Middle, 0, 180, 0x00FFFFFF, 255, fontScale, false, rotation);
    }
}
```

Continue with your own implementation of [`InformationLayer`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/journeymap/layers/InformationLayer.java):

```
class MyLayer extends InformationLayer {

    public MyLayer() {
        // You may skip MyLayerButton and use an existing Button like "OreVeinButton.instance"
        super(MyLayerButton.instance);
    }
    
    @Override
    protected List<DrawStep> generateDrawSteps(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        return Collections.singletonList(new MyDrawStep("Hello World", 0, 0);
    }

}
```

That's already it! Now you need to register both, `MyLayerButton` and `MyLayer`, in Forge's postInit:

```
VisualProspecting_API.LogicalClient.registerLayerButton(MyLayerButton.instance);
VisualProspecting_API.LogicalClient.registerLayer(new MyLayer());
```

Now you need to launch Minecraft, teleport to the right sport (`/tp 0 80 0`) and open JourneyMap.

For interactive layers you may take a look at extensions/implementations of [`WaypointProviderLayer`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/journeymap/layers/WaypointProviderLayer.java) and [`ClickableDrawStep`](https://github.com/SinTh0r4s/VisualProspecting/blob/master/src/main/java/com/sinthoras/visualprospecting/gui/journeymap/drawsteps/ClickableDrawStep.java).

Thank you and happy coding,\
SinTh0r4s
