package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.registry.CraftOldEnumRegistryItem;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;

public class CraftVillager extends CraftAbstractVillager implements Villager {
   public CraftVillager(CraftServer server, net.minecraft.world.entity.npc.Villager entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.npc.Villager getHandle() {
      return (net.minecraft.world.entity.npc.Villager)this.entity;
   }

   public String toString() {
      return "CraftVillager";
   }

   public void remove() {
      this.getHandle().releaseAllPois();
      super.remove();
   }

   public Villager.Profession getProfession() {
      return CraftVillager.CraftProfession.minecraftHolderToBukkit(this.getHandle().getVillagerData().profession());
   }

   public void setProfession(Villager.Profession profession) {
      Preconditions.checkArgument(profession != null, "Profession cannot be null");
      this.getHandle().setVillagerData(this.getHandle().getVillagerData().withProfession(CraftVillager.CraftProfession.bukkitToMinecraftHolder(profession)));
   }

   public Villager.Type getVillagerType() {
      return CraftVillager.CraftType.minecraftHolderToBukkit(this.getHandle().getVillagerData().type());
   }

   public void setVillagerType(Villager.Type type) {
      Preconditions.checkArgument(type != null, "Type cannot be null");
      this.getHandle().setVillagerData(this.getHandle().getVillagerData().withType(CraftVillager.CraftType.bukkitToMinecraftHolder(type)));
   }

   public int getVillagerLevel() {
      return this.getHandle().getVillagerData().level();
   }

   public void setVillagerLevel(int level) {
      Preconditions.checkArgument(1 <= level && level <= 5, "level (%s) must be between [1, 5]", level);
      this.getHandle().setVillagerData(this.getHandle().getVillagerData().withLevel(level));
   }

   public int getVillagerExperience() {
      return this.getHandle().getVillagerXp();
   }

   public void setVillagerExperience(int experience) {
      Preconditions.checkArgument(experience >= 0, "Experience (%s) must be positive", experience);
      this.getHandle().setVillagerXp(experience);
   }

   public boolean sleep(Location location) {
      Preconditions.checkArgument(location != null, "Location cannot be null");
      Preconditions.checkArgument(location.getWorld() != null, "Location needs to be in a world");
      Preconditions.checkArgument(location.getWorld().equals(this.getWorld()), "Cannot sleep across worlds");
      Preconditions.checkState(!this.getHandle().bridge$generation(), "Cannot sleep during world generation");
      BlockPos position = CraftLocation.toBlockPosition(location);
      BlockState iblockdata = this.getHandle().level().getBlockState(position);
      if (!(iblockdata.getBlock() instanceof BedBlock)) {
         return false;
      } else {
         this.getHandle().startSleeping(position);
         return true;
      }
   }

   public void wakeup() {
      Preconditions.checkState(this.isSleeping(), "Cannot wakeup if not sleeping");
      Preconditions.checkState(!this.getHandle().bridge$generation(), "Cannot wakeup during world generation");
      this.getHandle().stopSleeping();
   }

   public void shakeHead() {
      this.getHandle().setUnhappy();
   }

   public ZombieVillager zombify() {
      net.minecraft.world.entity.monster.ZombieVillager entityzombievillager = BukkitMethodHooks.convertVillagerToZombieVillager(this.getHandle().level().getMinecraftWorld(), this.getHandle(), this.getHandle().blockPosition(), this.isSilent(), TransformReason.INFECTION, SpawnReason.CUSTOM);
      return entityzombievillager != null ? (ZombieVillager)entityzombievillager.getBukkitEntity() : null;
   }

   public int getReputation(UUID uuid, Villager.ReputationType reputationType) {
      Preconditions.checkArgument(uuid != null, "UUID cannot be null");
      Preconditions.checkArgument(reputationType != null, "Reputation type cannot be null");
      return this.getHandle().getGossips().getReputation(uuid, Predicate.isEqual(CraftVillager.CraftReputationType.bukkitToMinecraft(reputationType))/*, false TODO fixme*/);
   }

   public int getWeightedReputation(UUID uuid, Villager.ReputationType reputationType) {
      Preconditions.checkArgument(uuid != null, "UUID cannot be null");
      Preconditions.checkArgument(reputationType != null, "Reputation type cannot be null");
      return this.getHandle().getGossips().getReputation(uuid, Predicate.isEqual(CraftVillager.CraftReputationType.bukkitToMinecraft(reputationType)) /*, true TODO fixme*/);
   }

   public int getReputation(UUID uuid) {
      Preconditions.checkArgument(uuid != null, "UUID cannot be null");
      return this.getHandle().getGossips().getReputation(uuid, (reputationType) -> {
         return true;
      });
   }

   public void addReputation(UUID uuid, Villager.ReputationType reputationType, int amount) {
      this.addReputation(uuid, reputationType, amount, ReputationEvent.UNSPECIFIED);
   }

   public void addReputation(UUID uuid, Villager.ReputationType reputationType, int amount, Villager.ReputationEvent changeReason) {
      Preconditions.checkArgument(uuid != null, "UUID cannot be null");
      Preconditions.checkArgument(reputationType != null, "Reputation type cannot be null");
      Preconditions.checkArgument(changeReason != null, "Change reason cannot be null");
      this.getHandle().getGossips().add(uuid, CraftVillager.CraftReputationType.bukkitToMinecraft(reputationType), amount/*, changeReason Taiyitist TODO fimxe*/);
   }

   public void removeReputation(UUID uuid, Villager.ReputationType reputationType, int amount) {
      this.removeReputation(uuid, reputationType, amount, ReputationEvent.UNSPECIFIED);
   }

   public void removeReputation(UUID uuid, Villager.ReputationType reputationType, int amount, Villager.ReputationEvent changeReason) {
      Preconditions.checkArgument(uuid != null, "UUID cannot be null");
      Preconditions.checkArgument(reputationType != null, "Reputation type cannot be null");
      Preconditions.checkArgument(changeReason != null, "Change reason cannot be null");
      this.getHandle().getGossips().remove(uuid, CraftVillager.CraftReputationType.bukkitToMinecraft(reputationType), amount/*, changeReason Taiyitist - TODO fixme*/);
   }

   public void setReputation(UUID uuid, Villager.ReputationType reputationType, int amount) {
      this.setReputation(uuid, reputationType, amount, ReputationEvent.UNSPECIFIED);
   }

   public void setReputation(UUID uuid, Villager.ReputationType reputationType, int amount, Villager.ReputationEvent changeReason) {
      Preconditions.checkArgument(uuid != null, "UUID cannot be null");
      Preconditions.checkArgument(reputationType != null, "Reputation type cannot be null");
      Preconditions.checkArgument(changeReason != null, "Change reason cannot be null");
      this.getHandle().getGossips().add(uuid, CraftVillager.CraftReputationType.bukkitToMinecraft(reputationType), amount/*, changeReason Taiyitist - TODO fixme*/);
   }

   public void setGossipDecayTime(long ticks) {
      this.getHandle().taiyitist$setGossipDecayInterval(ticks);
   }

   public long getGossipDecayTime() {
      return this.getHandle().bridge$gossipDecayInterval();
   }

   public static class CraftProfession extends CraftOldEnumRegistryItem<Villager.Profession, VillagerProfession> implements Villager.Profession {
      private static int count = 0;

      public static Villager.Profession minecraftToBukkit(VillagerProfession minecraft) {
         return (Villager.Profession)CraftRegistry.minecraftToBukkit(minecraft, Registries.VILLAGER_PROFESSION, Registry.VILLAGER_PROFESSION);
      }

      public static Villager.Profession minecraftHolderToBukkit(Holder<VillagerProfession> minecraft) {
         return minecraftToBukkit((VillagerProfession)minecraft.value());
      }

      public static VillagerProfession bukkitToMinecraft(Villager.Profession bukkit) {
         return (VillagerProfession)CraftRegistry.bukkitToMinecraft(bukkit);
      }

      public static Holder<VillagerProfession> bukkitToMinecraftHolder(Villager.Profession bukkit) {
         Preconditions.checkArgument(bukkit != null);
         net.minecraft.core.Registry<VillagerProfession> registry = CraftRegistry.getMinecraftRegistry(Registries.VILLAGER_PROFESSION);
         if (registry.wrapAsHolder(bukkitToMinecraft(bukkit)) instanceof Holder.Reference<VillagerProfession> holder) {
            return holder;
         } else {
            throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own villager profession without properly registering it.");
         }
      }

      public CraftProfession(NamespacedKey key, Holder<VillagerProfession> handle) {
         super(key, handle, count++);
      }

      public NamespacedKey getKey() {
         return this.getKeyOrThrow();
      }
   }

   public static class CraftType extends CraftOldEnumRegistryItem<Villager.Type, VillagerType> implements Villager.Type {
      private static int count = 0;

      public static Villager.Type minecraftToBukkit(VillagerType minecraft) {
         return (Villager.Type)CraftRegistry.minecraftToBukkit(minecraft, Registries.VILLAGER_TYPE, Registry.VILLAGER_TYPE);
      }

      public static Villager.Type minecraftHolderToBukkit(Holder<VillagerType> minecraft) {
         return minecraftToBukkit((VillagerType)minecraft.value());
      }

      public static VillagerType bukkitToMinecraft(Villager.Type bukkit) {
         return (VillagerType)CraftRegistry.bukkitToMinecraft(bukkit);
      }

      public static Holder<VillagerType> bukkitToMinecraftHolder(Villager.Type bukkit) {
         Preconditions.checkArgument(bukkit != null);
         net.minecraft.core.Registry<VillagerType> registry = CraftRegistry.getMinecraftRegistry(Registries.VILLAGER_TYPE);
         if (registry.wrapAsHolder(bukkitToMinecraft(bukkit)) instanceof Holder.Reference<VillagerType> holder) {
            return holder;
         } else {
            throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own villager type without properly registering it.");
         }
      }

      public CraftType(NamespacedKey key, Holder<VillagerType> handle) {
         super(key, handle, count++);
      }

      public NamespacedKey getKey() {
         return this.getKeyOrThrow();
      }
   }

   public static class CraftReputationType implements Villager.ReputationType, Handleable<GossipType> {
      public static final Map<String, CraftReputationType> BY_ID = (Map)Stream.of(GossipType.values()).collect(Collectors.toMap((reputationType) -> {
         return reputationType.id;
      }, CraftReputationType::new));
      private final GossipType handle;

      public CraftReputationType(GossipType handle) {
         this.handle = handle;
      }

      public GossipType getHandle() {
         return this.handle;
      }

      public int getMaxValue() {
         return this.handle.max;
      }

      public int getWeight() {
         return this.handle.weight;
      }

      public static GossipType bukkitToMinecraft(Villager.ReputationType bukkit) {
         Preconditions.checkArgument(bukkit != null);
         return ((CraftReputationType)bukkit).getHandle();
      }

      public static Villager.ReputationType minecraftToBukkit(GossipType minecraft) {
         Preconditions.checkArgument(minecraft != null);
         Villager.ReputationType var10000;
         switch (minecraft) {
            case MAJOR_NEGATIVE -> var10000 = ReputationType.MAJOR_NEGATIVE;
            case MINOR_NEGATIVE -> var10000 = ReputationType.MINOR_NEGATIVE;
            case MINOR_POSITIVE -> var10000 = ReputationType.MINOR_POSITIVE;
            case MAJOR_POSITIVE -> var10000 = ReputationType.MAJOR_POSITIVE;
            case TRADING -> var10000 = ReputationType.TRADING;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   public static class CraftReputationEvent implements Villager.ReputationEvent, Handleable<ReputationEventType> {
      private static final Map<String, Villager.ReputationEvent> ALL = Maps.newHashMap();
      private final ReputationEventType handle;

      public CraftReputationEvent(ReputationEventType handle) {
         this.handle = handle;
         ALL.put(handle.toString(), this);
      }

      public ReputationEventType getHandle() {
         return this.handle;
      }

      public static ReputationEventType bukkitToMinecraft(Villager.ReputationEvent bukkit) {
         Preconditions.checkArgument(bukkit != null);
         return ((CraftReputationEvent)bukkit).getHandle();
      }

      public static Villager.ReputationEvent minecraftToBukkit(ReputationEventType minecraft) {
         Preconditions.checkArgument(minecraft != null);
         Villager.ReputationEvent bukkit = (Villager.ReputationEvent)ALL.get(minecraft.toString());
         return (Villager.ReputationEvent)(bukkit == null ? new CraftReputationEvent(minecraft) : bukkit);
      }
   }
}
