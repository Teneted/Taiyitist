package org.bukkit.craftbukkit.v1_21_R5.inventory.components.consumable.effects;

import java.util.Map;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.PlaySoundConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;

public abstract class CraftConsumableEffect<T extends ConsumeEffect> implements ConsumableEffect {
   T handle;

   public static <T extends CraftConsumableEffect<?>> T minecraftToBukkitSpecific(ConsumeEffect effect) {
      if (effect instanceof ApplyStatusEffectsConsumeEffect nmsEffect) {
         return new CraftConsumableApplyEffects(nmsEffect);
      } else if (effect instanceof RemoveStatusEffectsConsumeEffect nmsEffect) {
         return new CraftConsumableRemoveEffect(nmsEffect);
      } else if (effect instanceof ClearAllStatusEffectsConsumeEffect nmsEffect) {
         return new CraftConsumableClearEffects(nmsEffect);
      } else if (effect instanceof TeleportRandomlyConsumeEffect nmsEffect) {
         return new CraftConsumableTeleportRandomly(nmsEffect);
      } else if (effect instanceof PlaySoundConsumeEffect nmsEffect) {
         return new CraftConsumablePlaySound(nmsEffect);
      } else {
         throw new IllegalStateException("Unexpected value: " + String.valueOf(effect.getType()));
      }
   }

   public static <T extends ConsumeEffect> T bukkitToMinecraftSpecific(CraftConsumableEffect<T> effect) {
      return effect.getHandle();
   }

   public CraftConsumableEffect(T consumeEffect) {
      this.handle = consumeEffect;
   }

   public CraftConsumableEffect(CraftConsumableEffect<T> consumeEffect) {
      this.handle = consumeEffect.handle;
   }

   public CraftConsumableEffect(Map<String, Object> map) {
   }

   public T getHandle() {
      return this.handle;
   }
}
