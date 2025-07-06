package org.bukkit.craftbukkit.v1_21_R5;

import com.google.common.base.Preconditions;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.bukkit.Axis;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlockType;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemType;

public class CraftEffect {
   public static <T> int getDataValue(Effect effect, T data) {
      int datavalue;
      switch (effect) {
         case VILLAGER_PLANT_GROW:
            datavalue = (Integer)data;
            break;
         case POTION_BREAK:
         case INSTANT_POTION_BREAK:
            datavalue = ((Color)data).asRGB();
            break;
         case RECORD_PLAY:
            Preconditions.checkArgument(data == Material.AIR || ((Material)data).isRecord(), "Invalid record type for Material %s!", data);
            datavalue = Item.getId(CraftItemType.bukkitToMinecraft((Material)data));
            break;
         case SMOKE:
            switch ((BlockFace)data) {
               case DOWN:
               case NORTH_EAST:
               case NORTH_WEST:
               case SOUTH_EAST:
               case SOUTH_WEST:
               case SELF:
                  datavalue = 0;
                  return datavalue;
               case UP:
                  datavalue = 1;
                  return datavalue;
               case NORTH:
                  datavalue = 2;
                  return datavalue;
               case SOUTH:
                  datavalue = 3;
                  return datavalue;
               case WEST:
                  datavalue = 4;
                  return datavalue;
               case EAST:
                  datavalue = 5;
                  return datavalue;
               default:
                  throw new IllegalArgumentException("Bad smoke direction!");
            }
         case STEP_SOUND:
            Preconditions.checkArgument(((Material)data).isBlock(), "Material %s is not a block!", data);
            datavalue = Block.getId(CraftBlockType.bukkitToMinecraft((Material)data).defaultBlockState());
            break;
         case COMPOSTER_FILL_ATTEMPT:
            datavalue = (Boolean)data ? 1 : 0;
            break;
         case BONE_MEAL_USE:
            datavalue = (Integer)data;
            break;
         case ELECTRIC_SPARK:
            if (data == null) {
               datavalue = -1;
               break;
            } else {
               switch ((Axis)data) {
                  case X:
                     datavalue = 0;
                     return datavalue;
                  case Y:
                     datavalue = 1;
                     return datavalue;
                  case Z:
                     datavalue = 2;
                     return datavalue;
                  default:
                     throw new IllegalArgumentException("Bad electric spark axis!");
               }
            }
         default:
            datavalue = 0;
      }

      return datavalue;
   }
}
