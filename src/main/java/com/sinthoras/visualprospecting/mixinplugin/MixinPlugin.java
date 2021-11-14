package com.sinthoras.visualprospecting.mixinplugin;

import static com.sinthoras.visualprospecting.mixinplugin.TargetedMod.VANILLA;
import static java.nio.file.Files.walk;

import com.google.common.io.Files;
import com.sinthoras.visualprospecting.Tags;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import ru.timeconqueror.spongemixins.MinecraftURLClassPath;

public class MixinPlugin implements IMixinConfigPlugin {

    private static final Logger LOG = LogManager.getLogger(Tags.MODID + " mixins");
    private static final Path MODS_DIRECTORY_PATH = new File(Launch.minecraftHome, "mods/").toPath();

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

    // This method return a List<String> of mixins. Every mixins in this list will be loaded.
    @Override
    public List<String> getMixins() {
        final boolean isDevelopmentEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        List<TargetedMod> loadedMods = Arrays.stream(TargetedMod.values())
                .filter(mod -> mod == VANILLA
                        || (mod.loadInDevelopment && isDevelopmentEnvironment)
                        || attemptLoadingJar(mod.jarNamePrefix))
                .collect(Collectors.toList());
        for (TargetedMod mod : TargetedMod.values()) {
            if(loadedMods.contains(mod)) {
                LOG.info("Found " + mod.modName + "! Integrating now...");
            }
            else {
                LOG.info("Could not find " + mod.modName + "! Skipping integration....");
            }
        }

        List<String> mixins = new ArrayList<>();
        for (Mixin mixin : Mixin.values()) {
            if (mixin.shouldLoad(loadedMods)) {
                mixins.add(mixin.mixinClass);
                LOG.debug("Loading mixin: " + mixin.mixinClass);
            }
        }
        return mixins;
    }

    private boolean attemptLoadingJar(final String namePrefix) {
        try {
            File jar = findJarInModPathBy(namePrefix);
            if(jar == null) {
                LOG.info("Jar not found: " + namePrefix);
                return false;
            }

            LOG.info("Attempting to add " + jar + " to the URL Class Path");
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

    public static File findJarInModPathBy(final String namePrefix) {
        try {
            return walk(MODS_DIRECTORY_PATH)
                .filter(isJarWithIgnoringCase(namePrefix))
                .map(Path::toFile)
                .findFirst()
                .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Predicate<Path> isJarWithIgnoringCase(String namePrefix) {
        final String namePrefixLowerCase = namePrefix.toLowerCase();
        return (Path path) -> {
            final String pathString = path.toString();
            final String nameLowerCase = Files.getNameWithoutExtension(pathString).toLowerCase();
            final String fileExtension = Files.getFileExtension(pathString);
            return nameLowerCase.startsWith(namePrefixLowerCase) && "jar".equals(fileExtension);
        };
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
