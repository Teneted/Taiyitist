package org.bukkit.craftbukkit.map;

import com.google.common.base.Preconditions;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.Util;
import org.bukkit.map.MapPalette;

public class CraftMapColorCache implements MapPalette.MapColorCache {
   private static final String MD5_CACHE_HASH = "E88EDD068D12D39934B40E8B6B124C83";
   private static final File CACHE_FILE = new File("map-color-cache.dat");
   private byte[] cache;
   private final Logger logger;
   private boolean cached = false;
   private final AtomicBoolean running = new AtomicBoolean(false);

   public CraftMapColorCache(Logger logger) {
      this.logger = logger;
   }

   public static void main(String[] args) {
      CraftMapColorCache craftMapColorCache = new CraftMapColorCache(Logger.getGlobal());
      craftMapColorCache.buildCache();

      try {
         byte[] hash = MessageDigest.getInstance("MD5").digest(craftMapColorCache.cache);
         System.out.println("MD5_CACHE_HASH: " + bytesToString(hash));
      } catch (NoSuchAlgorithmException var3) {
         NoSuchAlgorithmException e = var3;
         e.printStackTrace();
      }

   }

   public static String bytesToString(byte[] bytes) {
      char[] chars = "0123456789ABCDEF".toCharArray();
      StringBuilder builder = new StringBuilder();
      byte[] var3 = bytes;
      int var4 = bytes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte value = var3[var5];
         int first = (value & 240) >> 4;
         int second = value & 15;
         builder.append(chars[first]);
         builder.append(chars[second]);
      }

      return builder.toString();
   }

   public CompletableFuture<Void> initCache() {
      Preconditions.checkState(!this.cached && !this.running.getAndSet(true), "Cache is already build or is currently being build");
      this.cache = new byte[16777216];
      if (CACHE_FILE.exists()) {
         byte[] fileContent;
         try {
            InputStream inputStream = new InflaterInputStream(new FileInputStream(CACHE_FILE));

            try {
               fileContent = ((InputStream)inputStream).readAllBytes();
            } catch (Throwable var7) {
               try {
                  ((InputStream)inputStream).close();
               } catch (Throwable var5) {
                  var7.addSuppressed(var5);
               }

               throw var7;
            }

            ((InputStream)inputStream).close();
         } catch (IOException var8) {
            IOException e = var8;
            this.logger.warning("Error while reading map color cache");
            e.printStackTrace();
            return CompletableFuture.completedFuture((Object)null);
         }

         byte[] hash;
         try {
            hash = MessageDigest.getInstance("MD5").digest(fileContent);
         } catch (NoSuchAlgorithmException var6) {
            NoSuchAlgorithmException e = var6;
            this.logger.warning("Error while hashing map color cache");
            e.printStackTrace();
            return CompletableFuture.completedFuture((Object)null);
         }

         if (!"E88EDD068D12D39934B40E8B6B124C83".equals(bytesToString(hash))) {
            this.logger.info("Map color cache hash invalid, rebuilding cache in the background");
            return this.buildAndSaveCache();
         } else {
            System.arraycopy(fileContent, 0, this.cache, 0, fileContent.length);
            this.cached = true;
            return CompletableFuture.completedFuture((Object)null);
         }
      } else {
         this.logger.info("Map color cache not found, building it in the background");
         return this.buildAndSaveCache();
      }
   }

   private void buildCache() {
      for(int r = 0; r < 256; ++r) {
         for(int g = 0; g < 256; ++g) {
            for(int b = 0; b < 256; ++b) {
               Color color = new Color(r, g, b);
               this.cache[this.toInt(color)] = MapPalette.matchColor(color);
            }
         }
      }

   }

   private CompletableFuture<Void> buildAndSaveCache() {
      return CompletableFuture.runAsync(() -> {
         this.buildCache();
         IOException e;
         if (!CACHE_FILE.exists()) {
            try {
               if (!CACHE_FILE.createNewFile()) {
                  this.cached = true;
                  return;
               }
            } catch (IOException var7) {
               e = var7;
               this.logger.warning("Error while building map color cache");
               e.printStackTrace();
               this.cached = true;
               return;
            }
         }

         try {
            OutputStream outputStream = new DeflaterOutputStream(new FileOutputStream(CACHE_FILE));

            try {
               ((OutputStream)outputStream).write(this.cache);
            } catch (Throwable var5) {
               try {
                  ((OutputStream)outputStream).close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }

               throw var5;
            }

            ((OutputStream)outputStream).close();
         } catch (IOException var6) {
            e = var6;
            this.logger.warning("Error while building map color cache");
            e.printStackTrace();
            this.cached = true;
            return;
         }

         this.cached = true;
         this.logger.info("Map color cache build successfully");
      }, Util.backgroundExecutor());
   }

   private int toInt(Color color) {
      return color.getRGB() & 16777215;
   }

   public boolean isCached() {
      return this.cached || !this.running.get() && this.initCache().isDone();
   }

   public byte matchColor(Color color) {
      Preconditions.checkState(this.isCached(), "Cache not build jet");
      return this.cache[this.toInt(color)];
   }
}
