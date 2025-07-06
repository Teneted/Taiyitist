package org.bukkit.craftbukkit.v1_21_R5.legacy.reroute;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

public record RerouteMethodData(String methodKey, Type sourceDesc, Type sourceOwner, String sourceName, boolean staticReroute, Type targetType, String targetOwner, String targetName, List<RerouteArgument> arguments, RerouteReturn rerouteReturn, boolean isInBukkit, RequirePluginVersionData requiredPluginVersion) {
   public RerouteMethodData(String methodKey, Type sourceDesc, Type sourceOwner, String sourceName, boolean staticReroute, Type targetType, String targetOwner, String targetName, List<RerouteArgument> arguments, RerouteReturn rerouteReturn, boolean isInBukkit, @Nullable RequirePluginVersionData requiredPluginVersion) {
      this.methodKey = methodKey;
      this.sourceDesc = sourceDesc;
      this.sourceOwner = sourceOwner;
      this.sourceName = sourceName;
      this.staticReroute = staticReroute;
      this.targetType = targetType;
      this.targetOwner = targetOwner;
      this.targetName = targetName;
      this.arguments = arguments;
      this.rerouteReturn = rerouteReturn;
      this.isInBukkit = isInBukkit;
      this.requiredPluginVersion = requiredPluginVersion;
   }

   public String methodKey() {
      return this.methodKey;
   }

   public Type sourceDesc() {
      return this.sourceDesc;
   }

   public Type sourceOwner() {
      return this.sourceOwner;
   }

   public String sourceName() {
      return this.sourceName;
   }

   public boolean staticReroute() {
      return this.staticReroute;
   }

   public Type targetType() {
      return this.targetType;
   }

   public String targetOwner() {
      return this.targetOwner;
   }

   public String targetName() {
      return this.targetName;
   }

   public List<RerouteArgument> arguments() {
      return this.arguments;
   }

   public RerouteReturn rerouteReturn() {
      return this.rerouteReturn;
   }

   public boolean isInBukkit() {
      return this.isInBukkit;
   }

   @Nullable
   public RequirePluginVersionData requiredPluginVersion() {
      return this.requiredPluginVersion;
   }
}
