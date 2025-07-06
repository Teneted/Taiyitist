package org.bukkit.craftbukkit.util;

import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutionException;

public abstract class Waitable<T> implements Runnable {
   Throwable t = null;
   T value = null;
   Status status;

   public Waitable() {
      this.status = Waitable.Status.WAITING;
   }

   public final void run() {
      synchronized(this) {
         Preconditions.checkState(this.status == Waitable.Status.WAITING, "Invalid state %s", this.status);
         this.status = Waitable.Status.RUNNING;
      }

      boolean var14 = false;

      label114: {
         try {
            var14 = true;
            this.value = this.evaluate();
            var14 = false;
            break label114;
         } catch (Throwable var19) {
            Throwable t = var19;
            this.t = t;
            var14 = false;
         } finally {
            if (var14) {
               synchronized(this) {
                  this.status = Waitable.Status.FINISHED;
                  this.notifyAll();
               }
            }
         }

         synchronized(this) {
            this.status = Waitable.Status.FINISHED;
            this.notifyAll();
            return;
         }
      }

      synchronized(this) {
         this.status = Waitable.Status.FINISHED;
         this.notifyAll();
      }

   }

   protected abstract T evaluate();

   public synchronized T get() throws InterruptedException, ExecutionException {
      while(this.status != Waitable.Status.FINISHED) {
         this.wait();
      }

      if (this.t != null) {
         throw new ExecutionException(this.t);
      } else {
         return this.value;
      }
   }

   private static enum Status {
      WAITING,
      RUNNING,
      FINISHED;

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{WAITING, RUNNING, FINISHED};
      }
   }
}
