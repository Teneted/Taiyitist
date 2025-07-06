package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import net.minecraft.core.registries.Registries;
import org.bukkit.GameEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftGameEvent extends GameEvent implements Handleable<net.minecraft.world.level.gameevent.GameEvent> {
   private final NamespacedKey key;
   private final net.minecraft.world.level.gameevent.GameEvent handle;

   public static GameEvent minecraftToBukkit(net.minecraft.world.level.gameevent.GameEvent minecraft) {
      return (GameEvent)CraftRegistry.minecraftToBukkit(minecraft, Registries.GAME_EVENT, Registry.GAME_EVENT);
   }

   public static net.minecraft.world.level.gameevent.GameEvent bukkitToMinecraft(GameEvent bukkit) {
      return (net.minecraft.world.level.gameevent.GameEvent)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public CraftGameEvent(NamespacedKey key, net.minecraft.world.level.gameevent.GameEvent handle) {
      this.key = key;
      this.handle = handle;
   }

   public net.minecraft.world.level.gameevent.GameEvent getHandle() {
      return this.handle;
   }

   @NotNull
   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof CraftGameEvent) ? false : this.getKey().equals(((GameEvent)other).getKey());
      }
   }

   public int hashCode() {
      return this.getKey().hashCode();
   }

   public String toString() {
      return "CraftGameEvent{key=" + String.valueOf(this.key) + "}";
   }

   @NotNull
   public NamespacedKey getKeyOrThrow() {
      Preconditions.checkState(this.isRegistered(), "Cannot get key of this registry item, because it is not registered. Use #isRegistered() before calling this method.");
      return this.key;
   }

   @Nullable
   public NamespacedKey getKeyOrNull() {
      return this.key;
   }

   public boolean isRegistered() {
      return this.key != null;
   }
}
