package org.bukkit.craftbukkit.v1_21_R5;

import java.util.Locale;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;

public class CraftEquipmentSlot {
   private static final EquipmentSlot[] slots = new EquipmentSlot[org.bukkit.inventory.EquipmentSlot.values().length];
   private static final org.bukkit.inventory.EquipmentSlot[] enums = new org.bukkit.inventory.EquipmentSlot[EquipmentSlot.values().length];

   private static void set(org.bukkit.inventory.EquipmentSlot type, EquipmentSlot value) {
      slots[type.ordinal()] = value;
      enums[value.ordinal()] = type;
   }

   public static org.bukkit.inventory.EquipmentSlot getSlot(EquipmentSlot nms) {
      return enums[nms.ordinal()];
   }

   public static EquipmentSlotGroup getSlot(net.minecraft.world.entity.EquipmentSlotGroup nms) {
      return EquipmentSlotGroup.getByName(nms.getSerializedName());
   }

   public static EquipmentSlot getNMS(org.bukkit.inventory.EquipmentSlot slot) {
      return slots[slot.ordinal()];
   }

   public static net.minecraft.world.entity.EquipmentSlotGroup getNMSGroup(EquipmentSlotGroup slot) {
      return net.minecraft.world.entity.EquipmentSlotGroup.valueOf(slot.toString().toUpperCase(Locale.ROOT));
   }

   public static org.bukkit.inventory.EquipmentSlot getHand(InteractionHand enumhand) {
      return enumhand == InteractionHand.MAIN_HAND ? org.bukkit.inventory.EquipmentSlot.HAND : org.bukkit.inventory.EquipmentSlot.OFF_HAND;
   }

   public static InteractionHand getHand(org.bukkit.inventory.EquipmentSlot hand) {
      if (hand == org.bukkit.inventory.EquipmentSlot.HAND) {
         return InteractionHand.MAIN_HAND;
      } else if (hand == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
         return InteractionHand.OFF_HAND;
      } else {
         throw new IllegalArgumentException("EquipmentSlot." + String.valueOf(hand) + " is not a hand");
      }
   }

   static {
      set(org.bukkit.inventory.EquipmentSlot.HAND, EquipmentSlot.MAINHAND);
      set(org.bukkit.inventory.EquipmentSlot.OFF_HAND, EquipmentSlot.OFFHAND);
      set(org.bukkit.inventory.EquipmentSlot.FEET, EquipmentSlot.FEET);
      set(org.bukkit.inventory.EquipmentSlot.LEGS, EquipmentSlot.LEGS);
      set(org.bukkit.inventory.EquipmentSlot.CHEST, EquipmentSlot.CHEST);
      set(org.bukkit.inventory.EquipmentSlot.HEAD, EquipmentSlot.HEAD);
      set(org.bukkit.inventory.EquipmentSlot.BODY, EquipmentSlot.BODY);
      set(org.bukkit.inventory.EquipmentSlot.SADDLE, EquipmentSlot.SADDLE);
   }
}
