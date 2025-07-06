package org.bukkit.craftbukkit.v1_21_R5.persistence;

import org.bukkit.persistence.PersistentDataAdapterContext;

public final class CraftPersistentDataAdapterContext implements PersistentDataAdapterContext {
   private final CraftPersistentDataTypeRegistry registry;

   public CraftPersistentDataAdapterContext(CraftPersistentDataTypeRegistry registry) {
      this.registry = registry;
   }

   public CraftPersistentDataContainer newPersistentDataContainer() {
      return new CraftPersistentDataContainer(this.registry);
   }
}
