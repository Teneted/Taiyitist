package org.bukkit.craftbukkit.v1_21_R5.util;

import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R5.legacy.FieldRename;
import org.bukkit.craftbukkit.v1_21_R5.legacy.MaterialRerouting;
import org.bukkit.craftbukkit.v1_21_R5.legacy.MethodRerouting;
import org.bukkit.craftbukkit.v1_21_R5.legacy.enums.EnumEvil;
import org.bukkit.craftbukkit.v1_21_R5.legacy.reroute.Reroute;
import org.bukkit.craftbukkit.v1_21_R5.legacy.reroute.RerouteArgument;
import org.bukkit.craftbukkit.v1_21_R5.legacy.reroute.RerouteBuilder;
import org.bukkit.craftbukkit.v1_21_R5.legacy.reroute.RerouteMethodData;
import org.bukkit.plugin.AuthorNagException;
import org.jetbrains.annotations.VisibleForTesting;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;

public class Commodore {
   private static final String BUKKIT_GENERATED_METHOD_PREFIX = "BUKKIT_CUSTOM_METHOD_";
   private static final Set<String> EVIL = new HashSet(Arrays.asList("org/bukkit/World (III)I getBlockTypeIdAt", "org/bukkit/World (Lorg/bukkit/Location;)I getBlockTypeIdAt", "org/bukkit/block/Block ()I getTypeId", "org/bukkit/block/Block (I)Z setTypeId", "org/bukkit/block/Block (IZ)Z setTypeId", "org/bukkit/block/Block (IBZ)Z setTypeIdAndData", "org/bukkit/block/Block (B)V setData", "org/bukkit/block/Block (BZ)V setData", "org/bukkit/inventory/ItemStack ()I getTypeId", "org/bukkit/inventory/ItemStack (I)V setTypeId", "org/bukkit/inventory/ItemStack (S)V setDurability"));
   private static final Map<String, String> ENUM_RENAMES = Map.of("java/lang/Enum", "java/lang/Object", "java/util/EnumSet", "org/bukkit/craftbukkit/v1_21_R5/legacy/enums/ImposterEnumSet", "java/util/EnumMap", "org/bukkit/craftbukkit/v1_21_R5/legacy/enums/ImposterEnumMap");
   private static final Map<String, String> RENAMES = Map.of("org/bukkit/entity/TextDisplay$TextAligment", "org/bukkit/entity/TextDisplay$TextAlignment", "org/spigotmc/event/entity/EntityMountEvent", "org/bukkit/event/entity/EntityMountEvent", "org/spigotmc/event/entity/EntityDismountEvent", "org/bukkit/event/entity/EntityDismountEvent");
   private static final Map<String, String> CLASS_TO_INTERFACE = Map.ofEntries(Map.entry("org/bukkit/inventory/InventoryView", "org/bukkit/craftbukkit/v1_21_R5/inventory/CraftAbstractInventoryView"), Map.entry("org/bukkit/entity/Villager$Type", "NOP"), Map.entry("org/bukkit/entity/Villager$Profession", "NOP"), Map.entry("org/bukkit/entity/Frog$Variant", "NOP"), Map.entry("org/bukkit/entity/Cat$Type", "NOP"), Map.entry("org/bukkit/map/MapCursor$Type", "NOP"), Map.entry("org/bukkit/block/banner/PatternType", "NOP"), Map.entry("org/bukkit/Art", "NOP"), Map.entry("org/bukkit/attribute/Attribute", "NOP"), Map.entry("org/bukkit/block/Biome", "NOP"), Map.entry("org/bukkit/Fluid", "NOP"), Map.entry("org/bukkit/Sound", "NOP"));
   private final List<Reroute> reroutes = new ArrayList();
   private Reroute materialReroute;
   private Reroute reroute;

   public Commodore() {
   }

   public Commodore(Predicate<String> compatibilityPresent) {
      this.updateReroute(compatibilityPresent);
   }

   public void updateReroute(Predicate<String> compatibilityPresent) {
      this.materialReroute = RerouteBuilder.create(compatibilityPresent).forClass(MaterialRerouting.class).build();
      this.reroute = RerouteBuilder.create(compatibilityPresent).forClass(FieldRename.class).forClass(MethodRerouting.class).forClass(EnumEvil.class).build();
      this.reroutes.clear();
      this.reroutes.add(this.materialReroute);
      this.reroutes.add(this.reroute);
   }

   @VisibleForTesting
   public List<Reroute> getReroutes() {
      return this.reroutes;
   }

   public static void main(String[] args) {
      OptionParser parser = new OptionParser();
      OptionSpec<File> inputFlag = parser.acceptsAll(Arrays.asList("i", "input")).withRequiredArg().ofType(File.class).required();
      OptionSpec<File> outputFlag = parser.acceptsAll(Arrays.asList("o", "output")).withRequiredArg().ofType(File.class).required();
      OptionSet options = parser.parse(args);
      File input = (File)options.valueOf(inputFlag);
      File output = (File)options.valueOf(outputFlag);
      Commodore commodore = new Commodore(Predicates.alwaysTrue());
      if (input.isDirectory()) {
         if (!output.isDirectory()) {
            System.err.println("If input directory specified, output directory required too");
            return;
         }

         File[] var8 = input.listFiles();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            File in = var8[var10];
            if (in.getName().endsWith(".jar")) {
               convert(in, new File(output, in.getName()), commodore);
            }
         }
      } else {
         convert(input, output, commodore);
      }

   }

   private static void convert(File in, File out, Commodore commodore) {
      PrintStream var10000 = System.out;
      String var10001 = String.valueOf(in);
      var10000.println("Attempting to convert " + var10001 + " to " + String.valueOf(out));

      try {
         JarFile inJar = new JarFile(in, false);

         label105: {
            try {
               JarEntry entry = inJar.getJarEntry(".commodore");
               if (entry != null) {
                  break label105;
               }

               JarOutputStream outJar = new JarOutputStream(new FileOutputStream(out));

               try {
                  Enumeration<JarEntry> entries = inJar.entries();

                  while(entries.hasMoreElements()) {
                     entry = (JarEntry)entries.nextElement();
                     InputStream is = inJar.getInputStream(entry);

                     try {
                        byte[] b = ByteStreams.toByteArray(is);
                        if (entry.getName().endsWith(".class")) {
                           b = commodore.convert(b, "dummy", ApiVersion.NONE, Collections.emptySet());
                           entry = new JarEntry(entry.getName());
                        }

                        outJar.putNextEntry(entry);
                        outJar.write(b);
                     } catch (Throwable var13) {
                        if (is != null) {
                           try {
                              is.close();
                           } catch (Throwable var12) {
                              var13.addSuppressed(var12);
                           }
                        }

                        throw var13;
                     }

                     if (is != null) {
                        is.close();
                     }
                  }

                  outJar.putNextEntry(new ZipEntry(".commodore"));
               } catch (Throwable var14) {
                  try {
                     outJar.close();
                  } catch (Throwable var11) {
                     var14.addSuppressed(var11);
                  }

                  throw var14;
               }

               outJar.close();
            } catch (Throwable var15) {
               try {
                  inJar.close();
               } catch (Throwable var10) {
                  var15.addSuppressed(var10);
               }

               throw var15;
            }

            inJar.close();
            return;
         }

         inJar.close();
      } catch (Exception var16) {
         Exception ex = var16;
         System.err.println("Fatal error trying to convert " + String.valueOf(in));
         ex.printStackTrace();
      }
   }

   public byte[] convert(byte[] b, final String pluginName, final ApiVersion pluginVersion, final Set<String> activeCompatibilities) {
      final boolean modern = pluginVersion.isNewerThanOrSameAs(ApiVersion.FLATTENING);
      final boolean enumCompatibility = pluginVersion.isOlderThanOrSameAs(ApiVersion.getOrCreateVersion("1.20.6")) && activeCompatibilities.contains("enum-compatibility-mode");
      ClassReader cr = new ClassReader(b);
      ClassWriter cw = new ClassWriter(cr, 0);
      List<String> methodEnumSignatures = getMethodSignatures(b);
      final Multimap<String, String> enumLessToEnum = HashMultimap.create();
      Iterator var11 = methodEnumSignatures.iterator();

      while(var11.hasNext()) {
         String method = (String)var11.next();
         enumLessToEnum.put(method.replace("Ljava/lang/Enum;", "Ljava/lang/Object;"), method);
      }

      ClassVisitor visitor = cw;
      if (enumCompatibility) {
         visitor = new LimitedClassRemapper(cw, new SimpleRemapper(ENUM_RENAMES));
      }

      Map<String, String> renames = new HashMap(RENAMES);
      if (pluginVersion.isOlderThan(ApiVersion.ABSTRACT_COW)) {
         renames.put("org/bukkit/entity/Cow", "org/bukkit/entity/AbstractCow");
      }

      cr.accept(new ClassRemapper(new ClassVisitor(589824, (ClassVisitor)visitor) {
         final Set<RerouteMethodData> rerouteMethodData = new HashSet();
         String className;
         boolean isInterface;

         public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name;
            this.isInterface = (access & 512) != 0;
            String craftbukkitClass = (String)Commodore.CLASS_TO_INTERFACE.get(superName);
            if (craftbukkitClass != null) {
               superName = craftbukkitClass;
            }

            super.visit(version, access, name, signature, superName, interfaces);
         }

         public void visitEnd() {
            Iterator var1 = this.rerouteMethodData.iterator();

            while(var1.hasNext()) {
               RerouteMethodData rerouteMethodData = (RerouteMethodData)var1.next();
               MethodVisitor methodVisitor = super.visitMethod(4105, Commodore.buildMethodName(rerouteMethodData), Commodore.buildMethodDesc(rerouteMethodData), (String)null, (String[])null);
               methodVisitor.visitCode();
               int index = 0;
               int extraSize = 0;
               Iterator var6 = rerouteMethodData.arguments().iterator();

               while(var6.hasNext()) {
                  RerouteArgument argument = (RerouteArgument)var6.next();
                  if (argument.injectPluginName()) {
                     methodVisitor.visitLdcInsn(pluginName);
                  } else if (argument.injectPluginVersion()) {
                     methodVisitor.visitLdcInsn(pluginVersion.getVersionString());
                     methodVisitor.visitMethodInsn(184, Type.getInternalName(ApiVersion.class), "getOrCreateVersion", "(Ljava/lang/String;)L" + Type.getInternalName(ApiVersion.class) + ";", false);
                  } else if (argument.injectCompatibility() != null) {
                     methodVisitor.visitLdcInsn(activeCompatibilities.contains(argument.injectCompatibility()));
                  } else {
                     methodVisitor.visitVarInsn(argument.instruction(), index);
                     ++index;
                     extraSize += argument.type().getSize() - 1;
                  }
               }

               methodVisitor.visitMethodInsn(184, rerouteMethodData.targetOwner(), rerouteMethodData.targetName(), rerouteMethodData.targetType().getDescriptor(), false);
               methodVisitor.visitInsn(rerouteMethodData.rerouteReturn().instruction());
               methodVisitor.visitMaxs(rerouteMethodData.arguments().size() + extraSize, index + extraSize);
               methodVisitor.visitEnd();
            }

            super.visitEnd();
         }

         public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitAnnotation(descriptor, visible));
         }

         public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
         }

         public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (enumCompatibility && (access & 4096) != 0 && (access & 64) != 0 && desc.contains("Ljava/lang/Object;")) {
               Multimap var10000 = enumLessToEnum;
               String var10001 = desc.replace("Ljava/lang/Enum;", "Ljava/lang/Object;");
               Collection<String> result = var10000.get(var10001 + " " + name);
               if (result.size() == 2) {
                  name = name + "_BUKKIT_UNUSED";
               }
            }

            return new MethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions)) {
               public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                  name = FieldRename.rename(pluginVersion, owner, name);
                  if (modern) {
                     if (owner.equals("org/bukkit/Material")) {
                        switch (name) {
                           case "CACTUS_GREEN" -> name = "GREEN_DYE";
                           case "DANDELION_YELLOW" -> name = "YELLOW_DYE";
                           case "ROSE_RED" -> name = "RED_DYE";
                           case "SIGN" -> name = "OAK_SIGN";
                           case "WALL_SIGN" -> name = "OAK_WALL_SIGN";
                           case "ZOMBIE_PIGMAN_SPAWN_EGG" -> name = "ZOMBIFIED_PIGLIN_SPAWN_EGG";
                           case "GRASS_PATH" -> name = "DIRT_PATH";
                           case "GRASS" -> name = "SHORT_GRASS";
                           case "SCUTE" -> name = "TURTLE_SCUTE";
                        }
                     }

                     super.visitFieldInsn(opcode, owner, name, desc);
                  } else if (owner.equals("org/bukkit/Material")) {
                     try {
                        Material.valueOf("LEGACY_" + name);
                     } catch (IllegalArgumentException var7) {
                        throw new AuthorNagException("No legacy enum constant for " + name + ". Did you forget to define a modern (1.13+) api-version in your plugin.yml?");
                     }

                     super.visitFieldInsn(opcode, owner, "LEGACY_" + name, desc);
                  } else {
                     if (owner.equals("org/bukkit/Art")) {
                        switch (name) {
                           case "BURNINGSKULL":
                              super.visitFieldInsn(opcode, owner, "BURNING_SKULL", desc);
                              return;
                           case "DONKEYKONG":
                              super.visitFieldInsn(opcode, owner, "DONKEY_KONG", desc);
                              return;
                        }
                     }

                     if (owner.equals("org/bukkit/DyeColor")) {
                        switch (name) {
                           case "SILVER":
                              super.visitFieldInsn(opcode, owner, "LIGHT_GRAY", desc);
                              return;
                        }
                     }

                     super.visitFieldInsn(opcode, owner, name, desc);
                  }
               }

               private void handleMethod(MethodPrinter visitor, int opcode, String owner, String name, String desc, boolean itf, Type samMethodType, Type instantiatedMethodType) {
                  if (!this.checkReroute(visitor, Commodore.this.reroute, opcode, owner, name, desc, samMethodType, instantiatedMethodType)) {
                     String craftbukkitClass = (String)Commodore.CLASS_TO_INTERFACE.get(owner);
                     if (craftbukkitClass != null) {
                        if (opcode != 183 && opcode != 7) {
                           if (opcode == 182) {
                              opcode = 185;
                           }

                           if (opcode == 5) {
                              opcode = 9;
                           }

                           itf = true;
                        } else {
                           owner = craftbukkitClass;
                        }
                     }

                     if (owner.equals("org/bukkit/map/MapView") && name.equals("getId") && desc.equals("()S")) {
                        visitor.visit(opcode, owner, name, "()I", itf, samMethodType, Type.getMethodType("(Lorg/bukkit/map/MapView;)Ljava/lang/Integer;"));
                     } else if ((owner.equals("org/bukkit/Bukkit") || owner.equals("org/bukkit/Server")) && name.equals("getMap") && desc.equals("(S)Lorg/bukkit/map/MapView;")) {
                        visitor.visit(opcode, owner, name, "(I)Lorg/bukkit/map/MapView;", itf, samMethodType, instantiatedMethodType);
                     } else if (owner.startsWith("org/bukkit") && desc.contains("org/bukkit/util/Consumer")) {
                        visitor.visit(opcode, owner, name, desc.replace("org/bukkit/util/Consumer", "java/util/function/Consumer"), itf, samMethodType, instantiatedMethodType);
                     } else if (modern) {
                        if (owner.equals("org/bukkit/Material") || instantiatedMethodType != null && instantiatedMethodType.getDescriptor().startsWith("(Lorg/bukkit/Material;)")) {
                           switch (name) {
                              case "values":
                                 visitor.visit(opcode, "org/bukkit/craftbukkit/v1_21_R5/util/CraftLegacy", "modern_" + name, desc, itf, samMethodType, instantiatedMethodType);
                                 return;
                              case "ordinal":
                                 visitor.visit(184, "org/bukkit/craftbukkit/v1_21_R5/util/CraftLegacy", "modern_" + name, "(Lorg/bukkit/Material;)I", false, samMethodType, instantiatedMethodType);
                                 return;
                           }
                        }

                        visitor.visit(opcode, owner, name, desc, itf, samMethodType, instantiatedMethodType);
                     } else if (owner.equals("org/bukkit/Particle") && name.equals("getDataType") && desc.equals("()Ljava/lang/Class;")) {
                        visitor.visit(184, "org/bukkit/craftbukkit/v1_21_R5/legacy/CraftEvil", name, "(Lorg/bukkit/Particle;)Ljava/lang/Class;", false, samMethodType, instantiatedMethodType);
                     } else if (owner.equals("org/bukkit/ChunkSnapshot") && name.equals("getBlockData") && desc.equals("(III)I")) {
                        visitor.visit(opcode, owner, "getData", desc, itf, samMethodType, instantiatedMethodType);
                     } else {
                        Type retType = Type.getReturnType(desc);
                        if (!Commodore.EVIL.contains(owner + " " + desc + " " + name) && (!owner.startsWith("org/bukkit/block/") || !(desc + " " + name).equals("()I getTypeId")) && (!owner.startsWith("org/bukkit/block/") || !(desc + " " + name).equals("(I)Z setTypeId"))) {
                           if (owner.equals("org/bukkit/DyeColor") && name.equals("valueOf") && desc.equals("(Ljava/lang/String;)Lorg/bukkit/DyeColor;")) {
                              visitor.visit(opcode, owner, "legacyValueOf", desc, itf, samMethodType, instantiatedMethodType);
                           } else {
                              if (owner.equals("org/bukkit/Material") || instantiatedMethodType != null && instantiatedMethodType.getDescriptor().startsWith("(Lorg/bukkit/Material;)")) {
                                 if (name.equals("getMaterial") && desc.equals("(I)Lorg/bukkit/Material;")) {
                                    visitor.visit(opcode, "org/bukkit/craftbukkit/v1_21_R5/legacy/CraftEvil", name, desc, itf, samMethodType, instantiatedMethodType);
                                    return;
                                 }

                                 switch (name) {
                                    case "values":
                                    case "valueOf":
                                    case "getMaterial":
                                    case "matchMaterial":
                                       visitor.visit(opcode, "org/bukkit/craftbukkit/v1_21_R5/legacy/CraftLegacy", name, desc, itf, samMethodType, instantiatedMethodType);
                                       return;
                                    case "ordinal":
                                       visitor.visit(184, "org/bukkit/craftbukkit/v1_21_R5/legacy/CraftLegacy", "ordinal", "(Lorg/bukkit/Material;)I", false, samMethodType, instantiatedMethodType);
                                       return;
                                    case "name":
                                    case "toString":
                                       visitor.visit(184, "org/bukkit/craftbukkit/v1_21_R5/legacy/CraftLegacy", name, "(Lorg/bukkit/Material;)Ljava/lang/String;", false, samMethodType, instantiatedMethodType);
                                       return;
                                 }
                              }

                              if (!owner.startsWith("org/bukkit") || !this.checkReroute(visitor, Commodore.this.materialReroute, opcode, owner, name, desc, samMethodType, instantiatedMethodType)) {
                                 visitor.visit(opcode, owner, name, desc, itf, samMethodType, instantiatedMethodType);
                              }
                           }
                        } else {
                           Type[] args = Type.getArgumentTypes(desc);
                           Type[] newArgs = new Type[args.length + 1];
                           newArgs[0] = Type.getObjectType(owner);
                           System.arraycopy(args, 0, newArgs, 1, args.length);
                           visitor.visit(184, "org/bukkit/craftbukkit/v1_21_R5/legacy/CraftEvil", name, Type.getMethodDescriptor(retType, newArgs), false, samMethodType, instantiatedMethodType);
                        }
                     }
                  }
               }

               private boolean checkReroute(MethodPrinter visitor, Reroute reroute, int opcode, String owner, String name, String desc, Type samMethodType, Type instantiatedMethodType) {
                  return Commodore.rerouteMethods(pluginVersion, reroute, opcode == 184 || opcode == 6, owner, name, desc, (data) -> {
                     visitor.visit(184, className, Commodore.buildMethodName(data), Commodore.buildMethodDesc(data), isInterface, samMethodType, instantiatedMethodType);
                     rerouteMethodData.add(data);
                  });
               }

               public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                  this.handleMethod((newOpcode, newOwner, newName, newDescription, newItf, newSam, newInstantiated) -> {
                     super.visitMethodInsn(newOpcode, newOwner, newName, newDescription, newItf);
                  }, opcode, owner, name, desc, itf, (Type)null, (Type)null);
               }

               public void visitLdcInsn(Object value) {
                  if (value instanceof String && ((String)value).equals("com.mysql.jdbc.Driver")) {
                     super.visitLdcInsn("com.mysql.cj.jdbc.Driver");
                  } else {
                     super.visitLdcInsn(value);
                  }
               }

               public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                  if (bootstrapMethodHandle.getOwner().equals("java/lang/invoke/LambdaMetafactory") && bootstrapMethodHandle.getName().equals("metafactory") && bootstrapMethodArguments.length == 3) {
                     Type samMethodType = (Type)bootstrapMethodArguments[0];
                     Handle implMethod = (Handle)bootstrapMethodArguments[1];
                     Type instantiatedMethodType = (Type)bootstrapMethodArguments[2];
                     this.handleMethod((newOpcode, newOwner, newName, newDescription, newItf, newSam, newInstantiated) -> {
                        if (newOpcode == 184) {
                           newOpcode = 6;
                        }

                        List<Object> methodArgs = new ArrayList();
                        methodArgs.add(newSam);
                        methodArgs.add(new Handle(newOpcode, newOwner, newName, newDescription, newItf));
                        methodArgs.add(newInstantiated);
                        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, methodArgs.toArray((x$0) -> {
                           return new Object[x$0];
                        }));
                     }, implMethod.getTag(), implMethod.getOwner(), implMethod.getName(), implMethod.getDesc(), implMethod.isInterface(), samMethodType, instantiatedMethodType);
                  } else {
                     super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
                  }
               }

               public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitAnnotation(descriptor, visible));
               }

               public AnnotationVisitor visitAnnotationDefault() {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitAnnotationDefault());
               }

               public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitInsnAnnotation(typeRef, typePath, descriptor, visible));
               }

               public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible));
               }

               public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitParameterAnnotation(parameter, descriptor, visible));
               }

               public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible));
               }

               public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
               }
            };
         }

         public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            return new FieldVisitor(this.api, super.visitField(access, name, descriptor, signature, value)) {
               public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitAnnotation(descriptor, visible));
               }

               public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
               }
            };
         }

         public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
            return new RecordComponentVisitor(this.api, super.visitRecordComponent(name, descriptor, signature)) {
               public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitAnnotation(descriptor, visible));
               }

               public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                  return Commodore.createAnnotationVisitor(pluginVersion, this.api, super.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
               }
            };
         }
      }, new SimpleRemapper(renames)), 0);
      return cw.toByteArray();
   }

   private static AnnotationVisitor createAnnotationVisitor(final ApiVersion apiVersion, int api, AnnotationVisitor delegate) {
      return new AnnotationVisitor(api, delegate) {
         public void visitEnum(String name, String descriptor, String value) {
            super.visitEnum(name, descriptor, FieldRename.rename(apiVersion, Type.getType(descriptor).getInternalName(), value));
         }

         public AnnotationVisitor visitArray(String name) {
            return Commodore.createAnnotationVisitor(apiVersion, this.api, super.visitArray(name));
         }

         public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            return Commodore.createAnnotationVisitor(apiVersion, this.api, super.visitAnnotation(name, descriptor));
         }
      };
   }

   public static boolean rerouteMethods(ApiVersion pluginVersion, Reroute reroute, boolean staticCall, String owner, String name, String desc, Consumer<RerouteMethodData> consumer) {
      return reroute.apply(pluginVersion, owner, name, desc, staticCall, consumer);
   }

   private static List<String> getMethodSignatures(byte[] clazz) {
      final List<String> methods = new ArrayList();
      ClassReader cr = new ClassReader(clazz);
      cr.accept(new ClassVisitor(589824) {
         public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            methods.add(descriptor + " " + name);
            return null;
         }
      }, 0);
      return methods;
   }

   private static String buildMethodName(RerouteMethodData rerouteMethodData) {
      String var10000 = rerouteMethodData.targetOwner().replace('/', '_');
      return "BUKKIT_CUSTOM_METHOD_" + var10000 + "_" + rerouteMethodData.targetName();
   }

   private static String buildMethodDesc(RerouteMethodData rerouteMethodData) {
      return Type.getMethodDescriptor(rerouteMethodData.sourceDesc().getReturnType(), (Type[])rerouteMethodData.arguments().stream().filter((a) -> {
         return !a.injectPluginName();
      }).filter((a) -> {
         return !a.injectPluginVersion();
      }).filter((a) -> {
         return a.injectCompatibility() == null;
      }).map(RerouteArgument::sourceType).toArray((x$0) -> {
         return new Type[x$0];
      }));
   }

   @FunctionalInterface
   private interface MethodPrinter {
      void visit(int var1, String var2, String var3, String var4, boolean var5, Type var6, Type var7);
   }
}
