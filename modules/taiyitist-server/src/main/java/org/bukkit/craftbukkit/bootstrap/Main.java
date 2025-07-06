package org.bukkit.craftbukkit.bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
   public static void main(String[] argv) {
      (new Main()).run(argv);
   }

   private void run(String[] argv) {
      try {
         String defaultMainClassName = (String)this.readResource("main-class", BufferedReader::readLine);
         String mainClassName = System.getProperty("bundlerMainClass", defaultMainClassName);
         String repoDir = System.getProperty("bundlerRepoDir", "bundler");
         Path outputDir = Paths.get(repoDir).toAbsolutePath();
         if (!Files.isDirectory(outputDir, new LinkOption[0])) {
            Files.createDirectories(outputDir);
         }

         System.out.println("Unbundling libraries to " + String.valueOf(outputDir));
         boolean readOnly = Boolean.getBoolean("bundlerReadOnly");
         List<URL> extractedUrls = new ArrayList();
         this.readAndExtractDir("versions", outputDir, extractedUrls, readOnly);
         this.readAndExtractDir("libraries", outputDir, extractedUrls, readOnly);
         if (mainClassName == null || mainClassName.isEmpty()) {
            System.out.println("Empty main class specified, exiting");
            System.exit(0);
         }

         URLClassLoader classLoader = new URLClassLoader((URL[])extractedUrls.toArray(new URL[0]));
         System.out.println("Starting server");
         Thread runThread = new Thread(() -> {
            try {
               Class<?> mainClass = Class.forName(mainClassName, true, classLoader);
               MethodHandle mainHandle = MethodHandles.lookup().findStatic(mainClass, "main", MethodType.methodType(Void.TYPE, String[].class)).asFixedArity();
               mainHandle.invoke(argv);
            } catch (Throwable var5) {
               Throwable t = var5;
               Main.Thrower.INSTANCE.sneakyThrow(t);
            }

         }, "ServerMain");
         runThread.setContextClassLoader(classLoader);
         runThread.start();
      } catch (Exception var10) {
         Exception e = var10;
         e.printStackTrace(System.out);
         System.out.println("Failed to extract server libraries, exiting");
      }

   }

   private <T> T readResource(String resource, ResourceParser<T> parser) throws Exception {
      String fullPath = "/META-INF/" + resource;
      InputStream is = this.getClass().getResourceAsStream(fullPath);

      Object var5;
      try {
         if (is == null) {
            throw new IllegalStateException("Resource " + fullPath + " not found");
         }

         var5 = parser.parse(new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)));
      } catch (Throwable var8) {
         if (is != null) {
            try {
               is.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (is != null) {
         is.close();
      }

      return var5;
   }

   private void readAndExtractDir(String subdir, Path outputDir, List<URL> extractedUrls, boolean readOnly) throws Exception {
      List<FileEntry> entries = (List)this.readResource(subdir + ".list", (reader) -> {
         return reader.lines().map(FileEntry::parseLine).toList();
      });
      Path subdirPath = outputDir.resolve(subdir);
      Iterator var7 = entries.iterator();

      while(var7.hasNext()) {
         FileEntry entry = (FileEntry)var7.next();
         if (!entry.path.startsWith("minecraft-server")) {
            Path outputFile = subdirPath.resolve(entry.path);
            if (!readOnly) {
               this.checkAndExtractJar(subdir, entry, outputFile);
            }

            extractedUrls.add(outputFile.toUri().toURL());
         }
      }

   }

   private void checkAndExtractJar(String subdir, FileEntry entry, Path outputFile) throws Exception {
      if (!Files.exists(outputFile, new LinkOption[0]) || !checkIntegrity(outputFile, entry.hash())) {
         System.out.printf("Unpacking %s (%s:%s) to %s%n", entry.path, subdir, entry.id, outputFile);
         this.extractJar(subdir, entry.path, outputFile);
      }

   }

   private void extractJar(String subdir, String jarPath, Path outputFile) throws IOException {
      Files.createDirectories(outputFile.getParent());
      InputStream input = this.getClass().getResourceAsStream("/META-INF/" + subdir + "/" + jarPath);

      try {
         if (input == null) {
            throw new IllegalStateException("Declared library " + jarPath + " not found");
         }

         Files.copy(input, outputFile, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
      } catch (Throwable var8) {
         if (input != null) {
            try {
               input.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (input != null) {
         input.close();
      }

   }

   private static boolean checkIntegrity(Path file, String expectedHash) throws Exception {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      InputStream output = Files.newInputStream(file);

      boolean var5;
      label43: {
         try {
            output.transferTo(new DigestOutputStream(OutputStream.nullOutputStream(), digest));
            String actualHash = byteToHex(digest.digest());
            if (actualHash.equalsIgnoreCase(expectedHash)) {
               var5 = true;
               break label43;
            }

            System.out.printf("Expected file %s to have hash %s, but got %s%n", file, expectedHash, actualHash);
         } catch (Throwable var7) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (output != null) {
            output.close();
         }

         return false;
      }

      if (output != null) {
         output.close();
      }

      return var5;
   }

   private static String byteToHex(byte[] bytes) {
      StringBuilder result = new StringBuilder(bytes.length * 2);
      byte[] var2 = bytes;
      int var3 = bytes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte b = var2[var4];
         result.append(Character.forDigit(b >> 4 & 15, 16));
         result.append(Character.forDigit(b >> 0 & 15, 16));
      }

      return result.toString();
   }

   @FunctionalInterface
   private interface ResourceParser<T> {
      T parse(BufferedReader var1) throws Exception;
   }

   private static record FileEntry(String hash, String id, String path) {
      private FileEntry(String hash, String id, String path) {
         this.hash = hash;
         this.id = id;
         this.path = path;
      }

      public static FileEntry parseLine(String line) {
         String[] fields = line.split(" ");
         if (fields.length != 2) {
            throw new IllegalStateException("Malformed library entry: " + line);
         } else {
            String path = fields[1].substring(1);
            return new FileEntry(fields[0], path, path);
         }
      }

      public String hash() {
         return this.hash;
      }

      public String id() {
         return this.id;
      }

      public String path() {
         return this.path;
      }
   }

   private static class Thrower<T extends Throwable> {
      private static final Thrower<RuntimeException> INSTANCE = new Thrower();

      public void sneakyThrow(Throwable exception) throws T {
         throw exception;
      }
   }
}
