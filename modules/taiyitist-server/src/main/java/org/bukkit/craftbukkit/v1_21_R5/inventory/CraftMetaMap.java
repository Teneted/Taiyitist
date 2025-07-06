package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaMap extends CraftMetaItem implements MapMeta {
   static final CraftMetaItem.ItemMetaKeyType<MapPostProcessing> MAP_POST_PROCESSING;
   static final ItemMetaKey MAP_SCALING;
   /** @deprecated */
   @Deprecated
   static final ItemMetaKey MAP_LOC_NAME;
   static final CraftMetaItem.ItemMetaKeyType<MapItemColor> MAP_COLOR;
   static final CraftMetaItem.ItemMetaKeyType<MapId> MAP_ID;
   static final byte SCALING_EMPTY = 0;
   static final byte SCALING_TRUE = 1;
   static final byte SCALING_FALSE = 2;
   private Integer mapId;
   private byte scaling = 0;
   private Color color;

   CraftMetaMap(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaMap map) {
         this.mapId = map.mapId;
         this.scaling = map.scaling;
         this.color = map.color;
      }
   }

   CraftMetaMap(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, MAP_ID).ifPresent((mapId) -> {
         this.mapId = mapId.id();
      });
      getOrEmpty(tag, MAP_POST_PROCESSING).ifPresent((mapPostProcessing) -> {
         this.scaling = (byte)(mapPostProcessing == MapPostProcessing.SCALE ? 1 : 2);
      });
      getOrEmpty(tag, MAP_COLOR).ifPresent((mapColor) -> {
         try {
            this.color = Color.fromRGB(mapColor.rgb());
         } catch (IllegalArgumentException var3) {
         }

      });
   }

   CraftMetaMap(Map<String, Object> map) {
      super(map);
      Integer id = (Integer)SerializableMeta.getObject(Integer.class, map, MAP_ID.BUKKIT, true);
      if (id != null) {
         this.setMapId(id);
      }

      Boolean scaling = (Boolean)SerializableMeta.getObject(Boolean.class, map, MAP_SCALING.BUKKIT, true);
      if (scaling != null) {
         this.setScaling(scaling);
      }

      String locName = SerializableMeta.getString(map, MAP_LOC_NAME.BUKKIT, true);
      if (locName != null) {
         this.setLocationName(locName);
      }

      Color color = (Color)SerializableMeta.getObject(Color.class, map, MAP_COLOR.BUKKIT, true);
      if (color != null) {
         this.setColor(color);
      }

   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.hasMapId()) {
         tag.put(MAP_ID, new MapId(this.getMapId()));
      }

      if (this.hasScaling()) {
         tag.put(MAP_POST_PROCESSING, this.isScaling() ? MapPostProcessing.SCALE : MapPostProcessing.LOCK);
      }

      if (this.hasColor()) {
         tag.put(MAP_COLOR, new MapItemColor(this.color.asRGB()));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isMapEmpty();
   }

   boolean isMapEmpty() {
      return !this.hasMapId() && !(this.hasScaling() | this.hasLocationName()) && !this.hasColor();
   }

   public boolean hasMapId() {
      return this.mapId != null;
   }

   public int getMapId() {
      return this.mapId;
   }

   public void setMapId(int id) {
      this.mapId = id;
   }

   public boolean hasMapView() {
      return this.mapId != null;
   }

   public MapView getMapView() {
      Preconditions.checkState(this.hasMapView(), "Item does not have map associated - check hasMapView() first!");
      return Bukkit.getMap(this.mapId);
   }

   public void setMapView(MapView map) {
      this.mapId = map != null ? map.getId() : null;
   }

   boolean hasScaling() {
      return this.scaling != 0;
   }

   public boolean isScaling() {
      return this.scaling == 1;
   }

   public void setScaling(boolean scaling) {
      this.scaling = (byte)(scaling ? 1 : 2);
   }

   public boolean hasLocationName() {
      return this.hasLocalizedName();
   }

   public String getLocationName() {
      return this.getLocalizedName();
   }

   public void setLocationName(String name) {
      this.setLocalizedName(name);
   }

   public boolean hasColor() {
      return this.color != null;
   }

   public Color getColor() {
      return this.color;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaMap)) {
         return true;
      } else {
         CraftMetaMap that = (CraftMetaMap)meta;
         boolean var10000;
         if (this.scaling == that.scaling) {
            label54: {
               if (this.hasMapId()) {
                  if (!that.hasMapId() || !this.mapId.equals(that.mapId)) {
                     break label54;
                  }
               } else if (that.hasMapId()) {
                  break label54;
               }

               if (this.hasColor()) {
                  if (!that.hasColor() || !this.color.equals(that.color)) {
                     break label54;
                  }
               } else if (that.hasColor()) {
                  break label54;
               }

               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaMap || this.isMapEmpty());
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasMapId()) {
         hash = 61 * hash + this.mapId.hashCode();
      }

      if (this.hasScaling()) {
         hash ^= 572662306 << (this.isScaling() ? 1 : -1);
      }

      if (this.hasColor()) {
         hash = 61 * hash + this.color.hashCode();
      }

      return original != hash ? CraftMetaMap.class.hashCode() ^ hash : hash;
   }

   public CraftMetaMap clone() {
      return (CraftMetaMap)super.clone();
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasMapId()) {
         builder.put(MAP_ID.BUKKIT, this.getMapId());
      }

      if (this.hasScaling()) {
         builder.put(MAP_SCALING.BUKKIT, this.isScaling());
      }

      if (this.hasColor()) {
         builder.put(MAP_COLOR.BUKKIT, this.getColor());
      }

      return builder;
   }

   static {
      MAP_POST_PROCESSING = new CraftMetaItem.ItemMetaKeyType(DataComponents.MAP_POST_PROCESSING);
      MAP_SCALING = new ItemMetaKey("scaling");
      MAP_LOC_NAME = new ItemMetaKey("display-loc-name");
      MAP_COLOR = new CraftMetaItem.ItemMetaKeyType(DataComponents.MAP_COLOR, "display-map-color");
      MAP_ID = new CraftMetaItem.ItemMetaKeyType(DataComponents.MAP_ID, "map-id");
   }
}
