package org.bukkit.craftbukkit.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.craftbukkit.configuration.ConfigSerializationUtil;

final class CraftProfileProperty {
   private static final ServicesKeySet PUBLIC_KEYS;

   public static boolean hasValidSignature(@Nonnull Property property) {
      return property.hasSignature() && PUBLIC_KEYS.keys(ServicesKeyType.PROFILE_PROPERTY).stream().anyMatch((key) -> {
         return key.validateProperty(property);
      });
   }

   @Nullable
   private static String decodeBase64(@Nonnull String encoded) {
      try {
         return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   @Nullable
   public static JsonObject decodePropertyValue(@Nonnull String encodedPropertyValue) {
      String json = decodeBase64(encodedPropertyValue);
      if (json == null) {
         return null;
      } else {
         try {
            JsonElement jsonElement = JsonParser.parseString(json);
            return !jsonElement.isJsonObject() ? null : jsonElement.getAsJsonObject();
         } catch (JsonParseException var3) {
            return null;
         }
      }
   }

   @Nonnull
   public static String encodePropertyValue(@Nonnull JsonObject propertyValue, @Nonnull JsonFormatter formatter) {
      String json = formatter.format(propertyValue);
      return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
   }

   @Nonnull
   public static String toString(@Nonnull Property property) {
      StringBuilder builder = new StringBuilder();
      builder.append("{");
      builder.append("name=");
      builder.append(property.name());
      builder.append(", value=");
      builder.append(property.value());
      builder.append(", signature=");
      builder.append(property.signature());
      builder.append("}");
      return builder.toString();
   }

   public static int hashCode(@Nonnull Property property) {
      int result = 1;
      result = 31 * result + Objects.hashCode(property.name());
      result = 31 * result + Objects.hashCode(property.value());
      result = 31 * result + Objects.hashCode(property.signature());
      return result;
   }

   public static boolean equals(@Nullable Property property, @Nullable Property other) {
      if (property != null && other != null) {
         if (!Objects.equals(property.value(), other.value())) {
            return false;
         } else if (!Objects.equals(property.name(), other.name())) {
            return false;
         } else {
            return Objects.equals(property.signature(), other.signature());
         }
      } else {
         return property == other;
      }
   }

   public static Map<String, Object> serialize(@Nonnull Property property) {
      Map<String, Object> map = new LinkedHashMap();
      map.put("name", property.name());
      map.put("value", property.value());
      if (property.hasSignature()) {
         map.put("signature", property.signature());
      }

      return map;
   }

   public static Property deserialize(@Nonnull Map<?, ?> map) {
      String name = ConfigSerializationUtil.getString(map, "name", false);
      String value = ConfigSerializationUtil.getString(map, "value", false);
      String signature = ConfigSerializationUtil.getString(map, "signature", true);
      return new Property(name, value, signature);
   }

   private CraftProfileProperty() {
   }

   static {
      try {
         PUBLIC_KEYS = (new YggdrasilAuthenticationService(Proxy.NO_PROXY)).getServicesKeySet();
      } catch (Exception var1) {
         throw new Error("Could not load yggdrasil_session_pubkey.der! This indicates a bug.");
      }
   }

   public interface JsonFormatter {
      JsonFormatter COMPACT = new JsonFormatter() {
         private final Gson gson = (new GsonBuilder()).create();

         public String format(JsonElement jsonElement) {
            return this.gson.toJson(jsonElement);
         }
      };

      String format(JsonElement var1);
   }
}
