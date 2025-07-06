package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.boss.CraftDragonBattle;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;

public class CraftEnderDragon extends CraftMob implements EnderDragon, CraftEnemy {
   public CraftEnderDragon(CraftServer server, net.minecraft.world.entity.boss.enderdragon.EnderDragon entity) {
      super(server, entity);
   }

   public Set<ComplexEntityPart> getParts() {
      ImmutableSet.Builder<ComplexEntityPart> builder = ImmutableSet.builder();
      EnderDragonPart[] var2 = this.getHandle().subEntities;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnderDragonPart part = var2[var4];
         builder.add((ComplexEntityPart)part.getBukkitEntity());
      }

      return builder.build();
   }

   public net.minecraft.world.entity.boss.enderdragon.EnderDragon getHandle() {
      return (net.minecraft.world.entity.boss.enderdragon.EnderDragon)this.entity;
   }

   public String toString() {
      return "CraftEnderDragon";
   }

   public EnderDragon.Phase getPhase() {
      return Phase.values()[(Integer)this.getHandle().getEntityData().get(net.minecraft.world.entity.boss.enderdragon.EnderDragon.DATA_PHASE)];
   }

   public void setPhase(EnderDragon.Phase phase) {
      this.getHandle().getPhaseManager().setPhase(getMinecraftPhase(phase));
   }

   public static EnderDragon.Phase getBukkitPhase(EnderDragonPhase phase) {
      return Phase.values()[phase.getId()];
   }

   public static EnderDragonPhase getMinecraftPhase(EnderDragon.Phase phase) {
      return EnderDragonPhase.getById(phase.ordinal());
   }

   public BossBar getBossBar() {
      DragonBattle battle = this.getDragonBattle();
      return battle != null ? battle.getBossBar() : null;
   }

   public DragonBattle getDragonBattle() {
      return this.getHandle().getDragonFight() != null ? new CraftDragonBattle(this.getHandle().getDragonFight()) : null;
   }

   public int getDeathAnimationTicks() {
      return this.getHandle().dragonDeathTime;
   }
}
