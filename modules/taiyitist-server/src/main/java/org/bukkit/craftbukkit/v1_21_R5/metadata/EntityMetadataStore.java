package org.bukkit.craftbukkit.v1_21_R5.metadata;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;

public class EntityMetadataStore extends MetadataStoreBase<Entity> implements MetadataStore<Entity> {
   protected String disambiguate(Entity entity, String metadataKey) {
      String var10000 = entity.getUniqueId().toString();
      return var10000 + ":" + metadataKey;
   }
}
