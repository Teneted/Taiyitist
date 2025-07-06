package org.bukkit.craftbukkit.v1_21_R5.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.craftbukkit.v1_21_R5.damage.CraftDamageType;

public class CraftDamageTag extends CraftTag<DamageType, org.bukkit.damage.DamageType> {
   public CraftDamageTag(Registry<DamageType> registry, TagKey<DamageType> tag) {
      super(registry, tag);
   }

   public boolean isTagged(org.bukkit.damage.DamageType type) {
      return CraftDamageType.bukkitToMinecraftHolder(type).is(this.tag);
   }

   public Set<org.bukkit.damage.DamageType> getValues() {
      return (Set)this.getHandle().stream().map(Holder::value).map(CraftDamageType::minecraftToBukkit).collect(Collectors.toUnmodifiableSet());
   }
}
