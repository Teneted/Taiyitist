package org.bukkit.craftbukkit.metadata;

import org.bukkit.World;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;

public class WorldMetadataStore extends MetadataStoreBase<World> implements MetadataStore<World> {
   protected String disambiguate(World world, String metadataKey) {
      String var10000 = world.getUID().toString();
      return var10000 + ":" + metadataKey;
   }
}
