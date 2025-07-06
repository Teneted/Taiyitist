package org.bukkit.craftbukkit.inventory.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

final class CraftHolderUtil {
   private CraftHolderUtil() {
   }

   public static void serialize(Map<String, Object> result, String key, HolderSet<?> handle) {
      handle.unwrap().ifLeft((tag) -> {
         result.put(key, "#" + tag.location().toString());
      }).ifRight((list) -> {
         result.put(key, list.stream().map((entry) -> {
            return ((ResourceKey)entry.unwrapKey().orElseThrow()).location().toString();
         }).toList());
      });
   }

   public static <T> HolderSet<T> parse(Object parseObject, ResourceKey<Registry<T>> registryKey, Registry<T> registry) {
      Object holderSet;
      label38: {
         holderSet = null;
         if (parseObject instanceof String parseString) {
            if (parseString.startsWith("#")) {
               parseString = parseString.substring(1);
               ResourceLocation key = ResourceLocation.tryParse(parseString);
               if (key != null) {
                  holderSet = (HolderSet)registry.get(TagKey.create(registryKey, key)).orElse((HolderSet.Named<T>) null);
               }
               break label38;
            }
         }

         if (!(parseObject instanceof List)) {
            throw new IllegalArgumentException("(" + String.valueOf(parseObject) + ") is not a valid String or List");
         }

         List parseList = (List)parseObject;
         List<Holder.Reference<T>> holderList = new ArrayList(parseList.size());
         Iterator var7 = parseList.iterator();

         while(var7.hasNext()) {
            Object entry = var7.next();
            ResourceLocation key = ResourceLocation.tryParse(entry.toString());
            if (key != null) {
               Objects.requireNonNull(holderList);
               registry.get(key).ifPresent(holderList::add);
            }
         }

         holderSet = HolderSet.direct(holderList);
      }

      if (holderSet == null) {
         holderSet = HolderSet.empty();
      }

      return (HolderSet)holderSet;
   }
}
