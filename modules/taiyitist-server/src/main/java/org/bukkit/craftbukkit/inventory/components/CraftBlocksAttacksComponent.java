package org.bukkit.craftbukkit.inventory.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.component.BlocksAttacks;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.tag.CraftDamageTag;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.meta.components.BlocksAttacksComponent;

@SerializableAs("BlocksAttacks")
public final class CraftBlocksAttacksComponent implements BlocksAttacksComponent {
   private BlocksAttacks handle;

   public CraftBlocksAttacksComponent(BlocksAttacks blocksAttacks) {
      this.handle = blocksAttacks;
   }

   public CraftBlocksAttacksComponent(CraftBlocksAttacksComponent blocksAttacks) {
      this.handle = blocksAttacks.handle;
   }

   public CraftBlocksAttacksComponent(Map<String, Object> map) {
      Float blockDelaySeconds = (Float)SerializableMeta.getObject(Float.class, map, "default-mining-speed", false);
      Float disableCooldownScale = (Float)SerializableMeta.getObject(Float.class, map, "disable-cooldown-scale", false);
      ImmutableList.Builder<BlocksAttacksComponent.DamageReduction> reduction = ImmutableList.builder();
      Iterable<?> rawReductionList = (Iterable)SerializableMeta.getObject(Iterable.class, map, "damage-reductions", true);
      if (rawReductionList != null) {
         Iterator var6 = rawReductionList.iterator();

         while(var6.hasNext()) {
            Object obj = var6.next();
            Preconditions.checkArgument(obj instanceof BlocksAttacksComponent.DamageReduction, "Object (%s) in reduction list is not valid", obj.getClass());
            CraftDamageReduction rule = new CraftDamageReduction((BlocksAttacksComponent.DamageReduction)obj);
            reduction.add(rule);
         }
      }

      Float itemDamageThreshold = (Float)SerializableMeta.getObject(Float.class, map, "item-damage-threshold", false);
      Float itemDamageBase = (Float)SerializableMeta.getObject(Float.class, map, "item-damage-base", false);
      Float itemDamageFactor = (Float)SerializableMeta.getObject(Float.class, map, "item-damage-factor", false);
      String bypassedBy = SerializableMeta.getString(map, "bypassed-by", true);
      TagKey<DamageType> tag = null;
      if (bypassedBy != null) {
         tag = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(bypassedBy));
      }

      Holder<SoundEvent> blockSound = null;
      String blockSnd = SerializableMeta.getString(map, "block-sound", true);
      if (blockSnd != null) {
         blockSound = (Holder)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(blockSnd)).orElse((Holder.Reference<SoundEvent>) null);
      }

      Holder<SoundEvent> disableSound = null;
      String disableSnd = SerializableMeta.getString(map, "disable-sound", true);
      if (disableSnd != null) {
         disableSound = (Holder)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(disableSnd)).orElse((Holder.Reference<SoundEvent>) null);
      }

      this.handle = new BlocksAttacks(blockDelaySeconds, disableCooldownScale, reduction.build().stream().map(CraftDamageReduction::new).map(CraftDamageReduction::getHandle).toList(), new BlocksAttacks.ItemDamageFunction(itemDamageThreshold, itemDamageBase, itemDamageFactor), Optional.ofNullable(tag), Optional.ofNullable(blockSound), Optional.ofNullable(disableSound));
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("block-delay-seconds", this.getBlockDelaySeconds());
      result.put("disable-cooldown-scale", this.getDisableCooldownScale());
      result.put("damage-reductions", this.getDamageReductions());
      result.put("item-damage-threshold", this.getItemDamageThreshold());
      result.put("item-damage-base", this.getItemDamageBase());
      result.put("item-damage-factor", this.getItemDamageFactor());
      this.handle.bypassedBy().ifPresent((bypassedBy) -> {
         result.put("bypassed-by", bypassedBy.location().toString());
      });
      Sound blockSound = this.getBlockSound();
      if (blockSound != null) {
         result.put("block-sound", blockSound);
      }

      Sound disableSound = this.getDisableSound();
      if (disableSound != null) {
         result.put("disable-sound", disableSound);
      }

      return result;
   }

   public BlocksAttacks getHandle() {
      return this.handle;
   }

   public float getBlockDelaySeconds() {
      return this.handle.blockDelaySeconds();
   }

   public void setBlockDelaySeconds(float seconds) {
      Preconditions.checkArgument(seconds >= 0.0F, "seconds cannot be negative");
      this.handle = new BlocksAttacks(seconds, this.handle.disableCooldownScale(), this.handle.damageReductions(), this.handle.itemDamage(), this.handle.bypassedBy(), this.handle.blockSound(), this.handle.disableSound());
   }

   public float getDisableCooldownScale() {
      return this.handle.disableCooldownScale();
   }

   public void setDisableCooldownScale(float scale) {
      Preconditions.checkArgument(scale >= 0.0F, "scale cannot be negative");
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), scale, this.handle.damageReductions(), this.handle.itemDamage(), this.handle.bypassedBy(), this.handle.blockSound(), this.handle.disableSound());
   }

   public List<BlocksAttacksComponent.DamageReduction> getDamageReductions() {
      return (List)this.handle.damageReductions().stream().map(CraftDamageReduction::new).collect(Collectors.toList());
   }

   public void setDamageReductions(List<BlocksAttacksComponent.DamageReduction> reductions) {
      Preconditions.checkArgument(reductions != null, "reductions must not be null");
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), this.handle.disableCooldownScale(), reductions.stream().map(CraftDamageReduction::new).map(CraftDamageReduction::getHandle).toList(), this.handle.itemDamage(), this.handle.bypassedBy(), this.handle.blockSound(), this.handle.disableSound());
   }

   public BlocksAttacksComponent.DamageReduction addDamageReduction(org.bukkit.damage.DamageType type, float base, float factor, float horizontalBlockingAngle) {
      return this.addRule(type != null ? HolderSet.direct(new Holder[]{CraftDamageType.bukkitToMinecraftHolder(type)}) : null, base, factor, horizontalBlockingAngle);
   }

   public BlocksAttacksComponent.DamageReduction addDamageReduction(Collection<org.bukkit.damage.DamageType> types, float base, float factor, float horizontalBlockingAngle) {
      return this.addRule(types != null ? HolderSet.direct(types.stream().map(CraftDamageType::bukkitToMinecraftHolder).toList()) : null, base, factor, horizontalBlockingAngle);
   }

   public BlocksAttacksComponent.DamageReduction addDamageReduction(Tag<org.bukkit.damage.DamageType> tag, float base, float factor, float horizontalBlockingAngle) {
      Preconditions.checkArgument(tag == null || tag instanceof CraftDamageTag, "tag must be a damage tag");
      return this.addRule(tag != null ? ((CraftDamageTag)tag).getHandle() : null, base, factor, horizontalBlockingAngle);
   }

   private BlocksAttacksComponent.DamageReduction addRule(HolderSet<DamageType> types, float base, float factor, float horizontalBlockingAngle) {
      BlocksAttacks.DamageReduction reduction = new BlocksAttacks.DamageReduction(horizontalBlockingAngle, Optional.ofNullable(types), base, factor);
      List<BlocksAttacks.DamageReduction> reductions = new ArrayList(this.handle.damageReductions().size() + 1);
      reductions.addAll(this.handle.damageReductions());
      reductions.add(reduction);
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), this.handle.disableCooldownScale(), reductions, this.handle.itemDamage(), this.handle.bypassedBy(), this.handle.blockSound(), this.handle.disableSound());
      return new CraftDamageReduction(reduction);
   }

   public boolean removeDamageReduction(BlocksAttacksComponent.DamageReduction reduction) {
      Preconditions.checkArgument(reduction != null, "reduction must not be null");
      List<BlocksAttacks.DamageReduction> reductions = new ArrayList(this.handle.damageReductions());
      boolean removed = reductions.remove(((CraftDamageReduction)reduction).handle);
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), this.handle.disableCooldownScale(), reductions, this.handle.itemDamage(), this.handle.bypassedBy(), this.handle.blockSound(), this.handle.disableSound());
      return removed;
   }

   public float getItemDamageThreshold() {
      return this.handle.itemDamage().threshold();
   }

   public void setItemDamageThreshold(float threshold) {
      BlocksAttacks.ItemDamageFunction itemDamage = this.handle.itemDamage();
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), this.handle.disableCooldownScale(), this.handle.damageReductions(), new BlocksAttacks.ItemDamageFunction(threshold, itemDamage.base(), itemDamage.factor()), this.handle.bypassedBy(), this.handle.blockSound(), this.handle.disableSound());
   }

   public float getItemDamageBase() {
      return this.handle.itemDamage().base();
   }

   public void setItemDamageBase(float base) {
      BlocksAttacks.ItemDamageFunction itemDamage = this.handle.itemDamage();
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), this.handle.disableCooldownScale(), this.handle.damageReductions(), new BlocksAttacks.ItemDamageFunction(itemDamage.threshold(), base, itemDamage.factor()), this.handle.bypassedBy(), this.handle.blockSound(), this.handle.disableSound());
   }

   public float getItemDamageFactor() {
      return this.handle.itemDamage().factor();
   }

   public void setItemDamageFactor(float factor) {
      BlocksAttacks.ItemDamageFunction itemDamage = this.handle.itemDamage();
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), this.handle.disableCooldownScale(), this.handle.damageReductions(), new BlocksAttacks.ItemDamageFunction(itemDamage.threshold(), itemDamage.base(), factor), this.handle.bypassedBy(), this.handle.blockSound(), this.handle.disableSound());
   }

   public Sound getBlockSound() {
      return (Sound)this.handle.blockSound().map(CraftSound::minecraftHolderToBukkit).orElse((Sound) null);
   }

   public void setBlockSound(Sound sound) {
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), this.handle.disableCooldownScale(), this.handle.damageReductions(), this.handle.itemDamage(), this.handle.bypassedBy(), Optional.ofNullable(sound).map(CraftSound::bukkitToMinecraftHolder), this.handle.disableSound());
   }

   public Sound getDisableSound() {
      return (Sound)this.handle.disableSound().map(CraftSound::minecraftHolderToBukkit).orElse((Sound) null);
   }

   public void setDisableSound(Sound sound) {
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), this.handle.disableCooldownScale(), this.handle.damageReductions(), this.handle.itemDamage(), this.handle.bypassedBy(), this.handle.blockSound(), Optional.ofNullable(sound).map(CraftSound::bukkitToMinecraftHolder));
   }

   public Tag<org.bukkit.damage.DamageType> getBypassedBy() {
      return (Tag)this.handle.bypassedBy().map((bypassedBy) -> {
         return Bukkit.getTag("damage_types", CraftNamespacedKey.fromMinecraft(bypassedBy.location()), org.bukkit.damage.DamageType.class);
      }).orElse((Tag<org.bukkit.damage.DamageType>) null);
   }

   public void setBypassedBy(Tag<org.bukkit.damage.DamageType> tag) {
      this.handle = new BlocksAttacks(this.handle.blockDelaySeconds(), this.handle.disableCooldownScale(), this.handle.damageReductions(), this.handle.itemDamage(), Optional.ofNullable(tag).map((t) -> {
         return ((CraftDamageTag)t).getHandle().key();
      }), this.handle.blockSound(), this.handle.disableSound());
   }

   public int hashCode() {
      int hash = 7;
      hash = 73 * hash + Objects.hashCode(this.handle);
      return hash;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftBlocksAttacksComponent other = (CraftBlocksAttacksComponent)obj;
         return Objects.equals(this.handle, other.handle);
      }
   }

   public String toString() {
      return "CraftBlocksAttacksComponent{handle=" + String.valueOf(this.handle) + "}";
   }

   @SerializableAs("DamageReduction")
   public static class CraftDamageReduction implements BlocksAttacksComponent.DamageReduction {
      private BlocksAttacks.DamageReduction handle;

      public CraftDamageReduction(BlocksAttacks.DamageReduction handle) {
         this.handle = handle;
      }

      public CraftDamageReduction(BlocksAttacksComponent.DamageReduction bukkit) {
         BlocksAttacks.DamageReduction toCopy = ((CraftDamageReduction)bukkit).handle;
         this.handle = new BlocksAttacks.DamageReduction(toCopy.horizontalBlockingAngle(), toCopy.type(), toCopy.base(), toCopy.factor());
      }

      public CraftDamageReduction(Map<String, Object> map) {
         Float base = (Float)SerializableMeta.getObject(Float.class, map, "base", false);
         Float factor = (Float)SerializableMeta.getObject(Float.class, map, "factor", false);
         Float horizontalBlockingAngle = (Float)SerializableMeta.getObject(Float.class, map, "horizontal-blocking-angle", false);
         HolderSet<DamageType> typesSet = null;
         Object types = SerializableMeta.getObject(Object.class, map, "types", true);
         if (types != null) {
            typesSet = CraftHolderUtil.parse(types, Registries.DAMAGE_TYPE, CraftRegistry.getMinecraftRegistry(Registries.DAMAGE_TYPE));
         }

         this.handle = new BlocksAttacks.DamageReduction(horizontalBlockingAngle, Optional.ofNullable(typesSet), base, factor);
      }

      public Map<String, Object> serialize() {
         Map<String, Object> result = new LinkedHashMap();
         result.put("base", this.getBase());
         result.put("factor", this.getFactor());
         result.put("horizontal-blocking-angle", this.getHorizontalBlockingAngle());
         this.handle.type().ifPresent((type) -> {
            CraftHolderUtil.serialize(result, "types", type);
         });
         return result;
      }

      public BlocksAttacks.DamageReduction getHandle() {
         return this.handle;
      }

      public Collection<org.bukkit.damage.DamageType> getTypes() {
         return (Collection)this.handle.type().map((type) -> {
            return type.stream().map(CraftDamageType::minecraftHolderToBukkit).toList();
         }).orElse((List<org.bukkit.damage.DamageType>) null);
      }

      public void setTypes(org.bukkit.damage.DamageType type) {
         this.handle = new BlocksAttacks.DamageReduction(this.handle.horizontalBlockingAngle(), Optional.ofNullable(type).map((t) -> {
            return HolderSet.direct(new Holder[]{CraftDamageType.bukkitToMinecraftHolder(t)});
         }), this.handle.base(), this.handle.factor());
      }

      public void setTypes(Collection<org.bukkit.damage.DamageType> types) {
         this.handle = new BlocksAttacks.DamageReduction(this.handle.horizontalBlockingAngle(), Optional.ofNullable(types).map((t) -> {
            return HolderSet.direct(t.stream().map(CraftDamageType::bukkitToMinecraftHolder).toList());
         }), this.handle.base(), this.handle.factor());
      }

      public void setTypes(Tag<org.bukkit.damage.DamageType> tag) {
         Preconditions.checkArgument(tag == null || tag instanceof CraftDamageTag, "tag must be a damage tag");
         this.handle = new BlocksAttacks.DamageReduction(this.handle.horizontalBlockingAngle(), Optional.ofNullable(tag).map((t) -> {
            return ((CraftDamageTag)t).getHandle();
         }), this.handle.base(), this.handle.factor());
      }

      public float getBase() {
         return this.handle.base();
      }

      public void setBase(float base) {
         this.handle = new BlocksAttacks.DamageReduction(this.handle.horizontalBlockingAngle(), this.handle.type(), base, this.handle.factor());
      }

      public float getFactor() {
         return this.handle.factor();
      }

      public void setFactor(float factor) {
         this.handle = new BlocksAttacks.DamageReduction(this.handle.horizontalBlockingAngle(), this.handle.type(), this.handle.base(), factor);
      }

      public float getHorizontalBlockingAngle() {
         return this.handle.horizontalBlockingAngle();
      }

      public void setHorizontalBlockingAngle(float horizontalBlockingAngle) {
         this.handle = new BlocksAttacks.DamageReduction(horizontalBlockingAngle, this.handle.type(), this.handle.base(), this.handle.factor());
      }

      public int hashCode() {
         int hash = 5;
         hash = 97 * hash + Objects.hashCode(this.handle);
         return hash;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            CraftDamageReduction other = (CraftDamageReduction)obj;
            return Objects.equals(this.handle, other.handle);
         }
      }

      public String toString() {
         return "CraftDamageReduction{handle=" + String.valueOf(this.handle) + "}";
      }
   }
}
