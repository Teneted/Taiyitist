package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.animal.Animal;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.entity.Animals;
import org.bukkit.inventory.ItemStack;

public class CraftAnimals extends CraftAgeable implements Animals {
   public CraftAnimals(CraftServer server, Animal entity) {
      super(server, entity);
   }

   public Animal getHandle() {
      return (Animal)this.entity;
   }

   public String toString() {
      return "CraftAnimals";
   }

   public UUID getBreedCause() {
      EntityReference<ServerPlayer> loveCause = this.getHandle().loveCause;
      return loveCause != null ? loveCause.getUUID() : null;
   }

   public void setBreedCause(UUID uuid) {
      this.getHandle().loveCause = uuid != null ? new EntityReference(uuid) : null;
   }

   public boolean isLoveMode() {
      return this.getHandle().isInLove();
   }

   public void setLoveModeTicks(int ticks) {
      Preconditions.checkArgument(ticks >= 0, "Love mode ticks must be positive or 0");
      this.getHandle().setInLoveTime(ticks);
   }

   public int getLoveModeTicks() {
      return this.getHandle().inLove;
   }

   public boolean isBreedItem(ItemStack itemStack) {
      return this.getHandle().isFood(CraftItemStack.asNMSCopy(itemStack));
   }

   public boolean isBreedItem(Material material) {
      return this.isBreedItem(new ItemStack(material));
   }
}
