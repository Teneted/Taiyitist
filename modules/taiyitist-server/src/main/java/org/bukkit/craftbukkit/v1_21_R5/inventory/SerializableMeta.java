package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.bukkit.block.Banner;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.meta.ItemMeta;

@SerializableAs("ItemMeta")
public final class SerializableMeta implements ConfigurationSerializable {
   static final String TYPE_FIELD = "meta-type";
   static final ImmutableMap<Class<? extends CraftMetaItem>, String> classMap = ImmutableMap.builder().put(CraftMetaArmor.class, "ARMOR").put(CraftMetaArmorStand.class, "ARMOR_STAND").put(CraftMetaBanner.class, "BANNER").put(CraftMetaBlockState.class, "TILE_ENTITY").put(CraftMetaBook.class, "BOOK").put(CraftMetaBookSigned.class, "BOOK_SIGNED").put(CraftMetaSkull.class, "SKULL").put(CraftMetaLeatherArmor.class, "LEATHER_ARMOR").put(CraftMetaColorableArmor.class, "COLORABLE_ARMOR").put(CraftMetaMap.class, "MAP").put(CraftMetaPotion.class, "POTION").put(CraftMetaShield.class, "SHIELD").put(CraftMetaSpawnEgg.class, "SPAWN_EGG").put(CraftMetaEnchantedBook.class, "ENCHANTED").put(CraftMetaFirework.class, "FIREWORK").put(CraftMetaCharge.class, "FIREWORK_EFFECT").put(CraftMetaKnowledgeBook.class, "KNOWLEDGE_BOOK").put(CraftMetaTropicalFishBucket.class, "TROPICAL_FISH_BUCKET").put(CraftMetaAxolotlBucket.class, "AXOLOTL_BUCKET").put(CraftMetaCrossbow.class, "CROSSBOW").put(CraftMetaSuspiciousStew.class, "SUSPICIOUS_STEW").put(CraftMetaEntityTag.class, "ENTITY_TAG").put(CraftMetaCompass.class, "COMPASS").put(CraftMetaBundle.class, "BUNDLE").put(CraftMetaMusicInstrument.class, "MUSIC_INSTRUMENT").put(CraftMetaOminousBottle.class, "OMINOUS_BOTTLE").put(CraftMetaItem.class, "UNSPECIFIC").build();
   static final ImmutableMap<String, Constructor<? extends CraftMetaItem>> constructorMap;

   private SerializableMeta() {
   }

   public static ItemMeta deserialize(Map<String, Object> map) throws Throwable {
      Preconditions.checkArgument(map != null, "Cannot deserialize null map");
      String type = getString(map, "meta-type", false);
      Constructor<? extends CraftMetaItem> constructor = (Constructor)constructorMap.get(type);
      if (constructor == null) {
         throw new IllegalArgumentException(type + " is not a valid meta-type");
      } else {
         try {
            CraftMetaItem meta = (CraftMetaItem)constructor.newInstance(map);
            if (meta instanceof CraftMetaBlockState) {
               CraftMetaBlockState state = (CraftMetaBlockState)meta;
               if (state.hasBlockState() && state.getBlockState() instanceof Banner) {
                  meta = new CraftMetaShield((CraftMetaItem)meta);
                  ((CraftMetaItem)meta).unhandledTags.clear(CraftMetaShield.BASE_COLOR.TYPE);
               }
            }

            return (ItemMeta)meta;
         } catch (IllegalAccessException | InstantiationException var5) {
            ReflectiveOperationException e = var5;
            throw new AssertionError(e);
         } catch (InvocationTargetException var6) {
            InvocationTargetException e = var6;
            throw e.getCause();
         }
      }
   }

   public Map<String, Object> serialize() {
      throw new AssertionError();
   }

   public static String getString(Map<?, ?> map, Object field, boolean nullable) {
      return (String)getObject(String.class, map, field, nullable);
   }

   public static boolean getBoolean(Map<?, ?> map, Object field) {
      Boolean value = (Boolean)getObject(Boolean.class, map, field, true);
      return value != null && value;
   }

   public static int getInteger(Map<?, ?> map, Object field) {
      Integer value = (Integer)getObject(Integer.class, map, field, true);
      return value != null ? value : 0;
   }

   public static <T> T getObject(Class<T> clazz, Map<?, ?> map, Object field, boolean nullable) {
      Object object = map.get(field);
      if (clazz.isInstance(object)) {
         return clazz.cast(object);
      } else if ((clazz == Float.class || clazz == Double.class) && Number.class.isInstance(object)) {
         Number number = (Number)Number.class.cast(object);
         return clazz == Float.class ? clazz.cast(number.floatValue()) : clazz.cast(number.doubleValue());
      } else {
         String var10002;
         if (object == null) {
            if (!nullable) {
               var10002 = String.valueOf(map);
               throw new NoSuchElementException(var10002 + " does not contain " + String.valueOf(field));
            } else {
               return null;
            }
         } else {
            var10002 = String.valueOf(field);
            throw new IllegalArgumentException(var10002 + "(" + String.valueOf(object) + ") is not a valid " + String.valueOf(clazz));
         }
      }
   }

   public static <T> List<T> getList(Class<T> clazz, Map<?, ?> map, Object field) {
      List<T> result = new ArrayList();
      List<?> list = (List)getObject(List.class, map, field, true);
      if (list != null && !list.isEmpty()) {
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            Object object = var5.next();
            T cast = null;
            if (clazz.isInstance(object)) {
               cast = clazz.cast(object);
            }

            if ((clazz == Float.class || clazz == Double.class) && Number.class.isInstance(object)) {
               Number number = (Number)Number.class.cast(object);
               if (clazz == Float.class) {
                  cast = clazz.cast(number.floatValue());
               } else {
                  cast = clazz.cast(number.doubleValue());
               }
            }

            if (cast != null) {
               result.add(cast);
            }
         }

         return result;
      } else {
         return result;
      }
   }

   static {
      ImmutableMap.Builder<String, Constructor<? extends CraftMetaItem>> classConstructorBuilder = ImmutableMap.builder();
      UnmodifiableIterator var1 = classMap.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry<Class<? extends CraftMetaItem>, String> mapping = (Map.Entry)var1.next();

         try {
            classConstructorBuilder.put((String)mapping.getValue(), ((Class)mapping.getKey()).getDeclaredConstructor(Map.class));
         } catch (NoSuchMethodException var4) {
            NoSuchMethodException e = var4;
            throw new AssertionError(e);
         }
      }

      constructorMap = classConstructorBuilder.build();
   }
}
