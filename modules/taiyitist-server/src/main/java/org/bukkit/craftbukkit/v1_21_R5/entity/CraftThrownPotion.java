package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.potion.CraftPotionUtil;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public abstract class CraftThrownPotion extends CraftThrowableProjectile implements ThrownPotion {
   public CraftThrownPotion(CraftServer server, AbstractThrownPotion entity) {
      super(server, entity);
   }

   public Collection<PotionEffect> getEffects() {
      ImmutableList.Builder<PotionEffect> builder = ImmutableList.builder();
      Iterator var2 = ((PotionContents)this.getHandle().getItem().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).getAllEffects().iterator();

      while(var2.hasNext()) {
         MobEffectInstance effect = (MobEffectInstance)var2.next();
         builder.add(CraftPotionUtil.toBukkit(effect));
      }

      return builder.build();
   }

   public ItemStack getItem() {
      return CraftItemStack.asBukkitCopy(this.getHandle().getItem());
   }

   public void setItem(ItemStack item) {
      Preconditions.checkArgument(item != null, "ItemStack cannot be null");
      Preconditions.checkArgument(item.getType() == Material.LINGERING_POTION || item.getType() == Material.SPLASH_POTION, "ItemStack material must be Material.LINGERING_POTION or Material.SPLASH_POTION but was Material.%s", item.getType());
      this.getHandle().setItem(CraftItemStack.asNMSCopy(item));
   }

   public AbstractThrownPotion getHandle() {
      return (AbstractThrownPotion)this.entity;
   }
}
