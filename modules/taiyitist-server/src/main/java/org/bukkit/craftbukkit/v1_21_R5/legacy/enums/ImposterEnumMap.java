package org.bukkit.craftbukkit.v1_21_R5.legacy.enums;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ImposterEnumMap extends AbstractMap<Object, Object> {
   private final Class<?> objectClass;
   private final Map map;

   public ImposterEnumMap(Class<?> objectClass) {
      this.objectClass = objectClass;
      this.map = getMap(objectClass);
   }

   public ImposterEnumMap(EnumMap enumMap) {
      this.objectClass = DummyEnum.class;
      this.map = enumMap.clone();
   }

   public ImposterEnumMap(Map map) {
      if (map instanceof ImposterEnumMap) {
         this.objectClass = ((ImposterEnumMap)map).objectClass;
         this.map = getMap(this.objectClass);
      } else {
         this.objectClass = DummyEnum.class;
         this.map = new TreeMap();
      }

      this.map.putAll(map);
   }

   private static Map getMap(Class<?> objectClass) {
      return (Map)(objectClass.isEnum() ? new EnumMap(objectClass) : new HashMap());
   }

   public int size() {
      return this.map.size();
   }

   public boolean containsValue(Object value) {
      return this.map.containsValue(value);
   }

   public boolean containsKey(Object key) {
      return this.map.containsKey(key);
   }

   public Object get(Object key) {
      return this.map.get(key);
   }

   public Object put(Object key, Object value) {
      this.typeCheck(key);
      return this.map.put(key, value);
   }

   public Object remove(Object key) {
      return this.map.remove(key);
   }

   public void putAll(Map<? extends Object, ?> m) {
      if (this.map instanceof EnumMap) {
         this.map.putAll(m);
      }

      super.putAll(m);
   }

   public void clear() {
      this.map.clear();
   }

   public Set<Object> keySet() {
      return this.map.keySet();
   }

   public Collection<Object> values() {
      return this.map.values();
   }

   public Set<Map.Entry<Object, Object>> entrySet() {
      return this.map.entrySet();
   }

   public boolean equals(Object o) {
      return this.map.equals(o);
   }

   public int hashCode() {
      return this.map.hashCode();
   }

   public ImposterEnumMap clone() {
      ImposterEnumMap enumMap = new ImposterEnumMap(this.objectClass);
      enumMap.putAll(this.map);
      return enumMap;
   }

   private void typeCheck(Object object) {
      if (this.objectClass != DummyEnum.class && !this.objectClass.isAssignableFrom(object.getClass())) {
         String var10002 = String.valueOf(object.getClass());
         throw new ClassCastException(var10002 + " != " + String.valueOf(this.objectClass));
      }
   }
}
