package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Panda.Gene;

public class CraftPanda extends CraftAnimals implements Panda {
   public CraftPanda(CraftServer server, net.minecraft.world.entity.animal.Panda entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Panda getHandle() {
      return (net.minecraft.world.entity.animal.Panda)super.getHandle();
   }

   public String toString() {
      return "CraftPanda";
   }

   public Panda.Gene getMainGene() {
      return fromNms(this.getHandle().getMainGene());
   }

   public void setMainGene(Panda.Gene gene) {
      this.getHandle().setMainGene(toNms(gene));
   }

   public Panda.Gene getHiddenGene() {
      return fromNms(this.getHandle().getHiddenGene());
   }

   public void setHiddenGene(Panda.Gene gene) {
      this.getHandle().setHiddenGene(toNms(gene));
   }

   public boolean isRolling() {
      return this.getHandle().isRolling();
   }

   public void setRolling(boolean flag) {
      this.getHandle().roll(flag);
   }

   public boolean isSneezing() {
      return this.getHandle().isSneezing();
   }

   public void setSneezing(boolean flag) {
      this.getHandle().sneeze(flag);
   }

   public boolean isSitting() {
      return this.getHandle().isSitting();
   }

   public void setSitting(boolean flag) {
      this.getHandle().sit(flag);
   }

   public boolean isOnBack() {
      return this.getHandle().isOnBack();
   }

   public void setOnBack(boolean flag) {
      this.getHandle().setOnBack(flag);
   }

   public boolean isEating() {
      return this.getHandle().isEating();
   }

   public void setEating(boolean flag) {
      this.getHandle().eat(flag);
   }

   public boolean isScared() {
      return this.getHandle().isScared();
   }

   public int getUnhappyTicks() {
      return this.getHandle().getUnhappyCounter();
   }

   public static Panda.Gene fromNms(net.minecraft.world.entity.animal.Panda.Gene gene) {
      Preconditions.checkArgument(gene != null, "Gene may not be null");
      return Gene.values()[gene.ordinal()];
   }

   public static net.minecraft.world.entity.animal.Panda.Gene toNms(Panda.Gene gene) {
      Preconditions.checkArgument(gene != null, "Gene may not be null");
      return net.minecraft.world.entity.animal.Panda.Gene.values()[gene.ordinal()];
   }
}
