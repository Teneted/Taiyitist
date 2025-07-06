package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.Instrument;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.HolderHandleable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftMusicInstrument extends MusicInstrument implements HolderHandleable<Instrument> {
   private final NamespacedKey key;
   private final Holder<Instrument> handle;

   public static MusicInstrument minecraftToBukkit(Instrument minecraft) {
      return (MusicInstrument)CraftRegistry.minecraftToBukkit(minecraft, Registries.INSTRUMENT, Registry.INSTRUMENT);
   }

   public static MusicInstrument minecraftHolderToBukkit(Holder<Instrument> minecraft) {
      Preconditions.checkArgument(minecraft != null);
      if (minecraft instanceof Holder.Reference<Instrument> holder) {
         MusicInstrument bukkit = (MusicInstrument)Registry.INSTRUMENT.get(CraftNamespacedKey.fromMinecraft(holder.key().location()));
         Preconditions.checkArgument(bukkit != null);
         return bukkit;
      } else {
         return new CraftMusicInstrument((NamespacedKey)null, minecraft);
      }
   }

   public static Instrument bukkitToMinecraft(MusicInstrument bukkit) {
      return (Instrument)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<Instrument> bukkitToMinecraftHolder(MusicInstrument bukkit) {
      return ((HolderHandleable)bukkit).getHandleHolder();
   }

   public static String bukkitToString(MusicInstrument bukkit) {
      Preconditions.checkArgument(bukkit != null);
      Holder<Instrument> holder = bukkitToMinecraftHolder(bukkit);
      return ((JsonElement)Instrument.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, CraftRegistry.getMinecraftRegistry()), holder).result().get()).toString();
   }

   public static MusicInstrument stringToBukkit(String string) {
      Preconditions.checkArgument(string != null);
      NamespacedKey key = NamespacedKey.fromString(string);
      if (key != null) {
         MusicInstrument bukkit = (MusicInstrument)Registry.INSTRUMENT.get(key);
         if (bukkit != null) {
            return bukkit;
         }
      }

      JsonElement json = JsonParser.parseString(string);
      DataResult<Pair<Holder<Instrument>, JsonElement>> result = Instrument.CODEC.decode(RegistryOps.create(JsonOps.INSTANCE, CraftRegistry.getMinecraftRegistry()), json);
      return minecraftHolderToBukkit((Holder)((Pair)result.getOrThrow()).getFirst());
   }

   public CraftMusicInstrument(NamespacedKey key, Holder<Instrument> handle) {
      this.key = key;
      this.handle = handle;
   }

   public Instrument getHandle() {
      return (Instrument)this.handle.value();
   }

   public Holder<Instrument> getHandleHolder() {
      return this.handle;
   }

   public String getDescription() {
      return CraftChatMessage.fromComponent(this.getHandle().description());
   }

   public float getDuration() {
      return this.getHandle().useDuration();
   }

   public float getRange() {
      return this.getHandle().range();
   }

   public Sound getSoundEvent() {
      return CraftSound.minecraftHolderToBukkit(this.getHandle().soundEvent());
   }

   @NotNull
   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof CraftMusicInstrument) {
         CraftMusicInstrument other = (CraftMusicInstrument)o;
         return this.key != null && other.key != null ? this.key.equals(other.key) : this.getHandle().equals(other.getHandle());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.key != null ? this.key.hashCode() : this.getHandle().hashCode();
   }

   public String toString() {
      String var10000 = String.valueOf(this.key);
      return "CraftMusicInstrument{key=" + var10000 + ", handle=" + String.valueOf(this.handle) + "}";
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
