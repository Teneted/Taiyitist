package org.bukkit.craftbukkit.v1_21_R5.advancement;

import java.util.List;
import org.bukkit.advancement.AdvancementRequirement;
import org.bukkit.advancement.AdvancementRequirements;
import org.jetbrains.annotations.NotNull;

public class CraftAdvancementRequirements implements AdvancementRequirements {
   private final net.minecraft.advancements.AdvancementRequirements requirements;

   public CraftAdvancementRequirements(net.minecraft.advancements.AdvancementRequirements requirements) {
      this.requirements = requirements;
   }

   @NotNull
   public List<AdvancementRequirement> getRequirements() {
      return this.requirements.requirements().stream().map((requirement) -> {
         return new CraftAdvancementRequirement(requirement);
      }).toList();
   }
}
