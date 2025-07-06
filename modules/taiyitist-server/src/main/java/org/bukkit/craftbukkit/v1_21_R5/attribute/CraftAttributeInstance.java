package org.bukkit.craftbukkit.v1_21_R5.attribute;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftNamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;

public class CraftAttributeInstance implements AttributeInstance {
   private final net.minecraft.world.entity.ai.attributes.AttributeInstance handle;
   private final Attribute attribute;

   public CraftAttributeInstance(net.minecraft.world.entity.ai.attributes.AttributeInstance handle, Attribute attribute) {
      this.handle = handle;
      this.attribute = attribute;
   }

   public Attribute getAttribute() {
      return this.attribute;
   }

   public double getBaseValue() {
      return this.handle.getBaseValue();
   }

   public void setBaseValue(double d) {
      this.handle.setBaseValue(d);
   }

   public Collection<AttributeModifier> getModifiers() {
      List<AttributeModifier> result = new ArrayList();
      Iterator var2 = this.handle.getModifiers().iterator();

      while(var2.hasNext()) {
         net.minecraft.world.entity.ai.attributes.AttributeModifier nms = (net.minecraft.world.entity.ai.attributes.AttributeModifier)var2.next();
         result.add(convert(nms));
      }

      return result;
   }

   public void addModifier(AttributeModifier modifier) {
      Preconditions.checkArgument(modifier != null, "modifier");
      this.handle.addPermanentModifier(convert(modifier));
   }

   public void removeModifier(AttributeModifier modifier) {
      Preconditions.checkArgument(modifier != null, "modifier");
      this.handle.removeModifier(convert(modifier));
   }

   public double getValue() {
      return this.handle.getValue();
   }

   public double getDefaultValue() {
      return ((net.minecraft.world.entity.ai.attributes.Attribute)this.handle.getAttribute().value()).getDefaultValue();
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeModifier convert(AttributeModifier bukkit) {
      return new net.minecraft.world.entity.ai.attributes.AttributeModifier(CraftNamespacedKey.toMinecraft(bukkit.getKey()), bukkit.getAmount(), Operation.values()[bukkit.getOperation().ordinal()]);
   }

   public static AttributeModifier convert(net.minecraft.world.entity.ai.attributes.AttributeModifier nms) {
      return new AttributeModifier(CraftNamespacedKey.fromMinecraft(nms.id()), nms.amount(), org.bukkit.attribute.AttributeModifier.Operation.values()[nms.operation().ordinal()], EquipmentSlotGroup.ANY);
   }

   public static AttributeModifier convert(net.minecraft.world.entity.ai.attributes.AttributeModifier nms, EquipmentSlot slot) {
      return new AttributeModifier(CraftNamespacedKey.fromMinecraft(nms.id()), nms.amount(), org.bukkit.attribute.AttributeModifier.Operation.values()[nms.operation().ordinal()], slot.getGroup());
   }
}
