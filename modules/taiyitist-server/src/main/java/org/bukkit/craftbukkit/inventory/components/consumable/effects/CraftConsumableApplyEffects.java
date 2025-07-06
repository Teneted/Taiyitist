package org.bukkit.craftbukkit.inventory.components.consumable.effects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import org.bukkit.craftbukkit.inventory.ItemMetaKey;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableApplyEffects;
import org.bukkit.potion.PotionEffect;

public class CraftConsumableApplyEffects extends CraftConsumableEffect<ApplyStatusEffectsConsumeEffect> implements ConsumableApplyEffects {
   static final ItemMetaKey POTIONS = new ItemMetaKey("effects");

   public CraftConsumableApplyEffects(ApplyStatusEffectsConsumeEffect consumeEffect) {
      super(consumeEffect);
   }

   public CraftConsumableApplyEffects(CraftConsumableApplyEffects consumeEffect) {
      super((CraftConsumableEffect)consumeEffect);
   }

   public CraftConsumableApplyEffects(Map<String, Object> map) {
      super(map);
      List<PotionEffect> effectList = new ArrayList();
      Iterable<?> rawEffectTypeList = (Iterable)SerializableMeta.getObject(Iterable.class, map, POTIONS, true);
      if (rawEffectTypeList != null) {
         Iterator var4 = rawEffectTypeList.iterator();

         while(var4.hasNext()) {
            Object obj = var4.next();
            Preconditions.checkArgument(obj instanceof PotionEffect, "Object (%s) in effect list is not valid", obj.getClass());
            effectList.add((PotionEffect)obj);
         }

         Float probability = (Float)SerializableMeta.getObject(Float.class, map, "probability", false);
         this.handle = new ApplyStatusEffectsConsumeEffect(effectList.stream().map(CraftPotionUtil::fromBukkit).toList(), probability);
      }
   }

   public List<PotionEffect> getEffects() {
      List<MobEffectInstance> mobEffectList = ((ApplyStatusEffectsConsumeEffect)this.getHandle()).effects();
      return mobEffectList.stream().map(CraftPotionUtil::toBukkit).toList();
   }

   public void setEffects(List<PotionEffect> list) {
      this.handle = new ApplyStatusEffectsConsumeEffect(list.stream().map(CraftPotionUtil::fromBukkit).toList());
   }

   public PotionEffect addEffect(PotionEffect potionEffect) {
      List<MobEffectInstance> mobEffectList = ((ApplyStatusEffectsConsumeEffect)this.getHandle()).effects();
      mobEffectList.add(CraftPotionUtil.fromBukkit(potionEffect));
      this.handle = new ApplyStatusEffectsConsumeEffect(mobEffectList, ((ApplyStatusEffectsConsumeEffect)this.handle).probability());
      return potionEffect;
   }

   public float getProbability() {
      return ((ApplyStatusEffectsConsumeEffect)this.getHandle()).probability();
   }

   public void setProbability(float probability) {
      Preconditions.checkArgument(probability >= 0.0F && probability <= 1.0F, "Probability must be between 0.0f and 1.0f but is %s", probability);
      this.handle = new ApplyStatusEffectsConsumeEffect(((ApplyStatusEffectsConsumeEffect)this.getHandle()).effects(), probability);
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put(POTIONS.BUKKIT, ImmutableList.copyOf(this.getEffects()));
      return result;
   }
}
