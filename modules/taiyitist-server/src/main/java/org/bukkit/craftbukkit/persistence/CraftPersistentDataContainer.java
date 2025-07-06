package org.bukkit.craftbukkit.persistence;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNBTTagConfigSerializer;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CraftPersistentDataContainer implements PersistentDataContainer {
   private final Map<String, Tag> customDataTags;
   private final CraftPersistentDataTypeRegistry registry;
   private final CraftPersistentDataAdapterContext adapterContext;

   public CraftPersistentDataContainer(Map<String, Tag> customTags, CraftPersistentDataTypeRegistry registry) {
      this(registry);
      this.customDataTags.putAll(customTags);
   }

   public CraftPersistentDataContainer(CraftPersistentDataTypeRegistry registry) {
      this.customDataTags = new HashMap();
      this.registry = registry;
      this.adapterContext = new CraftPersistentDataAdapterContext(this.registry);
   }

   public <T, Z> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
      Preconditions.checkArgument(key != null, "The NamespacedKey key cannot be null");
      Preconditions.checkArgument(type != null, "The provided type cannot be null");
      Preconditions.checkArgument(value != null, "The provided value cannot be null");
      this.customDataTags.put(key.toString(), this.registry.wrap(type, type.toPrimitive(value, this.adapterContext)));
   }

   public <T, Z> boolean has(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
      Preconditions.checkArgument(key != null, "The NamespacedKey key cannot be null");
      Preconditions.checkArgument(type != null, "The provided type cannot be null");
      Tag value = (Tag)this.customDataTags.get(key.toString());
      return value == null ? false : this.registry.isInstanceOf(type, value);
   }

   public boolean has(NamespacedKey key) {
      return this.customDataTags.get(key.toString()) != null;
   }

   public <T, Z> Z get(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
      Preconditions.checkArgument(key != null, "The NamespacedKey key cannot be null");
      Preconditions.checkArgument(type != null, "The provided type cannot be null");
      Tag value = (Tag)this.customDataTags.get(key.toString());
      return value == null ? null : type.fromPrimitive(this.registry.extract(type, value), this.adapterContext);
   }

   @NotNull
   public <T, Z> Z getOrDefault(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z defaultValue) {
      Z z = this.get(key, type);
      return z != null ? z : defaultValue;
   }

   @NotNull
   public Set<NamespacedKey> getKeys() {
      Set<NamespacedKey> keys = new HashSet();
      this.customDataTags.keySet().forEach((key) -> {
         String[] keyData = key.split(":", 2);
         if (keyData.length == 2) {
            keys.add(new NamespacedKey(keyData[0], keyData[1]));
         }

      });
      return keys;
   }

   public void remove(@NotNull NamespacedKey key) {
      Preconditions.checkArgument(key != null, "The NamespacedKey key cannot be null");
      this.customDataTags.remove(key.toString());
   }

   public boolean isEmpty() {
      return this.customDataTags.isEmpty();
   }

   @NotNull
   public void copyTo(PersistentDataContainer other, boolean replace) {
      Preconditions.checkArgument(other != null, "The target container cannot be null");
      CraftPersistentDataContainer target = (CraftPersistentDataContainer)other;
      if (replace) {
         target.customDataTags.putAll(this.customDataTags);
      } else {
         Map var10000 = this.customDataTags;
         Map var10001 = target.customDataTags;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::putIfAbsent);
      }

   }

   public PersistentDataAdapterContext getAdapterContext() {
      return this.adapterContext;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof CraftPersistentDataContainer)) {
         return false;
      } else {
         Map<String, Tag> myRawMap = this.getRaw();
         Map<String, Tag> theirRawMap = ((CraftPersistentDataContainer)obj).getRaw();
         return Objects.equals(myRawMap, theirRawMap);
      }
   }

   public CompoundTag toTagCompound() {
      CompoundTag tag = new CompoundTag();
      Iterator var2 = this.customDataTags.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, Tag> entry = (Map.Entry)var2.next();
         tag.put((String)entry.getKey(), (Tag)entry.getValue());
      }

      return tag;
   }

   public void store(ValueOutput output) {
      Preconditions.checkArgument(output instanceof TagValueOutput, "Must be an NBT output");
      ((TagValueOutput)output).buildResult().merge(this.toTagCompound());
   }

   public void put(String key, Tag base) {
      this.customDataTags.put(key, base);
   }

   public void putAll(Map<String, Tag> map) {
      this.customDataTags.putAll(map);
   }

   public void putAll(CompoundTag compound) {
      Iterator var2 = compound.keySet().iterator();

      while(var2.hasNext()) {
         String key = (String)var2.next();
         this.customDataTags.put(key, compound.get(key));
      }

   }

   public void putAll(ValueInput input) {
      Preconditions.checkArgument(input instanceof TagValueInput, "Must be an NBT input");
      this.putAll(((TagValueInput)input).input);
   }

   public Map<String, Tag> getRaw() {
      return this.customDataTags;
   }

   public CraftPersistentDataTypeRegistry getDataTagTypeRegistry() {
      return this.registry;
   }

   public int hashCode() {
      int hashCode = 3;
      hashCode += this.customDataTags.hashCode();
      return hashCode;
   }

   public String serialize() {
      return CraftNBTTagConfigSerializer.serialize(this.toTagCompound());
   }
}
