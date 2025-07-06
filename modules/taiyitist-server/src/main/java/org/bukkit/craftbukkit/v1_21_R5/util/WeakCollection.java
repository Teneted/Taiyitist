package org.bukkit.craftbukkit.v1_21_R5.util;

import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class WeakCollection<T> implements Collection<T> {
   static final Object NO_VALUE = new Object();
   private final Collection<WeakReference<T>> collection = new ArrayList();

   public boolean add(T value) {
      Preconditions.checkArgument(value != null, "Cannot add null value");
      return this.collection.add(new WeakReference(value));
   }

   public boolean addAll(Collection<? extends T> collection) {
      Collection<WeakReference<T>> values = this.collection;
      boolean ret = false;

      Object value;
      for(Iterator var4 = collection.iterator(); var4.hasNext(); ret |= values.add(new WeakReference(value))) {
         value = var4.next();
         Preconditions.checkArgument(value != null, "Cannot add null value");
      }

      return ret;
   }

   public void clear() {
      this.collection.clear();
   }

   public boolean contains(Object object) {
      if (object == null) {
         return false;
      } else {
         Iterator var2 = this.iterator();

         Object compare;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            compare = var2.next();
         } while(!object.equals(compare));

         return true;
      }
   }

   public boolean containsAll(Collection<?> collection) {
      return this.toCollection().containsAll(collection);
   }

   public boolean isEmpty() {
      return !this.iterator().hasNext();
   }

   public Iterator<T> iterator() {
      return new Iterator<T>() {
         Iterator<WeakReference<T>> it;
         Object value;

         {
            this.it = WeakCollection.this.collection.iterator();
            this.value = WeakCollection.NO_VALUE;
         }

         public boolean hasNext() {
            Object value = this.value;
            if (value != null && value != WeakCollection.NO_VALUE) {
               return true;
            } else {
               Iterator<WeakReference<T>> it = this.it;
               value = null;

               while(it.hasNext()) {
                  WeakReference<T> ref = (WeakReference)it.next();
                  value = ref.get();
                  if (value != null) {
                     this.value = value;
                     return true;
                  }

                  it.remove();
               }

               return false;
            }
         }

         public T next() throws NoSuchElementException {
            if (!this.hasNext()) {
               throw new NoSuchElementException("No more elements");
            } else {
               T value = this.value;
               this.value = WeakCollection.NO_VALUE;
               return value;
            }
         }

         public void remove() throws IllegalStateException {
            Preconditions.checkState(this.value == WeakCollection.NO_VALUE, "No last element");
            this.value = null;
            this.it.remove();
         }
      };
   }

   public boolean remove(Object object) {
      if (object == null) {
         return false;
      } else {
         Iterator<T> it = this.iterator();

         do {
            if (!it.hasNext()) {
               return false;
            }
         } while(!object.equals(it.next()));

         it.remove();
         return true;
      }
   }

   public boolean removeAll(Collection<?> collection) {
      Iterator<T> it = this.iterator();
      boolean ret = false;

      while(it.hasNext()) {
         if (collection.contains(it.next())) {
            ret = true;
            it.remove();
         }
      }

      return ret;
   }

   public boolean retainAll(Collection<?> collection) {
      Iterator<T> it = this.iterator();
      boolean ret = false;

      while(it.hasNext()) {
         if (!collection.contains(it.next())) {
            ret = true;
            it.remove();
         }
      }

      return ret;
   }

   public int size() {
      int s = 0;

      for(Iterator var2 = this.iterator(); var2.hasNext(); ++s) {
         T value = var2.next();
      }

      return s;
   }

   public Object[] toArray() {
      return this.toArray(new Object[0]);
   }

   public <T> T[] toArray(T[] array) {
      return this.toCollection().toArray(array);
   }

   private Collection<T> toCollection() {
      ArrayList<T> collection = new ArrayList();
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         T value = var2.next();
         collection.add(value);
      }

      return collection;
   }
}
