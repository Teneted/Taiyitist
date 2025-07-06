package org.bukkit.craftbukkit.v1_21_R5.structure;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftNamespacedKey;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;

public class CraftStructureManager implements StructureManager {
   private final StructureTemplateManager structureManager;
   private final RegistryAccess registry;

   public CraftStructureManager(StructureTemplateManager structureManager, RegistryAccess registry) {
      this.structureManager = structureManager;
      this.registry = registry;
   }

   public Map<NamespacedKey, Structure> getStructures() {
      Map<NamespacedKey, Structure> cachedStructures = new HashMap();
      Iterator var2 = this.structureManager.structureRepository.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<ResourceLocation, Optional<StructureTemplate>> entry = (Map.Entry)var2.next();
         ((Optional)entry.getValue()).ifPresent((definedStructure) -> {
            cachedStructures.put(CraftNamespacedKey.fromMinecraft((ResourceLocation)entry.getKey()), new CraftStructure(definedStructure, this.registry));
         });
      }

      return Collections.unmodifiableMap(cachedStructures);
   }

   public Structure getStructure(NamespacedKey structureKey) {
      Preconditions.checkArgument(structureKey != null, "NamespacedKey structureKey cannot be null");
      Optional<StructureTemplate> definedStructure = (Optional)this.structureManager.structureRepository.get(CraftNamespacedKey.toMinecraft(structureKey));
      return definedStructure == null ? null : (Structure)definedStructure.map((s) -> {
         return new CraftStructure(s, this.registry);
      }).orElse((Object)null);
   }

   public Structure loadStructure(NamespacedKey structureKey, boolean register) {
      ResourceLocation minecraftKey = this.createAndValidateMinecraftStructureKey(structureKey);
      Optional<StructureTemplate> structure = (Optional)this.structureManager.structureRepository.get(minecraftKey);
      structure = structure == null ? Optional.empty() : structure;
      structure = structure.isPresent() ? structure : this.structureManager.loadFromGenerated(minecraftKey);
      structure = structure.isPresent() ? structure : this.structureManager.loadFromResource(minecraftKey);
      if (register) {
         this.structureManager.structureRepository.put(minecraftKey, structure);
      }

      return (Structure)structure.map((s) -> {
         return new CraftStructure(s, this.registry);
      }).orElse((Object)null);
   }

   public Structure loadStructure(NamespacedKey structureKey) {
      return this.loadStructure(structureKey, true);
   }

   public void saveStructure(NamespacedKey structureKey) {
      ResourceLocation minecraftKey = this.createAndValidateMinecraftStructureKey(structureKey);
      this.structureManager.save(minecraftKey);
   }

   public void saveStructure(NamespacedKey structureKey, Structure structure) throws IOException {
      Preconditions.checkArgument(structureKey != null, "NamespacedKey structure cannot be null");
      Preconditions.checkArgument(structure != null, "Structure cannot be null");
      File structureFile = this.getStructureFile(structureKey);
      Files.createDirectories(structureFile.toPath().getParent());
      this.saveStructure(structureFile, structure);
   }

   public Structure registerStructure(NamespacedKey structureKey, Structure structure) {
      Preconditions.checkArgument(structureKey != null, "NamespacedKey structureKey cannot be null");
      Preconditions.checkArgument(structure != null, "Structure cannot be null");
      ResourceLocation minecraftKey = this.createAndValidateMinecraftStructureKey(structureKey);
      Optional<StructureTemplate> optionalDefinedStructure = Optional.of(((CraftStructure)structure).getHandle());
      Optional<StructureTemplate> previousStructure = (Optional)this.structureManager.structureRepository.put(minecraftKey, optionalDefinedStructure);
      return previousStructure == null ? null : (Structure)previousStructure.map((s) -> {
         return new CraftStructure(s, this.registry);
      }).orElse((Object)null);
   }

   public Structure unregisterStructure(NamespacedKey structureKey) {
      Preconditions.checkArgument(structureKey != null, "NamespacedKey structureKey cannot be null");
      ResourceLocation minecraftKey = this.createAndValidateMinecraftStructureKey(structureKey);
      Optional<StructureTemplate> previousStructure = (Optional)this.structureManager.structureRepository.remove(minecraftKey);
      return previousStructure == null ? null : (Structure)previousStructure.map((s) -> {
         return new CraftStructure(s, this.registry);
      }).orElse((Object)null);
   }

   public void deleteStructure(NamespacedKey structureKey) throws IOException {
      this.deleteStructure(structureKey, true);
   }

   public void deleteStructure(NamespacedKey structureKey, boolean unregister) throws IOException {
      ResourceLocation key = CraftNamespacedKey.toMinecraft(structureKey);
      if (unregister) {
         this.structureManager.structureRepository.remove(key);
      }

      Path path = this.structureManager.createAndValidatePathToGeneratedStructure(key, ".nbt");
      Files.deleteIfExists(path);
   }

   public File getStructureFile(NamespacedKey structureKey) {
      ResourceLocation minecraftKey = this.createAndValidateMinecraftStructureKey(structureKey);
      return this.structureManager.createAndValidatePathToGeneratedStructure(minecraftKey, ".nbt").toFile();
   }

   public Structure loadStructure(File file) throws IOException {
      Preconditions.checkArgument(file != null, "File cannot be null");
      FileInputStream fileinputstream = new FileInputStream(file);
      return this.loadStructure((InputStream)fileinputstream);
   }

   public Structure loadStructure(InputStream inputStream) throws IOException {
      Preconditions.checkArgument(inputStream != null, "inputStream cannot be null");
      return new CraftStructure(this.structureManager.readStructure(inputStream), this.registry);
   }

   public void saveStructure(File file, Structure structure) throws IOException {
      Preconditions.checkArgument(file != null, "file cannot be null");
      Preconditions.checkArgument(structure != null, "structure cannot be null");
      FileOutputStream fileoutputstream = new FileOutputStream(file);
      this.saveStructure((OutputStream)fileoutputstream, structure);
   }

   public void saveStructure(OutputStream outputStream, Structure structure) throws IOException {
      Preconditions.checkArgument(outputStream != null, "outputStream cannot be null");
      Preconditions.checkArgument(structure != null, "structure cannot be null");
      CompoundTag nbttagcompound = ((CraftStructure)structure).getHandle().save(new CompoundTag());
      NbtIo.writeCompressed(nbttagcompound, outputStream);
   }

   public Structure createStructure() {
      return new CraftStructure(new StructureTemplate(), this.registry);
   }

   private ResourceLocation createAndValidateMinecraftStructureKey(NamespacedKey structureKey) {
      Preconditions.checkArgument(structureKey != null, "NamespacedKey structureKey cannot be null");
      ResourceLocation minecraftkey = CraftNamespacedKey.toMinecraft(structureKey);
      Preconditions.checkArgument(!minecraftkey.getPath().contains("//"), "Resource key for Structures can not contain \"//\"");
      return minecraftkey;
   }

   public Structure copy(Structure structure) {
      Preconditions.checkArgument(structure != null, "Structure cannot be null");
      return new CraftStructure(this.structureManager.readStructure(((CraftStructure)structure).getHandle().save(new CompoundTag())), this.registry);
   }
}
