package org.celestial_artistry.taiyitist;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.util.Arguments;

public class TaiyitistGameProvider extends MinecraftGameProvider {

    private Path modFile;

    @Override
    public void initialize(FabricLauncher launcher) {
        System.setProperty("log4j.configurationFile", "log4j2_taiyitist.xml");

        try {
            this.modFile = this.extract();
            launcher.addToClassPath(modFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            extractBootstrap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (var lib : System.getProperty("taiyitist.fabric.classpath").split(File.pathSeparator)) {
            launcher.addToClassPath(Paths.get(lib));
        }
        loadBootstrap(launcher);
        loadCustomLibs(launcher);
        super.initialize(launcher);
    }

    @Override
    public Arguments getArguments() {
        Arguments arguments = super.getArguments();
        String old = arguments.get(Arguments.ADD_MODS);
        var builtinMods = System.getProperty("taiyitist.fabric.builtinMods");
        var path = this.modFile.toString() + File.pathSeparator + builtinMods;
        if (old != null) {
            path = old + File.pathSeparator + path;
        }
        arguments.put(Arguments.ADD_MODS, path);
        arguments.addExtraArg("nogui");
        arguments.addExtraArg("chcp 65001");
        return arguments;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void unlockClassPath(FabricLauncher launcher) {
        super.unlockClassPath(launcher);
        try {
            var field = launcher.getClass().getDeclaredField("unlocked");
            field.setAccessible(true);
            field.set(launcher, true);
            var ctor = launcher.loadIntoTarget("org.celestial_artistry.taiyitist.boot.FabricBootstrap").getConstructor();
            ((Consumer<FabricLauncher>) ctor.newInstance()).accept(launcher);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getTaiyitistVersion() throws Exception {
        try (var stream = getClass().getResourceAsStream("/META-INF/MANIFEST.MF")) {
            var manifest = new Manifest(stream);
            var attributes = manifest.getMainAttributes();
            return attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        }
    }

    private Path extract() throws Exception {
        var version = getTaiyitistVersion();
        System.setProperty("taiyitist.version", version);
        var path = getClass().getModule().getResourceAsStream("/META-INF/jars/taiyitist-" + getTaiyitistVersion() + ".jar");
        var dir = Paths.get(".taiyitist", "mod_file");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        var mod = dir.resolve(version + ".jar");
        if (!Files.exists(mod) || Boolean.getBoolean("taiyitist.alwaysExtract")) {
            try (var files = Files.list(dir)) {
                for (Path old : files.toList()) {
                    Files.delete(old);
                }
                Files.copy(path, mod);
            }
        }
        return mod;
    }

    private Path extractBootstrap() throws Exception {
        var path = getClass().getModule().getResourceAsStream("/META-INF/jars/taiyitist-bootstrap-" + getTaiyitistVersion() + ".jar");
        var dir = Paths.get(".taiyitist", "bootstrap");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        var bootstrap = dir.resolve("taiyitist-bootstrap-" + getTaiyitistVersion() + ".jar");
        if (!Files.exists(bootstrap) || Boolean.getBoolean("taiyitist.alwaysExtract")) {
            try (var files = Files.list(dir)) {
                for (Path old : files.toList()) {
                    Files.delete(old);
                }
                Files.copy(path, bootstrap);
            }
        }
        return bootstrap;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getRawGameVersion() {
        try {
            return super.getRawGameVersion() + " Taiyitist " + getTaiyitistVersion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCustomLibs(FabricLauncher launcher) {
        File file = new File("libraries/customize_libraries");
        if (!file.exists()) {
            file.mkdirs();
        }

        for (File lib : file.listFiles((dir, name) -> name.endsWith(".jar"))) {
            launcher.addToClassPath(Paths.get(lib.toURI()));
            System.out.println(lib.getName() + " custom library loaded successfully.");
        }
    }

    private void loadBootstrap(FabricLauncher launcher) {
        try {
            File bootstrap = new File(".taiyitist", "bootstrap/" + "taiyitist-bootstrap-" + getTaiyitistVersion() + ".jar");
            launcher.addToClassPath(Paths.get(bootstrap.toURI()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
