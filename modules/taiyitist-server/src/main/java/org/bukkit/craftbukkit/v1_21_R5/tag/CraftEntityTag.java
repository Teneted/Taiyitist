package org.bukkit.craftbukkit.v1_21_R5.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntityType;

public class CraftEntityTag extends CraftTag<EntityType<?>, org.bukkit.entity.EntityType> {
   public CraftEntityTag(Registry<EntityType<?>> registry, TagKey<EntityType<?>> tag) {
      super(registry, tag);
   }

   public boolean isTagged(org.bukkit.entity.EntityType entity) {
      return CraftEntityType.bukkitToMinecraft(entity).is(this.tag);
   }

   public Set<org.bukkit.entity.EntityType> getValues() {
      return (Set)this.getHandle().stream().map(Holder::value).map(CraftEntityType::minecraftToBukkit).collect(Collectors.toUnmodifiableSet());
   }
}
