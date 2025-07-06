package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.animal.Rabbit.Variant;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;

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
