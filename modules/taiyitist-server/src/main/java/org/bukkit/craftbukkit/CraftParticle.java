package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import org.bukkit.Color;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Vibration;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public abstract class CraftParticle<D> implements Keyed {
   private static final Registry<CraftParticle<?>> CRAFT_PARTICLE_REGISTRY;
   private final NamespacedKey key;
   private final ParticleType<?> particle;
   private final Class<D> clazz;

   public static Particle minecraftToBukkit(ParticleType<?> minecraft) {
      Preconditions.checkArgument(minecraft != null);
      net.minecraft.core.Registry<ParticleType<?>> registry = CraftRegistry.getMinecraftRegistry(Registries.PARTICLE_TYPE);
      Particle bukkit = (Particle)Registry.PARTICLE_TYPE.get(CraftNamespacedKey.fromMinecraft(((ResourceKey)registry.getResourceKey(minecraft).orElseThrow()).location()));
      Preconditions.checkArgument(bukkit != null);
      return bukkit;
   }

   public static ParticleType<?> bukkitToMinecraft(Particle bukkit) {
      Preconditions.checkArgument(bukkit != null);
      return (ParticleType)CraftRegistry.getMinecraftRegistry(Registries.PARTICLE_TYPE).getOptional(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
   }

   public static <D> ParticleOptions createParticleParam(Particle particle, D data) {
      Preconditions.checkArgument(particle != null, "particle cannot be null");
      data = convertLegacy(data);
      if (particle.getDataType() != Void.class) {
         Preconditions.checkArgument(data != null, "missing required data %s", particle.getDataType());
      }

      if (data != null) {
         Preconditions.checkArgument(particle.getDataType().isInstance(data), "data (%s) should be %s", data.getClass(), particle.getDataType());
      }

      CraftParticle<D> craftParticle = (CraftParticle)CRAFT_PARTICLE_REGISTRY.get(particle.getKey());
      Preconditions.checkArgument(craftParticle != null);
      return craftParticle.createParticleParam(data);
   }

   public static <T> T convertLegacy(T object) {
      if (object instanceof MaterialData mat) {
         return (T) CraftBlockData.fromData(CraftMagicNumbers.getBlock(mat));
      } else {
         return object;
      }
   }

   public CraftParticle(NamespacedKey key, ParticleType<?> particle, Class<D> clazz) {
      this.key = key;
      this.particle = particle;
      this.clazz = clazz;
   }

   public ParticleType<?> getHandle() {
      return this.particle;
   }

   public abstract ParticleOptions createParticleParam(D var1);

   public NamespacedKey getKey() {
      return this.key;
   }

   static {
      CRAFT_PARTICLE_REGISTRY = new CraftParticleRegistry(CraftRegistry.getMinecraftRegistry(Registries.PARTICLE_TYPE));
   }

   public static class CraftParticleRegistry extends CraftRegistry<CraftParticle<?>, ParticleType<?>> {
      private static final Map<NamespacedKey, BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>>> PARTICLE_MAP = new HashMap();
      private static final BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> VOID_FUNCTION = (name, particle) -> {
         return new CraftParticle<Void>(name, particle, Void.class) {
            public ParticleOptions createParticleParam(Void data) {
               return (SimpleParticleType)this.getHandle();
            }
         };
      };

      private static void add(String name, BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> function) {
         PARTICLE_MAP.put(NamespacedKey.fromString(name), function);
      }

      public CraftParticleRegistry(net.minecraft.core.Registry<ParticleType<?>> minecraftRegistry) {
         super(CraftParticle.class, minecraftRegistry, (BiFunction)null, FieldRename.PARTICLE_TYPE_RENAME);
      }

      public CraftParticle<?> createBukkit(NamespacedKey namespacedKey, Holder<ParticleType<?>> particle) {
         if (particle == null) {
            return null;
         } else {
            BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> function = (BiFunction)PARTICLE_MAP.getOrDefault(namespacedKey, VOID_FUNCTION);
            return (CraftParticle)function.apply(namespacedKey, (ParticleType)particle.value());
         }
      }

      static {
         BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> dustOptionsFunction = (name, particle) -> {
            return new CraftParticle<Particle.DustOptions>(name, particle, Particle.DustOptions.class) {
               public ParticleOptions createParticleParam(Particle.DustOptions data) {
                  Color color = data.getColor();
                  return new DustParticleOptions(color.asRGB(), data.getSize());
               }
            };
         };
         BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> itemStackFunction = (name, particle) -> {
            return new CraftParticle<ItemStack>(name, particle, ItemStack.class) {
               public ParticleOptions createParticleParam(ItemStack data) {
                  return new ItemParticleOption((ParticleType<ItemParticleOption>) this.getHandle(), CraftItemStack.asNMSCopy(data));
               }
            };
         };
         BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> blockDataFunction = (name, particle) -> {
            return new CraftParticle<BlockData>(name, particle, BlockData.class) {
               public ParticleOptions createParticleParam(BlockData data) {
                  return new BlockParticleOption((ParticleType<BlockParticleOption>) this.getHandle(), ((CraftBlockData)data).getState());
               }
            };
         };
         BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> dustTransitionFunction = (name, particle) -> {
            return new CraftParticle<Particle.DustTransition>(name, particle, Particle.DustTransition.class) {
               public ParticleOptions createParticleParam(Particle.DustTransition data) {
                  Color from = data.getColor();
                  Color to = data.getToColor();
                  return new DustColorTransitionOptions(from.asRGB(), to.asRGB(), data.getSize());
               }
            };
         };
         BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> vibrationFunction = (name, particle) -> {
            return new CraftParticle<Vibration>(name, particle, Vibration.class) {
               public ParticleOptions createParticleParam(Vibration data) {
                  Object source;
                  if (data.getDestination() instanceof Vibration.Destination.BlockDestination) {
                     Location destination = ((Vibration.Destination.BlockDestination)data.getDestination()).getLocation();
                     source = new BlockPositionSource(CraftLocation.toBlockPosition(destination));
                  } else {
                     if (!(data.getDestination() instanceof Vibration.Destination.EntityDestination)) {
                        throw new IllegalArgumentException("Unknown vibration destination " + String.valueOf(data.getDestination()));
                     }

                     Entity destinationx = ((CraftEntity)((Vibration.Destination.EntityDestination)data.getDestination()).getEntity()).getHandle();
                     source = new EntityPositionSource(destinationx, destinationx.getEyeHeight());
                  }

                  return new VibrationParticleOption((PositionSource)source, data.getArrivalTime());
               }
            };
         };
         BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> floatFunction = (name, particle) -> {
            return new CraftParticle<Float>(name, particle, Float.class) {
               public ParticleOptions createParticleParam(Float data) {
                  return new SculkChargeParticleOptions(data);
               }
            };
         };
         BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> integerFunction = (name, particle) -> {
            return new CraftParticle<Integer>(name, particle, Integer.class) {
               public ParticleOptions createParticleParam(Integer data) {
                  return new ShriekParticleOption(data);
               }
            };
         };
         BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> colorFunction = (name, particle) -> {
            return new CraftParticle<Color>(name, particle, Color.class) {
               public ParticleOptions createParticleParam(Color color) {
                  return ColorParticleOption.create((ParticleType<ColorParticleOption>) particle, color.asARGB());
               }
            };
         };
         BiFunction<NamespacedKey, ParticleType<?>, CraftParticle<?>> trailFunction = (name, particle) -> {
            return new CraftParticle<Particle.Trail>(name, particle, Particle.Trail.class) {
               public ParticleOptions createParticleParam(Particle.Trail data) {
                  return new TrailParticleOption(CraftLocation.toVec3D(data.getTarget()), data.getColor().asRGB(), data.getDuration());
               }
            };
         };
         add("dust", dustOptionsFunction);
         add("item", itemStackFunction);
         add("block", blockDataFunction);
         add("falling_dust", blockDataFunction);
         add("dust_color_transition", dustTransitionFunction);
         add("vibration", vibrationFunction);
         add("sculk_charge", floatFunction);
         add("shriek", integerFunction);
         add("block_marker", blockDataFunction);
         add("entity_effect", colorFunction);
         add("tinted_leaves", colorFunction);
         add("dust_pillar", blockDataFunction);
         add("block_crumble", blockDataFunction);
         add("trail", trailFunction);
      }
   }
}
