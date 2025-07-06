package org.bukkit.craftbukkit.v1_21_R5.inventory.components.consumable.effects;

import java.util.Map;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableClearEffects;

public class CraftConsumableClearEffects extends CraftConsumableEffect<ClearAllStatusEffectsConsumeEffect> implements ConsumableClearEffects {
   public CraftConsumableClearEffects(ClearAllStatusEffectsConsumeEffect consumeEffect) {
      super((ConsumeEffect)consumeEffect);
   }

   public CraftConsumableClearEffects(CraftConsumableClearEffects consumeEffect) {
      super((CraftConsumableEffect)consumeEffect);
   }

   public Map<String, Object> serialize() {
      return Map.of();
   }
}
