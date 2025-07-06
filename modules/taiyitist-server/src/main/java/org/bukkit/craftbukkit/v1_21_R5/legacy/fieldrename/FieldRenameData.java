package org.bukkit.craftbukkit.v1_21_R5.legacy.fieldrename;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.util.ApiVersion;

public record FieldRenameData(RenameData<String> renameData, RenameData<NamespacedKey> keyRenameData) {
   public FieldRenameData(RenameData<String> renameData, RenameData<NamespacedKey> keyRenameData) {
      this.renameData = renameData;
      this.keyRenameData = keyRenameData;
   }

   public String getReplacement(ApiVersion apiVersion, String from) {
      if (from == null) {
         return null;
      } else {
         from = from.toUpperCase(Locale.ROOT);
         return (String)this.renameData.getReplacement(apiVersion, from);
      }
   }

   public NamespacedKey getReplacement(NamespacedKey from, ApiVersion apiVersion) {
      return from == null ? null : (NamespacedKey)this.keyRenameData.getReplacement(apiVersion, from);
   }

   public RenameData<String> renameData() {
      return this.renameData;
   }

   public RenameData<NamespacedKey> keyRenameData() {
      return this.keyRenameData;
   }

   private static record RenameData<T>(NavigableMap<ApiVersion, Map<T, T>> versionData, Map<T, T> data) {
      private RenameData(NavigableMap<ApiVersion, Map<T, T>> versionData, Map<T, T> data) {
         this.versionData = versionData;
         this.data = data;
      }

      public T getReplacement(ApiVersion apiVersion, T from) {
         from = this.data.getOrDefault(from, from);
         Iterator var3 = this.versionData.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<ApiVersion, Map<T, T>> entry = (Map.Entry)var3.next();
            if (!apiVersion.isNewerThanOrSameAs((ApiVersion)entry.getKey())) {
               from = ((Map)entry.getValue()).getOrDefault(from, from);
            }
         }

         return from;
      }

      public NavigableMap<ApiVersion, Map<T, T>> versionData() {
         return this.versionData;
      }

      public Map<T, T> data() {
         return this.data;
      }
   }

   public static class Builder {
      private final Map<String, String> data = new HashMap();
      private final NavigableMap<ApiVersion, Map<String, String>> versionData = new TreeMap();
      private final Map<NamespacedKey, NamespacedKey> keyData = new HashMap();
      private final NavigableMap<ApiVersion, Map<NamespacedKey, NamespacedKey>> versionKeyData = new TreeMap();
      private ApiVersion currentVersion;
      private boolean keyRename = false;

      public static Builder newBuilder() {
         return new Builder();
      }

      public Builder forVersionsBefore(ApiVersion apiVersion) {
         this.currentVersion = apiVersion;
         this.keyRename = false;
         return this;
      }

      public Builder forAllVersions() {
         this.currentVersion = null;
         this.keyRename = false;
         return this;
      }

      public Builder withKeyRename() {
         this.keyRename = true;
         return this;
      }

      public Builder change(String from, String to) {
         if (this.currentVersion != null) {
            ((Map)this.versionData.computeIfAbsent(this.currentVersion, (d) -> {
               return new HashMap();
            })).put(from.replace('.', '_'), to);
         } else {
            this.data.put(from.replace('.', '_'), to);
         }

         if (this.keyRename) {
            NamespacedKey fromKey = NamespacedKey.minecraft(from.toLowerCase(Locale.ROOT));
            NamespacedKey toKey = NamespacedKey.minecraft(to.toLowerCase(Locale.ROOT));
            if (this.currentVersion != null) {
               ((Map)this.versionKeyData.computeIfAbsent(this.currentVersion, (d) -> {
                  return new HashMap();
               })).put(fromKey, toKey);
            } else {
               this.keyData.put(fromKey, toKey);
            }
         }

         return this;
      }

      public FieldRenameData build() {
         return new FieldRenameData(new RenameData(this.versionData, this.data), new RenameData(this.versionKeyData, this.keyData));
      }
   }
}
