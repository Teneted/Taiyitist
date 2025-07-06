package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.animal.Rabbit.Variant;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Rabbit;

public class CraftRabbit extends CraftAnimals implements Rabbit {
   public CraftRabbit(CraftServer server, net.minecraft.world.entity.animal.Rabbit entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Rabbit getHandle() {
      return (net.minecraft.world.entity.animal.Rabbit)this.entity;
   }

   public String toString() {
      return "CraftRabbit{RabbitType=" + String.valueOf(this.getRabbitType()) + "}";
   }

   public Rabbit.Type getRabbitType() {
      return Type.values()[this.getHandle().getVariant().ordinal()];
   }

   public void setRabbitType(Rabbit.Type type) {
      this.getHandle().setVariant(Variant.values()[type.ordinal()]);
   }
}
