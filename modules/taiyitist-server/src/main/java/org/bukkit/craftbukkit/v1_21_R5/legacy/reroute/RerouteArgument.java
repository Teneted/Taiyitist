package org.bukkit.craftbukkit.v1_21_R5.legacy.reroute;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

public record RerouteArgument(Type type, Type sourceType, boolean injectPluginName, boolean injectPluginVersion, String injectCompatibility) {
   public RerouteArgument(Type type, Type sourceType, boolean injectPluginName, boolean injectPluginVersion, @Nullable String injectCompatibility) {
      this.type = type;
      this.sourceType = sourceType;
      this.injectPluginName = injectPluginName;
      this.injectPluginVersion = injectPluginVersion;
      this.injectCompatibility = injectCompatibility;
   }

   public int instruction() {
      if (!this.injectPluginName() && !this.injectPluginVersion() && this.injectCompatibility() == null) {
         return this.type.getOpcode(21);
      } else {
         throw new IllegalStateException(String.format("Cannot get instruction for plugin name / version argument / compatibility: %s", this));
      }
   }

   public Type type() {
      return this.type;
   }

   public Type sourceType() {
      return this.sourceType;
   }

   public boolean injectPluginName() {
      return this.injectPluginName;
   }

   public boolean injectPluginVersion() {
      return this.injectPluginVersion;
   }

   @Nullable
   public String injectCompatibility() {
      return this.injectCompatibility;
   }
}
