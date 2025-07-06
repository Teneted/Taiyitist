package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownLingeringPotion;
import net.minecraft.world.entity.projectile.ThrownSplashPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.craftbukkit.entity.memory.CraftMemoryKey;
import org.bukkit.craftbukkit.entity.memory.CraftMemoryMapper;
import org.bukkit.craftbukkit.inventory.CraftEntityEquipment;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AbstractWindCharge;
import org.bukkit.entity.BreezeWindCharge;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class CraftLivingEntity extends CraftEntity implements LivingEntity {
   private CraftEntityEquipment equipment;

   public CraftLivingEntity(CraftServer server, net.minecraft.world.entity.LivingEntity entity) {
      super(server, entity);
      if (entity instanceof Mob || entity instanceof ArmorStand) {
         this.equipment = new CraftEntityEquipment(this);
      }

   }

   public double getHealth() {
      return Math.min((double)Math.max(0.0F, this.getHandle().getHealth()), this.getMaxHealth());
   }

   public void setHealth(double health) {
      health = (double)((float)health);
      Preconditions.checkArgument(health >= 0.0 && health <= this.getMaxHealth(), "Health value (%s) must be between 0 and %s", health, this.getMaxHealth());
      if (this.getHandle().generation && health == 0.0) {
         this.getHandle().discard((EntityRemoveEvent.Cause)null);
      } else {
         this.getHandle().setHealth((float)health);
         if (health == 0.0) {
            this.getHandle().die(this.getHandle().damageSources().generic());
         }

      }
   }

   public double getAbsorptionAmount() {
      return (double)this.getHandle().getAbsorptionAmount();
   }

   public void setAbsorptionAmount(double amount) {
      Preconditions.checkArgument(amount >= 0.0 && Double.isFinite(amount), "amount < 0 or non-finite");
      this.getHandle().setAbsorptionAmount((float)amount);
   }

   public double getMaxHealth() {
      return (double)this.getHandle().getMaxHealth();
   }

   public void setMaxHealth(double amount) {
      Preconditions.checkArgument(amount > 0.0, "Max health amount (%s) must be greater than 0", amount);
      this.getHandle().getAttribute(Attributes.MAX_HEALTH).setBaseValue(amount);
      if (this.getHealth() > amount) {
         this.setHealth(amount);
      }

   }

   public void resetMaxHealth() {
      this.setMaxHealth(((Attribute)this.getHandle().getAttribute(Attributes.MAX_HEALTH).getAttribute().value()).getDefaultValue());
   }

   public double getEyeHeight() {
      return (double)this.getHandle().getEyeHeight();
   }

   public double getEyeHeight(boolean ignorePose) {
      return this.getEyeHeight();
   }

   private List<Block> getLineOfSight(Set<Material> transparent, int maxDistance, int maxLength) {
      Preconditions.checkState(!this.getHandle().generation, "Cannot get line of sight during world generation");
      if (transparent == null) {
         transparent = Sets.newHashSet(new Material[]{Material.AIR, Material.CAVE_AIR, Material.VOID_AIR});
      }

      if (maxDistance > 120) {
         maxDistance = 120;
      }

      ArrayList<Block> blocks = new ArrayList();
      Iterator<Block> itr = new BlockIterator(this, maxDistance);

      while(itr.hasNext()) {
         Block block = (Block)itr.next();
         blocks.add(block);
         if (maxLength != 0 && blocks.size() > maxLength) {
            blocks.remove(0);
         }

         Material material = block.getType();
         if (!((Set)transparent).contains(material)) {
            break;
         }
      }

      return blocks;
   }

   public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
      return this.getLineOfSight(transparent, maxDistance, 0);
   }

   public Block getTargetBlock(Set<Material> transparent, int maxDistance) {
      List<Block> blocks = this.getLineOfSight(transparent, maxDistance, 1);
      return (Block)blocks.get(0);
   }

   public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance) {
      return this.getLineOfSight(transparent, maxDistance, 2);
   }

   public Block getTargetBlockExact(int maxDistance) {
      return this.getTargetBlockExact(maxDistance, FluidCollisionMode.NEVER);
   }

   public Block getTargetBlockExact(int maxDistance, FluidCollisionMode fluidCollisionMode) {
      RayTraceResult hitResult = this.rayTraceBlocks((double)maxDistance, fluidCollisionMode);
      return hitResult != null ? hitResult.getHitBlock() : null;
   }

   public RayTraceResult rayTraceBlocks(double maxDistance) {
      return this.rayTraceBlocks(maxDistance, FluidCollisionMode.NEVER);
   }

   public RayTraceResult rayTraceBlocks(double maxDistance, FluidCollisionMode fluidCollisionMode) {
      Preconditions.checkState(!this.getHandle().generation, "Cannot ray tray blocks during world generation");
      Location eyeLocation = this.getEyeLocation();
      Vector direction = eyeLocation.getDirection();
      return this.getWorld().rayTraceBlocks(eyeLocation, direction, maxDistance, fluidCollisionMode, false);
   }

   public int getRemainingAir() {
      return this.getHandle().getAirSupply();
   }

   public void setRemainingAir(int ticks) {
      this.getHandle().setAirSupply(ticks);
   }

   public int getMaximumAir() {
      return this.getHandle().maxAirTicks;
   }

   public void setMaximumAir(int ticks) {
      this.getHandle().maxAirTicks = ticks;
   }

   public ItemStack getItemInUse() {
      net.minecraft.world.item.ItemStack item = this.getHandle().getUseItem();
      return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
   }

   public int getItemInUseTicks() {
      return this.getHandle().getUseItemRemainingTicks();
   }

   public void setItemInUseTicks(int ticks) {
      this.getHandle().useItemRemaining = ticks;
   }

   public int getArrowCooldown() {
      return this.getHandle().removeArrowTime;
   }

   public void setArrowCooldown(int ticks) {
      this.getHandle().removeArrowTime = ticks;
   }

   public int getArrowsInBody() {
      return this.getHandle().getArrowCount();
   }

   public void setArrowsInBody(int count) {
      Preconditions.checkArgument(count >= 0, "New arrow amount must be >= 0");
      this.getHandle().getEntityData().set(net.minecraft.world.entity.LivingEntity.DATA_ARROW_COUNT_ID, count);
   }

   public boolean isInvulnerable() {
      return this.getHandle().isInvulnerableTo((ServerLevel)this.getHandle().level(), this.getHandle().damageSources().generic());
   }

   public void damage(double amount) {
      this.damage(amount, this.getHandle().damageSources().generic());
   }

   public void damage(double amount, Entity source) {
      DamageSource reason = this.getHandle().damageSources().generic();
      if (source instanceof HumanEntity) {
         reason = this.getHandle().damageSources().playerAttack(((CraftHumanEntity)source).getHandle());
      } else if (source instanceof LivingEntity) {
         reason = this.getHandle().damageSources().mobAttack(((CraftLivingEntity)source).getHandle());
      }

      this.damage(amount, reason);
   }

   public void damage(double amount, org.bukkit.damage.DamageSource damageSource) {
      Preconditions.checkArgument(damageSource != null, "damageSource cannot be null");
      this.damage(amount, ((CraftDamageSource)damageSource).getHandle());
   }

   private void damage(double amount, DamageSource damageSource) {
      Preconditions.checkArgument(damageSource != null, "damageSource cannot be null");
      Preconditions.checkState(!this.getHandle().generation, "Cannot damage entity during world generation");
      this.entity.hurt(damageSource, (float)amount);
   }

   public Location getEyeLocation() {
      Location loc = this.getLocation();
      loc.setY(loc.getY() + this.getEyeHeight());
      return loc;
   }

   public int getMaximumNoDamageTicks() {
      return this.getHandle().invulnerableDuration;
   }

   public void setMaximumNoDamageTicks(int ticks) {
      this.getHandle().invulnerableDuration = ticks;
   }

   public double getLastDamage() {
      return (double)this.getHandle().lastHurt;
   }

   public void setLastDamage(double damage) {
      this.getHandle().lastHurt = (float)damage;
   }

   public int getNoDamageTicks() {
      return this.getHandle().invulnerableTime;
   }

   public void setNoDamageTicks(int ticks) {
      this.getHandle().invulnerableTime = ticks;
   }

   public int getNoActionTicks() {
      return this.getHandle().getNoActionTime();
   }

   public void setNoActionTicks(int ticks) {
      Preconditions.checkArgument(ticks >= 0, "ticks must be >= 0");
      this.getHandle().setNoActionTime(ticks);
   }

   public net.minecraft.world.entity.LivingEntity getHandle() {
      return (net.minecraft.world.entity.LivingEntity)this.entity;
   }

   public void setHandle(net.minecraft.world.entity.LivingEntity entity) {
      super.setHandle(entity);
   }

   public String toString() {
      return "CraftLivingEntity{id=" + this.getEntityId() + "}";
   }

   public Player getKiller() {
      net.minecraft.world.entity.player.Player lastHurtByPlayer = this.getHandle().getLastHurtByPlayer();
      return lastHurtByPlayer == null ? null : (Player)lastHurtByPlayer.getBukkitEntity();
   }

   public boolean addPotionEffect(PotionEffect effect) {
      return this.addPotionEffect(effect, false);
   }

   public boolean addPotionEffect(PotionEffect effect, boolean force) {
      this.getHandle().addEffect(new MobEffectInstance(CraftPotionEffectType.bukkitToMinecraftHolder(effect.getType()), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()), Cause.PLUGIN);
      return true;
   }

   public boolean addPotionEffects(Collection<PotionEffect> effects) {
      boolean success = true;

      PotionEffect effect;
      for(Iterator var3 = effects.iterator(); var3.hasNext(); success &= this.addPotionEffect(effect)) {
         effect = (PotionEffect)var3.next();
      }

      return success;
   }

   public boolean hasPotionEffect(PotionEffectType type) {
      return this.getHandle().hasEffect(CraftPotionEffectType.bukkitToMinecraftHolder(type));
   }

   public PotionEffect getPotionEffect(PotionEffectType type) {
      MobEffectInstance handle = this.getHandle().getEffect(CraftPotionEffectType.bukkitToMinecraftHolder(type));
      return handle == null ? null : new PotionEffect(CraftPotionEffectType.minecraftHolderToBukkit(handle.getEffect()), handle.getDuration(), handle.getAmplifier(), handle.isAmbient(), handle.isVisible());
   }

   public void removePotionEffect(PotionEffectType type) {
      this.getHandle().removeEffect(CraftPotionEffectType.bukkitToMinecraftHolder(type), Cause.PLUGIN);
   }

   public Collection<PotionEffect> getActivePotionEffects() {
      List<PotionEffect> effects = new ArrayList();
      Iterator var2 = this.getHandle().activeEffects.values().iterator();

      while(var2.hasNext()) {
         MobEffectInstance handle = (MobEffectInstance)var2.next();
         effects.add(new PotionEffect(CraftPotionEffectType.minecraftHolderToBukkit(handle.getEffect()), handle.getDuration(), handle.getAmplifier(), handle.isAmbient(), handle.isVisible()));
      }

      return effects;
   }

   public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
      return this.launchProjectile(projectile, (Vector)null);
   }

   public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
      Preconditions.checkState(!this.getHandle().generation, "Cannot launch projectile during world generation");
      Level world = ((CraftWorld)this.getWorld()).getHandle();
      net.minecraft.world.entity.Entity launch = null;
      if (Snowball.class.isAssignableFrom(projectile)) {
         launch = new net.minecraft.world.entity.projectile.Snowball(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.SNOWBALL));
         ((ThrowableProjectile)launch).shootFromRotation(this.getHandle(), this.getHandle().getXRot(), this.getHandle().getYRot(), 0.0F, 1.5F, 1.0F);
      } else if (Egg.class.isAssignableFrom(projectile)) {
         launch = new ThrownEgg(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.EGG));
         ((ThrowableProjectile)launch).shootFromRotation(this.getHandle(), this.getHandle().getXRot(), this.getHandle().getYRot(), 0.0F, 1.5F, 1.0F);
      } else if (EnderPearl.class.isAssignableFrom(projectile)) {
         launch = new ThrownEnderpearl(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.ENDER_PEARL));
         ((ThrowableProjectile)launch).shootFromRotation(this.getHandle(), this.getHandle().getXRot(), this.getHandle().getYRot(), 0.0F, 1.5F, 1.0F);
      } else if (AbstractArrow.class.isAssignableFrom(projectile)) {
         if (TippedArrow.class.isAssignableFrom(projectile)) {
            launch = new Arrow(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.ARROW), (net.minecraft.world.item.ItemStack)null);
            ((org.bukkit.entity.Arrow)((net.minecraft.world.entity.Entity)launch).getBukkitEntity()).setBasePotionType(PotionType.WATER);
         } else if (SpectralArrow.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.world.entity.projectile.SpectralArrow(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.SPECTRAL_ARROW), (net.minecraft.world.item.ItemStack)null);
         } else if (Trident.class.isAssignableFrom(projectile)) {
            launch = new ThrownTrident(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.TRIDENT));
         } else {
            launch = new Arrow(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.ARROW), (net.minecraft.world.item.ItemStack)null);
         }

         ((net.minecraft.world.entity.projectile.AbstractArrow)launch).shootFromRotation(this.getHandle(), this.getHandle().getXRot(), this.getHandle().getYRot(), 0.0F, 3.0F, 1.0F);
      } else if (ThrownPotion.class.isAssignableFrom(projectile)) {
         if (LingeringPotion.class.isAssignableFrom(projectile)) {
            launch = new ThrownLingeringPotion(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.LINGERING_POTION));
         } else {
            launch = new ThrownSplashPotion(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.SPLASH_POTION));
         }

         ((ThrowableProjectile)launch).shootFromRotation(this.getHandle(), this.getHandle().getXRot(), this.getHandle().getYRot(), -20.0F, 0.5F, 1.0F);
      } else if (ThrownExpBottle.class.isAssignableFrom(projectile)) {
         launch = new ThrownExperienceBottle(world, this.getHandle(), new net.minecraft.world.item.ItemStack(Items.EXPERIENCE_BOTTLE));
         ((ThrowableProjectile)launch).shootFromRotation(this.getHandle(), this.getHandle().getXRot(), this.getHandle().getYRot(), -20.0F, 0.7F, 1.0F);
      } else if (FishHook.class.isAssignableFrom(projectile) && this.getHandle() instanceof net.minecraft.world.entity.player.Player) {
         launch = new FishingHook((net.minecraft.world.entity.player.Player)this.getHandle(), world, 0, 0);
      } else {
         Location location;
         Vector direction;
         if (Fireball.class.isAssignableFrom(projectile)) {
            location = this.getEyeLocation();
            direction = location.getDirection().multiply(10);
            Vec3 vec = new Vec3(direction.getX(), direction.getY(), direction.getZ());
            if (SmallFireball.class.isAssignableFrom(projectile)) {
               launch = new net.minecraft.world.entity.projectile.SmallFireball(world, this.getHandle(), vec);
            } else if (WitherSkull.class.isAssignableFrom(projectile)) {
               launch = new net.minecraft.world.entity.projectile.WitherSkull(world, this.getHandle(), vec);
            } else if (DragonFireball.class.isAssignableFrom(projectile)) {
               launch = new net.minecraft.world.entity.projectile.DragonFireball(world, this.getHandle(), vec);
            } else if (AbstractWindCharge.class.isAssignableFrom(projectile)) {
               if (BreezeWindCharge.class.isAssignableFrom(projectile)) {
                  launch = EntityType.BREEZE_WIND_CHARGE.create(world, EntitySpawnReason.TRIGGERED);
               } else {
                  launch = EntityType.WIND_CHARGE.create(world, EntitySpawnReason.TRIGGERED);
               }

               ((net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge)launch).setOwner(this.getHandle());
               ((net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge)launch).shootFromRotation(this.getHandle(), this.getHandle().getXRot(), this.getHandle().getYRot(), 0.0F, 1.5F, 1.0F);
            } else {
               launch = new LargeFireball(world, this.getHandle(), vec, 1);
            }

            ((AbstractHurtingProjectile)launch).projectileSource = this;
            ((net.minecraft.world.entity.Entity)launch).snapTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
         } else if (LlamaSpit.class.isAssignableFrom(projectile)) {
            location = this.getEyeLocation();
            direction = location.getDirection();
            launch = EntityType.LLAMA_SPIT.create(world, EntitySpawnReason.TRIGGERED);
            ((net.minecraft.world.entity.projectile.LlamaSpit)launch).setOwner(this.getHandle());
            ((net.minecraft.world.entity.projectile.LlamaSpit)launch).shoot(direction.getX(), direction.getY(), direction.getZ(), 1.5F, 10.0F);
            ((net.minecraft.world.entity.Entity)launch).snapTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
         } else if (ShulkerBullet.class.isAssignableFrom(projectile)) {
            location = this.getEyeLocation();
            launch = new net.minecraft.world.entity.projectile.ShulkerBullet(world, this.getHandle(), (net.minecraft.world.entity.Entity)null, (Direction.Axis)null);
            ((net.minecraft.world.entity.Entity)launch).snapTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
         } else if (Firework.class.isAssignableFrom(projectile)) {
            location = this.getEyeLocation();
            launch = new FireworkRocketEntity(world, net.minecraft.world.item.ItemStack.EMPTY, this.getHandle());
            ((net.minecraft.world.entity.Entity)launch).snapTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
         }
      }

      Preconditions.checkArgument(launch != null, "Projectile (%s) not supported", projectile.getName());
      if (velocity != null) {
         ((Projectile)((net.minecraft.world.entity.Entity)launch).getBukkitEntity()).setVelocity(velocity);
      }

      ((Level)world).addFreshEntity((net.minecraft.world.entity.Entity)launch);
      return (Projectile)((net.minecraft.world.entity.Entity)launch).getBukkitEntity();
   }

   public boolean hasLineOfSight(Entity other) {
      Preconditions.checkState(!this.getHandle().generation, "Cannot check line of sight during world generation");
      return this.getHandle().hasLineOfSight(((CraftEntity)other).getHandle());
   }

   public boolean getRemoveWhenFarAway() {
      return this.getHandle() instanceof Mob && !((Mob)this.getHandle()).isPersistenceRequired();
   }

   public void setRemoveWhenFarAway(boolean remove) {
      if (this.getHandle() instanceof Mob) {
         ((Mob)this.getHandle()).setPersistenceRequired(!remove);
      }

   }

   public EntityEquipment getEquipment() {
      return this.equipment;
   }

   public void setCanPickupItems(boolean pickup) {
      if (this.getHandle() instanceof Mob) {
         ((Mob)this.getHandle()).setCanPickUpLoot(pickup);
      } else {
         this.getHandle().bukkitPickUpLoot = pickup;
      }

   }

   public boolean getCanPickupItems() {
      return this.getHandle() instanceof Mob ? ((Mob)this.getHandle()).canPickUpLoot() : this.getHandle().bukkitPickUpLoot;
   }

   public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
      return this.getHealth() == 0.0 ? false : super.teleport(location, cause);
   }

   public boolean isLeashed() {
      if (!(this.getHandle() instanceof Mob)) {
         return false;
      } else {
         return ((Mob)this.getHandle()).getLeashHolder() != null;
      }
   }

   public Entity getLeashHolder() throws IllegalStateException {
      Preconditions.checkState(this.isLeashed(), "Entity not leashed");
      return ((Mob)this.getHandle()).getLeashHolder().getBukkitEntity();
   }

   private boolean unleash() {
      if (!this.isLeashed()) {
         return false;
      } else {
         ((Mob)this.getHandle()).removeLeash();
         return true;
      }
   }

   public boolean setLeashHolder(Entity holder) {
      if (!this.getHandle().generation && !(this.getHandle() instanceof WitherBoss) && this.getHandle() instanceof Mob) {
         if (holder == null) {
            return this.unleash();
         } else if (holder.isDead()) {
            return false;
         } else {
            this.unleash();
            ((Mob)this.getHandle()).setLeashedTo(((CraftEntity)holder).getHandle(), true);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean isGliding() {
      return this.getHandle().getSharedFlag(7);
   }

   public void setGliding(boolean gliding) {
      this.getHandle().setSharedFlag(7, gliding);
   }

   public boolean isSwimming() {
      return this.getHandle().isSwimming();
   }

   public void setSwimming(boolean swimming) {
      this.getHandle().setSwimming(swimming);
   }

   public boolean isRiptiding() {
      return this.getHandle().isAutoSpinAttack();
   }

   public void setRiptiding(boolean riptiding) {
      this.getHandle().setLivingEntityFlag(4, riptiding);
   }

   public boolean isSleeping() {
      return this.getHandle().isSleeping();
   }

   public boolean isClimbing() {
      Preconditions.checkState(!this.getHandle().generation, "Cannot check if climbing during world generation");
      return this.getHandle().onClimbable();
   }

   public AttributeInstance getAttribute(org.bukkit.attribute.Attribute attribute) {
      return this.getHandle().craftAttributes.getAttribute(attribute);
   }

   public void setAI(boolean ai) {
      if (this.getHandle() instanceof Mob) {
         ((Mob)this.getHandle()).setNoAi(!ai);
      }

   }

   public boolean hasAI() {
      return this.getHandle() instanceof Mob ? !((Mob)this.getHandle()).isNoAi() : false;
   }

   public void attack(Entity target) {
      Preconditions.checkArgument(target != null, "target == null");
      Preconditions.checkState(!this.getHandle().generation, "Cannot attack during world generation");
      if (this.getHandle() instanceof net.minecraft.world.entity.player.Player) {
         ((net.minecraft.world.entity.player.Player)this.getHandle()).attack(((CraftEntity)target).getHandle());
      } else {
         this.getHandle().doHurtTarget((ServerLevel)((CraftEntity)target).getHandle().level(), ((CraftEntity)target).getHandle());
      }

   }

   public void swingMainHand() {
      Preconditions.checkState(!this.getHandle().generation, "Cannot swing hand during world generation");
      this.getHandle().swing(InteractionHand.MAIN_HAND, true);
   }

   public void swingOffHand() {
      Preconditions.checkState(!this.getHandle().generation, "Cannot swing hand during world generation");
      this.getHandle().swing(InteractionHand.OFF_HAND, true);
   }

   public void playHurtAnimation(float yaw) {
      Level var3 = this.getHandle().level();
      if (var3 instanceof ServerLevel world) {
         float actualYaw = yaw + 90.0F;
         ClientboundHurtAnimationPacket packet = new ClientboundHurtAnimationPacket(this.getEntityId(), actualYaw);
         world.getChunkSource().broadcastAndSend(this.getHandle(), packet);
      }

   }

   public void setCollidable(boolean collidable) {
      this.getHandle().collides = collidable;
   }

   public boolean isCollidable() {
      return this.getHandle().collides;
   }

   public Set<UUID> getCollidableExemptions() {
      return this.getHandle().collidableExemptions;
   }

   public <T> T getMemory(MemoryKey<T> memoryKey) {
      return this.getHandle().getBrain().getMemory(CraftMemoryKey.bukkitToMinecraft(memoryKey)).map(CraftMemoryMapper::fromNms).orElse((Object)null);
   }

   public <T> void setMemory(MemoryKey<T> memoryKey, T t) {
      this.getHandle().getBrain().setMemory(CraftMemoryKey.bukkitToMinecraft(memoryKey), CraftMemoryMapper.toNms(t));
   }

   public Sound getHurtSound() {
      SoundEvent sound = this.getHandle().getHurtSound0(this.getHandle().damageSources().generic());
      return sound != null ? CraftSound.minecraftToBukkit(sound) : null;
   }

   public Sound getDeathSound() {
      SoundEvent sound = this.getHandle().getDeathSound0();
      return sound != null ? CraftSound.minecraftToBukkit(sound) : null;
   }

   public Sound getFallDamageSound(int fallHeight) {
      return CraftSound.minecraftToBukkit(this.getHandle().getFallDamageSound0(fallHeight));
   }

   public Sound getFallDamageSoundSmall() {
      return CraftSound.minecraftToBukkit(this.getHandle().getFallSounds().small());
   }

   public Sound getFallDamageSoundBig() {
      return CraftSound.minecraftToBukkit(this.getHandle().getFallSounds().big());
   }

   public Sound getDrinkingSound(ItemStack itemStack) {
      return this.getEatingSound(itemStack);
   }

   public Sound getEatingSound(ItemStack itemStack) {
      Preconditions.checkArgument(itemStack != null, "itemStack must not be null");
      net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
      Consumable consumable = (Consumable)nms.get(DataComponents.CONSUMABLE);
      SoundEvent soundeffect = (SoundEvent)SoundEvents.GENERIC_DRINK.value();
      if (consumable != null) {
         net.minecraft.world.entity.LivingEntity var6 = this.getHandle();
         if (var6 instanceof Consumable.OverrideConsumeSound) {
            Consumable.OverrideConsumeSound consumable_b = (Consumable.OverrideConsumeSound)var6;
            soundeffect = consumable_b.getConsumeSound(nms);
         } else {
            soundeffect = (SoundEvent)consumable.sound().value();
         }
      }

      return CraftSound.minecraftToBukkit(soundeffect);
   }

   public boolean canBreatheUnderwater() {
      return this.getHandle().canBreatheUnderwater();
   }

   public EntityCategory getCategory() {
      throw new UnsupportedOperationException("Method no longer applicable. Use Tags instead.");
   }

   public boolean isInvisible() {
      return this.getHandle().isInvisible();
   }

   public void setInvisible(boolean invisible) {
      this.getHandle().persistentInvisibility = invisible;
      this.getHandle().setSharedFlag(5, invisible);
   }

   public Color getWaypointColor() {
      return (Color)this.getHandle().waypointIcon().color.map(Color::fromRGB).orElse((Object)null);
   }

   public void setWaypointColor(Color color) {
      this.mutateIcon((icon) -> {
         icon.color = Optional.ofNullable(color).map(Color::asRGB);
      });
   }

   public NamespacedKey getWaypointStyle() {
      return CraftNamespacedKey.fromMinecraft(this.getHandle().waypointIcon().style.location());
   }

   public void setWaypointStyle(NamespacedKey key) {
      this.mutateIcon((icon) -> {
         icon.style = (ResourceKey)Optional.ofNullable(key).map((k) -> {
            return ResourceKey.create(WaypointStyleAssets.ROOT_ID, CraftNamespacedKey.toMinecraft(k));
         }).orElse(WaypointStyleAssets.DEFAULT);
      });
   }

   private void mutateIcon(Consumer<Waypoint.Icon> consumer) {
      net.minecraft.world.entity.LivingEntity handle = this.getHandle();
      ServerLevel worldserver = (ServerLevel)handle.level();
      worldserver.getWaypointManager().untrackWaypoint(handle);
      consumer.accept(handle.waypointIcon());
      worldserver.getWaypointManager().trackWaypoint(handle);
   }
}
