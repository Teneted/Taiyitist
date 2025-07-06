package org.bukkit.craftbukkit.v1_21_R5;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_21_R5.registry.CraftRegistryItem;
import org.jetbrains.annotations.NotNull;

public class CraftJukeboxSong extends CraftRegistryItem<JukeboxSong> implements org.bukkit.JukeboxSong {
   public static org.bukkit.JukeboxSong minecraftToBukkit(JukeboxSong minecraft) {
      return (org.bukkit.JukeboxSong)CraftRegistry.minecraftToBukkit(minecraft, Registries.JUKEBOX_SONG, Registry.JUKEBOX_SONG);
   }

   public static org.bukkit.JukeboxSong minecraftHolderToBukkit(Holder<JukeboxSong> minecraft) {
      return minecraftToBukkit((JukeboxSong)minecraft.value());
   }

   public static JukeboxSong bukkitToMinecraft(org.bukkit.JukeboxSong bukkit) {
      return (JukeboxSong)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<JukeboxSong> bukkitToMinecraftHolder(org.bukkit.JukeboxSong bukkit) {
      Preconditions.checkArgument(bukkit != null);
      net.minecraft.core.Registry<JukeboxSong> registry = CraftRegistry.getMinecraftRegistry(Registries.JUKEBOX_SONG);
      Holder var3 = registry.wrapAsHolder(bukkitToMinecraft(bukkit));
      if (var3 instanceof Holder.Reference<JukeboxSong> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own trim pattern without properly registering it.");
      }
   }

   public CraftJukeboxSong(NamespacedKey key, Holder<JukeboxSong> handle) {
      super(key, handle);
   }

   @NotNull
   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   @NotNull
   public String getTranslationKey() {
      return ((TranslatableContents)((JukeboxSong)this.getHandle()).description().getContents()).getKey();
   }
}
