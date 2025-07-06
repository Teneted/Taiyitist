package org.bukkit.craftbukkit.v1_21_R5.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlockType;

public class CraftBlockTag extends CraftTag<Block, Material> {
   public CraftBlockTag(Registry<Block> registry, TagKey<Block> tag) {
      super(registry, tag);
   }

   public boolean isTagged(Material item) {
      Block block = CraftBlockType.bukkitToMinecraft(item);
      return block == null ? false : block.builtInRegistryHolder().is(this.tag);
   }

   public Set<Material> getValues() {
      return (Set)this.getHandle().stream().map((block) -> {
         return CraftBlockType.minecraftToBukkit((Block)block.value());
      }).collect(Collectors.toUnmodifiableSet());
   }
}
