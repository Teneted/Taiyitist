package org.bukkit.craftbukkit.persistence;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public final class CraftPersistentDataTypeRegistry {
   private final Function<Class, TagAdapter> CREATE_ADAPTER = this::createAdapter;
   private final Map<Class, TagAdapter> adapters = new HashMap();

   private <T> TagAdapter createAdapter(Class<T> type) {
      if (!Primitives.isWrapperType(type)) {
         type = Primitives.wrap(type);
      }

      if (Objects.equals(Byte.class, type)) {
         return this.createAdapter(Byte.class, ByteTag.class, (byte)1, ByteTag::valueOf, ByteTag::byteValue);
      } else if (Objects.equals(Short.class, type)) {
         return this.createAdapter(Short.class, ShortTag.class, (byte)2, ShortTag::valueOf, ShortTag::shortValue);
      } else if (Objects.equals(Integer.class, type)) {
         return this.createAdapter(Integer.class, IntTag.class, (byte)3, IntTag::valueOf, IntTag::intValue);
      } else if (Objects.equals(Long.class, type)) {
         return this.createAdapter(Long.class, LongTag.class, (byte)4, LongTag::valueOf, LongTag::longValue);
      } else if (Objects.equals(Float.class, type)) {
         return this.createAdapter(Float.class, FloatTag.class, (byte)5, FloatTag::valueOf, FloatTag::floatValue);
      } else if (Objects.equals(Double.class, type)) {
         return this.createAdapter(Double.class, DoubleTag.class, (byte)6, DoubleTag::valueOf, DoubleTag::doubleValue);
      } else if (Objects.equals(String.class, type)) {
         return this.createAdapter(String.class, StringTag.class, (byte)8, StringTag::valueOf, StringTag::value);
      } else if (Objects.equals(byte[].class, type)) {
         return this.createAdapter(byte[].class, ByteArrayTag.class, (byte)7, (array) -> {
            return new ByteArrayTag(Arrays.copyOf(array, array.length));
         }, (n) -> {
            return Arrays.copyOf(n.getAsByteArray(), n.size());
         });
      } else if (Objects.equals(int[].class, type)) {
         return this.createAdapter(int[].class, IntArrayTag.class, (byte)11, (array) -> {
            return new IntArrayTag(Arrays.copyOf(array, array.length));
         }, (n) -> {
            return Arrays.copyOf(n.getAsIntArray(), n.size());
         });
      } else if (Objects.equals(long[].class, type)) {
         return this.createAdapter(long[].class, LongArrayTag.class, (byte)12, (array) -> {
            return new LongArrayTag(Arrays.copyOf(array, array.length));
         }, (n) -> {
            return Arrays.copyOf(n.getAsLongArray(), n.size());
         });
      } else if (Objects.equals(PersistentDataContainer[].class, type)) {
         return this.createAdapter(PersistentDataContainer[].class, ListTag.class, (byte)9, (containerArray) -> {
            ListTag list = new ListTag();
            PersistentDataContainer[] var2 = containerArray;
            int var3 = containerArray.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               PersistentDataContainer persistentDataContainer = var2[var4];
               list.add(((CraftPersistentDataContainer)persistentDataContainer).toTagCompound());
            }

            return list;
         }, (tag) -> {
            PersistentDataContainer[] containerArray = new CraftPersistentDataContainer[tag.size()];

            for(int i = 0; i < tag.size(); ++i) {
               CraftPersistentDataContainer container = new CraftPersistentDataContainer(this);
               CompoundTag compound = tag.getCompoundOrEmpty(i);
               Iterator var6 = compound.keySet().iterator();

               while(var6.hasNext()) {
                  String key = (String)var6.next();
                  container.put(key, compound.get(key));
               }

               containerArray[i] = container;
            }

            return containerArray;
         });
      } else if (Objects.equals(PersistentDataContainer.class, type)) {
         return this.createAdapter(CraftPersistentDataContainer.class, CompoundTag.class, (byte)10, CraftPersistentDataContainer::toTagCompound, (tag) -> {
            CraftPersistentDataContainer container = new CraftPersistentDataContainer(this);
            Iterator var3 = tag.keySet().iterator();

            while(var3.hasNext()) {
               String key = (String)var3.next();
               container.put(key, tag.get(key));
            }

            return container;
         });
      } else if (Objects.equals(List.class, type)) {
         return this.createAdapter(List.class, ListTag.class, (byte)9, this::constructList, this::extractList, this::matchesListTag);
      } else {
         throw new IllegalArgumentException("Could not find a valid TagAdapter implementation for the requested type " + type.getSimpleName());
      }
   }

   private <T, Z extends Tag> TagAdapter<T, Z> createAdapter(Class<T> primitiveType, Class<Z> nbtBaseType, byte nmsTypeByte, Function<T, Z> builder, Function<Z, T> extractor) {
      return this.createAdapter(primitiveType, nbtBaseType, nmsTypeByte, (type, t) -> {
         return (Tag)builder.apply(t);
      }, (type, z) -> {
         return extractor.apply(z);
      }, (type, t) -> {
         return nbtBaseType.isInstance(t);
      });
   }

   private <T, Z extends Tag> TagAdapter<T, Z> createAdapter(Class<T> primitiveType, Class<Z> nbtBaseType, byte nmsTypeByte, BiFunction<PersistentDataType<T, ?>, T, Z> builder, BiFunction<PersistentDataType<T, ?>, Z, T> extractor, BiPredicate<PersistentDataType<T, ?>, Tag> matcher) {
      return new TagAdapter(primitiveType, nbtBaseType, nmsTypeByte, builder, extractor, matcher);
   }

   public <T> Tag wrap(PersistentDataType<T, ?> type, T value) {
      return this.getOrCreateAdapter(type).build(type, value);
   }

   public <T> boolean isInstanceOf(PersistentDataType<T, ?> type, Tag base) {
      return this.getOrCreateAdapter(type).isInstance(type, base);
   }

   @NotNull
   private <T, Z extends Tag> TagAdapter<T, Z> getOrCreateAdapter(@NotNull PersistentDataType<T, ?> type) {
      return (TagAdapter)this.adapters.computeIfAbsent(type.getPrimitiveType(), this.CREATE_ADAPTER);
   }

   public <T, Z extends Tag> T extract(PersistentDataType<T, ?> type, Tag tag) throws ClassCastException, IllegalArgumentException {
      Class<T> primitiveType = type.getPrimitiveType();
      TagAdapter<T, Z> adapter = this.getOrCreateAdapter(type);
      Preconditions.checkArgument(adapter.isInstance(type, tag), "The found tag instance (%s) cannot store %s", tag.getClass().getSimpleName(), primitiveType.getSimpleName());
      Object foundValue = adapter.extract(type, tag);
      Preconditions.checkArgument(primitiveType.isInstance(foundValue), "The found object is of the type %s. Expected type %s", foundValue.getClass().getSimpleName(), primitiveType.getSimpleName());
      return primitiveType.cast(foundValue);
   }

   private <P, T extends List<P>> ListTag constructList(@NotNull PersistentDataType<T, ?> type, @NotNull List<P> list) {
      Preconditions.checkArgument(type instanceof ListPersistentDataType, "The passed list cannot be written to the PDC with a %s (expected a list data type)", type.getClass().getSimpleName());
      ListPersistentDataType<P, ?> listPersistentDataType = (ListPersistentDataType)type;
      List<Tag> values = Lists.newArrayListWithCapacity(list.size());
      Iterator var5 = list.iterator();

      while(var5.hasNext()) {
         P primitiveValue = var5.next();
         values.add(this.wrap(listPersistentDataType.elementType(), primitiveValue));
      }

      return new ListTag(values);
   }

   private <P> List<P> extractList(@NotNull PersistentDataType<P, ?> type, @NotNull ListTag listTag) {
      Preconditions.checkArgument(type instanceof ListPersistentDataType, "The found list tag cannot be read with a %s (expected a list data type)", type.getClass().getSimpleName());
      ListPersistentDataType<P, ?> listPersistentDataType = (ListPersistentDataType)type;
      List<P> output = new ObjectArrayList(listTag.size());
      Iterator var5 = listTag.iterator();

      while(var5.hasNext()) {
         Tag tag = (Tag)var5.next();
         output.add(this.extract(listPersistentDataType.elementType(), tag));
      }

      return output;
   }

   private boolean matchesListTag(PersistentDataType<List, ?> type, Tag tag) {
      if (type instanceof ListPersistentDataType listPersistentDataType) {
         if (!(tag instanceof ListTag listTag)) {
            return false;
         } else {
            byte elementType = listTag.identifyRawElementType();
            TagAdapter var6 = this.getOrCreateAdapter(listPersistentDataType.elementType());
            return var6.nmsTypeByte() == elementType || elementType == 0;
         }
      } else {
         return false;
      }
   }

   private static record TagAdapter<P, T extends Tag>(Class<P> primitiveType, Class<T> nbtBaseType, byte nmsTypeByte, BiFunction<PersistentDataType<P, ?>, P, T> builder, BiFunction<PersistentDataType<P, ?>, T, P> extractor, BiPredicate<PersistentDataType<P, ?>, Tag> matcher) {
      private TagAdapter(Class<P> primitiveType, Class<T> nbtBaseType, byte nmsTypeByte, BiFunction<PersistentDataType<P, ?>, P, T> builder, BiFunction<PersistentDataType<P, ?>, T, P> extractor, BiPredicate<PersistentDataType<P, ?>, Tag> matcher) {
         this.primitiveType = primitiveType;
         this.nbtBaseType = nbtBaseType;
         this.nmsTypeByte = nmsTypeByte;
         this.builder = builder;
         this.extractor = extractor;
         this.matcher = matcher;
      }

      private P extract(PersistentDataType<P, ?> dataType, Tag base) {
         Preconditions.checkArgument(this.nbtBaseType.isInstance(base), "The provided NBTBase was of the type %s. Expected type %s", base.getClass().getSimpleName(), this.nbtBaseType.getSimpleName());
         return this.extractor.apply(dataType, (Tag)this.nbtBaseType.cast(base));
      }

      private T build(PersistentDataType<P, ?> dataType, Object value) {
         Preconditions.checkArgument(this.primitiveType.isInstance(value), "The provided value was of the type %s. Expected type %s", value.getClass().getSimpleName(), this.primitiveType.getSimpleName());
         return (Tag)this.builder.apply(dataType, this.primitiveType.cast(value));
      }

      private boolean isInstance(PersistentDataType<P, ?> persistentDataType, Tag base) {
         return this.matcher.test(persistentDataType, base);
      }

      public Class<P> primitiveType() {
         return this.primitiveType;
      }

      public Class<T> nbtBaseType() {
         return this.nbtBaseType;
      }

      public byte nmsTypeByte() {
         return this.nmsTypeByte;
      }

      public BiFunction<PersistentDataType<P, ?>, P, T> builder() {
         return this.builder;
      }

      public BiFunction<PersistentDataType<P, ?>, T, P> extractor() {
         return this.extractor;
      }

      public BiPredicate<PersistentDataType<P, ?>, Tag> matcher() {
         return this.matcher;
      }
   }
}
