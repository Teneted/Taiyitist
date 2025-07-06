package org.bukkit.craftbukkit.v1_21_R5.inventory.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.world.item.component.CustomModelData;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_21_R5.inventory.SerializableMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

@SerializableAs("CustomModelData")
public final class CraftCustomModelDataComponent implements CustomModelDataComponent {
   private CustomModelData handle;

   public CraftCustomModelDataComponent(CustomModelData handle) {
      this.handle = handle;
   }

   public CraftCustomModelDataComponent(CraftCustomModelDataComponent craft) {
      this.handle = craft.handle;
   }

   public CraftCustomModelDataComponent(Map<String, Object> map) {
      this.handle = new CustomModelData(SerializableMeta.getList(Float.class, map, "floats"), SerializableMeta.getList(Boolean.class, map, "flags"), SerializableMeta.getList(String.class, map, "strings"), SerializableMeta.getList(Color.class, map, "colors").stream().map(Color::asRGB).toList());
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("floats", this.getFloats());
      result.put("flags", this.getFlags());
      result.put("strings", this.getStrings());
      result.put("colors", this.getColors());
      return result;
   }

   public CustomModelData getHandle() {
      return this.handle;
   }

   public List<Float> getFloats() {
      return Collections.unmodifiableList(this.handle.floats());
   }

   public void setFloats(List<Float> floats) {
      this.handle = new CustomModelData(new ArrayList(floats), this.handle.flags(), this.handle.strings(), this.handle.colors());
   }

   public List<Boolean> getFlags() {
      return Collections.unmodifiableList(this.handle.flags());
   }

   public void setFlags(List<Boolean> flags) {
      this.handle = new CustomModelData(this.handle.floats(), new ArrayList(flags), this.handle.strings(), this.handle.colors());
   }

   public List<String> getStrings() {
      return Collections.unmodifiableList(this.handle.strings());
   }

   public void setStrings(List<String> strings) {
      this.handle = new CustomModelData(this.handle.floats(), this.handle.flags(), new ArrayList(strings), this.handle.colors());
   }

   public List<Color> getColors() {
      return this.getHandle().colors().stream().map(Color::fromRGB).toList();
   }

   public void setColors(List<Color> colors) {
      this.handle = new CustomModelData(this.handle.floats(), this.handle.flags(), this.handle.strings(), (List)colors.stream().map(Color::asRGB).collect(Collectors.toList()));
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftCustomModelDataComponent other = (CraftCustomModelDataComponent)obj;
         return Objects.equals(this.handle, other.handle);
      }
   }

   public int hashCode() {
      int hash = 7;
      hash = 19 * hash + Objects.hashCode(this.handle);
      return hash;
   }

   public String toString() {
      return "CraftCustomModelDataComponent{handle=" + String.valueOf(this.handle) + "}";
   }
}
