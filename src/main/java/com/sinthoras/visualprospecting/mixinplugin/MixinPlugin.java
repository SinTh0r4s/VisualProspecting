package com.sinthoras.visualprospecting.mixinplugin;

import com.google.common.collect.Lists;
import com.sinthoras.visualprospecting.VP;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import ru.timeconqueror.spongemixins.MinecraftURLClassPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        final boolean developerEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        if(developerEnvironment) {
            return Lists.newArrayList(
                    "GT_Block_Ores_AbstractMixin",
                    "GT_MetaTileEntity_AdvSeismicProspectorMixin",
                    "GT_MetaTileEntity_ScannerMixin",
                    "ItemEditableBookMixin",
                    "WorldGenContainerMixin",
                    "journeymap.FullscreenMixin"
            );
        }
        else {
            if (loadJar("gregtech") == false) {
                VP.error("Could not load gregtech's jar!");
                return null;
            }
            List<String> mixins = Lists.newArrayList(
                    "GT_Block_Ores_AbstractMixin",
                    "GT_MetaTileEntity_AdvSeismicProspectorMixin",
                    "GT_MetaTileEntity_ScannerMixin",
                    "ItemEditableBookMixin",
                    "WorldGenContainerMixin"
            );
            if (loadJar("journeymap-1.7.10")) {
                VP.info("Found JourneyMap! Integrating now...");
                mixins.add("journeymap.FullscreenMixin");
            } else {
                VP.info("Could not find JourneyMap! Skipping integration....");
            }
            return mixins;
        }
    }

    public boolean loadJar(final String jarName) {
        try {
            File jar = MinecraftURLClassPath.getJarInModPath(jarName);
            if(jar == null) {
                VP.info("Jar not found: " + jarName);
                return false;
            }

            VP.info("Attempting to add " + jar.toString() + " to the URL Class Path");
            if(!jar.exists()) {
                throw new FileNotFoundException(jar.toString());
            }
            MinecraftURLClassPath.addJar(jar);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
