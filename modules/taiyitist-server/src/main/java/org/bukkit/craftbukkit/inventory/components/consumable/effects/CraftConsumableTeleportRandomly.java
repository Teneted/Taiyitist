package org.bukkit.craftbukkit.inventory.components.consumable.effects;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableTeleportRandomly;
import org.jetbrains.annotations.NotNull;

public class CraftConsumableTeleportRandomly extends CraftConsumableEffect<TeleportRandomlyConsumeEffect> implements ConsumableTeleportRandomly {
   private TeleportRandomlyConsumeEffect handle;

   public CraftConsumableTeleportRandomly(TeleportRandomlyConsumeEffect consumableEffect) {
      super(consumableEffect);
   }

   public CraftConsumableTeleportRandomly(CraftConsumableTeleportRandomly consumableEffect) {
      super((CraftConsumableEffect)consumableEffect);
      this.handle = consumableEffect.handle;
   }

   public CraftConsumableTeleportRandomly(Map<String, Object> map) {
      super(map);
      Float diameter = (Float)SerializableMeta.getObject(Float.class, map, "diameter", false);
      this.handle = new TeleportRandomlyConsumeEffect(diameter);
   }

   public float getDiameter() {
      return this.handle.diameter();
   }

   public void setDiameter(float diameter) {
      this.handle = new TeleportRandomlyConsumeEffect(diameter);
   }

   @NotNull
   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("diameter", this.getDiameter());
      return result;
   }
}
