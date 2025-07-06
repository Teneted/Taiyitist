package org.bukkit.craftbukkit.v1_21_R5.inventory.components.consumable;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_21_R5.CraftSound;
import org.bukkit.craftbukkit.v1_21_R5.inventory.SerializableMeta;
import org.bukkit.craftbukkit.v1_21_R5.inventory.components.consumable.effects.CraftConsumableEffect;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent.Animation;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;

@SerializableAs("Consumable")
public class CraftConsumableComponent implements ConsumableComponent {
   private Consumable handle;

   public CraftConsumableComponent(Consumable consumable) {
      this.handle = consumable;
   }

   public CraftConsumableComponent(CraftConsumableComponent consumable) {
      this.handle = consumable.handle;
   }

   public CraftConsumableComponent(Map<String, Object> map) {
      Float consumeSeconds = (Float)SerializableMeta.getObject(Float.class, map, "consume-seconds", false);
      ConsumableComponent.Animation animation = Animation.valueOf(SerializableMeta.getString(map, "animation", false));
      Boolean hasConsumeParticles = SerializableMeta.getBoolean(map, "has-consume-particles");
      Sound sound = (Sound)Registry.SOUNDS.get(NamespacedKey.fromString(SerializableMeta.getString(map, "sound", false)));
      List<ConsumableEffect> consumableEffects = SerializableMeta.getList(ConsumableEffect.class, map, "effects");
      List<ConsumeEffect> consumeEffects = consumableEffects.stream().map((consumableEffect) -> {
         return CraftConsumableEffect.bukkitToMinecraftSpecific((CraftConsumableEffect)consumableEffect);
      }).toList();
      this.handle = new Consumable(consumeSeconds, CraftConsumableComponent.CraftAnimation.bukkitToMinecraft(animation), CraftSound.bukkitToMinecraftHolder(sound), hasConsumeParticles, consumeEffects);
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("consume-seconds", this.getConsumeSeconds());
      result.put("animation", this.getAnimation().name());
      result.put("sound", this.getSound().getKey().toString());
      result.put("has-consume-particles", this.hasConsumeParticles());
      result.put("effects", this.getEffects());
      return result;
   }

   public Consumable getHandle() {
      return this.handle;
   }

   public float getConsumeSeconds() {
      return this.handle.consumeSeconds();
   }

   public void setConsumeSeconds(float consumeSeconds) {
      this.handle = new Consumable(consumeSeconds, this.handle.animation(), this.handle.sound(), this.handle.hasConsumeParticles(), this.handle.onConsumeEffects());
   }

   public ConsumableComponent.Animation getAnimation() {
      return CraftConsumableComponent.CraftAnimation.minecraftToBukkit(this.handle.animation());
   }

   public void setAnimation(ConsumableComponent.Animation animation) {
      Preconditions.checkArgument(animation != null, "Animation cannot be null");
      this.handle = new Consumable(this.handle.consumeSeconds(), CraftConsumableComponent.CraftAnimation.bukkitToMinecraft(animation), this.handle.sound(), this.handle.hasConsumeParticles(), this.handle.onConsumeEffects());
   }

   public Sound getSound() {
      return CraftSound.minecraftHolderToBukkit(this.handle.sound());
   }

   public void setSound(Sound sound) {
      Holder<SoundEvent> soundEffectHolder = sound != null ? CraftSound.bukkitToMinecraftHolder(sound) : SoundEvents.GENERIC_EAT;
      this.handle = new Consumable(this.handle.consumeSeconds(), this.handle.animation(), (Holder)soundEffectHolder, this.handle.hasConsumeParticles(), this.handle.onConsumeEffects());
   }

   public boolean hasConsumeParticles() {
      return this.handle.hasConsumeParticles();
   }

   public void setConsumeParticles(boolean consumeParticles) {
      this.handle = new Consumable(this.handle.consumeSeconds(), this.handle.animation(), this.handle.sound(), consumeParticles, this.handle.onConsumeEffects());
   }

   public List<ConsumableEffect> getEffects() {
      return this.getHandle().onConsumeEffects().stream().map(CraftConsumableEffect::minecraftToBukkitSpecific).map((o) -> {
         return (ConsumableEffect)o;
      }).toList();
   }

   public void setEffects(List<ConsumableEffect> effects) {
      this.handle = new Consumable(this.handle.consumeSeconds(), this.handle.animation(), this.handle.sound(), this.handle.hasConsumeParticles(), effects.stream().map((consumableEffect) -> {
         return CraftConsumableEffect.bukkitToMinecraftSpecific((CraftConsumableEffect)consumableEffect);
      }).toList());
   }

   public ConsumableEffect addEffect(ConsumableEffect consumableEffect) {
      List<ConsumeEffect> effects = new ArrayList(this.handle.onConsumeEffects());
      ConsumeEffect newEffect = CraftConsumableEffect.bukkitToMinecraftSpecific((CraftConsumableEffect)consumableEffect);
      effects.add(newEffect);
      this.handle = new Consumable(this.handle.consumeSeconds(), this.handle.animation(), this.handle.sound(), this.handle.hasConsumeParticles(), effects);
      return consumableEffect;
   }

   public static class CraftAnimation {
      public static ConsumableComponent.Animation minecraftToBukkit(ItemUseAnimation minecraft) {
         return Animation.valueOf(minecraft.name());
      }

      public static ItemUseAnimation bukkitToMinecraft(ConsumableComponent.Animation bukkit) {
         return ItemUseAnimation.valueOf(bukkit.name());
      }
   }
}
