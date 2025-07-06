package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.world.item.Item;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.entity.Piglin;
import org.bukkit.inventory.Inventory;

public class CraftPiglin extends CraftPiglinAbstract implements Piglin {
   public CraftPiglin(CraftServer server, net.minecraft.world.entity.monster.piglin.Piglin entity) {
      super(server, entity);
   }

   public boolean isAbleToHunt() {
      return this.getHandle().cannotHunt;
   }

   public void setIsAbleToHunt(boolean flag) {
      this.getHandle().cannotHunt = flag;
   }

   public boolean addBarterMaterial(Material material) {
      Preconditions.checkArgument(material != null, "material cannot be null");
      Item item = CraftItemType.bukkitToMinecraft(material);
      return this.getHandle().bridge$allowedBarterItems().add(item);
   }

   public boolean removeBarterMaterial(Material material) {
      Preconditions.checkArgument(material != null, "material cannot be null");
      Item item = CraftItemType.bukkitToMinecraft(material);
      return this.getHandle().bridge$allowedBarterItems().remove(item);
   }

   public boolean addMaterialOfInterest(Material material) {
      Preconditions.checkArgument(material != null, "material cannot be null");
      Item item = CraftItemType.bukkitToMinecraft(material);
      return this.getHandle().bridge$interestItems().add(item);
   }

   public boolean removeMaterialOfInterest(Material material) {
      Preconditions.checkArgument(material != null, "material cannot be null");
      Item item = CraftItemType.bukkitToMinecraft(material);
      return this.getHandle().bridge$interestItems().remove(item);
   }

   public Set<Material> getInterestList() {
      return Collections.unmodifiableSet((Set)this.getHandle().bridge$interestItems().stream().map(CraftItemType::minecraftToBukkit).collect(Collectors.toSet()));
   }

   public Set<Material> getBarterList() {
      return Collections.unmodifiableSet((Set)this.getHandle().bridge$allowedBarterItems().stream().map(CraftItemType::minecraftToBukkit).collect(Collectors.toSet()));
   }

   public Inventory getInventory() {
      return new CraftInventory(this.getHandle().inventory);
   }

   public net.minecraft.world.entity.monster.piglin.Piglin getHandle() {
      return (net.minecraft.world.entity.monster.piglin.Piglin)super.getHandle();
   }

   public String toString() {
      return "CraftPiglin";
   }
}
