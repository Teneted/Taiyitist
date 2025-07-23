package com.taiyitistmc.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import sun.misc.Unsafe;

public class EnumHelper {

    public static final Unsafe unsafe;
    private static final MethodHandles.Lookup implLookup;
    private static final List<String> ENUM_CACHE = List.of("enumConstantDirectory", "enumConstants", "enumVars");

    public EnumHelper() {
    }

    private static <T> T makeEnum(Class<T> enumClass, String value, int ordinal, List<Class<?>> additionalParameterTypes, List<Object> additionalValues) {
        try {
            implLookup.ensureInitialized(enumClass);
            List<Class<?>> ptypes = new ArrayList(additionalParameterTypes.size() + 2);
            ptypes.add(String.class);
            ptypes.add(Integer.TYPE);
            ptypes.addAll(additionalParameterTypes);
            MethodHandle constructor = implLookup.findConstructor(enumClass, MethodType.methodType(Void.TYPE, ptypes));
            List<Object> arguments = new ArrayList(additionalValues.size() + 2);
            arguments.add(normalizeName(value));
            arguments.add(ordinal);
            arguments.addAll(additionalValues);
            return (T) constructor.invokeWithArguments(arguments);
        } catch (Throwable var8) {
            Throwable e = var8;
            e.fillInStackTrace();
            return null;
        }
    }

    private static void cleanEnumCache(Class<?> enumClass) {
        ENUM_CACHE.forEach((s) -> {
            Arrays.stream(Class.class.getDeclaredFields()).filter((field) -> {
                return field.getName().equals(s);
            }).forEachOrdered((field) -> {
                unsafe.putObjectVolatile(enumClass, unsafe.objectFieldOffset(field), (Object)null);
            });
        });
    }

    public static <T> T addEnum(Class<T> cl, String name, List<Class<?>> additionalParameterTypes, List<Object> additionalValues) {
        try {
            implLookup.ensureInitialized(cl);
            Field[] var15 = cl.getDeclaredFields();
            int var5 = var15.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Field field = var15[var6];
                if (field.getName().equals("$VALUES") || field.getName().equals("ENUM$VALUES")) {
                    Object base = unsafe.staticFieldBase(field);
                    long offset = unsafe.staticFieldOffset(field);
                    T[] arr = (T[]) unsafe.getObject(base, offset);
                    T[] newArr = (T[]) Array.newInstance(cl, arr.length + 1);
                    System.arraycopy(arr, 0, newArr, 0, arr.length);
                    T newInstance = makeEnum(cl, name, arr.length, additionalParameterTypes, additionalValues);
                    newArr[arr.length] = newInstance;
                    unsafe.putObject(base, offset, newArr);
                    cleanEnumCache(cl);
                    return newInstance;
                }
            }

            return null;
        } catch (Throwable var14) {
            Throwable e = var14;
            e.fillInStackTrace();
            return null;
        }
    }

    public static <T> T addEnum(Class<T> cl, String name) {
        return addEnum(cl, name, List.of(), List.of());
    }

    public static String normalizeName(String name) {
        return name.replace(':', '_').replaceAll("\\s+", "_").replaceAll("\\W", "").toUpperCase(Locale.ENGLISH);
    }

    static {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe)unsafeField.get((Object)null);
            MethodHandles.lookup().ensureInitialized(MethodHandles.Lookup.class);
            Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            implLookup = (MethodHandles.Lookup)unsafe.getObject(unsafe.staticFieldBase(implLookupField), unsafe.staticFieldOffset(implLookupField));
        } catch (Exception var2) {
            Exception e = var2;
            throw new RuntimeException(e);
        }
    }
}
