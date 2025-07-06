package org.bukkit.craftbukkit.legacy.enums;

import com.google.common.base.Converter;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.lang.Enum.EnumDesc;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.bukkit.Art;
import org.bukkit.Fluid;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.legacy.reroute.DoNotReroute;
import org.bukkit.craftbukkit.legacy.reroute.InjectPluginVersion;
import org.bukkit.craftbukkit.legacy.reroute.NotInBukkit;
import org.bukkit.craftbukkit.legacy.reroute.RequireCompatibility;
import org.bukkit.craftbukkit.legacy.reroute.RequirePluginVersion;
import org.bukkit.craftbukkit.legacy.reroute.RerouteArgumentType;
import org.bukkit.craftbukkit.legacy.reroute.RerouteReturnType;
import org.bukkit.craftbukkit.legacy.reroute.RerouteStatic;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.ClassTraverser;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.map.MapCursor;
import org.bukkit.util.OldEnum;

@NotInBukkit
@RequireCompatibility("enum-compatibility-mode")
@RequirePluginVersion(
   maxInclusive = "1.20.6"
)
public class EnumEvil {
   private static final Map<Class<?>, LegacyRegistryData> REGISTRIES = new HashMap();

   public static LegacyRegistryData getRegistryData(Class<?> clazz) {
      ClassTraverser it = new ClassTraverser(clazz);

      LegacyRegistryData registryData;
      do {
         if (!it.hasNext()) {
            return null;
         }

         registryData = (LegacyRegistryData)REGISTRIES.get(it.next());
      } while(registryData == null);

      return registryData;
   }

   @DoNotReroute
   public static Registry<?> getRegistry(Class<?> clazz) {
      LegacyRegistryData registryData = getRegistryData(clazz);
      return registryData != null ? registryData.registry() : null;
   }

   @RerouteStatic("com/google/common/collect/Maps")
   @RerouteReturnType("java/util/EnumSet")
   public static ImposterEnumMap newEnumMap(Class<?> objectClass) {
      return new ImposterEnumMap(objectClass);
   }

   @RerouteStatic("com/google/common/collect/Maps")
   @RerouteReturnType("java/util/EnumSet")
   public static ImposterEnumMap newEnumMap(Map map) {
      return new ImposterEnumMap(map);
   }

   @RerouteStatic("com/google/common/collect/Sets")
   public static Collector<?, ?, ?> toImmutableEnumSet() {
      return Collectors.toUnmodifiableSet();
   }

   @RerouteStatic("com/google/common/collect/Sets")
   @RerouteReturnType("java/util/EnumSet")
   public static ImposterEnumSet newEnumSet(Iterable<?> iterable, Class<?> clazz) {
      ImposterEnumSet set = ImposterEnumSet.noneOf(clazz);
      Iterator var3 = iterable.iterator();

      while(var3.hasNext()) {
         Object some = var3.next();
         set.add(some);
      }

      return set;
   }

   @RerouteStatic("com/google/common/collect/Sets")
   public static ImmutableSet<?> immutableEnumSet(Iterable<?> iterable) {
      return ImmutableSet.of(iterable);
   }

   @RerouteStatic("com/google/common/collect/Sets")
   public static ImmutableSet<?> immutableEnumSet(@RerouteArgumentType("java/lang/Enum") Object first, @RerouteArgumentType("[java/lang/Enum") Object... rest) {
      return ImmutableSet.of(first, rest);
   }

   @RerouteStatic("com/google/common/base/Enums")
   public static Field getField(@RerouteArgumentType("java/lang/Enum") Object value) {
      if (value instanceof Enum eValue) {
         return Enums.getField(eValue);
      } else {
         try {
            return value.getClass().getField(((OldEnum)value).name());
         } catch (NoSuchFieldException var2) {
            NoSuchFieldException impossible = var2;
            throw new AssertionError(impossible);
         }
      }
   }

   @RerouteStatic("com/google/common/base/Enums")
   public static Optional getIfPresent(Class clazz, String name, @InjectPluginVersion ApiVersion apiVersion) {
      if (clazz.isEnum()) {
         return Enums.getIfPresent(clazz, name);
      } else {
         Registry registry = getRegistry(clazz);
         if (registry == null) {
            return Optional.absent();
         } else {
            name = FieldRename.rename(apiVersion, clazz.getName().replace('.', '/'), name);
            return Optional.fromNullable(registry.get(NamespacedKey.fromString(name.toLowerCase(Locale.ROOT))));
         }
      }
   }

   @RerouteStatic("com/google/common/base/Enums")
   public static Converter stringConverter(Class clazz, @InjectPluginVersion ApiVersion apiVersion) {
      return (Converter)(clazz.isEnum() ? Enums.stringConverter(clazz) : new StringConverter(apiVersion, clazz));
   }

   public static Object[] getEnumConstants(Class<?> clazz) {
      if (clazz.isEnum()) {
         return clazz.getEnumConstants();
      } else {
         Registry<?> registry = getRegistry(clazz);
         if (registry == null) {
            return clazz.getEnumConstants();
         } else {
            List<?> values = Lists.newArrayList(registry);
            Object array = Array.newInstance(clazz, values.size());

            for(int i = 0; i < values.size(); ++i) {
               Array.set(array, i, values.get(i));
            }

            return (Object[])array;
         }
      }
   }

   public static String name(@RerouteArgumentType("java/lang/Enum") Object object) {
      return object instanceof OldEnum ? ((OldEnum)object).name() : ((Enum)object).name();
   }

   public static int compareTo(@RerouteArgumentType("java/lang/Enum") Object object, @RerouteArgumentType("java/lang/Enum") Object other) {
      return object instanceof OldEnum ? ((OldEnum)object).compareTo((OldEnum)other) : ((Enum)object).compareTo((Enum)other);
   }

   public static Class<?> getDeclaringClass(@RerouteArgumentType("java/lang/Enum") Object object) {
      Class<?> clazz = object.getClass();
      Class<?> zuper = clazz.getSuperclass();
      return zuper == Enum.class ? clazz : zuper;
   }

   public static java.util.Optional<Enum.EnumDesc> describeConstable(@RerouteArgumentType("java/lang/Enum") Object object) {
      return getDeclaringClass(object).describeConstable().map((c) -> {
         return EnumDesc.of(c, name(object));
      });
   }

   @RerouteStatic("java/lang/Enum")
   @RerouteReturnType("java/lang/Enum")
   public static Object valueOf(Class enumClass, String name, @InjectPluginVersion ApiVersion apiVersion) {
      name = FieldRename.rename(apiVersion, enumClass.getName().replace('.', '/'), name);
      LegacyRegistryData registryData = getRegistryData(enumClass);
      return registryData != null ? registryData.function().apply(name) : Enum.valueOf(enumClass, name);
   }

   public static String toString(@RerouteArgumentType("java/lang/Enum") Object object) {
      return object.toString();
   }

   public static int ordinal(@RerouteArgumentType("java/lang/Enum") Object object) {
      return object instanceof OldEnum ? ((OldEnum)object).ordinal() : ((Enum)object).ordinal();
   }

   static {
      REGISTRIES.put(Art.class, new LegacyRegistryData(Registry.ART, Art::valueOf));
      REGISTRIES.put(Attribute.class, new LegacyRegistryData(Registry.ATTRIBUTE, Attribute::valueOf));
      REGISTRIES.put(Biome.class, new LegacyRegistryData(Registry.BIOME, Biome::valueOf));
      REGISTRIES.put(Fluid.class, new LegacyRegistryData(Registry.FLUID, Fluid::valueOf));
      REGISTRIES.put(Villager.Type.class, new LegacyRegistryData(Registry.VILLAGER_TYPE, Villager.Type::valueOf));
      REGISTRIES.put(Villager.Profession.class, new LegacyRegistryData(Registry.VILLAGER_PROFESSION, Villager.Profession::valueOf));
      REGISTRIES.put(Sound.class, new LegacyRegistryData(Registry.SOUNDS, Sound::valueOf));
      REGISTRIES.put(Frog.Variant.class, new LegacyRegistryData(Registry.FROG_VARIANT, Frog.Variant::valueOf));
      REGISTRIES.put(Cat.Type.class, new LegacyRegistryData(Registry.CAT_VARIANT, Cat.Type::valueOf));
      REGISTRIES.put(MapCursor.Type.class, new LegacyRegistryData(Registry.MAP_DECORATION_TYPE, MapCursor.Type::valueOf));
      REGISTRIES.put(PatternType.class, new LegacyRegistryData(Registry.BANNER_PATTERN, PatternType::valueOf));
   }

   public static record LegacyRegistryData(Registry<?> registry, Function<String, ?> function) {
      public LegacyRegistryData(Registry<?> registry, Function<String, ?> function) {
         this.registry = registry;
         this.function = function;
      }

      public Registry<?> registry() {
         return this.registry;
      }

      public Function<String, ?> function() {
         return this.function;
      }
   }

   private static final class StringConverter<T extends OldEnum<T>> extends Converter<String, T> implements Serializable {
      private final ApiVersion apiVersion;
      private final Class<T> clazz;
      private transient LegacyRegistryData registryData;
      private static final long serialVersionUID = 0L;

      StringConverter(ApiVersion apiVersion, Class<T> clazz) {
         this.apiVersion = apiVersion;
         this.clazz = clazz;
      }

      protected T doForward(String value) {
         if (this.registryData == null) {
            this.registryData = EnumEvil.getRegistryData(this.clazz);
         }

         value = FieldRename.rename(this.apiVersion, this.clazz.getName().replace('.', '/'), value);
         return (OldEnum)this.registryData.function().apply(value);
      }

      protected String doBackward(T enumValue) {
         return enumValue.name();
      }

      public boolean equals(Object object) {
         if (object instanceof StringConverter<?> that) {
            return this.clazz.equals(that.clazz);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.clazz.hashCode();
      }

      public String toString() {
         return "Enums.stringConverter(" + this.clazz.getName() + ".class)";
      }
   }
}
