package org.bukkit.craftbukkit.inventory.components;

import com.google.common.base.Preconditions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.food.FoodProperties;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.meta.components.FoodComponent;

@SerializableAs("Food")
public final class CraftFoodComponent implements FoodComponent {
   private FoodProperties handle;

   public CraftFoodComponent(FoodProperties food) {
      this.handle = food;
   }

   public CraftFoodComponent(CraftFoodComponent food) {
      this.handle = food.handle;
   }

   public CraftFoodComponent(Map<String, Object> map) {
      Integer nutrition = (Integer)SerializableMeta.getObject(Integer.class, map, "nutrition", false);
      Float saturationModifier = (Float)SerializableMeta.getObject(Float.class, map, "saturation", false);
      Boolean canAlwaysEat = SerializableMeta.getBoolean(map, "can-always-eat");
      this.handle = new FoodProperties(nutrition, saturationModifier, canAlwaysEat);
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("nutrition", this.getNutrition());
      result.put("saturation", this.getSaturation());
      result.put("can-always-eat", this.canAlwaysEat());
      return result;
   }

   public FoodProperties getHandle() {
      return this.handle;
   }

   public int getNutrition() {
      return this.handle.nutrition();
   }

   public void setNutrition(int nutrition) {
      Preconditions.checkArgument(nutrition >= 0, "Nutrition cannot be negative");
      this.handle = new FoodProperties(nutrition, this.handle.saturation(), this.handle.canAlwaysEat());
   }

   public float getSaturation() {
      return this.handle.saturation();
   }

   public void setSaturation(float saturation) {
      this.handle = new FoodProperties(this.handle.nutrition(), saturation, this.handle.canAlwaysEat());
   }

   public boolean canAlwaysEat() {
      return this.handle.canAlwaysEat();
   }

   public void setCanAlwaysEat(boolean canAlwaysEat) {
      this.handle = new FoodProperties(this.handle.nutrition(), this.handle.saturation(), canAlwaysEat);
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
         CraftFoodComponent other = (CraftFoodComponent)obj;
         return Objects.equals(this.handle, other.handle);
      }
   }

   public String toString() {
      return "CraftFoodComponent{handle=" + String.valueOf(this.handle) + "}";
   }
}
