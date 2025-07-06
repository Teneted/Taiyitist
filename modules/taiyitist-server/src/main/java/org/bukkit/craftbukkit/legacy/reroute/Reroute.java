package org.bukkit.craftbukkit.legacy.reroute;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.ClassTraverser;
import org.jetbrains.annotations.VisibleForTesting;
import org.objectweb.asm.Type;

public class Reroute {
   @VisibleForTesting
   final Map<String, RerouteDataHolder> rerouteDataMap;

   Reroute(Map<String, RerouteDataHolder> rerouteDataMap) {
      this.rerouteDataMap = rerouteDataMap;
   }

   public boolean apply(ApiVersion pluginVersion, String owner, String name, String desc, boolean staticCall, Consumer<RerouteMethodData> consumer) {
      RerouteDataHolder rerouteData = (RerouteDataHolder)this.rerouteDataMap.get(desc + name);
      if (rerouteData == null) {
         return false;
      } else {
         Type ownerType = Type.getObjectType(owner);
         RerouteMethodData data = rerouteData.get(ownerType);
         if (staticCall && data == null) {
            return false;
         } else if (data != null) {
            if (data.requiredPluginVersion() != null && !data.requiredPluginVersion().test(pluginVersion)) {
               return false;
            } else {
               consumer.accept(data);
               return true;
            }
         } else {
            Class ownerClass;
            try {
               ownerClass = Class.forName(ownerType.getClassName(), false, Reroute.class.getClassLoader());
            } catch (ClassNotFoundException var13) {
               return false;
            }

            ClassTraverser it = new ClassTraverser(ownerClass);

            do {
               if (!it.hasNext()) {
                  return false;
               }

               Class<?> clazz = it.next();
               data = rerouteData.get(Type.getType(clazz));
            } while(data == null);

            if (data.requiredPluginVersion() != null && !data.requiredPluginVersion().test(pluginVersion)) {
               return false;
            } else {
               consumer.accept(data);
               return true;
            }
         }
      }
   }

   static class RerouteDataHolder {
      @VisibleForTesting
      final Map<String, RerouteMethodData> rerouteMethodDataMap = new HashMap();

      public RerouteMethodData get(Class<?> clazz) {
         return (RerouteMethodData)this.rerouteMethodDataMap.get(Type.getInternalName(clazz));
      }

      private RerouteMethodData get(Type owner) {
         return (RerouteMethodData)this.rerouteMethodDataMap.get(owner.getInternalName());
      }

      void add(RerouteMethodData value) {
         RerouteMethodData rerouteMethodData = this.get(value.sourceOwner());
         if (rerouteMethodData != null) {
            throw new IllegalStateException("Reroute method data already exists: " + String.valueOf(rerouteMethodData));
         } else {
            this.rerouteMethodDataMap.put(value.sourceOwner().getInternalName(), value);
         }
      }
   }
}
