package org.bukkit.craftbukkit.metadata;

import com.google.common.base.Preconditions;
import java.util.List;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class BlockMetadataStore extends MetadataStoreBase<Block> implements MetadataStore<Block> {
   private final World owningWorld;

   public BlockMetadataStore(World owningWorld) {
      this.owningWorld = owningWorld;
   }

   protected String disambiguate(Block block, String metadataKey) {
      String var10000 = Integer.toString(block.getX());
      return var10000 + ":" + Integer.toString(block.getY()) + ":" + Integer.toString(block.getZ()) + ":" + metadataKey;
   }

   public List<MetadataValue> getMetadata(Block block, String metadataKey) {
      Preconditions.checkArgument(block.getWorld() == this.owningWorld, "Block does not belong to world %s", this.owningWorld.getName());
      return super.getMetadata(block, metadataKey);
   }

   public boolean hasMetadata(Block block, String metadataKey) {
      Preconditions.checkArgument(block.getWorld() == this.owningWorld, "Block does not belong to world %s", this.owningWorld.getName());
      return super.hasMetadata(block, metadataKey);
   }

   public void removeMetadata(Block block, String metadataKey, Plugin owningPlugin) {
      Preconditions.checkArgument(block.getWorld() == this.owningWorld, "Block does not belong to world %s", this.owningWorld.getName());
      super.removeMetadata(block, metadataKey, owningPlugin);
   }

   public void setMetadata(Block block, String metadataKey, MetadataValue newMetadataValue) {
      Preconditions.checkArgument(block.getWorld() == this.owningWorld, "Block does not belong to world %s", this.owningWorld.getName());
      super.setMetadata(block, metadataKey, newMetadataValue);
   }
}
