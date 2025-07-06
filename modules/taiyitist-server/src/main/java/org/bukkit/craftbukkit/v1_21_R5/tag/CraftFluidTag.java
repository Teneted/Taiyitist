package org.bukkit.craftbukkit.v1_21_R5.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.bukkit.craftbukkit.v1_21_R5.CraftFluid;

public class CraftFluidTag extends CraftTag<Fluid, org.bukkit.Fluid> {
   public CraftFluidTag(Registry<Fluid> registry, TagKey<Fluid> tag) {
      super(registry, tag);
   }

   public boolean isTagged(org.bukkit.Fluid fluid) {
      return CraftFluid.bukkitToMinecraft(fluid).is(this.tag);
   }

   public Set<org.bukkit.Fluid> getValues() {
      return (Set)this.getHandle().stream().map(Holder::value).map(CraftFluid::minecraftToBukkit).collect(Collectors.toUnmodifiableSet());
   }
}
