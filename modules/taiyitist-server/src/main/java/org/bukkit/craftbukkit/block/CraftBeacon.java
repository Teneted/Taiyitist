package org.bukkit.craftbukkit.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CraftBeacon extends CraftBlockEntityState<BeaconBlockEntity> implements Beacon {
   public CraftBeacon(World world, BeaconBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftBeacon(CraftBeacon state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public Collection<LivingEntity> getEntitiesInRange() {
      this.ensureNoWorldGeneration();
      BlockEntity tileEntity = this.getTileEntityFromWorld();
      if (!(tileEntity instanceof BeaconBlockEntity beacon)) {
         return new ArrayList();
      } else {
         Collection<Player> nms = BukkitMethodHooks.getHumansInRange(beacon.getLevel(), beacon.getBlockPos(), beacon.levels);
         Collection<LivingEntity> bukkit = new ArrayList(nms.size());
         Iterator var5 = nms.iterator();

         while(var5.hasNext()) {
            Player human = (Player)var5.next();
            bukkit.add(human.getBukkitEntity());
         }

         return bukkit;
      }
   }

   public int getTier() {
      return ((BeaconBlockEntity)this.getSnapshot()).levels;
   }

   public PotionEffect getPrimaryEffect() {
      return ((BeaconBlockEntity)this.getSnapshot()).getPrimaryEffect();
   }

   public void setPrimaryEffect(PotionEffectType effect) {
      ((BeaconBlockEntity)this.getSnapshot()).primaryPower = effect != null ? CraftPotionEffectType.bukkitToMinecraftHolder(effect) : null;
   }

   public PotionEffect getSecondaryEffect() {
      return ((BeaconBlockEntity)this.getSnapshot()).getSecondaryEffect();
   }

   public void setSecondaryEffect(PotionEffectType effect) {
      ((BeaconBlockEntity)this.getSnapshot()).secondaryPower = effect != null ? CraftPotionEffectType.bukkitToMinecraftHolder(effect) : null;
   }

   public String getCustomName() {
      BeaconBlockEntity beacon = (BeaconBlockEntity)this.getSnapshot();
      return beacon.name != null ? CraftChatMessage.fromComponent(beacon.name) : null;
   }

   public void setCustomName(String name) {
      ((BeaconBlockEntity)this.getSnapshot()).setCustomName(CraftChatMessage.fromStringOrNull(name));
   }

   public boolean isLocked() {
      return ((BeaconBlockEntity)this.getSnapshot()).lockKey != LockCode.NO_LOCK;
   }

   public String getLock() {
      Optional<? extends Component> customName = ((BeaconBlockEntity)this.getSnapshot()).lockKey.predicate().components().exact().asPatch().get(DataComponents.CUSTOM_NAME);
      return customName != null ? (String)customName.map(CraftChatMessage::fromComponent).orElse("") : "";
   }

   public void setLock(String key) {
      if (key == null) {
         ((BeaconBlockEntity)this.getSnapshot()).lockKey = LockCode.NO_LOCK;
      } else {
         DataComponentExactPredicate predicate = DataComponentExactPredicate.builder().expect(DataComponents.CUSTOM_NAME, CraftChatMessage.fromStringOrNull(key)).build();
         ((BeaconBlockEntity)this.getSnapshot()).lockKey = new LockCode(new ItemPredicate(Optional.empty(), Ints.ANY, new DataComponentMatchers(predicate, Collections.emptyMap())));
      }

   }

   public void setLockItem(ItemStack key) {
      if (key == null) {
         ((BeaconBlockEntity)this.getSnapshot()).lockKey = LockCode.NO_LOCK;
      } else {
         ((BeaconBlockEntity)this.getSnapshot()).lockKey = new LockCode(CraftItemStack.asCriterionConditionItem(key));
      }

   }

   public CraftBeacon copy() {
      return new CraftBeacon(this, (Location)null);
   }

   public CraftBeacon copy(Location location) {
      return new CraftBeacon(this, location);
   }
}
