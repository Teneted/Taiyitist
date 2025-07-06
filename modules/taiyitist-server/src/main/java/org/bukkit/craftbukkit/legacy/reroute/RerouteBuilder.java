package org.bukkit.craftbukkit.legacy.reroute;

import com.google.common.base.Preconditions;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.objectweb.asm.Type;

public class RerouteBuilder {
   private final List<Class<?>> classes = new ArrayList();
   private final Predicate<String> compatibilityPresent;

   private RerouteBuilder(Predicate<String> compatibilityPresent) {
      this.compatibilityPresent = compatibilityPresent;
   }

   public static RerouteBuilder create(Predicate<String> compatibilityPresent) {
      return new RerouteBuilder(compatibilityPresent);
   }

   public RerouteBuilder forClass(Class<?> clazz) {
      this.classes.add(clazz);
      return this;
   }

   public Reroute build() {
      Map<String, Reroute.RerouteDataHolder> rerouteDataHolderMap = new HashMap();
      Iterator var2 = this.classes.iterator();

      while(var2.hasNext()) {
         Class<?> clazz = (Class)var2.next();
         List<RerouteMethodData> data = buildFromClass(clazz, this.compatibilityPresent);
         data.forEach((value) -> {
            ((Reroute.RerouteDataHolder)rerouteDataHolderMap.computeIfAbsent(value.methodKey(), (v) -> {
               return new Reroute.RerouteDataHolder();
            })).add(value);
         });
      }

      return new Reroute(rerouteDataHolderMap);
   }

   private static List<RerouteMethodData> buildFromClass(Class<?> clazz, Predicate<String> compatibilityPresent) {
      Preconditions.checkArgument(!clazz.isInterface(), "Interface Classes are currently not supported");
      List<RerouteMethodData> result = new ArrayList();
      boolean shouldInclude = shouldInclude(getRequireCompatibility(clazz), true, compatibilityPresent);
      Method[] var4 = clazz.getDeclaredMethods();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Method method = var4[var6];
         if (isMethodValid(method) && shouldInclude(getRequireCompatibility(method), shouldInclude, compatibilityPresent)) {
            result.add(buildFromMethod(method));
         }
      }

      return result;
   }

   private static RerouteMethodData buildFromMethod(Method method) {
      RerouteReturn rerouteReturn = new RerouteReturn(Type.getReturnType(method));
      List<RerouteArgument> arguments = new ArrayList();
      List<RerouteArgument> sourceArguments = new ArrayList();
      Parameter[] var4 = method.getParameters();
      int var5 = var4.length;

      Type type;
      for(int var6 = 0; var6 < var5; ++var6) {
         Parameter parameter = var4[var6];
         type = Type.getType(parameter.getType());
         int count = 0;
         boolean injectPluginName = false;
         boolean injectPluginVersion = false;
         String injectCompatibility = null;
         if (parameter.isAnnotationPresent(InjectPluginName.class)) {
            if (parameter.getType() != String.class) {
               throw new RuntimeException("Plugin name argument must be of type name, but got " + String.valueOf(parameter.getType()));
            }

            injectPluginName = true;
            ++count;
         }

         if (parameter.isAnnotationPresent(InjectPluginVersion.class)) {
            if (parameter.getType() != ApiVersion.class) {
               throw new RuntimeException("Plugin version argument must be of type ApiVersion, but got " + String.valueOf(parameter.getType()));
            }

            injectPluginVersion = true;
            ++count;
         }

         if (parameter.isAnnotationPresent(InjectCompatibility.class)) {
            if (parameter.getType() != Boolean.TYPE) {
               throw new RuntimeException("Compatibility argument must be of type boolean, but got " + String.valueOf(parameter.getType()));
            }

            injectCompatibility = ((InjectCompatibility)parameter.getAnnotation(InjectCompatibility.class)).value();
            ++count;
         }

         if (count > 1) {
            throw new RuntimeException("Wtf?");
         }

         RerouteArgumentType rerouteArgumentType = (RerouteArgumentType)parameter.getAnnotation(RerouteArgumentType.class);
         if (count == 1 && rerouteArgumentType != null) {
            throw new RuntimeException("Wtf?");
         }

         Type sourceType;
         if (rerouteArgumentType != null) {
            sourceType = Type.getObjectType(rerouteArgumentType.value());
         } else {
            sourceType = type;
         }

         RerouteArgument argument = new RerouteArgument(type, sourceType, injectPluginName, injectPluginVersion, injectCompatibility);
         arguments.add(argument);
         if (count == 0) {
            sourceArguments.add(argument);
         }
      }

      RerouteStatic rerouteStatic = (RerouteStatic)method.getAnnotation(RerouteStatic.class);
      Type sourceOwner;
      if (rerouteStatic != null) {
         sourceOwner = Type.getObjectType(rerouteStatic.value());
      } else {
         if (sourceArguments.isEmpty()) {
            throw new RuntimeException("Source argument list is empty, no owner class found");
         }

         RerouteArgument argument = (RerouteArgument)sourceArguments.getFirst();
         sourceOwner = argument.sourceType();
         sourceArguments.remove(argument);
      }

      RerouteReturnType rerouteReturnType = (RerouteReturnType)method.getAnnotation(RerouteReturnType.class);
      Type returnType;
      if (rerouteReturnType != null) {
         returnType = Type.getObjectType(rerouteReturnType.value());
      } else {
         returnType = rerouteReturn.type();
      }

      type = Type.getMethodType(returnType, (Type[])sourceArguments.stream().map(RerouteArgument::sourceType).toArray((x$0) -> {
         return new Type[x$0];
      }));
      RerouteMethodName rerouteMethodName = (RerouteMethodName)method.getAnnotation(RerouteMethodName.class);
      String methodName;
      if (rerouteMethodName != null) {
         methodName = rerouteMethodName.value();
      } else {
         methodName = method.getName();
      }

      String var10000 = type.getDescriptor();
      String methodKey = var10000 + methodName;
      Type targetType = Type.getType(method);
      boolean inBukkit = !method.isAnnotationPresent(NotInBukkit.class) && !method.getDeclaringClass().isAnnotationPresent(NotInBukkit.class);
      RequirePluginVersionData requiredPluginVersion = null;
      if (method.isAnnotationPresent(RequirePluginVersion.class)) {
         requiredPluginVersion = RequirePluginVersionData.create((RequirePluginVersion)method.getAnnotation(RequirePluginVersion.class));
      } else if (method.getDeclaringClass().isAnnotationPresent(RequirePluginVersion.class)) {
         requiredPluginVersion = RequirePluginVersionData.create((RequirePluginVersion)method.getDeclaringClass().getAnnotation(RequirePluginVersion.class));
      }

      return new RerouteMethodData(methodKey, type, sourceOwner, methodName, rerouteStatic != null, targetType, Type.getInternalName(method.getDeclaringClass()), method.getName(), arguments, rerouteReturn, inBukkit, requiredPluginVersion);
   }

   private static boolean isMethodValid(Method method) {
      if (method.isBridge()) {
         return false;
      } else if (method.isSynthetic()) {
         return false;
      } else if (!Modifier.isPublic(method.getModifiers())) {
         return false;
      } else if (!Modifier.isStatic(method.getModifiers())) {
         return false;
      } else {
         return !method.isAnnotationPresent(DoNotReroute.class);
      }
   }

   private static String getRequireCompatibility(AnnotatedElement element) {
      RequireCompatibility annotation = (RequireCompatibility)element.getAnnotation(RequireCompatibility.class);
      return annotation == null ? null : annotation.value();
   }

   private static boolean shouldInclude(String string, boolean parent, Predicate<String> compatibilityPresent) {
      return string == null ? parent : compatibilityPresent.test(string);
   }
}
