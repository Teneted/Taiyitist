package org.bukkit.craftbukkit.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class ApiVersion implements Comparable<ApiVersion>, Serializable {
   public static final ApiVersion CURRENT = getOrCreateVersion("1.21.7");
   public static final ApiVersion FLATTENING = getOrCreateVersion("1.13");
   public static final ApiVersion FIELD_NAME_PARITY = getOrCreateVersion("1.20.5");
   public static final ApiVersion ABSTRACT_COW = getOrCreateVersion("1.21.5");
   public static final ApiVersion NONE = getOrCreateVersion("none");
   private static final Map<String, ApiVersion> versions = new HashMap();
   private final boolean none;
   private final int major;
   private final int minor;
   private final int patch;
   private static final long serialVersionUID = 0L;

   private ApiVersion() {
      this.none = true;
      this.major = Integer.MIN_VALUE;
      this.minor = Integer.MIN_VALUE;
      this.patch = Integer.MIN_VALUE;
   }

   private ApiVersion(int major, int minor, int patch) {
      this.none = false;
      this.major = major;
      this.minor = minor;
      this.patch = patch;
   }

   public static ApiVersion getOrCreateVersion(String versionString) {
      if (versionString != null && !versionString.trim().isEmpty() && !versionString.equalsIgnoreCase("none")) {
         ApiVersion version = (ApiVersion)versions.get(versionString);
         if (version != null) {
            return version;
         } else {
            String[] versionParts = versionString.split("\\.");
            if (versionParts.length != 2 && versionParts.length != 3) {
               throw new IllegalArgumentException(String.format("API version string should be of format \"major.minor.patch\" or \"major.minor\", where \"major\", \"minor\" and \"patch\" are numbers. For example \"1.18.2\" or \"1.13\", but got '%s' instead.", versionString));
            } else {
               int major = parseNumber(versionParts[0]);
               int minor = parseNumber(versionParts[1]);
               int patch;
               if (versionParts.length == 3) {
                  patch = parseNumber(versionParts[2]);
               } else {
                  patch = 0;
               }

               versionString = toVersionString(major, minor, patch);
               return (ApiVersion)versions.computeIfAbsent(versionString, (s) -> {
                  return new ApiVersion(major, minor, patch);
               });
            }
         }
      } else {
         return (ApiVersion)versions.computeIfAbsent("none", (s) -> {
            return new ApiVersion();
         });
      }
   }

   private static int parseNumber(String number) {
      return Integer.parseInt(number);
   }

   private static String toVersionString(int major, int minor, int patch) {
      return "" + major + "." + minor + "." + patch;
   }

   public int compareTo(@NotNull ApiVersion other) {
      int result = Integer.compare(this.major, other.major);
      if (result == 0) {
         result = Integer.compare(this.minor, other.minor);
      }

      if (result == 0) {
         result = Integer.compare(this.patch, other.patch);
      }

      return result;
   }

   public String getVersionString() {
      return this.none ? "none" : toVersionString(this.major, this.minor, this.patch);
   }

   public boolean isNewerThan(ApiVersion apiVersion) {
      return this.compareTo(apiVersion) > 0;
   }

   public boolean isOlderThan(ApiVersion apiVersion) {
      return this.compareTo(apiVersion) < 0;
   }

   public boolean isNewerThanOrSameAs(ApiVersion apiVersion) {
      return this.compareTo(apiVersion) >= 0;
   }

   public boolean isOlderThanOrSameAs(ApiVersion apiVersion) {
      return this.compareTo(apiVersion) <= 0;
   }

   public String toString() {
      return this.getVersionString();
   }
}
