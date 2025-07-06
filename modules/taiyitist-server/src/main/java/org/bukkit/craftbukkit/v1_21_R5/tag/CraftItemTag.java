package org.bukkit.craftbukkit.v1_21_R5.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemType;

public class CraftItemTag extends CraftTag<Item, Material> {
   public CraftItemTag(Registry<Item> registry, TagKey<Item> tag) {
      super(registry, tag);
   }

   public boolean isTagged(Material item) {
      Item minecraft = CraftItemType.bukkitToMinecraft(item);
      return minecraft == null ? false : minecraft.builtInRegistryHolder().is(this.tag);
   }

   public Set<Material> getValues() {
      return (Set)this.getHandle().stream().map((item) -> {
         return CraftItemType.minecraftToBukkit((Item)item.value());
      }).collect(Collectors.toUnmodifiableSet());
   }
}
