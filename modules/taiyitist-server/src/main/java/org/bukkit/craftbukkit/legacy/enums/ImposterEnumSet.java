package org.bukkit.craftbukkit.legacy.enums;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.Registry;
import org.bukkit.util.OldEnum;
import org.jetbrains.annotations.NotNull;

public class ImposterEnumSet extends AbstractSet<Object> {
   private final Class<?> objectClass;
   private final Set set;

   private static Set createSet(Class<?> clazz) {
      return (Set)(clazz.isEnum() ? EnumSet.noneOf(clazz) : new TreeSet());
   }

   public static ImposterEnumSet noneOf(Class<?> clazz) {
      Set set = createSet(clazz);
      return new ImposterEnumSet(set, clazz);
   }

   public static ImposterEnumSet allOf(Class<?> clazz) {
      Object set;
      if (clazz.isEnum()) {
         set = EnumSet.allOf(clazz);
      } else {
         set = new HashSet();
         Registry registry = EnumEvil.getRegistry(clazz);
         if (registry == null) {
            throw new IllegalArgumentException("Class " + String.valueOf(clazz) + " is not an Enum nor an OldEnum");
         }

         Iterator var3 = registry.iterator();

         while(var3.hasNext()) {
            Object object = var3.next();
            ((Set)set).add(object);
         }
      }

      return new ImposterEnumSet((Set)set, clazz);
   }

   public static ImposterEnumSet copyOf(Set set) {
      Class clazz;
      if (set instanceof ImposterEnumSet imposter) {
         set = imposter.set;
         clazz = imposter.objectClass;
      } else if (!set.isEmpty()) {
         clazz = (Class)set.stream().filter((val) -> {
            return val != null;
         }).map((val) -> {
            return val.getClass();
         }).findAny().orElse(Object.class);
      } else {
         clazz = Object.class;
      }

      Set newSet = createSet(clazz);
      newSet.addAll(set);
      return new ImposterEnumSet(newSet, clazz);
   }

   public static ImposterEnumSet copyOf(Collection collection) {
      Class clazz;
      if (collection instanceof ImposterEnumSet imposter) {
         collection = imposter.set;
         clazz = imposter.objectClass;
      } else if (!((Collection)collection).isEmpty()) {
         clazz = (Class)((Collection)collection).stream().filter((val) -> {
            return val != null;
         }).map((val) -> {
            return val.getClass();
         }).findAny().orElse(Object.class);
      } else {
         clazz = Object.class;
      }

      Set newSet = createSet(clazz);
      newSet.addAll((Collection)collection);
      return new ImposterEnumSet(newSet, clazz);
   }

   public static ImposterEnumSet complementOf(Set set) {
      Class<?> clazz = null;
      if (set instanceof ImposterEnumSet imposter) {
         set = imposter.set;
         clazz = imposter.objectClass;
      }

      if (set instanceof EnumSet<?> enumSet) {
         enumSet = EnumSet.complementOf(enumSet);
         if (clazz != null) {
            return new ImposterEnumSet(enumSet, clazz);
         } else {
            if (!set.isEmpty()) {
               clazz = (Class)set.stream().filter((val) -> {
                  return val != null;
               }).map((val) -> {
                  return val.getClass();
               }).findAny().orElse(Object.class);
            } else {
               clazz = (Class)enumSet.stream().filter((val) -> {
                  return val != null;
               }).map((val) -> {
                  return val.getClass();
               }).map((val) -> {
                  return val;
               }).findAny().orElse(Object.class);
            }

            return new ImposterEnumSet(enumSet, clazz);
         }
      } else if (set.isEmpty() && clazz == null) {
         throw new IllegalStateException("Class is null and set is empty, cannot get class!");
      } else {
         if (clazz == null) {
            clazz = (Class)set.stream().filter((val) -> {
               return val != null;
            }).map((val) -> {
               return val.getClass();
            }).findAny().orElse(Object.class);
         }

         Registry registry = EnumEvil.getRegistry(clazz);
         Set newSet = new HashSet();
         Iterator var4 = registry.iterator();

         while(var4.hasNext()) {
            Object value = var4.next();
            if (!set.contains(value)) {
               newSet.add(value);
            }
         }

         return new ImposterEnumSet(newSet, clazz);
      }
   }

   public static ImposterEnumSet of(Object e) {
      Set set = createSet(e.getClass());
      set.add(e);
      return new ImposterEnumSet(set, e.getClass());
   }

   public static ImposterEnumSet of(Object e1, Object e2) {
      Set set = createSet(e1.getClass());
      set.add(e1);
      set.add(e2);
      return new ImposterEnumSet(set, e1.getClass());
   }

   public static ImposterEnumSet of(Object e1, Object e2, Object e3) {
      Set set = createSet(e1.getClass());
      set.add(e1);
      set.add(e2);
      set.add(e3);
      return new ImposterEnumSet(set, e1.getClass());
   }

   public static ImposterEnumSet of(Object e1, Object e2, Object e3, Object e4) {
      Set set = createSet(e1.getClass());
      set.add(e1);
      set.add(e2);
      set.add(e3);
      set.add(e4);
      return new ImposterEnumSet(set, e1.getClass());
   }

   public static ImposterEnumSet of(Object e1, Object e2, Object e3, Object e4, Object e5) {
      Set set = createSet(e1.getClass());
      set.add(e1);
      set.add(e2);
      set.add(e3);
      set.add(e4);
      set.add(e5);
      return new ImposterEnumSet(set, e1.getClass());
   }

   public static ImposterEnumSet of(Object e, Object... rest) {
      Set set = createSet(e.getClass());
      set.add(e);
      Collections.addAll(set, rest);
      return new ImposterEnumSet(set, e.getClass());
   }

   public static ImposterEnumSet range(Object from, Object to) {
      Object set;
      if (from.getClass().isEnum()) {
         set = EnumSet.range((Enum)from, (Enum)to);
      } else {
         set = new HashSet();
         Registry registry = EnumEvil.getRegistry(from.getClass());
         Iterator var4 = registry.iterator();

         while(var4.hasNext()) {
            Object o = var4.next();
            if (((OldEnum)o).ordinal() >= ((OldEnum)from).ordinal() && ((OldEnum)o).ordinal() <= ((OldEnum)to).ordinal()) {
               ((Set)set).add(o);
            }
         }
      }

      return new ImposterEnumSet((Set)set, from.getClass());
   }

   private ImposterEnumSet(Set set, Class<?> objectClass) {
      this.set = set;
      this.objectClass = objectClass;
   }

   public Iterator<Object> iterator() {
      return this.set.iterator();
   }

   public int size() {
      return this.set.size();
   }

   public boolean equals(Object o) {
      return this.set.equals(o);
   }

   public int hashCode() {
      return this.set.hashCode();
   }

   public boolean removeAll(Collection<?> c) {
      return this.set.removeAll(c);
   }

   public boolean isEmpty() {
      return this.set.isEmpty();
   }

   public boolean contains(Object o) {
      return this.set.contains(o);
   }

   @NotNull
   public Object[] toArray() {
      return this.set.toArray();
   }

   @NotNull
   public <T> T[] toArray(@NotNull T[] a) {
      return this.set.toArray(a);
   }

   public boolean add(Object o) {
      this.typeCheck(o);
      return this.set.add(o);
   }

   public boolean remove(Object o) {
      return this.set.remove(o);
   }

   public boolean containsAll(@NotNull Collection<?> c) {
      return this.set.containsAll(c);
   }

   public boolean addAll(@NotNull Collection<?> c) {
      if (this.set instanceof EnumSet) {
         this.set.addAll(c);
      }

      return super.addAll(c);
   }

   public boolean retainAll(@NotNull Collection<?> c) {
      return this.set.retainAll(c);
   }

   public void clear() {
      this.set.clear();
   }

   public String toString() {
      return this.set.toString();
   }

   public ImposterEnumSet clone() {
      Set var3 = this.set;
      Object newSet;
      if (var3 instanceof EnumSet<?> enumSet) {
         newSet = enumSet.clone();
      } else {
         newSet = new HashSet();
         ((Set)newSet).addAll(this.set);
      }

      return new ImposterEnumSet((Set)newSet, this.objectClass);
   }

   private void typeCheck(Object object) {
      if (this.objectClass != DummyEnum.class && !this.objectClass.isAssignableFrom(object.getClass())) {
         String var10002 = String.valueOf(object.getClass());
         throw new ClassCastException(var10002 + " != " + String.valueOf(this.objectClass));
      }
   }
}
