package org.bukkit.craftbukkit.v1_21_R5;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.bukkit.Art;
import org.bukkit.Fluid;
import org.bukkit.GameEvent;
import org.bukkit.JukeboxSong;
import org.bukkit.Keyed;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_21_R5.attribute.CraftAttribute;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBiome;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlockType;
import org.bukkit.craftbukkit.v1_21_R5.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.v1_21_R5.damage.CraftDamageType;
import org.bukkit.craftbukkit.v1_21_R5.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftCat;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftChicken;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftCow;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftFrog;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPig;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftWolf;
import org.bukkit.craftbukkit.v1_21_R5.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.v1_21_R5.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemType;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftMenuType;
import org.bukkit.craftbukkit.v1_21_R5.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.v1_21_R5.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.v1_21_R5.legacy.FieldRename;
import org.bukkit.craftbukkit.v1_21_R5.map.CraftMapCursor;
import org.bukkit.craftbukkit.v1_21_R5.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_21_R5.util.ApiVersion;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.util.Handleable;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class CraftRegistry<B extends Keyed, M> implements Registry<B> {
   private static RegistryAccess registry;
   private final Class<? super B> bukkitClass;
   private final Map<NamespacedKey, B> cache = new HashMap();
   private final net.minecraft.core.Registry<M> minecraftRegistry;
   private final BiFunction<NamespacedKey, Holder<M>, B> minecraftToBukkit;
   private final BiFunction<NamespacedKey, ApiVersion, NamespacedKey> updater;
   private boolean init;

   public static void setMinecraftRegistry(RegistryAccess registry) {
      Preconditions.checkState(CraftRegistry.registry == null, "Registry already set");
      CraftRegistry.registry = registry;
   }

   public static RegistryAccess getMinecraftRegistry() {
      return registry;
   }

   public static <E> net.minecraft.core.Registry<E> getMinecraftRegistry(ResourceKey<net.minecraft.core.Registry<E>> key) {
      return getMinecraftRegistry().lookupOrThrow(key);
   }

   public static <B extends Keyed, M> B minecraftToBukkit(M minecraft, ResourceKey<net.minecraft.core.Registry<M>> registryKey, Registry<B> bukkitRegistry) {
      Preconditions.checkArgument(minecraft != null);
      net.minecraft.core.Registry<M> registry = getMinecraftRegistry(registryKey);
      B bukkit = bukkitRegistry.get(CraftNamespacedKey.fromMinecraft(((ResourceKey)registry.getResourceKey(minecraft).orElseThrow(() -> {
         return new IllegalStateException(String.format("Cannot convert '%s' to bukkit representation, since it is not registered.", minecraft));
      })).location()));
      Preconditions.checkArgument(bukkit != null);
      return bukkit;
   }

   public static <B extends Keyed, M> M bukkitToMinecraft(B bukkit) {
      Preconditions.checkArgument(bukkit != null);
      return ((Handleable)bukkit).getHandle();
   }

   public static <B extends Keyed, M> Holder<M> bukkitToMinecraftHolder(B bukkit, ResourceKey<net.minecraft.core.Registry<M>> registryKey) {
      Preconditions.checkArgument(bukkit != null);
      net.minecraft.core.Registry<M> registry = getMinecraftRegistry(registryKey);
      Holder var4 = registry.wrapAsHolder(bukkitToMinecraft(bukkit));
      if (var4 instanceof Holder.Reference<M> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own registry entry with out properly registering it.");
      }
   }

   private static <M, B> BiFunction<NamespacedKey, Holder<M>, B> wrap(BiFunction<NamespacedKey, M, B> minecraftToBukkit) {
      return (namespacedKey, holder) -> {
         return minecraftToBukkit.apply(namespacedKey, holder.value());
      };
   }

   public static <B extends Keyed> Registry<?> createRegistry(Class<? super B> bukkitClass, RegistryAccess registryHolder) {
      if (bukkitClass == Art.class) {
         return new CraftRegistry(Art.class, registryHolder.lookupOrThrow(Registries.PAINTING_VARIANT), CraftArt::new, FieldRename.NONE);
      } else if (bukkitClass == Attribute.class) {
         return new CraftRegistry(Attribute.class, registryHolder.lookupOrThrow(Registries.ATTRIBUTE), CraftAttribute::new, FieldRename.ATTRIBUTE_RENAME);
      } else if (bukkitClass == Biome.class) {
         return new CraftRegistry(Biome.class, registryHolder.lookupOrThrow(Registries.BIOME), CraftBiome::new, FieldRename.BIOME_RENAME);
      } else if (bukkitClass == Enchantment.class) {
         return new CraftRegistry(Enchantment.class, registryHolder.lookupOrThrow(Registries.ENCHANTMENT), wrap(CraftEnchantment::new), FieldRename.ENCHANTMENT_RENAME);
      } else if (bukkitClass == Fluid.class) {
         return new CraftRegistry(Fluid.class, registryHolder.lookupOrThrow(Registries.FLUID), CraftFluid::new, FieldRename.NONE);
      } else if (bukkitClass == GameEvent.class) {
         return new CraftRegistry(GameEvent.class, registryHolder.lookupOrThrow(Registries.GAME_EVENT), wrap(CraftGameEvent::new), FieldRename.NONE);
      } else if (bukkitClass == MusicInstrument.class) {
         return new CraftRegistry(MusicInstrument.class, registryHolder.lookupOrThrow(Registries.INSTRUMENT), CraftMusicInstrument::new, FieldRename.NONE);
      } else if (bukkitClass == MenuType.class) {
         return new CraftRegistry(MenuType.class, registryHolder.lookupOrThrow(Registries.MENU), CraftMenuType::new, FieldRename.NONE);
      } else if (bukkitClass == PotionEffectType.class) {
         return new CraftRegistry(PotionEffectType.class, registryHolder.lookupOrThrow(Registries.MOB_EFFECT), wrap(CraftPotionEffectType::new), FieldRename.NONE);
      } else if (bukkitClass == Sound.class) {
         return new CraftRegistry(Sound.class, registryHolder.lookupOrThrow(Registries.SOUND_EVENT), CraftSound::new, FieldRename.NONE);
      } else if (bukkitClass == Structure.class) {
         return new CraftRegistry(Structure.class, registryHolder.lookupOrThrow(Registries.STRUCTURE), wrap(CraftStructure::new), FieldRename.NONE);
      } else if (bukkitClass == StructureType.class) {
         return new CraftRegistry(StructureType.class, registryHolder.lookupOrThrow(Registries.STRUCTURE_TYPE), wrap(CraftStructureType::new), FieldRename.NONE);
      } else if (bukkitClass == Villager.Type.class) {
         return new CraftRegistry(Villager.Type.class, registryHolder.lookupOrThrow(Registries.VILLAGER_TYPE), CraftVillager.CraftType::new, FieldRename.NONE);
      } else if (bukkitClass == Villager.Profession.class) {
         return new CraftRegistry(Villager.Profession.class, registryHolder.lookupOrThrow(Registries.VILLAGER_PROFESSION), CraftVillager.CraftProfession::new, FieldRename.NONE);
      } else if (bukkitClass == TrimMaterial.class) {
         return new CraftRegistry(TrimMaterial.class, registryHolder.lookupOrThrow(Registries.TRIM_MATERIAL), CraftTrimMaterial::new, FieldRename.NONE);
      } else if (bukkitClass == TrimPattern.class) {
         return new CraftRegistry(TrimPattern.class, registryHolder.lookupOrThrow(Registries.TRIM_PATTERN), CraftTrimPattern::new, FieldRename.NONE);
      } else if (bukkitClass == DamageType.class) {
         return new CraftRegistry(DamageType.class, registryHolder.lookupOrThrow(Registries.DAMAGE_TYPE), CraftDamageType::new, FieldRename.NONE);
      } else if (bukkitClass == JukeboxSong.class) {
         return new CraftRegistry(JukeboxSong.class, registryHolder.lookupOrThrow(Registries.JUKEBOX_SONG), CraftJukeboxSong::new, FieldRename.NONE);
      } else if (bukkitClass == Wolf.Variant.class) {
         return new CraftRegistry(Wolf.Variant.class, registryHolder.lookupOrThrow(Registries.WOLF_VARIANT), CraftWolf.CraftVariant::new, FieldRename.NONE);
      } else if (bukkitClass == BlockType.class) {
         return new CraftRegistry(BlockType.class, registryHolder.lookupOrThrow(Registries.BLOCK), CraftBlockType::new, FieldRename.NONE);
      } else if (bukkitClass == ItemType.class) {
         return new CraftRegistry(ItemType.class, registryHolder.lookupOrThrow(Registries.ITEM), CraftItemType::new, FieldRename.NONE);
      } else if (bukkitClass == Frog.Variant.class) {
         return new CraftRegistry(Frog.Variant.class, registryHolder.lookupOrThrow(Registries.FROG_VARIANT), CraftFrog.CraftVariant::new, FieldRename.NONE);
      } else if (bukkitClass == Cat.Type.class) {
         return new CraftRegistry(Cat.Type.class, registryHolder.lookupOrThrow(Registries.CAT_VARIANT), CraftCat.CraftType::new, FieldRename.NONE);
      } else if (bukkitClass == Pig.Variant.class) {
         return new CraftRegistry(Pig.Variant.class, registryHolder.lookupOrThrow(Registries.PIG_VARIANT), CraftPig.CraftVariant::new, FieldRename.NONE);
      } else if (bukkitClass == Cow.Variant.class) {
         return new CraftRegistry(Cow.Variant.class, registryHolder.lookupOrThrow(Registries.COW_VARIANT), CraftCow.CraftVariant::new, FieldRename.NONE);
      } else if (bukkitClass == Chicken.Variant.class) {
         return new CraftRegistry(Chicken.Variant.class, registryHolder.lookupOrThrow(Registries.CHICKEN_VARIANT), CraftChicken.CraftVariant::new, FieldRename.NONE);
      } else if (bukkitClass == MapCursor.Type.class) {
         return new CraftRegistry(MapCursor.Type.class, registryHolder.lookupOrThrow(Registries.MAP_DECORATION_TYPE), CraftMapCursor.CraftType::new, FieldRename.NONE);
      } else {
         return bukkitClass == PatternType.class ? new CraftRegistry(PatternType.class, registryHolder.lookupOrThrow(Registries.BANNER_PATTERN), CraftPatternType::new, FieldRename.NONE) : null;
      }
   }

   public static <B extends Keyed> B get(Registry<B> bukkit, NamespacedKey namespacedKey, ApiVersion apiVersion) {
      if (bukkit instanceof CraftRegistry<B, ?> craft) {
         return craft.get(namespacedKey, apiVersion);
      } else {
         if (bukkit instanceof Registry.SimpleRegistry<?> simple) {
            Class<?> bClass = simple.getType();
            if (bClass == EntityType.class) {
               return bukkit.get((NamespacedKey)FieldRename.ENTITY_TYPE_RENAME.apply(namespacedKey, apiVersion));
            }

            if (bClass == Particle.class) {
               return bukkit.get((NamespacedKey)FieldRename.PARTICLE_TYPE_RENAME.apply(namespacedKey, apiVersion));
            }
         }

         return bukkit.get(namespacedKey);
      }
   }

   public CraftRegistry(Class<? super B> bukkitClass, net.minecraft.core.Registry<M> minecraftRegistry, BiFunction<NamespacedKey, Holder<M>, B> minecraftToBukkit, BiFunction<NamespacedKey, ApiVersion, NamespacedKey> updater) {
      this.bukkitClass = bukkitClass;
      this.minecraftRegistry = minecraftRegistry;
      this.minecraftToBukkit = minecraftToBukkit;
      this.updater = updater;
   }

   public B get(NamespacedKey namespacedKey, ApiVersion apiVersion) {
      return this.get((NamespacedKey)this.updater.apply(namespacedKey, apiVersion));
   }

   public B get(NamespacedKey namespacedKey) {
      B cached = (Keyed)this.cache.get(namespacedKey);
      if (cached != null) {
         return cached;
      } else if (!this.init) {
         this.init = true;

         try {
            Class.forName(this.bukkitClass.getName());
         } catch (ClassNotFoundException var4) {
            ClassNotFoundException e = var4;
            throw new RuntimeException("Could not load registry class " + String.valueOf(this.bukkitClass), e);
         }

         return this.get(namespacedKey);
      } else {
         B bukkit = this.createBukkit(namespacedKey, (Holder)this.minecraftRegistry.get(CraftNamespacedKey.toMinecraft(namespacedKey)).orElse((Object)null));
         if (bukkit == null) {
            return null;
         } else {
            this.cache.put(namespacedKey, bukkit);
            return bukkit;
         }
      }
   }

   @NotNull
   public B getOrThrow(@NotNull NamespacedKey namespacedKey) {
      B object = this.get(namespacedKey);
      Preconditions.checkArgument(object != null, "No %s registry entry found for key %s.", this.minecraftRegistry.key(), namespacedKey);
      return object;
   }

   @NotNull
   public Stream<B> stream() {
      return this.minecraftRegistry.keySet().stream().map((minecraftKey) -> {
         return this.get(CraftNamespacedKey.fromMinecraft(minecraftKey));
      });
   }

   public Iterator<B> iterator() {
      return this.stream().iterator();
   }

   public B createBukkit(NamespacedKey namespacedKey, Holder<M> minecraft) {
      return minecraft == null ? null : (Keyed)this.minecraftToBukkit.apply(namespacedKey, minecraft);
   }
}
