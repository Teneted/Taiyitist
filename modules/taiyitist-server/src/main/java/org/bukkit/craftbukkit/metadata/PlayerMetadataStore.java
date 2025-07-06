package org.bukkit.craftbukkit.metadata;

import org.bukkit.OfflinePlayer;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;

public class PlayerMetadataStore extends MetadataStoreBase<OfflinePlayer> implements MetadataStore<OfflinePlayer> {
   protected String disambiguate(OfflinePlayer player, String metadataKey) {
      String var10000 = String.valueOf(player.getUniqueId());
      return var10000 + ":" + metadataKey;
   }
}
