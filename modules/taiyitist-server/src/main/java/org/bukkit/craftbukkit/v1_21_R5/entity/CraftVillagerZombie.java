package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import java.util.UUID;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;

public class CraftVillagerZombie extends CraftZombie implements ZombieVillager {
   public CraftVillagerZombie(CraftServer server, net.minecraft.world.entity.monster.ZombieVillager entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.ZombieVillager getHandle() {
      return (net.minecraft.world.entity.monster.ZombieVillager)super.getHandle();
   }

   public String toString() {
      return "CraftVillagerZombie";
   }

   public Villager.Profession getVillagerProfession() {
      return CraftVillager.CraftProfession.minecraftHolderToBukkit(this.getHandle().getVillagerData().profession());
   }

   public void setVillagerProfession(Villager.Profession profession) {
      Preconditions.checkArgument(profession != null, "Villager.Profession cannot be null");
      this.getHandle().setVillagerData(this.getHandle().getVillagerData().withProfession(CraftVillager.CraftProfession.bukkitToMinecraftHolder(profession)));
   }

   public Villager.Type getVillagerType() {
      return CraftVillager.CraftType.minecraftHolderToBukkit(this.getHandle().getVillagerData().type());
   }

   public void setVillagerType(Villager.Type type) {
      Preconditions.checkArgument(type != null, "Villager.Type cannot be null");
      this.getHandle().setVillagerData(this.getHandle().getVillagerData().withType(CraftVillager.CraftType.bukkitToMinecraftHolder(type)));
   }

   public boolean isConverting() {
      return this.getHandle().isConverting();
   }

   public int getConversionTime() {
      Preconditions.checkState(this.isConverting(), "Entity not converting");
      return this.getHandle().villagerConversionTime;
   }

   public void setConversionTime(int time) {
      if (time < 0) {
         this.getHandle().villagerConversionTime = -1;
         this.getHandle().getEntityData().set(net.minecraft.world.entity.monster.ZombieVillager.DATA_CONVERTING_ID, false);
         this.getHandle().conversionStarter = null;
         this.getHandle().removeEffect(MobEffects.STRENGTH, Cause.CONVERSION);
      } else {
         this.getHandle().startConverting((UUID)null, time);
      }

   }

   public OfflinePlayer getConversionPlayer() {
      return this.getHandle().conversionStarter == null ? null : Bukkit.getOfflinePlayer(this.getHandle().conversionStarter);
   }

   public void setConversionPlayer(OfflinePlayer conversionPlayer) {
      if (this.isConverting()) {
         this.getHandle().conversionStarter = conversionPlayer == null ? null : conversionPlayer.getUniqueId();
      }
   }
}
