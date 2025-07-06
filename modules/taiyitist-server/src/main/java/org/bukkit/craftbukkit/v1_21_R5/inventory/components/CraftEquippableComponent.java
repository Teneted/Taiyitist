package org.bukkit.craftbukkit.v1_21_R5.inventory.components;

import com.google.common.base.Preconditions;
import java.util.Collection;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_21_R5.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R5.CraftSound;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntityType;
import org.bukkit.craftbukkit.v1_21_R5.inventory.SerializableMeta;
import org.bukkit.craftbukkit.v1_21_R5.tag.CraftEntityTag;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftNamespacedKey;
import org.bukkit.inventory.meta.components.EquippableComponent;

@SerializableAs("Equippable")
public final class CraftEquippableComponent implements EquippableComponent {
   private Equippable handle;

   public CraftEquippableComponent(Equippable handle) {
      this.handle = handle;
   }

   public CraftEquippableComponent(CraftEquippableComponent craft) {
      this.handle = craft.handle;
   }

   public CraftEquippableComponent(Map<String, Object> map) {
      EquipmentSlot slot = CraftEquipmentSlot.getNMS(org.bukkit.inventory.EquipmentSlot.valueOf(SerializableMeta.getString(map, "slot", false)));
      Sound equipSound = null;
      String snd = SerializableMeta.getString(map, "equip-sound", true);
      if (snd != null) {
         equipSound = (Sound)Registry.SOUNDS.get(NamespacedKey.fromString(snd));
      }

      String model = SerializableMeta.getString(map, "model", true);
      String cameraOverlay = SerializableMeta.getString(map, "camera-overlay", true);
      HolderSet<EntityType<?>> allowedEntities = null;
      Object allowed = SerializableMeta.getObject(Object.class, map, "allowed-entities", true);
      if (allowed != null) {
         allowedEntities = CraftHolderUtil.parse(allowed, Registries.ENTITY_TYPE, BuiltInRegistries.ENTITY_TYPE);
      }

      Boolean dispensable = (Boolean)SerializableMeta.getObject(Boolean.class, map, "dispensable", true);
      Boolean swappable = (Boolean)SerializableMeta.getObject(Boolean.class, map, "swappable", true);
      Boolean damageOnHurt = (Boolean)SerializableMeta.getObject(Boolean.class, map, "damage-on-hurt", true);
      Boolean equipOnInteract = (Boolean)SerializableMeta.getObject(Boolean.class, map, "equip-on-interact", true);
      Boolean canBeSheared = (Boolean)SerializableMeta.getObject(Boolean.class, map, "can-be-sheared", true);
      Sound shearSound = null;
      String shearSnd = SerializableMeta.getString(map, "equip-sound", true);
      if (shearSnd != null) {
         shearSound = (Sound)Registry.SOUNDS.get(NamespacedKey.fromString(shearSnd));
      }

      this.handle = new Equippable(slot, equipSound != null ? CraftSound.bukkitToMinecraftHolder(equipSound) : SoundEvents.ARMOR_EQUIP_GENERIC, Optional.ofNullable(model).map(ResourceLocation::parse).map((k) -> {
         return ResourceKey.create(EquipmentAssets.ROOT_ID, k);
      }), Optional.ofNullable(cameraOverlay).map(ResourceLocation::parse), Optional.ofNullable(allowedEntities), dispensable != null ? dispensable : true, swappable != null ? swappable : true, damageOnHurt != null ? damageOnHurt : true, equipOnInteract != null ? equipOnInteract : true, canBeSheared, shearSound != null ? CraftSound.bukkitToMinecraftHolder(shearSound) : BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SHEARS_SNIP));
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("slot", this.getSlot().name());
      result.put("equip-sound", this.getEquipSound().getKey().toString());
      NamespacedKey model = this.getModel();
      if (model != null) {
         result.put("model", model.toString());
      }

      NamespacedKey cameraOverlay = this.getCameraOverlay();
      if (cameraOverlay != null) {
         result.put("camera-overlay", cameraOverlay.toString());
      }

      Optional<HolderSet<EntityType<?>>> allowed = this.handle.allowedEntities();
      if (allowed.isPresent()) {
         CraftHolderUtil.serialize(result, "allowed-entities", (HolderSet)allowed.get());
      }

      result.put("dispensable", this.isDispensable());
      result.put("swappable", this.isSwappable());
      result.put("damage-on-hurt", this.isDamageOnHurt());
      result.put("equip-on-interact", this.isEquipOnInteract());
      result.put("can-be-sheared", this.isCanBeSheared());
      result.put("shearing-sound", this.getShearingSound().getKey().toString());
      return result;
   }

   public Equippable getHandle() {
      return this.handle;
   }

   public org.bukkit.inventory.EquipmentSlot getSlot() {
      return CraftEquipmentSlot.getSlot(this.handle.slot());
   }

   public void setSlot(org.bukkit.inventory.EquipmentSlot slot) {
      this.handle = new Equippable(CraftEquipmentSlot.getNMS(slot), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public Sound getEquipSound() {
      return CraftSound.minecraftToBukkit((SoundEvent)this.handle.equipSound().value());
   }

   public void setEquipSound(Sound sound) {
      this.handle = new Equippable(this.handle.slot(), sound != null ? CraftSound.bukkitToMinecraftHolder(sound) : SoundEvents.ARMOR_EQUIP_GENERIC, this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public NamespacedKey getModel() {
      return (NamespacedKey)this.handle.assetId().map((a) -> {
         return CraftNamespacedKey.fromMinecraft(a.location());
      }).orElse((Object)null);
   }

   public void setModel(NamespacedKey key) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), Optional.ofNullable(key).map(CraftNamespacedKey::toMinecraft).map((k) -> {
         return ResourceKey.create(EquipmentAssets.ROOT_ID, k);
      }), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public NamespacedKey getCameraOverlay() {
      return (NamespacedKey)this.handle.cameraOverlay().map(CraftNamespacedKey::fromMinecraft).orElse((Object)null);
   }

   public void setCameraOverlay(NamespacedKey key) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), Optional.ofNullable(key).map(CraftNamespacedKey::toMinecraft), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public Collection<org.bukkit.entity.EntityType> getAllowedEntities() {
      return (Collection)this.handle.allowedEntities().map(HolderSet::stream).map((stream) -> {
         return (List)stream.map(Holder::value).map(CraftEntityType::minecraftToBukkit).collect(Collectors.toList());
      }).orElse((Object)null);
   }

   public void setAllowedEntities(org.bukkit.entity.EntityType entities) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), entities != null ? Optional.of(HolderSet.direct(new Holder[]{CraftEntityType.bukkitToMinecraftHolder(entities)})) : Optional.empty(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public void setAllowedEntities(Collection<org.bukkit.entity.EntityType> entities) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), entities != null ? Optional.of(HolderSet.direct((List)entities.stream().map(CraftEntityType::bukkitToMinecraftHolder).collect(Collectors.toList()))) : Optional.empty(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public void setAllowedEntities(Tag<org.bukkit.entity.EntityType> tag) {
      Preconditions.checkArgument(tag instanceof CraftEntityTag, "tag must be an entity tag");
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), tag != null ? Optional.of(((CraftEntityTag)tag).getHandle()) : Optional.empty(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public boolean isDispensable() {
      return this.handle.dispensable();
   }

   public void setDispensable(boolean dispensable) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), dispensable, this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public boolean isSwappable() {
      return this.handle.swappable();
   }

   public void setSwappable(boolean swappable) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), swappable, this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public boolean isDamageOnHurt() {
      return this.handle.damageOnHurt();
   }

   public void setDamageOnHurt(boolean damage) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), damage, this.handle.equipOnInteract(), this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public boolean isEquipOnInteract() {
      return this.handle.equipOnInteract();
   }

   public void setEquipOnInteract(boolean equip) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), equip, this.handle.canBeSheared(), this.handle.shearingSound());
   }

   public boolean isCanBeSheared() {
      return this.handle.canBeSheared();
   }

   public void setCanBeSheared(boolean sheared) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), sheared, this.handle.shearingSound());
   }

   public Sound getShearingSound() {
      return CraftSound.minecraftToBukkit((SoundEvent)this.handle.shearingSound().value());
   }

   public void setShearingSound(Sound sound) {
      this.handle = new Equippable(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt(), this.handle.equipOnInteract(), this.handle.canBeSheared(), sound != null ? CraftSound.bukkitToMinecraftHolder(sound) : BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SHEARS_SNIP));
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftEquippableComponent other = (CraftEquippableComponent)obj;
         return Objects.equals(this.handle, other.handle);
      }
   }

   public int hashCode() {
      int hash = 7;
      hash = 19 * hash + Objects.hashCode(this.handle);
      return hash;
   }

   public String toString() {
      return "CraftEquippableComponent{handle=" + String.valueOf(this.handle) + "}";
   }
}
