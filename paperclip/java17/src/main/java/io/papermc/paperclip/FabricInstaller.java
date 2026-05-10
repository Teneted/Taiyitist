/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.papermc.paperclip;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipError;
import java.util.zip.ZipInputStream;

import mjson.Json;
import net.fabricmc.installer.LoaderVersion;
import net.fabricmc.installer.server.ServerInstaller;
import net.fabricmc.installer.util.InstallerProgress;

/**
 * Mainly copied from the net.fabricmc.installer.ServerLauncher class.
 */
public final class FabricInstaller {
    private static final Path DATA_DIR = Paths.get(".fabric", "server");

    public static LaunchData initialize(LoaderVersion loaderVersion, String gameVersion, String bannerVersion) {
        try {
            final Path baseDir = Paths.get(".").toAbsolutePath().normalize();
            final Path dataDir = baseDir.resolve(DATA_DIR);

            // Includes the mc version as this jar contains intermediary
            Path serverLaunchJar = dataDir.resolve(String.format("fabric-loader-server-%s-minecraft-%s-banner-%s.jar", loaderVersion.name, gameVersion, bannerVersion));
            if (Files.exists(serverLaunchJar)) {
                try {
                    List<Path> classPath = new ArrayList<>();
                    String mainClass = readManifest(serverLaunchJar, classPath);
                    boolean allPresent = true;

                    for (Path file : classPath) {
                        if (!Files.exists(file)) {
                            allPresent = false;
                            break;
                        }
                    }

                    if (allPresent) {
                        // All seems good, no need to reinstall
                        return new LaunchData(serverLaunchJar, mainClass);
                    } else {
                        System.err.println("Detected incomplete install, reinstalling");
                    }
                } catch (IOException | ZipError e) {
                    // Wont throw here, will try to reinstall
                    System.err.println("Failed to analyze or verify existing install: " + e.getMessage());
                }
            }

            Files.createDirectories(dataDir);
            ServerInstaller.install(baseDir, loaderVersion, gameVersion, InstallerProgress.CONSOLE, serverLaunchJar);

            String mainClass = readManifest(serverLaunchJar, null);
            return new LaunchData(serverLaunchJar, mainClass);
        } catch (final IOException e) {
            throw new RuntimeException("Something went wrong while installing fabric loader: " + e);
        }
    }

    public static LaunchData initialize() {
        try {
            final Path fabricLoaderOutput = FabricInstaller.extractFabricLoaderToCache();
            final Properties properties = FabricInstaller.readInstallProperties();
            final String gameVersion = properties.getProperty("gameVersion");
            return FabricInstaller.initialize(new net.fabricmc.installer.LoaderVersion(fabricLoaderOutput), gameVersion, properties.getProperty("bannerVersion"));
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to install fabric loader: ", e);
        }
    }

    private static Properties readInstallProperties() {
        final URL resource = FabricInstaller.class.getResource("/banner-install.properties");
        if (resource != null) {
            final Properties properties = new Properties();
            try (final InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream())) {
                properties.load(inputStreamReader);
                return properties;
            } catch (final IOException e) {
                throw new RuntimeException("Something went wrong while reading banner-install.properties: ", e);
            }
        } else {
            throw new RuntimeException("Couldn't find banner-install.properties inside your paperclip jar!");
        }
    }

    // Find the mainclass of a jar file
    private static String readManifest(Path path, List<Path> classPathOut) throws IOException {
        try (JarFile jarFile = new JarFile(path.toFile())) {
            Manifest manifest = jarFile.getManifest();
            String mainClass = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);

            if (mainClass == null) {
                throw new IOException("Jar does not have a Main-Class attribute");
            }

            if (classPathOut != null) {
                String cp = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);

                StringTokenizer tokenizer = new StringTokenizer(cp);
                URL baseUrl = path.toUri().toURL();

                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    URL url = new URL(baseUrl, token);

                    try {
                        classPathOut.add(Paths.get(url.toURI()));
                    } catch (URISyntaxException e) {
                        throw new IOException(String.format("invalid class path entry in %s manifest: %s", path, token));
                    }
                }
            }

            return mainClass;
        }
    }

    /**
     * Extracts fabric loader to the given folder.
     * @param output folder where you want to extract.
     */
    public static Path extractFabricLoader(final Path output) {
        final CodeSource src = FabricInstaller.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            try (final ZipInputStream zipInputStream = new ZipInputStream(jar.openStream())) {
                for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream.getNextEntry()) {
                    if (zipEntry.getName().startsWith("fabric-loader")) {
                        final Path outputFile = output.resolve(zipEntry.getName());
                        try (
                            final ReadableByteChannel inputChannel = Channels.newChannel(zipInputStream);
                            final FileChannel outputChannel = FileChannel.open(outputFile, CREATE, WRITE, TRUNCATE_EXISTING)
                        ) {
                            outputChannel.transferFrom(inputChannel, 0, Long.MAX_VALUE);
                        }
                        return outputFile;
                    }
                }
                throw new IllegalStateException("Didn't find any fabric loader in the paperclip jar!");
            } catch (final Throwable e) {
                throw new IllegalStateException("Couldn't extract fabric loader from resources: ", e);
            }
        } else {
            throw new IllegalStateException("Couldn't extract fabric loader from resources!");
        }
    }

    public static Path extractFabricLoaderToCache() {
        try {
            final Path cache = Path.of(".banner");
            Files.createDirectories(cache);
            return FabricInstaller.extractFabricLoader(cache);
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't extract fabric loader: ", e);
        }
    }

    public static URLClassLoader createFabricLoaderClassLoader(LaunchData launchData) {
        try {
            URL[] urls = {launchData.launchJar().toUri().toURL()};
            return new URLClassLoader(urls);
        } catch (final MalformedURLException e) {
            throw new IllegalStateException("Something went wrong in transforming URL while creating FabricLoader class loader: ", e);
        }
    }

    public static void setupRemappingClasspath(URL[] libraries, LaunchData launchData) {
        final List<File> remapClasspath = new ArrayList<>(Arrays.stream(libraries).map(lib -> {
            try {
                return new File(lib.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Error while setting up remapping classpath: ", e);
            }
        }).toList());
        final List<Path> classPath = new ArrayList<>();
        try {
            readManifest(launchData.launchJar(), classPath);
            remapClasspath.addAll(classPath.stream().map(Path::toFile).toList());
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong while reading server launch jar manifest for the remap classpath", e);
        }
        try {
            remapClasspath.add(new File(Paperclip.versions.stream().findFirst().get().toURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error while setting up remapping classpath: ", e);
        }
        System.setProperty("fabric.remapClasspathFile", remapClasspath.stream().map(File::getAbsolutePath).collect(Collectors.joining(File.pathSeparator)));
    }

    public static void setLibraryURLs(final URL[] libraries) {
        final String s = Json.make(Arrays.stream(libraries).map(url -> {
            try {
                return Path.of(url.toURI()).toAbsolutePath().toString();
            } catch (final Throwable e) {
                throw new RuntimeException("Couldn't setup library URLs for the Fabric loader: ", e);
            }
        }).toList()).toString();
        System.setProperty("banner.libraries", s); // Used in MinecraftGameProvider#unlockClassPath
    }

    /**
     *	Hacks MinecraftGameProvider data to an enum field so FabricLoader launches org.bukkit.craftbukkit.Main class.
     */
	/* In case we'll want to use unmodified fabric loader.
	public static void hackServerEntrypointsForBukkit(URLClassLoader launchClassLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
		final Class<?> mcLibraries = launchClassLoader.loadClass("net.fabricmc.loader.impl.game.minecraft.McLibrary");
		for (final Enum<?> enumConstant : (Enum<?>[]) mcLibraries.getEnumConstants()) {
			if (enumConstant.name().equals("MC_SERVER")) {
				final Field paths = enumConstant.getClass().getDeclaredField("paths");
				paths.setAccessible(true);
				paths.set(enumConstant, SERVER_ENTRYPOINTS);
				break;
			}
		}

		// System.setProperty("fabric.debug.disableClassPathIsolation", "true");
		System.setProperty("fabric.debug.logLibClassification", "true");

	}
	*/

    public record LaunchData(Path launchJar, String mainClass) {}
}
