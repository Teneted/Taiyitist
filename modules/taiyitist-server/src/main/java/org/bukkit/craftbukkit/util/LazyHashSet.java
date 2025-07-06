package org.bukkit.craftbukkit.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class LazyHashSet<E> implements Set<E> {
   Set<E> reference = null;

   public int size() {
      return this.getReference().size();
   }

   public boolean isEmpty() {
      return this.getReference().isEmpty();
   }

   public boolean contains(Object o) {
      return this.getReference().contains(o);
   }

   public Iterator<E> iterator() {
      return this.getReference().iterator();
   }

   public Object[] toArray() {
      return this.getReference().toArray();
   }

   public <T> T[] toArray(T[] a) {
      return this.getReference().toArray(a);
   }

   public boolean add(E o) {
      return this.getReference().add(o);
   }

   public boolean remove(Object o) {
      return this.getReference().remove(o);
   }

   public boolean containsAll(Collection<?> c) {
      return this.getReference().containsAll(c);
   }

   public boolean addAll(Collection<? extends E> c) {
      return this.getReference().addAll(c);
   }

   public boolean retainAll(Collection<?> c) {
      return this.getReference().retainAll(c);
   }

   public boolean removeAll(Collection<?> c) {
      return this.getReference().removeAll(c);
   }

   public void clear() {
      this.getReference().clear();
   }

   public Set<E> getReference() {
      Set<E> reference = this.reference;
      return reference != null ? reference : (this.reference = this.makeReference());
   }

   abstract Set<E> makeReference();

   public boolean isLazy() {
      return this.reference == null;
   }

   public int hashCode() {
      return 157 * this.getReference().hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         LazyHashSet<?> that = (LazyHashSet)obj;
         return this.isLazy() && that.isLazy() || this.getReference().equals(that.getReference());
      } else {
         return false;
      }
   }

   public String toString() {
      return this.getReference().toString();
   }
}
