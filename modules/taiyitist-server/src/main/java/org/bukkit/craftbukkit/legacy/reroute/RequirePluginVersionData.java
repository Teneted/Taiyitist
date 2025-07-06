package org.bukkit.craftbukkit.legacy.reroute;

import org.bukkit.craftbukkit.util.ApiVersion;

public record RequirePluginVersionData(ApiVersion minInclusive, ApiVersion maxInclusive) {
   public RequirePluginVersionData(ApiVersion minInclusive, ApiVersion maxInclusive) {
      this.minInclusive = minInclusive;
      this.maxInclusive = maxInclusive;
   }

   public static RequirePluginVersionData create(RequirePluginVersion requirePluginVersion) {
      if (!requirePluginVersion.value().isBlank()) {
         if (requirePluginVersion.minInclusive().isBlank() && requirePluginVersion.maxInclusive().isBlank()) {
            return new RequirePluginVersionData(ApiVersion.getOrCreateVersion(requirePluginVersion.value()), ApiVersion.getOrCreateVersion(requirePluginVersion.value()));
         } else {
            throw new IllegalArgumentException("When setting value, min inclusive and max inclusive data is not allowed.");
         }
      } else {
         ApiVersion minInclusive = null;
         ApiVersion maxInclusive = null;
         if (!requirePluginVersion.minInclusive().isBlank()) {
            minInclusive = ApiVersion.getOrCreateVersion(requirePluginVersion.minInclusive());
         }

         if (!requirePluginVersion.maxInclusive().isBlank()) {
            maxInclusive = ApiVersion.getOrCreateVersion(requirePluginVersion.maxInclusive());
         }

         if (minInclusive != null && maxInclusive != null && minInclusive.isNewerThan(maxInclusive)) {
            throw new IllegalArgumentException("Min inclusive cannot be newer than max inclusive.");
         } else {
            return new RequirePluginVersionData(minInclusive, maxInclusive);
         }
      }
   }

   public boolean test(ApiVersion pluginVersion) {
      if (this.minInclusive != null && pluginVersion.isOlderThan(this.minInclusive)) {
         return false;
      } else {
         return this.maxInclusive == null || !pluginVersion.isNewerThan(this.maxInclusive);
      }
   }

   public ApiVersion minInclusive() {
      return this.minInclusive;
   }

   public ApiVersion maxInclusive() {
      return this.maxInclusive;
   }
}
