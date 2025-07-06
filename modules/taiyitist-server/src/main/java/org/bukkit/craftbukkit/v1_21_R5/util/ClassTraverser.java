package org.bukkit.craftbukkit.v1_21_R5.util;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ClassTraverser implements Iterator<Class<?>> {
   private final Set<Class<?>> visit = new HashSet();
   private final Set<Class<?>> toVisit = new HashSet();
   private Class<?> next;

   public ClassTraverser(Class<?> next) {
      this.next = next;
   }

   public boolean hasNext() {
      return this.next != null;
   }

   public Class<?> next() {
      Class<?> clazz = this.next;
      this.visit.add(this.next);
      Set<Class<?>> classes = Sets.newHashSet(clazz.getInterfaces());
      classes.add(clazz.getSuperclass());
      classes.remove((Object)null);
      classes.removeAll(this.visit);
      this.toVisit.addAll(classes);
      if (this.toVisit.isEmpty()) {
         this.next = null;
         return clazz;
      } else {
         this.next = (Class)this.toVisit.iterator().next();
         this.toVisit.remove(this.next);
         return clazz;
      }
   }
}
