package org.bukkit.craftbukkit.v1_21_R5.event;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.block.TrialSpawner;
import org.bukkit.block.sign.Side;
import org.bukkit.craftbukkit.v1_21_R5.CraftChunk;
import org.bukkit.craftbukkit.v1_21_R5.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R5.CraftExplosionResult;
import org.bukkit.craftbukkit.v1_21_R5.CraftLootTable;
import org.bukkit.craftbukkit.v1_21_R5.CraftRaid;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.CraftStatistic;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlock;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlockStates;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.damage.CraftDamageSource;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftRaider;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftSpellcaster;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemType;
import org.bukkit.craftbukkit.v1_21_R5.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftLocation;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftVector;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BellResonateEvent;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.event.block.BlockBrushEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.block.VaultDisplayItemEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.ArrowBodyCountChangeEvent;
import org.bukkit.event.entity.BatToggleSleepEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityKnockbackByEntityEvent;
import org.bukkit.event.entity.EntityKnockbackEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.StriderTemperatureChangeEvent;
import org.bukkit.event.entity.TrialSpawnerSpawnEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.entity.VillagerReputationChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerBucketFishEvent;
import org.bukkit.event.player.PlayerCustomClickEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerExpCooldownChangeEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerRecipeBookClickEvent;
import org.bukkit.event.player.PlayerRecipeBookSettingsChangeEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.LightningStrikeEvent.Cause;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class CraftEventFactory {
   public static BlockPos sourceBlockOverride = null;
   private static final Function<? super Double, Double> ZERO = Functions.constant(-0.0);

   private static boolean canBuild(Level world, Player player, int x, int z) {
      int spawnSize = Bukkit.getServer().getSpawnRadius();
      if (world.dimension() != Level.OVERWORLD) {
         return true;
      } else if (spawnSize <= 0) {
         return true;
      } else if (((CraftServer)Bukkit.getServer()).getHandle().getOps().isEmpty()) {
         return true;
      } else if (player.isOp()) {
         return true;
      } else {
         BlockPos chunkcoordinates = world.getSharedSpawnPos();
         int distanceFromSpawn = Math.max(Math.abs(x - chunkcoordinates.getX()), Math.abs(z - chunkcoordinates.getZ()));
         return distanceFromSpawn > spawnSize;
      }
   }

   public static <T extends Event> T callEvent(T event) {
      Bukkit.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static boolean callPlayerSignOpenEvent(net.minecraft.world.entity.player.Player player, SignBlockEntity tileEntitySign, boolean front, PlayerSignOpenEvent.Cause cause) {
      Block block = CraftBlock.at(tileEntitySign.getLevel(), tileEntitySign.getBlockPos());
      Sign sign = (Sign)CraftBlockStates.getBlockState(block);
      Side side = front ? Side.FRONT : Side.BACK;
      return callPlayerSignOpenEvent((Player)player.getBukkitEntity(), sign, side, cause);
   }

   public static boolean callPlayerSignOpenEvent(Player player, Sign sign, Side side, PlayerSignOpenEvent.Cause cause) {
      PlayerSignOpenEvent event = new PlayerSignOpenEvent(player, sign, side, cause);
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static Either<net.minecraft.world.entity.player.Player.BedSleepingProblem, Unit> callPlayerBedEnterEvent(net.minecraft.world.entity.player.Player player, BlockPos bed, Either<net.minecraft.world.entity.player.Player.BedSleepingProblem, Unit> nmsBedResult) {
      PlayerBedEnterEvent.BedEnterResult bedEnterResult = (PlayerBedEnterEvent.BedEnterResult)nmsBedResult.mapBoth(new Function<net.minecraft.world.entity.player.Player.BedSleepingProblem, PlayerBedEnterEvent.BedEnterResult>() {
         public PlayerBedEnterEvent.BedEnterResult apply(net.minecraft.world.entity.player.Player.BedSleepingProblem t) {
            switch (t) {
               case NOT_POSSIBLE_HERE -> {
                  return BedEnterResult.NOT_POSSIBLE_HERE;
               }
               case NOT_POSSIBLE_NOW -> {
                  return BedEnterResult.NOT_POSSIBLE_NOW;
               }
               case TOO_FAR_AWAY -> {
                  return BedEnterResult.TOO_FAR_AWAY;
               }
               case NOT_SAFE -> {
                  return BedEnterResult.NOT_SAFE;
               }
               default -> {
                  return BedEnterResult.OTHER_PROBLEM;
               }
            }
         }
      }, (t) -> {
         return BedEnterResult.OK;
      }).map(java.util.function.Function.identity(), java.util.function.Function.identity());
      PlayerBedEnterEvent event = new PlayerBedEnterEvent((Player)player.getBukkitEntity(), CraftBlock.at(player.level(), bed), bedEnterResult);
      Bukkit.getServer().getPluginManager().callEvent(event);
      Event.Result result = event.useBed();
      if (result == Result.ALLOW) {
         return Either.right(Unit.INSTANCE);
      } else {
         return result == Result.DENY ? Either.left(BedSleepingProblem.OTHER_PROBLEM) : nmsBedResult;
      }
   }

   public static EntityEnterLoveModeEvent callEntityEnterLoveModeEvent(net.minecraft.world.entity.player.Player entityHuman, Animal entityAnimal, int loveTicks) {
      EntityEnterLoveModeEvent entityEnterLoveModeEvent = new EntityEnterLoveModeEvent((Animals)entityAnimal.getBukkitEntity(), entityHuman != null ? entityHuman.getBukkitEntity() : null, loveTicks);
      Bukkit.getPluginManager().callEvent(entityEnterLoveModeEvent);
      return entityEnterLoveModeEvent;
   }

   public static PlayerHarvestBlockEvent callPlayerHarvestBlockEvent(Level world, BlockPos blockposition, net.minecraft.world.entity.player.Player who, InteractionHand enumhand, List<ItemStack> itemsToHarvest) {
      List<org.bukkit.inventory.ItemStack> bukkitItemsToHarvest = new ArrayList((Collection)itemsToHarvest.stream().map(CraftItemStack::asBukkitCopy).collect(Collectors.toList()));
      Player player = (Player)who.getBukkitEntity();
      PlayerHarvestBlockEvent playerHarvestBlockEvent = new PlayerHarvestBlockEvent(player, CraftBlock.at(world, blockposition), CraftEquipmentSlot.getHand(enumhand), bukkitItemsToHarvest);
      Bukkit.getPluginManager().callEvent(playerHarvestBlockEvent);
      return playerHarvestBlockEvent;
   }

   public static PlayerBucketEntityEvent callPlayerFishBucketEvent(LivingEntity fish, net.minecraft.world.entity.player.Player entityHuman, ItemStack originalBucket, ItemStack entityBucket, InteractionHand enumhand) {
      Player player = (Player)entityHuman.getBukkitEntity();
      EquipmentSlot hand = CraftEquipmentSlot.getHand(enumhand);
      Object event;
      if (fish instanceof AbstractFish) {
         event = new PlayerBucketFishEvent(player, (Fish)fish.getBukkitEntity(), CraftItemStack.asBukkitCopy(originalBucket), CraftItemStack.asBukkitCopy(entityBucket), hand);
      } else {
         event = new PlayerBucketEntityEvent(player, fish.getBukkitEntity(), CraftItemStack.asBukkitCopy(originalBucket), CraftItemStack.asBukkitCopy(entityBucket), hand);
      }

      Bukkit.getPluginManager().callEvent((Event)event);
      return (PlayerBucketEntityEvent)event;
   }

   public static TradeSelectEvent callTradeSelectEvent(ServerPlayer player, int newIndex, MerchantMenu merchant) {
      TradeSelectEvent tradeSelectEvent = new TradeSelectEvent(merchant.getBukkitView(), newIndex);
      Bukkit.getPluginManager().callEvent(tradeSelectEvent);
      return tradeSelectEvent;
   }

   public static boolean handleBellRingEvent(Level world, BlockPos position, Direction direction, Entity entity) {
      Block block = CraftBlock.at(world, position);
      BlockFace bukkitDirection = CraftBlock.notchToBlockFace(direction);
      BellRingEvent event = new BellRingEvent(block, bukkitDirection, entity != null ? entity.getBukkitEntity() : null);
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static Stream<LivingEntity> handleBellResonateEvent(Level world, BlockPos position, List<org.bukkit.entity.LivingEntity> bukkitEntities) {
      Block block = CraftBlock.at(world, position);
      BellResonateEvent event = new BellResonateEvent(block, bukkitEntities);
      Bukkit.getPluginManager().callEvent(event);
      return event.getResonatedEntities().stream().map((bukkitEntity) -> {
         return ((CraftLivingEntity)bukkitEntity).getHandle();
      });
   }

   public static BlockMultiPlaceEvent callBlockMultiPlaceEvent(ServerLevel world, net.minecraft.world.entity.player.Player who, InteractionHand hand, List<BlockState> blockStates, int clickedX, int clickedY, int clickedZ) {
      CraftWorld craftWorld = world.getWorld();
      CraftServer craftServer = world.getCraftServer();
      Player player = (Player)who.getBukkitEntity();
      Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
      boolean canBuild = true;

      for(int i = 0; i < blockStates.size(); ++i) {
         if (!canBuild(world, player, ((BlockState)blockStates.get(i)).getX(), ((BlockState)blockStates.get(i)).getZ())) {
            canBuild = false;
            break;
         }
      }

      org.bukkit.inventory.ItemStack item;
      if (hand == InteractionHand.MAIN_HAND) {
         item = player.getInventory().getItemInMainHand();
      } else {
         item = player.getInventory().getItemInOffHand();
      }

      BlockMultiPlaceEvent event = new BlockMultiPlaceEvent(blockStates, blockClicked, item, player, canBuild);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static BlockPlaceEvent callBlockPlaceEvent(ServerLevel world, net.minecraft.world.entity.player.Player who, InteractionHand hand, BlockState replacedBlockState, int clickedX, int clickedY, int clickedZ) {
      CraftWorld craftWorld = world.getWorld();
      CraftServer craftServer = world.getCraftServer();
      Player player = (Player)who.getBukkitEntity();
      Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
      Block placedBlock = replacedBlockState.getBlock();
      boolean canBuild = canBuild(world, player, placedBlock.getX(), placedBlock.getZ());
      org.bukkit.inventory.ItemStack item;
      EquipmentSlot equipmentSlot;
      if (hand == InteractionHand.MAIN_HAND) {
         item = player.getInventory().getItemInMainHand();
         equipmentSlot = EquipmentSlot.HAND;
      } else {
         item = player.getInventory().getItemInOffHand();
         equipmentSlot = EquipmentSlot.OFF_HAND;
      }

      BlockPlaceEvent event = new BlockPlaceEvent(placedBlock, replacedBlockState, blockClicked, item, player, canBuild, equipmentSlot);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static void handleBlockDropItemEvent(Block block, BlockState state, ServerPlayer player, List<ItemEntity> items) {
      BlockDropItemEvent event = new BlockDropItemEvent(block, state, player.getBukkitEntity(), Lists.transform(items, (itemx) -> {
         return (Item)itemx.getBukkitEntity();
      }));
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         Iterator var5 = items.iterator();

         while(var5.hasNext()) {
            ItemEntity item = (ItemEntity)var5.next();
            item.level().addFreshEntity(item);
         }
      }

   }

   public static EntityPlaceEvent callEntityPlaceEvent(UseOnContext itemactioncontext, Entity entity) {
      return callEntityPlaceEvent(itemactioncontext.getLevel(), itemactioncontext.getClickedPos(), itemactioncontext.getClickedFace(), itemactioncontext.getPlayer(), entity, itemactioncontext.getHand());
   }

   public static EntityPlaceEvent callEntityPlaceEvent(Level world, BlockPos clickPosition, Direction clickedFace, net.minecraft.world.entity.player.Player human, Entity entity, InteractionHand enumhand) {
      Player who = human == null ? null : (Player)human.getBukkitEntity();
      Block blockClicked = CraftBlock.at(world, clickPosition);
      BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);
      EntityPlaceEvent event = new EntityPlaceEvent(entity.getBukkitEntity(), who, blockClicked, blockFace, CraftEquipmentSlot.getHand(enumhand));
      entity.level().getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerBucketEmptyEvent callPlayerBucketEmptyEvent(Level world, ServerPlayer who, BlockPos changed, BlockPos clicked, Direction clickedFace, ItemStack itemInHand, InteractionHand enumhand) {
      return (PlayerBucketEmptyEvent)getPlayerBucketEvent(false, world, who, changed, clicked, clickedFace, itemInHand, Items.BUCKET, enumhand);
   }

   public static PlayerBucketFillEvent callPlayerBucketFillEvent(Level world, net.minecraft.world.entity.player.Player who, BlockPos changed, BlockPos clicked, Direction clickedFace, ItemStack itemInHand, net.minecraft.world.item.Item bucket, InteractionHand enumhand) {
      return (PlayerBucketFillEvent)getPlayerBucketEvent(true, world, who, clicked, changed, clickedFace, itemInHand, bucket, enumhand);
   }

   private static PlayerEvent getPlayerBucketEvent(boolean isFilling, Level world, net.minecraft.world.entity.player.Player who, BlockPos changed, BlockPos clicked, Direction clickedFace, ItemStack itemstack, net.minecraft.world.item.Item item, InteractionHand enumhand) {
      Player player = (Player)who.getBukkitEntity();
      CraftItemStack itemInHand = CraftItemStack.asNewCraftStack(item);
      Material bucket = CraftItemType.minecraftToBukkit(itemstack.getItem());
      CraftServer craftServer = (CraftServer)player.getServer();
      Block block = CraftBlock.at(world, changed);
      Block blockClicked = CraftBlock.at(world, clicked);
      BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);
      EquipmentSlot hand = CraftEquipmentSlot.getHand(enumhand);
      Object event;
      if (isFilling) {
         event = new PlayerBucketFillEvent(player, block, blockClicked, blockFace, bucket, itemInHand, hand);
         ((PlayerBucketFillEvent)event).setCancelled(!canBuild(world, player, changed.getX(), changed.getZ()));
      } else {
         event = new PlayerBucketEmptyEvent(player, block, blockClicked, blockFace, bucket, itemInHand, hand);
         ((PlayerBucketEmptyEvent)event).setCancelled(!canBuild(world, player, changed.getX(), changed.getZ()));
      }

      craftServer.getPluginManager().callEvent((Event)event);
      return (PlayerEvent)event;
   }

   public static PlayerInteractEvent callPlayerInteractEvent(net.minecraft.world.entity.player.Player who, Action action, ItemStack itemstack, InteractionHand hand) {
      if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) {
         throw new AssertionError(String.format("%s performing %s with %s", who, action, itemstack));
      } else {
         return callPlayerInteractEvent(who, action, (BlockPos)null, Direction.SOUTH, itemstack, hand);
      }
   }

   public static PlayerInteractEvent callPlayerInteractEvent(net.minecraft.world.entity.player.Player who, Action action, BlockPos position, Direction direction, ItemStack itemstack, InteractionHand hand) {
      return callPlayerInteractEvent(who, action, (BlockPos)position, (Direction)direction, itemstack, false, hand, (Vec3)null);
   }

   public static PlayerInteractEvent callPlayerInteractEvent(net.minecraft.world.entity.player.Player who, Action action, BlockPos position, Direction direction, ItemStack itemstack, boolean cancelledBlock, InteractionHand hand, Vec3 targetPos) {
      Vector clickedPos = null;
      if (position != null && targetPos != null) {
         clickedPos = CraftVector.toBukkit(targetPos.subtract(Vec3.atLowerCornerOf(position)));
      }

      Block blockClicked = null;
      if (position != null) {
         blockClicked = CraftBlock.at(who.level(), position);
      }

      return callPlayerInteractEvent(who, action, (Block)blockClicked, (BlockFace)CraftBlock.notchToBlockFace(direction), itemstack, cancelledBlock, hand, (Vector)clickedPos);
   }

   public static PlayerInteractEvent callPlayerInteractEvent(net.minecraft.world.entity.player.Player who, Action action, Block blockClicked, BlockFace blockFace, ItemStack itemstack, boolean cancelledBlock, InteractionHand hand, Vector clickedPos) {
      Player player = (Player)who.getBukkitEntity();
      CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);
      if (blockClicked == null) {
         switch (action) {
            case LEFT_CLICK_BLOCK -> action = Action.LEFT_CLICK_AIR;
            case RIGHT_CLICK_BLOCK -> action = Action.RIGHT_CLICK_AIR;
         }
      }

      if (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0) {
         itemInHand = null;
      }

      PlayerInteractEvent event = new PlayerInteractEvent(player, action, itemInHand, blockClicked, blockFace, hand == null ? null : (hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND), clickedPos);
      if (cancelledBlock) {
         event.setUseInteractedBlock(Result.DENY);
      }

      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static EntityTransformEvent callEntityTransformEvent(LivingEntity original, LivingEntity coverted, EntityTransformEvent.TransformReason transformReason) {
      return callEntityTransformEvent(original, Collections.singletonList(coverted), transformReason);
   }

   public static EntityTransformEvent callEntityTransformEvent(LivingEntity original, List<LivingEntity> convertedList, EntityTransformEvent.TransformReason convertType) {
      List<org.bukkit.entity.Entity> list = new ArrayList();
      Iterator var4 = convertedList.iterator();

      while(var4.hasNext()) {
         LivingEntity entityLiving = (LivingEntity)var4.next();
         list.add(entityLiving.getBukkitEntity());
      }

      EntityTransformEvent event = new EntityTransformEvent(original.getBukkitEntity(), list, convertType);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static EntityShootBowEvent callEntityShootBowEvent(LivingEntity who, ItemStack bow, ItemStack consumableItem, Entity entityArrow, InteractionHand hand, float force, boolean consumeItem) {
      org.bukkit.entity.LivingEntity shooter = (org.bukkit.entity.LivingEntity)who.getBukkitEntity();
      CraftItemStack itemInHand = CraftItemStack.asCraftMirror(bow);
      CraftItemStack itemConsumable = CraftItemStack.asCraftMirror(consumableItem);
      org.bukkit.entity.Entity arrow = entityArrow.getBukkitEntity();
      EquipmentSlot handSlot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
      if (itemInHand != null && (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0)) {
         itemInHand = null;
      }

      EntityShootBowEvent event = new EntityShootBowEvent(shooter, itemInHand, itemConsumable, arrow, handSlot, force, consumeItem);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static VillagerCareerChangeEvent callVillagerCareerChangeEvent(Villager vilager, org.bukkit.entity.Villager.Profession future, VillagerCareerChangeEvent.ChangeReason reason) {
      VillagerCareerChangeEvent event = new VillagerCareerChangeEvent((org.bukkit.entity.Villager)vilager.getBukkitEntity(), future, reason);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static BlockDamageEvent callBlockDamageEvent(ServerPlayer who, BlockPos pos, ItemStack itemstack, boolean instaBreak) {
      Player player = who.getBukkitEntity();
      CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);
      Block blockClicked = CraftBlock.at(who.level(), pos);
      BlockDamageEvent event = new BlockDamageEvent(player, blockClicked, itemInHand, instaBreak);
      player.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockDamageAbortEvent callBlockDamageAbortEvent(ServerPlayer who, BlockPos pos, ItemStack itemstack) {
      Player player = who.getBukkitEntity();
      CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);
      Block blockClicked = CraftBlock.at(who.level(), pos);
      BlockDamageAbortEvent event = new BlockDamageAbortEvent(player, blockClicked, itemInHand);
      player.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static boolean doEntityAddEventCalling(Level world, Entity entity, CreatureSpawnEvent.SpawnReason spawnReason) {
      if (entity == null) {
         return false;
      } else {
         Cancellable event = null;
         if (entity instanceof LivingEntity && !(entity instanceof ServerPlayer)) {
            boolean isAnimal = entity instanceof Animal || entity instanceof WaterAnimal || entity instanceof AbstractGolem;
            boolean isMonster = entity instanceof Monster || entity instanceof Ghast || entity instanceof Slime;
            if (spawnReason != SpawnReason.CUSTOM && (isAnimal && !world.getWorld().getAllowAnimals() || isMonster && !world.getWorld().getAllowMonsters())) {
               entity.discard((EntityRemoveEvent.Cause)null);
               return false;
            }

            event = callCreatureSpawnEvent((LivingEntity)entity, spawnReason);
         } else if (entity instanceof ItemEntity) {
            event = callItemSpawnEvent((ItemEntity)entity);
         } else if (entity.getBukkitEntity() instanceof Projectile) {
            event = callProjectileLaunchEvent(entity);
         } else if (entity.getBukkitEntity() instanceof Vehicle) {
            event = callVehicleCreateEvent(entity);
         } else if (entity.getBukkitEntity() instanceof LightningStrike) {
            LightningStrikeEvent.Cause cause = Cause.UNKNOWN;
            switch (spawnReason) {
               case COMMAND -> cause = Cause.COMMAND;
               case CUSTOM -> cause = Cause.CUSTOM;
               case SPAWNER -> cause = Cause.SPAWNER;
            }

            if (cause == Cause.UNKNOWN && spawnReason == SpawnReason.DEFAULT) {
               return true;
            }

            event = callLightningStrikeEvent((LightningStrike)entity.getBukkitEntity(), cause);
         } else if (!(entity instanceof ServerPlayer)) {
            event = callEntitySpawnEvent(entity);
         }

         if (event == null || !((Cancellable)event).isCancelled() && !entity.isRemoved()) {
            if (entity instanceof ExperienceOrb) {
               ExperienceOrb xp = (ExperienceOrb)entity;
               double radius = world.spigotConfig.expMerge;
               if (radius > 0.0) {
                  List<Entity> entities = world.getEntities(entity, entity.getBoundingBox().inflate(radius, radius, radius));
                  Iterator var8 = entities.iterator();

                  while(var8.hasNext()) {
                     Entity e = (Entity)var8.next();
                     if (e instanceof ExperienceOrb) {
                        ExperienceOrb loopItem = (ExperienceOrb)e;
                        if (!loopItem.isRemoved()) {
                           xp.setValue(xp.getValue() + loopItem.getValue());
                           loopItem.discard((EntityRemoveEvent.Cause)null);
                        }
                     }
                  }
               }
            }

            return true;
         } else {
            Entity vehicle = entity.getVehicle();
            if (vehicle != null) {
               vehicle.discard((EntityRemoveEvent.Cause)null);
            }

            Iterator var11 = entity.getIndirectPassengers().iterator();

            while(var11.hasNext()) {
               Entity passenger = (Entity)var11.next();
               passenger.discard((EntityRemoveEvent.Cause)null);
            }

            entity.discard((EntityRemoveEvent.Cause)null);
            return false;
         }
      }
   }

   public static EntitySpawnEvent callEntitySpawnEvent(Entity entity) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      EntitySpawnEvent event = new EntitySpawnEvent(bukkitEntity);
      bukkitEntity.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static CreatureSpawnEvent callCreatureSpawnEvent(LivingEntity entityliving, CreatureSpawnEvent.SpawnReason spawnReason) {
      org.bukkit.entity.LivingEntity entity = (org.bukkit.entity.LivingEntity)entityliving.getBukkitEntity();
      CraftServer craftServer = (CraftServer)entity.getServer();
      CreatureSpawnEvent event = new CreatureSpawnEvent(entity, spawnReason);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static EntityTameEvent callEntityTameEvent(Mob entity, net.minecraft.world.entity.player.Player tamer) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      AnimalTamer bukkitTamer = tamer != null ? tamer.getBukkitEntity() : null;
      CraftServer craftServer = (CraftServer)bukkitEntity.getServer();
      EntityTameEvent event = new EntityTameEvent((org.bukkit.entity.LivingEntity)bukkitEntity, bukkitTamer);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static ItemSpawnEvent callItemSpawnEvent(ItemEntity entityitem) {
      Item entity = (Item)entityitem.getBukkitEntity();
      CraftServer craftServer = (CraftServer)entity.getServer();
      ItemSpawnEvent event = new ItemSpawnEvent(entity);
      craftServer.getPluginManager().callEvent(event);
      return event;
   }

   public static ItemDespawnEvent callItemDespawnEvent(ItemEntity entityitem) {
      Item entity = (Item)entityitem.getBukkitEntity();
      ItemDespawnEvent event = new ItemDespawnEvent(entity, entity.getLocation());
      entity.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static boolean callItemMergeEvent(ItemEntity merging, ItemEntity mergingWith) {
      Item entityMerging = (Item)merging.getBukkitEntity();
      Item entityMergingWith = (Item)mergingWith.getBukkitEntity();
      ItemMergeEvent event = new ItemMergeEvent(entityMerging, entityMergingWith);
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static PotionSplashEvent callPotionSplashEvent(AbstractThrownPotion potion, HitResult position, Map<org.bukkit.entity.LivingEntity, Double> affectedEntities) {
      ThrownPotion thrownPotion = (ThrownPotion)potion.getBukkitEntity();
      Block hitBlock = null;
      BlockFace hitFace = null;
      if (position.getType() == Type.BLOCK) {
         BlockHitResult positionBlock = (BlockHitResult)position;
         hitBlock = CraftBlock.at(potion.level(), positionBlock.getBlockPos());
         hitFace = CraftBlock.notchToBlockFace(positionBlock.getDirection());
      }

      org.bukkit.entity.Entity hitEntity = null;
      if (position.getType() == Type.ENTITY) {
         hitEntity = ((EntityHitResult)position).getEntity().getBukkitEntity();
      }

      PotionSplashEvent event = new PotionSplashEvent(thrownPotion, hitEntity, hitBlock, hitFace, affectedEntities);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static LingeringPotionSplashEvent callLingeringPotionSplashEvent(AbstractThrownPotion potion, HitResult position, AreaEffectCloud cloud) {
      ThrownPotion thrownPotion = (ThrownPotion)potion.getBukkitEntity();
      org.bukkit.entity.AreaEffectCloud effectCloud = (org.bukkit.entity.AreaEffectCloud)cloud.getBukkitEntity();
      Block hitBlock = null;
      BlockFace hitFace = null;
      if (position.getType() == Type.BLOCK) {
         BlockHitResult positionBlock = (BlockHitResult)position;
         hitBlock = CraftBlock.at(potion.level(), positionBlock.getBlockPos());
         hitFace = CraftBlock.notchToBlockFace(positionBlock.getDirection());
      }

      org.bukkit.entity.Entity hitEntity = null;
      if (position.getType() == Type.ENTITY) {
         hitEntity = ((EntityHitResult)position).getEntity().getBukkitEntity();
      }

      LingeringPotionSplashEvent event = new LingeringPotionSplashEvent(thrownPotion, hitEntity, hitBlock, hitFace, effectCloud);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static BlockFadeEvent callBlockFadeEvent(LevelAccessor world, BlockPos pos, net.minecraft.world.level.block.state.BlockState newBlock) {
      CraftBlockState state = CraftBlockStates.getBlockState(world, pos);
      state.setData(newBlock);
      BlockFadeEvent event = new BlockFadeEvent(state.getBlock(), state);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static boolean handleMoistureChangeEvent(Level world, BlockPos pos, net.minecraft.world.level.block.state.BlockState newBlock, int flag) {
      CraftBlockState state = CraftBlockStates.getBlockState(world, pos, flag);
      state.setData(newBlock);
      MoistureChangeEvent event = new MoistureChangeEvent(state.getBlock(), state);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         state.update(true);
      }

      return !event.isCancelled();
   }

   public static boolean handleBlockSpreadEvent(Level world, BlockPos source, BlockPos target, net.minecraft.world.level.block.state.BlockState block) {
      return handleBlockSpreadEvent(world, source, target, block, 2);
   }

   public static boolean handleBlockSpreadEvent(LevelAccessor world, BlockPos source, BlockPos target, net.minecraft.world.level.block.state.BlockState block, int flag) {
      if (!(world instanceof Level)) {
         world.setBlock(target, block, flag);
         return true;
      } else {
         CraftBlockState state = CraftBlockStates.getBlockState(world, target, flag);
         state.setData(block);
         BlockSpreadEvent event = new BlockSpreadEvent(state.getBlock(), CraftBlock.at(world, sourceBlockOverride != null ? sourceBlockOverride : source), state);
         Bukkit.getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            state.update(true);
         }

         return !event.isCancelled();
      }
   }

   public static EntityDeathEvent callEntityDeathEvent(LivingEntity victim, DamageSource damageSource) {
      return callEntityDeathEvent(victim, damageSource, new ArrayList(0));
   }

   public static EntityDeathEvent callEntityDeathEvent(LivingEntity victim, DamageSource damageSource, List<org.bukkit.inventory.ItemStack> drops) {
      CraftLivingEntity entity = (CraftLivingEntity)victim.getBukkitEntity();
      CraftDamageSource bukkitDamageSource = new CraftDamageSource(damageSource);
      CraftWorld world = (CraftWorld)entity.getWorld();
      EntityDeathEvent event = new EntityDeathEvent(entity, bukkitDamageSource, drops, victim.getExpReward(world.getHandle(), damageSource.getEntity()));
      Bukkit.getServer().getPluginManager().callEvent(event);
      victim.expToDrop = event.getDroppedExp();
      Iterator var7 = event.getDrops().iterator();

      while(var7.hasNext()) {
         org.bukkit.inventory.ItemStack stack = (org.bukkit.inventory.ItemStack)var7.next();
         if (stack != null && stack.getType() != Material.AIR && stack.getAmount() != 0) {
            world.dropItem(entity.getLocation(), stack);
         }
      }

      return event;
   }

   public static PlayerDeathEvent callPlayerDeathEvent(ServerPlayer victim, DamageSource damageSource, List<org.bukkit.inventory.ItemStack> drops, String deathMessage, boolean keepInventory) {
      CraftPlayer entity = victim.getBukkitEntity();
      CraftDamageSource bukkitDamageSource = new CraftDamageSource(damageSource);
      PlayerDeathEvent event = new PlayerDeathEvent(entity, bukkitDamageSource, drops, victim.getExpReward(victim.level(), damageSource.getEntity()), 0, deathMessage);
      event.setKeepInventory(keepInventory);
      event.setKeepLevel(victim.keepLevel);
      Bukkit.getServer().getPluginManager().callEvent(event);
      victim.keepLevel = event.getKeepLevel();
      victim.newLevel = event.getNewLevel();
      victim.newTotalExp = event.getNewTotalExp();
      victim.expToDrop = event.getDroppedExp();
      victim.newExp = event.getNewExp();
      Iterator var8 = event.getDrops().iterator();

      while(true) {
         while(true) {
            org.bukkit.inventory.ItemStack stack;
            do {
               do {
                  if (!var8.hasNext()) {
                     return event;
                  }

                  stack = (org.bukkit.inventory.ItemStack)var8.next();
               } while(stack == null);
            } while(stack.getType() == Material.AIR);

            if (stack instanceof CraftItemStack craftItemStack) {
               if (craftItemStack.isForInventoryDrop()) {
                  victim.drop(CraftItemStack.asNMSCopy(stack), true, false, false);
                  continue;
               }
            }

            victim.forceDrops = true;
            victim.spawnAtLocation(victim.level(), CraftItemStack.asNMSCopy(stack));
            victim.forceDrops = false;
         }
      }
   }

   public static ServerListPingEvent callServerListPingEvent(SocketAddress address, String motd, int numPlayers, int maxPlayers) {
      ServerListPingEvent event = new ServerListPingEvent("", ((InetSocketAddress)address).getAddress(), motd, numPlayers, maxPlayers);
      Bukkit.getServer().getPluginManager().callEvent(event);
      return event;
   }

   private static EntityDamageEvent handleEntityDamageEvent(Entity entity, DamageSource source, Map<EntityDamageEvent.DamageModifier, Double> modifiers, Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions) {
      return handleEntityDamageEvent(entity, source, modifiers, modifierFunctions, false);
   }

   private static EntityDamageEvent handleEntityDamageEvent(Entity entity, DamageSource source, Map<EntityDamageEvent.DamageModifier, Double> modifiers, Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions, boolean cancelled) {
      CraftDamageSource bukkitDamageSource = new CraftDamageSource(source);
      Entity damager = source.getDamager() != null ? source.getDamager() : source.getEntity();
      EntityDamageEvent.DamageCause cause;
      if (source.is(DamageTypeTags.IS_EXPLOSION)) {
         if (damager == null) {
            return callEntityDamageEvent(source.getDirectBlock(), source.getDirectBlockState(), entity, DamageCause.BLOCK_EXPLOSION, bukkitDamageSource, modifiers, modifierFunctions, cancelled);
         } else {
            cause = damager.getBukkitEntity() instanceof TNTPrimed ? DamageCause.BLOCK_EXPLOSION : DamageCause.ENTITY_EXPLOSION;
            return callEntityDamageEvent(damager, entity, cause, bukkitDamageSource, modifiers, modifierFunctions, cancelled);
         }
      } else if (damager == null && source.getDirectEntity() == null) {
         if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return callEntityDamageEvent(source.getDirectBlock(), source.getDirectBlockState(), entity, DamageCause.VOID, bukkitDamageSource, modifiers, modifierFunctions, cancelled);
         } else if (source.is(DamageTypes.LAVA)) {
            return callEntityDamageEvent(source.getDirectBlock(), source.getDirectBlockState(), entity, DamageCause.LAVA, bukkitDamageSource, modifiers, modifierFunctions, cancelled);
         } else if (source.getDirectBlock() == null) {
            if (source.is(DamageTypes.IN_FIRE)) {
               cause = DamageCause.FIRE;
            } else if (source.is(DamageTypes.STARVE)) {
               cause = DamageCause.STARVATION;
            } else if (source.is(DamageTypes.WITHER)) {
               cause = DamageCause.WITHER;
            } else if (source.is(DamageTypes.IN_WALL)) {
               cause = DamageCause.SUFFOCATION;
            } else if (source.is(DamageTypes.DROWN)) {
               cause = DamageCause.DROWNING;
            } else if (source.is(DamageTypes.ON_FIRE)) {
               cause = DamageCause.FIRE_TICK;
            } else if (source.isMelting()) {
               cause = DamageCause.MELTING;
            } else if (source.isPoison()) {
               cause = DamageCause.POISON;
            } else if (source.is(DamageTypes.MAGIC)) {
               cause = DamageCause.MAGIC;
            } else if (source.is(DamageTypes.FALL)) {
               cause = DamageCause.FALL;
            } else if (source.is(DamageTypes.FLY_INTO_WALL)) {
               cause = DamageCause.FLY_INTO_WALL;
            } else if (source.is(DamageTypes.CRAMMING)) {
               cause = DamageCause.CRAMMING;
            } else if (source.is(DamageTypes.DRY_OUT)) {
               cause = DamageCause.DRYOUT;
            } else if (source.is(DamageTypes.FREEZE)) {
               cause = DamageCause.FREEZE;
            } else if (source.is(DamageTypes.GENERIC_KILL)) {
               cause = DamageCause.KILL;
            } else if (source.is(DamageTypes.OUTSIDE_BORDER)) {
               cause = DamageCause.WORLD_BORDER;
            } else {
               cause = DamageCause.CUSTOM;
            }

            return callEntityDamageEvent((Entity)null, entity, cause, bukkitDamageSource, modifiers, modifierFunctions, cancelled);
         } else {
            if (!source.is(DamageTypes.CACTUS) && !source.is(DamageTypes.SWEET_BERRY_BUSH) && !source.is(DamageTypes.STALAGMITE) && !source.is(DamageTypes.FALLING_STALACTITE) && !source.is(DamageTypes.FALLING_ANVIL)) {
               if (source.is(DamageTypes.HOT_FLOOR)) {
                  cause = DamageCause.HOT_FLOOR;
               } else if (source.is(DamageTypes.MAGIC)) {
                  cause = DamageCause.MAGIC;
               } else if (source.is(DamageTypes.IN_FIRE)) {
                  cause = DamageCause.FIRE;
               } else {
                  if (!source.is(DamageTypes.CAMPFIRE)) {
                     throw new IllegalStateException(String.format("Unhandled damage of %s by %s from %s [%s]", entity, source.getDirectBlock(), source.getMsgId(), source.typeHolder().getRegisteredName()));
                  }

                  cause = DamageCause.CAMPFIRE;
               }
            } else {
               cause = DamageCause.CONTACT;
            }

            return callEntityDamageEvent(source.getDirectBlock(), source.getDirectBlockState(), entity, cause, bukkitDamageSource, modifiers, modifierFunctions, cancelled);
         }
      } else {
         cause = source.isSweep() ? DamageCause.ENTITY_SWEEP_ATTACK : DamageCause.ENTITY_ATTACK;
         if (damager instanceof net.minecraft.world.entity.projectile.Projectile) {
            if (damager.getBukkitEntity() instanceof ThrownPotion) {
               cause = DamageCause.MAGIC;
            } else if (damager.getBukkitEntity() instanceof Projectile) {
               cause = DamageCause.PROJECTILE;
            }
         } else if (source.is(DamageTypes.THORNS)) {
            cause = DamageCause.THORNS;
         } else if (source.is(DamageTypes.SONIC_BOOM)) {
            cause = DamageCause.SONIC_BOOM;
         } else if (!source.is(DamageTypes.FALLING_STALACTITE) && !source.is(DamageTypes.FALLING_BLOCK) && !source.is(DamageTypes.FALLING_ANVIL)) {
            if (source.is(DamageTypes.LIGHTNING_BOLT)) {
               cause = DamageCause.LIGHTNING;
            } else if (source.is(DamageTypes.FALL)) {
               cause = DamageCause.FALL;
            } else if (source.is(DamageTypes.DRAGON_BREATH)) {
               cause = DamageCause.DRAGON_BREATH;
            } else if (source.is(DamageTypes.MAGIC)) {
               cause = DamageCause.MAGIC;
            }
         } else {
            cause = DamageCause.FALLING_BLOCK;
         }

         return callEntityDamageEvent(damager, entity, cause, bukkitDamageSource, modifiers, modifierFunctions, cancelled);
      }
   }

   private static EntityDamageEvent callEntityDamageEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, org.bukkit.damage.DamageSource bukkitDamageSource, Map<EntityDamageEvent.DamageModifier, Double> modifiers, Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions, boolean cancelled) {
      Object event;
      if (damager != null) {
         event = new EntityDamageByEntityEvent(damager.getBukkitEntity(), damagee.getBukkitEntity(), cause, bukkitDamageSource, modifiers, modifierFunctions);
      } else {
         event = new EntityDamageEvent(damagee.getBukkitEntity(), cause, bukkitDamageSource, modifiers, modifierFunctions);
      }

      return callEntityDamageEvent((EntityDamageEvent)event, damagee, cancelled);
   }

   private static EntityDamageEvent callEntityDamageEvent(Block damager, BlockState damagerState, Entity damagee, EntityDamageEvent.DamageCause cause, org.bukkit.damage.DamageSource bukkitDamageSource, Map<EntityDamageEvent.DamageModifier, Double> modifiers, Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions, boolean cancelled) {
      EntityDamageByBlockEvent event = new EntityDamageByBlockEvent(damager, damagerState, damagee.getBukkitEntity(), cause, bukkitDamageSource, modifiers, modifierFunctions);
      return callEntityDamageEvent(event, damagee, cancelled);
   }

   private static EntityDamageEvent callEntityDamageEvent(EntityDamageEvent event, Entity damagee, boolean cancelled) {
      event.setCancelled(cancelled);
      callEvent(event);
      if (!event.isCancelled()) {
         event.getEntity().setLastDamageCause(event);
      } else {
         damagee.lastDamageCancelled = true;
      }

      return event;
   }

   public static EntityDamageEvent handleLivingEntityDamageEvent(Entity damagee, DamageSource source, double rawDamage, double freezingModifier, double hardHatModifier, double blockingModifier, double armorModifier, double resistanceModifier, double magicModifier, double absorptionModifier, Function<Double, Double> freezing, Function<Double, Double> hardHat, Function<Double, Double> blocking, Function<Double, Double> armor, Function<Double, Double> resistance, Function<Double, Double> magic, Function<Double, Double> absorption) {
      Map<EntityDamageEvent.DamageModifier, Double> modifiers = new EnumMap(EntityDamageEvent.DamageModifier.class);
      Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions = new EnumMap(EntityDamageEvent.DamageModifier.class);
      modifiers.put(DamageModifier.BASE, rawDamage);
      modifierFunctions.put(DamageModifier.BASE, ZERO);
      if (source.is(DamageTypes.FREEZE)) {
         modifiers.put(DamageModifier.FREEZING, freezingModifier);
         modifierFunctions.put(DamageModifier.FREEZING, freezing);
      }

      if (source.is(DamageTypes.FALLING_BLOCK) || source.is(DamageTypes.FALLING_ANVIL)) {
         modifiers.put(DamageModifier.HARD_HAT, hardHatModifier);
         modifierFunctions.put(DamageModifier.HARD_HAT, hardHat);
      }

      if (damagee instanceof net.minecraft.world.entity.player.Player) {
         modifiers.put(DamageModifier.BLOCKING, blockingModifier);
         modifierFunctions.put(DamageModifier.BLOCKING, blocking);
      }

      modifiers.put(DamageModifier.ARMOR, armorModifier);
      modifierFunctions.put(DamageModifier.ARMOR, armor);
      modifiers.put(DamageModifier.RESISTANCE, resistanceModifier);
      modifierFunctions.put(DamageModifier.RESISTANCE, resistance);
      modifiers.put(DamageModifier.MAGIC, magicModifier);
      modifierFunctions.put(DamageModifier.MAGIC, magic);
      modifiers.put(DamageModifier.ABSORPTION, absorptionModifier);
      modifierFunctions.put(DamageModifier.ABSORPTION, absorption);
      return handleEntityDamageEvent(damagee, source, modifiers, modifierFunctions);
   }

   public static boolean handleNonLivingEntityDamageEvent(Entity entity, DamageSource source, double damage) {
      return handleNonLivingEntityDamageEvent(entity, source, damage, true);
   }

   public static boolean handleNonLivingEntityDamageEvent(Entity entity, DamageSource source, double damage, boolean cancelOnZeroDamage) {
      return handleNonLivingEntityDamageEvent(entity, source, damage, cancelOnZeroDamage, false);
   }

   public static EntityDamageEvent callNonLivingEntityDamageEvent(Entity entity, DamageSource source, double damage, boolean cancelled) {
      EnumMap<EntityDamageEvent.DamageModifier, Double> modifiers = new EnumMap(EntityDamageEvent.DamageModifier.class);
      EnumMap<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> functions = new EnumMap(EntityDamageEvent.DamageModifier.class);
      modifiers.put(DamageModifier.BASE, damage);
      functions.put(DamageModifier.BASE, ZERO);
      return handleEntityDamageEvent(entity, source, modifiers, functions, cancelled);
   }

   public static boolean handleNonLivingEntityDamageEvent(Entity entity, DamageSource source, double damage, boolean cancelOnZeroDamage, boolean cancelled) {
      EntityDamageEvent event = callNonLivingEntityDamageEvent(entity, source, damage, cancelled);
      if (event == null) {
         return false;
      } else {
         return event.isCancelled() || cancelOnZeroDamage && event.getDamage() == 0.0;
      }
   }

   public static PlayerLevelChangeEvent callPlayerLevelChangeEvent(Player player, int oldLevel, int newLevel) {
      PlayerLevelChangeEvent event = new PlayerLevelChangeEvent(player, oldLevel, newLevel);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerExpChangeEvent callPlayerExpChangeEvent(net.minecraft.world.entity.player.Player entity, int expAmount) {
      Player player = (Player)entity.getBukkitEntity();
      PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, expAmount);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerExpCooldownChangeEvent callPlayerXpCooldownEvent(net.minecraft.world.entity.player.Player entity, int newCooldown, PlayerExpCooldownChangeEvent.ChangeReason changeReason) {
      Player player = (Player)entity.getBukkitEntity();
      PlayerExpCooldownChangeEvent event = new PlayerExpCooldownChangeEvent(player, newCooldown, changeReason);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerItemMendEvent callPlayerItemMendEvent(net.minecraft.world.entity.player.Player entity, ExperienceOrb orb, ItemStack nmsMendedItem, net.minecraft.world.entity.EquipmentSlot slot, int repairAmount) {
      Player player = (Player)entity.getBukkitEntity();
      org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asCraftMirror(nmsMendedItem);
      PlayerItemMendEvent event = new PlayerItemMendEvent(player, bukkitStack, CraftEquipmentSlot.getSlot(slot), (org.bukkit.entity.ExperienceOrb)orb.getBukkitEntity(), repairAmount);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static boolean handleBlockGrowEvent(Level world, BlockPos pos, net.minecraft.world.level.block.state.BlockState block) {
      return handleBlockGrowEvent(world, pos, block, 3);
   }

   public static boolean handleBlockGrowEvent(Level world, BlockPos pos, net.minecraft.world.level.block.state.BlockState newData, int flag) {
      Block block = world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
      CraftBlockState state = (CraftBlockState)block.getState();
      state.setData(newData);
      BlockGrowEvent event = new BlockGrowEvent(block, state);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         state.update(true);
      }

      return !event.isCancelled();
   }

   public static BlockBrushEvent callBlockBrushEvent(Level world, BlockPos pos, net.minecraft.world.level.block.state.BlockState newData, int flag, net.minecraft.world.entity.player.Player entity) {
      Player player = (Player)entity.getBukkitEntity();
      Block block = CraftBlock.at(world, pos);
      CraftBlockState state = (CraftBlockState)block.getState();
      state.setData(newData);
      BlockBrushEvent event = new BlockBrushEvent(block, state, player);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static FluidLevelChangeEvent callFluidLevelChangeEvent(Level world, BlockPos block, net.minecraft.world.level.block.state.BlockState newData) {
      FluidLevelChangeEvent event = new FluidLevelChangeEvent(CraftBlock.at(world, block), CraftBlockData.fromData(newData));
      world.getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static FoodLevelChangeEvent callFoodLevelChangeEvent(net.minecraft.world.entity.player.Player entity, int level) {
      return callFoodLevelChangeEvent(entity, level, (ItemStack)null);
   }

   public static FoodLevelChangeEvent callFoodLevelChangeEvent(net.minecraft.world.entity.player.Player entity, int level, ItemStack item) {
      FoodLevelChangeEvent event = new FoodLevelChangeEvent(entity.getBukkitEntity(), level, item == null ? null : CraftItemStack.asBukkitCopy(item));
      entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static PigZapEvent callPigZapEvent(Entity pig, Entity lightning, Entity pigzombie) {
      PigZapEvent event = new PigZapEvent((Pig)pig.getBukkitEntity(), (LightningStrike)lightning.getBukkitEntity(), (PigZombie)pigzombie.getBukkitEntity());
      pig.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static boolean callHorseJumpEvent(Entity horse, float power) {
      HorseJumpEvent event = new HorseJumpEvent((AbstractHorse)horse.getBukkitEntity(), power);
      horse.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static boolean callEntityChangeBlockEvent(Entity entity, BlockPos position, net.minecraft.world.level.block.state.BlockState newBlock) {
      return callEntityChangeBlockEvent(entity, position, newBlock, false);
   }

   public static boolean callEntityChangeBlockEvent(Entity entity, BlockPos position, net.minecraft.world.level.block.state.BlockState newBlock, boolean cancelled) {
      Block block = entity.level().getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
      EntityChangeBlockEvent event = new EntityChangeBlockEvent(entity.getBukkitEntity(), block, CraftBlockData.fromData(newBlock));
      event.setCancelled(cancelled);
      event.getEntity().getServer().getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static CreeperPowerEvent callCreeperPowerEvent(Entity creeper, Entity lightning, CreeperPowerEvent.PowerCause cause) {
      CreeperPowerEvent event = new CreeperPowerEvent((Creeper)creeper.getBukkitEntity(), (LightningStrike)lightning.getBukkitEntity(), cause);
      creeper.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static EntityTargetEvent callEntityTargetEvent(Entity entity, Entity target, EntityTargetEvent.TargetReason reason) {
      EntityTargetEvent event = new EntityTargetEvent(entity.getBukkitEntity(), target == null ? null : target.getBukkitEntity(), reason);
      entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static EntityTargetLivingEntityEvent callEntityTargetLivingEvent(Entity entity, LivingEntity target, EntityTargetEvent.TargetReason reason) {
      EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(entity.getBukkitEntity(), target == null ? null : (org.bukkit.entity.LivingEntity)target.getBukkitEntity(), reason);
      entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static EntityBreakDoorEvent callEntityBreakDoorEvent(Entity entity, BlockPos pos) {
      org.bukkit.entity.Entity entity1 = entity.getBukkitEntity();
      Block block = CraftBlock.at(entity.level(), pos);
      EntityBreakDoorEvent event = new EntityBreakDoorEvent((org.bukkit.entity.LivingEntity)entity1, block);
      entity1.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static AbstractContainerMenu callInventoryOpenEvent(ServerPlayer player, AbstractContainerMenu container) {
      return callInventoryOpenEvent(player, container, false);
   }

   public static AbstractContainerMenu callInventoryOpenEvent(ServerPlayer player, AbstractContainerMenu container, boolean cancelled) {
      if (player.containerMenu != player.inventoryMenu) {
         player.connection.handleContainerClose(new ServerboundContainerClosePacket(player.containerMenu.containerId));
      }

      CraftServer server = player.level().getCraftServer();
      CraftPlayer craftPlayer = player.getBukkitEntity();
      player.containerMenu.transferTo(container, craftPlayer);
      InventoryOpenEvent event = new InventoryOpenEvent(container.getBukkitView());
      event.setCancelled(cancelled);
      server.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         container.transferTo(player.containerMenu, craftPlayer);
         return null;
      } else {
         return container;
      }
   }

   public static ItemStack callPreCraftEvent(CraftingContainer matrix, Container resultInventory, ItemStack result, InventoryView lastCraftView, Optional<RecipeHolder<CraftingRecipe>> recipe) {
      CraftInventoryCrafting inventory = new CraftInventoryCrafting(matrix, resultInventory);
      inventory.setResult(CraftItemStack.asCraftMirror(result));
      PrepareItemCraftEvent event = new PrepareItemCraftEvent(inventory, lastCraftView, recipe.map(RecipeHolder::value).orElse((Object)null) instanceof RepairItemRecipe);
      Bukkit.getPluginManager().callEvent(event);
      org.bukkit.inventory.ItemStack bitem = event.getInventory().getResult();
      return CraftItemStack.asNMSCopy(bitem);
   }

   public static CrafterCraftEvent callCrafterCraftEvent(BlockPos pos, Level world, CraftingContainer inventoryCrafting, ItemStack result, NonNullList<ItemStack> remaining, RecipeHolder<CraftingRecipe> holder) {
      CraftBlock block = CraftBlock.at(world, pos);
      CraftItemStack itemStack = CraftItemStack.asCraftMirror(result);
      org.bukkit.inventory.CraftingRecipe craftingRecipe = (org.bukkit.inventory.CraftingRecipe)holder.toBukkitRecipe();
      List<org.bukkit.inventory.ItemStack> bukkitRemaining = (List)remaining.stream().map(CraftItemStack::asCraftMirror).collect(Collectors.toCollection(ArrayList::new));
      CrafterCraftEvent crafterCraftEvent = new CrafterCraftEvent(block, craftingRecipe, itemStack, bukkitRemaining);
      Bukkit.getPluginManager().callEvent(crafterCraftEvent);
      return crafterCraftEvent;
   }

   public static ProjectileLaunchEvent callProjectileLaunchEvent(Entity entity) {
      Projectile bukkitEntity = (Projectile)entity.getBukkitEntity();
      ProjectileLaunchEvent event = new ProjectileLaunchEvent(bukkitEntity);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static ProjectileHitEvent callProjectileHitEvent(Entity entity, HitResult position) {
      if (position.getType() == Type.MISS) {
         return null;
      } else {
         Block hitBlock = null;
         BlockFace hitFace = null;
         if (position.getType() == Type.BLOCK) {
            BlockHitResult positionBlock = (BlockHitResult)position;
            hitBlock = CraftBlock.at(entity.level(), positionBlock.getBlockPos());
            hitFace = CraftBlock.notchToBlockFace(positionBlock.getDirection());
         }

         org.bukkit.entity.Entity hitEntity = null;
         if (position.getType() == Type.ENTITY) {
            hitEntity = ((EntityHitResult)position).getEntity().getBukkitEntity();
         }

         ProjectileHitEvent event = new ProjectileHitEvent((Projectile)entity.getBukkitEntity(), hitEntity, hitBlock, hitFace);
         entity.level().getCraftServer().getPluginManager().callEvent(event);
         return event;
      }
   }

   public static ExpBottleEvent callExpBottleEvent(Entity entity, HitResult position, int exp) {
      ThrownExpBottle bottle = (ThrownExpBottle)entity.getBukkitEntity();
      Block hitBlock = null;
      BlockFace hitFace = null;
      if (position.getType() == Type.BLOCK) {
         BlockHitResult positionBlock = (BlockHitResult)position;
         hitBlock = CraftBlock.at(entity.level(), positionBlock.getBlockPos());
         hitFace = CraftBlock.notchToBlockFace(positionBlock.getDirection());
      }

      org.bukkit.entity.Entity hitEntity = null;
      if (position.getType() == Type.ENTITY) {
         hitEntity = ((EntityHitResult)position).getEntity().getBukkitEntity();
      }

      ExpBottleEvent event = new ExpBottleEvent(bottle, hitEntity, hitBlock, hitFace, exp);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static BlockRedstoneEvent callRedstoneChange(Level world, BlockPos pos, int oldCurrent, int newCurrent) {
      BlockRedstoneEvent event = new BlockRedstoneEvent(world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), oldCurrent, newCurrent);
      world.getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static NotePlayEvent callNotePlayEvent(Level world, BlockPos pos, NoteBlockInstrument instrument, int note) {
      NotePlayEvent event = new NotePlayEvent(world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), Instrument.getByType((byte)instrument.ordinal()), new Note(note));
      world.getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static void callPlayerItemBreakEvent(ServerPlayer human, ItemStack brokenItem) {
      CraftItemStack item = CraftItemStack.asCraftMirror(brokenItem);
      PlayerItemBreakEvent event = new PlayerItemBreakEvent(human.getBukkitEntity(), item);
      Bukkit.getPluginManager().callEvent(event);
   }

   public static BlockIgniteEvent callBlockIgniteEvent(Level world, BlockPos block, BlockPos source) {
      World bukkitWorld = world.getWorld();
      Block igniter = bukkitWorld.getBlockAt(source.getX(), source.getY(), source.getZ());
      BlockIgniteEvent.IgniteCause cause;
      switch (igniter.getType()) {
         case LAVA:
            cause = IgniteCause.LAVA;
            break;
         case DISPENSER:
            cause = IgniteCause.FLINT_AND_STEEL;
            break;
         case FIRE:
         default:
            cause = IgniteCause.SPREAD;
      }

      BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(block.getX(), block.getY(), block.getZ()), cause, igniter);
      world.getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockIgniteEvent callBlockIgniteEvent(Level world, BlockPos pos, Entity igniter) {
      World bukkitWorld = world.getWorld();
      org.bukkit.entity.Entity bukkitIgniter = igniter.getBukkitEntity();
      BlockIgniteEvent.IgniteCause cause;
      switch (bukkitIgniter.getType()) {
         case END_CRYSTAL:
            cause = IgniteCause.ENDER_CRYSTAL;
            break;
         case LIGHTNING_BOLT:
            cause = IgniteCause.LIGHTNING;
            break;
         case SMALL_FIREBALL:
         case FIREBALL:
            cause = IgniteCause.FIREBALL;
            break;
         case ARROW:
            cause = IgniteCause.ARROW;
            break;
         default:
            cause = IgniteCause.FLINT_AND_STEEL;
      }

      if (igniter instanceof net.minecraft.world.entity.projectile.Projectile) {
         Entity shooter = ((net.minecraft.world.entity.projectile.Projectile)igniter).getOwner();
         if (shooter != null) {
            bukkitIgniter = shooter.getBukkitEntity();
         }
      }

      BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(pos.getX(), pos.getY(), pos.getZ()), cause, bukkitIgniter);
      world.getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockIgniteEvent callBlockIgniteEvent(Level world, BlockPos blockposition, Explosion explosion) {
      org.bukkit.entity.Entity igniter = explosion.getDirectSourceEntity() == null ? null : explosion.getDirectSourceEntity().getBukkitEntity();
      BlockIgniteEvent event = new BlockIgniteEvent(CraftBlock.at(world, blockposition), IgniteCause.EXPLOSION, igniter);
      world.getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockIgniteEvent callBlockIgniteEvent(Level world, BlockPos pos, BlockIgniteEvent.IgniteCause cause, Entity igniter) {
      BlockIgniteEvent event = new BlockIgniteEvent(world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), cause, igniter.getBukkitEntity());
      world.getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static void handleInventoryCloseEvent(net.minecraft.world.entity.player.Player human) {
      InventoryCloseEvent event = new InventoryCloseEvent(human.containerMenu.getBukkitView());
      human.level().getCraftServer().getPluginManager().callEvent(event);
      human.containerMenu.transferTo(human.inventoryMenu, human.getBukkitEntity());
   }

   public static ItemStack handleEditBookEvent(ServerPlayer player, int itemInHandIndex, ItemStack itemInHand, ItemStack newBookItem) {
      PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(player.getBukkitEntity(), itemInHandIndex >= 0 && itemInHandIndex <= 8 ? itemInHandIndex : -1, (BookMeta)CraftItemStack.getItemMeta(itemInHand), (BookMeta)CraftItemStack.getItemMeta(newBookItem), newBookItem.getItem() == Items.WRITTEN_BOOK);
      player.level().getCraftServer().getPluginManager().callEvent(editBookEvent);
      if (itemInHand != null && itemInHand.getItem() == Items.WRITABLE_BOOK) {
         if (!editBookEvent.isCancelled()) {
            if (editBookEvent.isSigning()) {
               itemInHand.setItem(Items.WRITTEN_BOOK);
            }

            BookMeta meta = editBookEvent.getNewBookMeta();
            CraftItemStack.setItemMeta(itemInHand, meta);
         } else {
            player.getBukkitEntity().updateInventory();
         }
      }

      return itemInHand;
   }

   public static void callRecipeBookSettingsEvent(ServerPlayer player, RecipeBookType type, boolean open, boolean filter) {
      PlayerRecipeBookSettingsChangeEvent.RecipeBookType bukkitType = org.bukkit.event.player.PlayerRecipeBookSettingsChangeEvent.RecipeBookType.values()[type.ordinal()];
      Bukkit.getPluginManager().callEvent(new PlayerRecipeBookSettingsChangeEvent(player.getBukkitEntity(), bukkitType, open, filter));
   }

   public static PlayerUnleashEntityEvent callPlayerUnleashEntityEvent(Entity entity, net.minecraft.world.entity.player.Player player, InteractionHand enumhand) {
      PlayerUnleashEntityEvent event = new PlayerUnleashEntityEvent(entity.getBukkitEntity(), (Player)player.getBukkitEntity(), CraftEquipmentSlot.getHand(enumhand));
      entity.level().getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerLeashEntityEvent callPlayerLeashEntityEvent(Entity entity, Entity leashHolder, net.minecraft.world.entity.player.Player player, InteractionHand enumhand) {
      PlayerLeashEntityEvent event = new PlayerLeashEntityEvent(entity.getBukkitEntity(), leashHolder.getBukkitEntity(), (Player)player.getBukkitEntity(), CraftEquipmentSlot.getHand(enumhand));
      entity.level().getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static void callPlayerRiptideEvent(net.minecraft.world.entity.player.Player player, ItemStack tridentItemStack, float velocityX, float velocityY, float velocityZ) {
      PlayerRiptideEvent event = new PlayerRiptideEvent((Player)player.getBukkitEntity(), CraftItemStack.asCraftMirror(tridentItemStack), new Vector(velocityX, velocityY, velocityZ));
      player.level().getCraftServer().getPluginManager().callEvent(event);
   }

   public static BlockShearEntityEvent callBlockShearEntityEvent(Entity animal, Block dispenser, CraftItemStack is) {
      BlockShearEntityEvent bse = new BlockShearEntityEvent(dispenser, animal.getBukkitEntity(), is);
      Bukkit.getPluginManager().callEvent(bse);
      return bse;
   }

   public static boolean handlePlayerShearEntityEvent(net.minecraft.world.entity.player.Player player, Entity sheared, ItemStack shears, InteractionHand hand) {
      if (!(player instanceof ServerPlayer)) {
         return true;
      } else {
         PlayerShearEntityEvent event = new PlayerShearEntityEvent((Player)player.getBukkitEntity(), sheared.getBukkitEntity(), CraftItemStack.asCraftMirror(shears), hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
         Bukkit.getPluginManager().callEvent(event);
         return !event.isCancelled();
      }
   }

   public static Cancellable handleStatisticsIncrease(net.minecraft.world.entity.player.Player entityHuman, Stat<?> statistic, int current, int newValue) {
      Player player = ((ServerPlayer)entityHuman).getBukkitEntity();
      Statistic stat = CraftStatistic.getBukkitStatistic(statistic);
      if (stat == null) {
         System.err.println("Unhandled statistic: " + String.valueOf(statistic));
         return null;
      } else {
         switch (stat) {
            case FALL_ONE_CM:
            case BOAT_ONE_CM:
            case CLIMB_ONE_CM:
            case WALK_ON_WATER_ONE_CM:
            case WALK_UNDER_WATER_ONE_CM:
            case FLY_ONE_CM:
            case HORSE_ONE_CM:
            case MINECART_ONE_CM:
            case PIG_ONE_CM:
            case PLAY_ONE_MINUTE:
            case SWIM_ONE_CM:
            case WALK_ONE_CM:
            case SPRINT_ONE_CM:
            case CROUCH_ONE_CM:
            case TIME_SINCE_DEATH:
            case SNEAK_TIME:
            case TOTAL_WORLD_TIME:
            case TIME_SINCE_REST:
            case AVIATE_ONE_CM:
            case STRIDER_ONE_CM:
               return null;
            default:
               PlayerStatisticIncrementEvent event;
               if (stat.getType() == org.bukkit.Statistic.Type.UNTYPED) {
                  event = new PlayerStatisticIncrementEvent(player, stat, current, newValue);
               } else if (stat.getType() == org.bukkit.Statistic.Type.ENTITY) {
                  EntityType entityType = CraftStatistic.getEntityTypeFromStatistic(statistic);
                  event = new PlayerStatisticIncrementEvent(player, stat, current, newValue, entityType);
               } else {
                  Material material = CraftStatistic.getMaterialFromStatistic(statistic);
                  event = new PlayerStatisticIncrementEvent(player, stat, current, newValue, material);
               }

               entityHuman.level().getCraftServer().getPluginManager().callEvent(event);
               return (Cancellable)event;
         }
      }
   }

   public static FireworkExplodeEvent callFireworkExplodeEvent(FireworkRocketEntity firework) {
      FireworkExplodeEvent event = new FireworkExplodeEvent((Firework)firework.getBukkitEntity());
      firework.level().getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static PrepareAnvilEvent callPrepareAnvilEvent(AnvilView view, ItemStack item) {
      PrepareAnvilEvent event = new PrepareAnvilEvent(view, CraftItemStack.asCraftMirror(item).clone());
      event.getView().getPlayer().getServer().getPluginManager().callEvent(event);
      event.getInventory().setItem(2, event.getResult());
      return event;
   }

   public static PrepareGrindstoneEvent callPrepareGrindstoneEvent(InventoryView view, ItemStack item) {
      PrepareGrindstoneEvent event = new PrepareGrindstoneEvent(view, CraftItemStack.asCraftMirror(item).clone());
      event.getView().getPlayer().getServer().getPluginManager().callEvent(event);
      event.getInventory().setItem(2, event.getResult());
      return event;
   }

   public static PrepareSmithingEvent callPrepareSmithingEvent(InventoryView view, ItemStack item) {
      PrepareSmithingEvent event = new PrepareSmithingEvent(view, CraftItemStack.asCraftMirror(item).clone());
      event.getView().getPlayer().getServer().getPluginManager().callEvent(event);
      event.getInventory().setResult(event.getResult());
      return event;
   }

   public static SpawnerSpawnEvent callSpawnerSpawnEvent(Entity spawnee, BlockPos pos) {
      CraftEntity entity = spawnee.getBukkitEntity();
      BlockState state = CraftBlock.at(spawnee.level(), pos).getState();
      if (!(state instanceof CreatureSpawner)) {
         state = null;
      }

      SpawnerSpawnEvent event = new SpawnerSpawnEvent(entity, (CreatureSpawner)state);
      entity.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static TrialSpawnerSpawnEvent callTrialSpawnerSpawnEvent(Entity spawnee, BlockPos pos) {
      CraftEntity entity = spawnee.getBukkitEntity();
      BlockState state = CraftBlock.at(spawnee.level(), pos).getState();
      if (!(state instanceof TrialSpawner)) {
         state = null;
      }

      TrialSpawnerSpawnEvent event = new TrialSpawnerSpawnEvent(entity, (TrialSpawner)state);
      entity.getServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockDispenseLootEvent callBlockDispenseLootEvent(ServerLevel worldServer, BlockPos blockPosition, net.minecraft.world.entity.player.Player player, List<ItemStack> rewardLoot) {
      List<org.bukkit.inventory.ItemStack> craftItemStacks = (List)rewardLoot.stream().map(CraftItemStack::asBukkitCopy).collect(Collectors.toList());
      BlockDispenseLootEvent event = new BlockDispenseLootEvent(player == null ? null : (Player)player.getBukkitEntity(), CraftBlock.at(worldServer, blockPosition), craftItemStacks);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static VaultDisplayItemEvent callVaultDisplayItemEvent(ServerLevel worldServer, BlockPos blockPosition, ItemStack displayitemStack) {
      VaultDisplayItemEvent event = new VaultDisplayItemEvent(CraftBlock.at(worldServer, blockPosition), CraftItemStack.asBukkitCopy(displayitemStack));
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static EntityToggleGlideEvent callToggleGlideEvent(LivingEntity entity, boolean gliding) {
      EntityToggleGlideEvent event = new EntityToggleGlideEvent((org.bukkit.entity.LivingEntity)entity.getBukkitEntity(), gliding);
      entity.level().getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static EntityToggleSwimEvent callToggleSwimEvent(LivingEntity entity, boolean swimming) {
      EntityToggleSwimEvent event = new EntityToggleSwimEvent((org.bukkit.entity.LivingEntity)entity.getBukkitEntity(), swimming);
      entity.level().getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static AreaEffectCloudApplyEvent callAreaEffectCloudApplyEvent(AreaEffectCloud cloud, List<org.bukkit.entity.LivingEntity> entities) {
      AreaEffectCloudApplyEvent event = new AreaEffectCloudApplyEvent((org.bukkit.entity.AreaEffectCloud)cloud.getBukkitEntity(), entities);
      cloud.level().getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static VehicleCreateEvent callVehicleCreateEvent(Entity entity) {
      Vehicle bukkitEntity = (Vehicle)entity.getBukkitEntity();
      VehicleCreateEvent event = new VehicleCreateEvent(bukkitEntity);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static EntityBreedEvent callEntityBreedEvent(LivingEntity child, LivingEntity mother, LivingEntity father, LivingEntity breeder, ItemStack bredWith, int experience) {
      org.bukkit.entity.LivingEntity breederEntity = (org.bukkit.entity.LivingEntity)(breeder == null ? null : breeder.getBukkitEntity());
      CraftItemStack bredWithStack = bredWith == null ? null : CraftItemStack.asCraftMirror(bredWith).clone();
      EntityBreedEvent event = new EntityBreedEvent((org.bukkit.entity.LivingEntity)child.getBukkitEntity(), (org.bukkit.entity.LivingEntity)mother.getBukkitEntity(), (org.bukkit.entity.LivingEntity)father.getBukkitEntity(), breederEntity, bredWithStack, experience);
      child.level().getCraftServer().getPluginManager().callEvent(event);
      return event;
   }

   public static BlockPhysicsEvent callBlockPhysicsEvent(LevelAccessor world, BlockPos blockposition) {
      Block block = CraftBlock.at(world, blockposition);
      BlockPhysicsEvent event = new BlockPhysicsEvent(block, block.getBlockData());
      if (world instanceof Level) {
         ((Level)world).getServer().server.getPluginManager().callEvent(event);
      }

      return event;
   }

   public static boolean handleBlockFormEvent(Level world, BlockPos pos, net.minecraft.world.level.block.state.BlockState block) {
      return handleBlockFormEvent(world, pos, block, 3);
   }

   public static EntityPotionEffectEvent callEntityPotionEffectChangeEvent(LivingEntity entity, @Nullable MobEffectInstance oldEffect, @Nullable MobEffectInstance newEffect, EntityPotionEffectEvent.Cause cause) {
      return callEntityPotionEffectChangeEvent(entity, oldEffect, newEffect, cause, true);
   }

   public static EntityPotionEffectEvent callEntityPotionEffectChangeEvent(LivingEntity entity, @Nullable MobEffectInstance oldEffect, @Nullable MobEffectInstance newEffect, EntityPotionEffectEvent.Cause cause, EntityPotionEffectEvent.Action action) {
      return callEntityPotionEffectChangeEvent(entity, oldEffect, newEffect, cause, action, true);
   }

   public static EntityPotionEffectEvent callEntityPotionEffectChangeEvent(LivingEntity entity, @Nullable MobEffectInstance oldEffect, @Nullable MobEffectInstance newEffect, EntityPotionEffectEvent.Cause cause, boolean willOverride) {
      EntityPotionEffectEvent.Action action = org.bukkit.event.entity.EntityPotionEffectEvent.Action.CHANGED;
      if (oldEffect == null) {
         action = org.bukkit.event.entity.EntityPotionEffectEvent.Action.ADDED;
      } else if (newEffect == null) {
         action = org.bukkit.event.entity.EntityPotionEffectEvent.Action.REMOVED;
      }

      return callEntityPotionEffectChangeEvent(entity, oldEffect, newEffect, cause, action, willOverride);
   }

   public static EntityPotionEffectEvent callEntityPotionEffectChangeEvent(LivingEntity entity, @Nullable MobEffectInstance oldEffect, @Nullable MobEffectInstance newEffect, EntityPotionEffectEvent.Cause cause, EntityPotionEffectEvent.Action action, boolean willOverride) {
      PotionEffect bukkitOldEffect = oldEffect == null ? null : CraftPotionUtil.toBukkit(oldEffect);
      PotionEffect bukkitNewEffect = newEffect == null ? null : CraftPotionUtil.toBukkit(newEffect);
      Preconditions.checkState(bukkitOldEffect != null || bukkitNewEffect != null, "Old and new potion effect are both null");
      EntityPotionEffectEvent event = new EntityPotionEffectEvent((org.bukkit.entity.LivingEntity)entity.getBukkitEntity(), bukkitOldEffect, bukkitNewEffect, cause, action, willOverride);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static boolean handleBlockFormEvent(Level world, BlockPos pos, net.minecraft.world.level.block.state.BlockState block, @Nullable Entity entity) {
      return handleBlockFormEvent(world, pos, block, 3, entity);
   }

   public static boolean handleBlockFormEvent(Level world, BlockPos pos, net.minecraft.world.level.block.state.BlockState block, int flag) {
      return handleBlockFormEvent(world, pos, block, flag, (Entity)null);
   }

   public static boolean handleBlockFormEvent(Level world, BlockPos pos, net.minecraft.world.level.block.state.BlockState block, int flag, @Nullable Entity entity) {
      CraftBlockState blockState = CraftBlockStates.getBlockState(world, pos, flag);
      blockState.setData(block);
      BlockFormEvent event = entity == null ? new BlockFormEvent(blockState.getBlock(), blockState) : new EntityBlockFormEvent(entity.getBukkitEntity(), blockState.getBlock(), blockState);
      world.getCraftServer().getPluginManager().callEvent((Event)event);
      if (!((BlockFormEvent)event).isCancelled()) {
         blockState.update(true);
      }

      return !((BlockFormEvent)event).isCancelled();
   }

   public static boolean handleBatToggleSleepEvent(Entity bat, boolean awake) {
      BatToggleSleepEvent event = new BatToggleSleepEvent((Bat)bat.getBukkitEntity(), awake);
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static boolean handlePlayerRecipeListUpdateEvent(net.minecraft.world.entity.player.Player who, ResourceLocation recipe) {
      PlayerRecipeDiscoverEvent event = new PlayerRecipeDiscoverEvent((Player)who.getBukkitEntity(), CraftNamespacedKey.fromMinecraft(recipe));
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static EntityPickupItemEvent callEntityPickupItemEvent(Entity who, ItemEntity item, int remaining, boolean cancelled) {
      EntityPickupItemEvent event = new EntityPickupItemEvent((org.bukkit.entity.LivingEntity)who.getBukkitEntity(), (Item)item.getBukkitEntity(), remaining);
      event.setCancelled(cancelled);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static LightningStrikeEvent callLightningStrikeEvent(LightningStrike entity, LightningStrikeEvent.Cause cause) {
      LightningStrikeEvent event = new LightningStrikeEvent(entity.getWorld(), entity, cause);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static boolean callRaidTriggerEvent(Raid raid, Level world, ServerPlayer player) {
      RaidTriggerEvent event = new RaidTriggerEvent(new CraftRaid(raid, world), world.getWorld(), player.getBukkitEntity());
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static void callRaidFinishEvent(Raid raid, Level world, List<Player> players) {
      RaidFinishEvent event = new RaidFinishEvent(new CraftRaid(raid, world), world.getWorld(), players);
      Bukkit.getPluginManager().callEvent(event);
   }

   public static void callRaidStopEvent(Raid raid, Level world, RaidStopEvent.Reason reason) {
      RaidStopEvent event = new RaidStopEvent(new CraftRaid(raid, world), world.getWorld(), reason);
      Bukkit.getPluginManager().callEvent(event);
   }

   public static void callRaidSpawnWaveEvent(Raid raid, Level world, Raider leader, List<Raider> raiders) {
      org.bukkit.entity.Raider craftLeader = (CraftRaider)leader.getBukkitEntity();
      List<org.bukkit.entity.Raider> craftRaiders = new ArrayList();
      Iterator var6 = raiders.iterator();

      while(var6.hasNext()) {
         Raider entityRaider = (Raider)var6.next();
         craftRaiders.add((org.bukkit.entity.Raider)entityRaider.getBukkitEntity());
      }

      RaidSpawnWaveEvent event = new RaidSpawnWaveEvent(new CraftRaid(raid, world), world.getWorld(), craftLeader, craftRaiders);
      Bukkit.getPluginManager().callEvent(event);
   }

   public static LootGenerateEvent callLootGenerateEvent(Container inventory, LootTable lootTable, LootContext lootInfo, List<ItemStack> loot, boolean plugin) {
      CraftWorld world = lootInfo.getLevel().getWorld();
      Entity entity = (Entity)lootInfo.getOptionalParameter(LootContextParams.THIS_ENTITY);
      List<org.bukkit.inventory.ItemStack> bukkitLoot = (List)loot.stream().map(CraftItemStack::asCraftMirror).collect(Collectors.toCollection(ArrayList::new));
      LootGenerateEvent event = new LootGenerateEvent(world, entity != null ? entity.getBukkitEntity() : null, inventory.getOwner(), lootTable.craftLootTable, CraftLootTable.convertContext(lootInfo), bukkitLoot, plugin);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static boolean callStriderTemperatureChangeEvent(Strider strider, boolean shivering) {
      StriderTemperatureChangeEvent event = new StriderTemperatureChangeEvent((org.bukkit.entity.Strider)strider.getBukkitEntity(), shivering);
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static boolean handleEntitySpellCastEvent(SpellcasterIllager caster, SpellcasterIllager.IllagerSpell spell) {
      EntitySpellCastEvent event = new EntitySpellCastEvent((Spellcaster)caster.getBukkitEntity(), CraftSpellcaster.toBukkitSpell(spell));
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static ArrowBodyCountChangeEvent callArrowBodyCountChangeEvent(LivingEntity entity, int oldAmount, int newAmount, boolean isReset) {
      org.bukkit.entity.LivingEntity bukkitEntity = (org.bukkit.entity.LivingEntity)entity.getBukkitEntity();
      ArrowBodyCountChangeEvent event = new ArrowBodyCountChangeEvent(bukkitEntity, oldAmount, newAmount, isReset);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static EntityExhaustionEvent callPlayerExhaustionEvent(net.minecraft.world.entity.player.Player humanEntity, EntityExhaustionEvent.ExhaustionReason exhaustionReason, float exhaustion) {
      EntityExhaustionEvent event = new EntityExhaustionEvent(humanEntity.getBukkitEntity(), exhaustionReason, exhaustion);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static PiglinBarterEvent callPiglinBarterEvent(Piglin piglin, List<ItemStack> outcome, ItemStack input) {
      PiglinBarterEvent event = new PiglinBarterEvent((org.bukkit.entity.Piglin)piglin.getBukkitEntity(), CraftItemStack.asBukkitCopy(input), (List)outcome.stream().map(CraftItemStack::asBukkitCopy).collect(Collectors.toList()));
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static void callEntitiesLoadEvent(Level world, ChunkPos coords, List<Entity> entities) {
      List<org.bukkit.entity.Entity> bukkitEntities = Collections.unmodifiableList((List)entities.stream().map(Entity::getBukkitEntity).collect(Collectors.toList()));
      EntitiesLoadEvent event = new EntitiesLoadEvent(new CraftChunk((ServerLevel)world, coords.x, coords.z), bukkitEntities);
      Bukkit.getPluginManager().callEvent(event);
   }

   public static void callEntitiesUnloadEvent(Level world, ChunkPos coords, List<Entity> entities) {
      List<org.bukkit.entity.Entity> bukkitEntities = Collections.unmodifiableList((List)entities.stream().map(Entity::getBukkitEntity).collect(Collectors.toList()));
      EntitiesUnloadEvent event = new EntitiesUnloadEvent(new CraftChunk((ServerLevel)world, coords.x, coords.z), bukkitEntities);
      Bukkit.getPluginManager().callEvent(event);
   }

   public static boolean callTNTPrimeEvent(Level world, BlockPos pos, TNTPrimeEvent.PrimeCause cause, Entity causingEntity, BlockPos causePosition) {
      org.bukkit.entity.Entity bukkitEntity = causingEntity == null ? null : causingEntity.getBukkitEntity();
      Block bukkitBlock = causePosition == null ? null : CraftBlock.at(world, causePosition);
      TNTPrimeEvent event = new TNTPrimeEvent(CraftBlock.at(world, pos), cause, bukkitEntity, bukkitBlock);
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static PlayerRecipeBookClickEvent callRecipeBookClickEvent(ServerPlayer player, Recipe recipe, boolean shiftClick) {
      PlayerRecipeBookClickEvent event = new PlayerRecipeBookClickEvent(player.getBukkitEntity(), recipe, shiftClick);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static EntityTeleportEvent callEntityTeleportEvent(Entity nmsEntity, double x, double y, double z) {
      CraftEntity entity = nmsEntity.getBukkitEntity();
      Location to = new Location(entity.getWorld(), x, y, z, nmsEntity.getYRot(), nmsEntity.getXRot());
      return callEntityTeleportEvent(nmsEntity, to);
   }

   public static EntityTeleportEvent callEntityTeleportEvent(Entity nmsEntity, Location to) {
      CraftEntity entity = nmsEntity.getBukkitEntity();
      EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static boolean callEntityInteractEvent(Entity nmsEntity, Block block) {
      EntityInteractEvent event = new EntityInteractEvent(nmsEntity.getBukkitEntity(), block);
      Bukkit.getPluginManager().callEvent(event);
      return !event.isCancelled();
   }

   public static List<Block> handleExplodeEvent(ServerExplosion serverExplosion, List<BlockPos> blockPositions) {
      World bworld = serverExplosion.level().getWorld();
      List<Block> blockList = new ObjectArrayList();

      for(int i1 = blockPositions.size() - 1; i1 >= 0; --i1) {
         BlockPos cpos = (BlockPos)blockPositions.get(i1);
         Block bblock = bworld.getBlockAt(cpos.getX(), cpos.getY(), cpos.getZ());
         if (!bblock.getType().isAir()) {
            blockList.add(bblock);
         }
      }

      if (serverExplosion.getDirectSourceEntity() == null && serverExplosion.getDamageSource().getCausingDamager() == null) {
         Location location = CraftLocation.toBukkit((Vec3)serverExplosion.center(), (World)bworld);
         Block block = location.getBlock();
         BlockState blockState = serverExplosion.getDamageSource().getDirectBlockState() != null ? serverExplosion.getDamageSource().getDirectBlockState() : block.getState();
         BlockExplodeEvent event = callBlockExplodeEvent(block, blockState, blockList, serverExplosion.yield, serverExplosion.getBlockInteraction());
         serverExplosion.wasCanceled = event.isCancelled();
         serverExplosion.yield = event.getYield();
         return event.blockList();
      } else {
         EntityExplodeEvent event = callEntityExplodeEvent(serverExplosion.getDirectSourceEntity() != null ? serverExplosion.getDirectSourceEntity() : serverExplosion.getDamageSource().getCausingDamager(), blockList, serverExplosion.yield, serverExplosion.getBlockInteraction());
         serverExplosion.wasCanceled = event.isCancelled();
         serverExplosion.yield = event.getYield();
         return event.blockList();
      }
   }

   public static EntityExplodeEvent callEntityExplodeEvent(Entity entity, List<Block> blocks, float yield, Explosion.BlockInteraction effect) {
      EntityExplodeEvent event = new EntityExplodeEvent(entity.getBukkitEntity(), entity.getBukkitEntity().getLocation(), blocks, yield, CraftExplosionResult.toBukkit(effect));
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static BlockExplodeEvent callBlockExplodeEvent(Block block, BlockState state, List<Block> blocks, float yield, Explosion.BlockInteraction effect) {
      BlockExplodeEvent event = new BlockExplodeEvent(block, state, blocks, yield, CraftExplosionResult.toBukkit(effect));
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static ExplosionPrimeEvent callExplosionPrimeEvent(Explosive explosive) {
      ExplosionPrimeEvent event = new ExplosionPrimeEvent(explosive);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static ExplosionPrimeEvent callExplosionPrimeEvent(Entity nmsEntity, float size, boolean fire) {
      ExplosionPrimeEvent event = new ExplosionPrimeEvent(nmsEntity.getBukkitEntity(), size, fire);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static EntityKnockbackEvent callEntityKnockbackEvent(CraftLivingEntity entity, Entity attacker, EntityKnockbackEvent.KnockbackCause cause, double force, Vec3 raw, double x, double y, double z) {
      Vector bukkitRaw = new Vector(-raw.x, raw.y, -raw.z);
      Object event;
      if (attacker != null) {
         event = new EntityKnockbackByEntityEvent(entity, attacker.getBukkitEntity(), cause, force, bukkitRaw, new Vector(x, y, z));
      } else {
         event = new EntityKnockbackEvent(entity, cause, force, bukkitRaw, new Vector(x, y, z));
      }

      Bukkit.getPluginManager().callEvent((Event)event);
      return (EntityKnockbackEvent)event;
   }

   public static void callEntityRemoveEvent(Entity entity, EntityRemoveEvent.Cause cause) {
      if (!(entity instanceof ServerPlayer)) {
         if (cause != null) {
            if (entity.inWorld) {
               Bukkit.getPluginManager().callEvent(new EntityRemoveEvent(entity.getBukkitEntity(), cause));
            }
         }
      }
   }

   public static VillagerReputationChangeEvent callVillagerReputationChangeEvent(org.bukkit.entity.Villager villager, UUID targetUuid, org.bukkit.entity.Villager.ReputationEvent reason, org.bukkit.entity.Villager.ReputationType reputationType, int oldValue, int newValue, int maxValue) {
      VillagerReputationChangeEvent event = new VillagerReputationChangeEvent(villager, targetUuid, reason, reputationType, oldValue, newValue, maxValue);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   public static PlayerCustomClickEvent callPlayerCustomClickEvent(ResourceLocation minecraftkey, Optional<Tag> optional, ServerPlayer player) {
      NamespacedKey id = CraftNamespacedKey.fromMinecraft(minecraftkey);
      JsonElement data = (JsonElement)optional.map((nbt) -> {
         return (JsonElement)NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, nbt);
      }).orElse((Object)null);
      PlayerCustomClickEvent event = new PlayerCustomClickEvent(player.getBukkitEntity(), id, data);
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }
}
