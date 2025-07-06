package org.bukkit.craftbukkit.inventory.components;

import com.google.common.base.Preconditions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.JukeboxPlayable;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.CraftJukeboxSong;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;

@SerializableAs("JukeboxPlayable")
public final class CraftJukeboxComponent implements JukeboxPlayableComponent {
   private JukeboxPlayable handle;

   public CraftJukeboxComponent(JukeboxPlayable jukebox) {
      this.handle = jukebox;
   }

   public CraftJukeboxComponent(CraftJukeboxComponent jukebox) {
      this.handle = jukebox.handle;
   }

   public CraftJukeboxComponent(Map<String, Object> map) {
      String song = (String)SerializableMeta.getObject(String.class, map, "song", false);
      this.handle = new JukeboxPlayable(new EitherHolder(ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.parse(song))));
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("song", this.getSongKey().toString());
      return result;
   }

   public JukeboxPlayable getHandle() {
      return this.handle;
   }

   public JukeboxSong getSong() {
      Optional<Holder<net.minecraft.world.item.JukeboxSong>> song = this.handle.song().unwrap(CraftRegistry.getMinecraftRegistry());
      return song.isPresent() ? CraftJukeboxSong.minecraftHolderToBukkit((Holder)song.get()) : null;
   }

   public NamespacedKey getSongKey() {
      return (NamespacedKey)this.handle.song().key().map(ResourceKey::location).map(CraftNamespacedKey::fromMinecraft).orElse((Object)null);
   }

   public void setSong(JukeboxSong song) {
      Preconditions.checkArgument(song != null, "song cannot be null");
      this.handle = new JukeboxPlayable(new EitherHolder(CraftJukeboxSong.bukkitToMinecraftHolder(song)));
   }

   public void setSongKey(NamespacedKey song) {
      Preconditions.checkArgument(song != null, "song cannot be null");
      this.handle = new JukeboxPlayable(new EitherHolder(ResourceKey.create(Registries.JUKEBOX_SONG, CraftNamespacedKey.toMinecraft(song))));
   }

   public int hashCode() {
      int hash = 7;
      hash = 73 * hash + Objects.hashCode(this.handle);
      return hash;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftJukeboxComponent other = (CraftJukeboxComponent)obj;
         return Objects.equals(this.handle, other.handle);
      }
   }

   public String toString() {
      return "CraftJukeboxComponent{handle=" + String.valueOf(this.handle) + "}";
   }
}
