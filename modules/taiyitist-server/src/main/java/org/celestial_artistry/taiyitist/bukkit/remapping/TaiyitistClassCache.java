package org.celestial_artistry.taiyitist.bukkit.remapping;


import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.celestial_artistry.taiyitist.TaiyitistMCStart;
import org.celestial_artistry.taiyitist.bukkit.remapping.patcher.PluginPatcher;
import org.celestial_artistry.taiyitist.config.TaiyitistConfigUtil;
import io.izzel.tools.product.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarFile;

public abstract class TaiyitistClassCache implements AutoCloseable {

    public abstract CacheSegment makeSegment(URLConnection connection) throws IOException;

    public abstract void save() throws IOException;

    public interface CacheSegment {

        Optional<byte[]> findByName(String name, TaiyitistRemapConfig config) throws IOException;

        void addToCache(String name, byte[] value, TaiyitistRemapConfig config);

        void save() throws IOException;
    }

    private static final Marker MARKER = MarkerManager.getMarker("CLCACHE");
    private static final TaiyitistClassCache INSTANCE = new Impl();

    public static TaiyitistClassCache instance() {
        return INSTANCE;
    }

    private static class Impl extends TaiyitistClassCache {

        private static final int SPEC_VERSION = 3;
        private static final boolean ENABLED = TaiyitistConfigUtil.isCachePluginClass();

        private final ConcurrentHashMap<String, String> nameToHash = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, JarSegment> hashToSegment = new ConcurrentHashMap<>();

        private final Path basePath = Paths.get(".taiyitist/class_cache");
        private ScheduledExecutorService executor;

        private static String currentVersionInfo() {
            var builder = new StringBuilder();
            builder.append("Taiyitist class cache").append(", ");
            builder.append("spec=").append(SPEC_VERSION).append(", ");
            var taiyitist = TaiyitistClassCache.class.getPackage().getImplementationVersion();
            builder.append("taiyitist=").append(taiyitist).append(", ");
            builder.append("patcher=[");
            for (PluginPatcher patcher : TaiyitistRemapper.INSTANCE.getPatchers()) {
                builder.append('\0')
                        .append(patcher.getClass().getName())
                        .append('\0')
                        .append(patcher.version())
                        .append(", ");
            }
            builder.append("]");
            return builder.toString();
        }

        public Impl() {
            if (!ENABLED) return;
            executor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setName("taiyitist class cache saving thread");
                thread.setDaemon(true);
                return thread;
            });
            executor.scheduleWithFixedDelay(() -> {
                try {
                    this.save();
                } catch (IOException e) {
                    TaiyitistMCStart.LOGGER.error(MARKER, "Failed to save class cache", e);
                }
            }, 1, 10, TimeUnit.MINUTES);
            try {
                if (Files.isRegularFile(basePath)) {
                    Files.delete(basePath);
                }
                if (!Files.isDirectory(basePath)) {
                    Files.createDirectories(basePath);
                }
                String current = currentVersionInfo();
                String store;
                Path version = basePath.resolve(".version");
                if (Files.exists(version)) {
                    store = Files.readString(version);
                } else {
                    store = null;
                }
                boolean obsolete = !Objects.equals(current, store);
                Path index = basePath.resolve("index");
                if (obsolete) {
                    FileUtils.deleteDirectory(index.toFile());
                }
                if (!Files.exists(index)) {
                    Files.createDirectories(index);
                }
                Path blob = basePath.resolve("blob");
                if (obsolete) {
                    FileUtils.deleteDirectory(blob.toFile());
                }
                if (!Files.exists(blob)) {
                    Files.createDirectories(blob);
                }
                if (obsolete) {
                    Files.deleteIfExists(version);
                    Files.writeString(version, current, StandardOpenOption.CREATE);
                    TaiyitistMCStart.LOGGER.info(MARKER, "Obsolete plugin class cache is cleared");
                }
            } catch (IOException e) {
                TaiyitistMCStart.LOGGER.error(MARKER, "Failed to initialize class cache", e);
            }
            Thread thread = new Thread(() -> {
                try {
                    this.close();
                } catch (Exception e) {
                    TaiyitistMCStart.LOGGER.error(MARKER, "Failed to close class cache", e);
                }
            }, "taiyitist class cache cleanup");
            thread.setDaemon(true);
            Runtime.getRuntime().addShutdownHook(thread);
        }

        private String calculateHash(String fileName) throws IOException {
            Path jarFile = new File(fileName).toPath();
            Hasher hasher = Hashing.sha256().newHasher();
            hasher.putBytes(Files.readAllBytes(jarFile));
            String hash = hasher.hash().toString();
            return hash;
        }

        private String acquireHashC(String fileName) {
            return nameToHash.computeIfAbsent(fileName, n -> {
                try {
                    return calculateHash(n);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        private JarSegment acquireSegmentC(String hash) {
            return hashToSegment.computeIfAbsent(hash, k -> {
                try {
                    return new JarSegment(k);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Override
        public CacheSegment makeSegment(URLConnection connection) throws IOException {
            if (ENABLED && connection instanceof JarURLConnection) {
                final JarFile file = ((JarURLConnection) connection).getJarFile();
                final String hash = acquireHashC(file.getName());
                return acquireSegmentC(hash);
            } else {
                return EmptySegment.INSTANCE;
            }
        }

        @Override
        public void save() throws IOException {
            if (ENABLED) {
                try {
                    hashToSegment.forEach((k, v) -> {
                        try {
                            v.save();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (RuntimeException e) {
                    if (e.getCause() instanceof IOException io) {
                        throw io;
                    }
                    throw e;
                }
            }
        }

        @Override
        public void close() throws Exception {
            if (ENABLED) {
                save();
                executor.shutdownNow();
            }
        }

        private class JarSegment implements CacheSegment {

            private final Map<String, Product3<Long, Integer, TaiyitistRemapConfig>> rangeMap = new ConcurrentHashMap<>();
            private final ConcurrentLinkedQueue<Product5<String, byte[], Long, Integer, TaiyitistRemapConfig>> savingQueue = new ConcurrentLinkedQueue<>();
            private final AtomicLong sizeAllocator;
            private final Path indexPath, blobPath;

            private JarSegment(String hash) throws IOException {
                this.indexPath = basePath.resolve("index").resolve(hash);
                this.blobPath = basePath.resolve("blob").resolve(hash);
                if (!Files.exists(indexPath)) {
                    Files.createFile(indexPath);
                }
                if (!Files.exists(blobPath)) {
                    Files.createFile(blobPath);
                }
                sizeAllocator = new AtomicLong(Files.size(blobPath));
                read();
            }

            @Override
            public Optional<byte[]> findByName(String name, TaiyitistRemapConfig config) throws IOException {
                Product3<Long, Integer, TaiyitistRemapConfig> product = rangeMap.get(name);
                if (product != null) {
                    long off = product._1;
                    int len = product._2;
                    var cfg = product._3;
                    if (!cfg.equals(config)) {
                        return Optional.empty();
                    }
                    try (SeekableByteChannel channel = Files.newByteChannel(blobPath)) {
                        channel.position(off);
                        ByteBuffer buffer = ByteBuffer.allocate(len);
                        channel.read(buffer);
                        return Optional.of(buffer.array());
                    }
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public void addToCache(String name, byte[] value, TaiyitistRemapConfig config) {
                int len = value.length;
                long off = sizeAllocator.getAndAdd(len);
                savingQueue.add(Product.of(name, value, off, len, config.copy()));
            }

            @Override
            public void save() throws IOException {
                if (savingQueue.isEmpty()) return;
                List<Product5<String, byte[], Long, Integer, TaiyitistRemapConfig>> list = new ArrayList<>();
                while (!savingQueue.isEmpty()) {
                    list.add(savingQueue.poll());
                }
                try (OutputStream outIndex = Files.newOutputStream(indexPath, StandardOpenOption.APPEND);
                     DataOutputStream dataOutIndex = new DataOutputStream(outIndex);
                     SeekableByteChannel channel = Files.newByteChannel(blobPath, StandardOpenOption.WRITE)) {
                    for (Product5<String, byte[], Long, Integer, TaiyitistRemapConfig> product : list) {
                        channel.position(product._3);
                        channel.write(ByteBuffer.wrap(product._2));
                        dataOutIndex.writeUTF(product._1);
                        dataOutIndex.writeLong(product._3);
                        dataOutIndex.writeInt(product._4);
                        product._5.write(dataOutIndex);
                        rangeMap.put(product._1, Product.of(product._3, product._4, product._5));
                    }
                }
            }

            private void read() throws IOException {
                try (InputStream inputStream = Files.newInputStream(indexPath);
                     DataInputStream dataIn = new DataInputStream(inputStream)) {
                    while (dataIn.available() > 0) {
                        String name = dataIn.readUTF();
                        long off = dataIn.readLong();
                        int len = dataIn.readInt();
                        var cfg = TaiyitistRemapConfig.read(dataIn);
                        rangeMap.put(name, Product.of(off, len, cfg));
                    }
                }
            }
        }

        private static class EmptySegment implements CacheSegment {

            public static final EmptySegment INSTANCE = new EmptySegment();

            @Override
            public Optional<byte[]> findByName(String name, TaiyitistRemapConfig config) {
                return Optional.empty();
            }

            @Override
            public void addToCache(String name, byte[] value, TaiyitistRemapConfig config) {
            }

            @Override
            public void save() {
            }
        }
    }
}