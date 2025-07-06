package org.bukkit.craftbukkit.v1_21_R5.inventory.components;

import com.google.common.base.Preconditions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.item.component.Weapon;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_21_R5.inventory.SerializableMeta;
import org.bukkit.inventory.meta.components.WeaponComponent;

@SerializableAs("Weapon")
public final class CraftWeaponComponent implements WeaponComponent {
   private Weapon handle;

   public CraftWeaponComponent(Weapon weapon) {
      this.handle = weapon;
   }

   public CraftWeaponComponent(CraftWeaponComponent tool) {
      this.handle = tool.handle;
   }

   public CraftWeaponComponent(Map<String, Object> map) {
      Integer itemDamagePerAttack = (Integer)SerializableMeta.getObject(Integer.class, map, "item-damage-per-attack", false);
      Float disableBlockingForSeconds = (Float)SerializableMeta.getObject(Float.class, map, "disable-blocking-for-seconds", true);
      this.handle = new Weapon(itemDamagePerAttack, disableBlockingForSeconds != null ? disableBlockingForSeconds : 0.0F);
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("item-damage-per-attack", this.getItemDamagePerAttack());
      if (this.getDisableBlockingForSeconds() != 0.0F) {
         result.put("disable-blocking-for-seconds", this.getDisableBlockingForSeconds());
      }

      return result;
   }

   public Weapon getHandle() {
      return this.handle;
   }

   public int getItemDamagePerAttack() {
      return this.getHandle().itemDamagePerAttack();
   }

   public void setItemDamagePerAttack(int damage) {
      Preconditions.checkArgument(damage >= 0, "damage must be >= 0");
      this.handle = new Weapon(damage, this.handle.disableBlockingForSeconds());
   }

   public float getDisableBlockingForSeconds() {
      return this.getHandle().disableBlockingForSeconds();
   }

   public void setDisableBlockingForSeconds(float time) {
      Preconditions.checkArgument(time >= 0.0F, "time must be >= 0");
      this.handle = new Weapon(this.handle.itemDamagePerAttack(), time);
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
         CraftWeaponComponent other = (CraftWeaponComponent)obj;
         return Objects.equals(this.handle, other.handle);
      }
   }

   public String toString() {
      return "CraftWeaponComponent{handle=" + String.valueOf(this.handle) + "}";
   }
}
