package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.inventory.meta.CompassMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaCompass extends CraftMetaItem implements CompassMeta {
   static final CraftMetaItem.ItemMetaKeyType<LodestoneTracker> LODESTONE_TARGET;
   static final ItemMetaKey LODESTONE_POS;
   static final ItemMetaKey LODESTONE_POS_WORLD;
   static final ItemMetaKey LODESTONE_POS_X;
   static final ItemMetaKey LODESTONE_POS_Y;
   static final ItemMetaKey LODESTONE_POS_Z;
   static final ItemMetaKey LODESTONE_TRACKED;
   private ResourceKey<Level> lodestoneWorld;
   private int lodestoneX;
   private int lodestoneY;
   private int lodestoneZ;
   private boolean tracked = true;

   CraftMetaCompass(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaCompass compassMeta) {
         this.lodestoneWorld = compassMeta.lodestoneWorld;
         this.lodestoneX = compassMeta.lodestoneX;
         this.lodestoneY = compassMeta.lodestoneY;
         this.lodestoneZ = compassMeta.lodestoneZ;
         this.tracked = compassMeta.tracked;
      }
   }

   CraftMetaCompass(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, LODESTONE_TARGET).ifPresent((lodestoneTarget) -> {
         lodestoneTarget.target().ifPresent((target) -> {
            this.lodestoneWorld = target.dimension();
            BlockPos pos = target.pos();
            this.lodestoneX = pos.getX();
            this.lodestoneY = pos.getY();
            this.lodestoneZ = pos.getZ();
         });
         this.tracked = lodestoneTarget.tracked();
      });
   }

   CraftMetaCompass(Map<String, Object> map) {
      super(map);
      String lodestoneWorldString = SerializableMeta.getString(map, LODESTONE_POS_WORLD.BUKKIT, true);
      if (lodestoneWorldString != null) {
         this.lodestoneWorld = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(lodestoneWorldString));
         this.lodestoneX = (Integer)map.get(LODESTONE_POS_X.BUKKIT);
         this.lodestoneY = (Integer)map.get(LODESTONE_POS_Y.BUKKIT);
         this.lodestoneZ = (Integer)map.get(LODESTONE_POS_Z.BUKKIT);
      } else {
         Location lodestone = (Location)SerializableMeta.getObject(Location.class, map, LODESTONE_POS.BUKKIT, true);
         if (lodestone != null && lodestone.getWorld() != null) {
            this.setLodestone(lodestone);
         }
      }

      this.tracked = SerializableMeta.getBoolean(map, LODESTONE_TRACKED.BUKKIT);
   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      Optional<GlobalPos> target = Optional.empty();
      if (this.lodestoneWorld != null) {
         target = Optional.of(new GlobalPos(this.lodestoneWorld, new BlockPos(this.lodestoneX, this.lodestoneY, this.lodestoneZ)));
      }

      if (target.isPresent() || this.hasLodestoneTracked()) {
         tag.put(LODESTONE_TARGET, new LodestoneTracker(target, this.tracked));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isCompassEmpty();
   }

   boolean isCompassEmpty() {
      return !this.hasLodestone() && !this.hasLodestoneTracked();
   }

   public CraftMetaCompass clone() {
      CraftMetaCompass clone = (CraftMetaCompass)super.clone();
      return clone;
   }

   public boolean hasLodestone() {
      return this.lodestoneWorld != null;
   }

   public Location getLodestone() {
      if (this.lodestoneWorld == null) {
         return null;
      } else {
         ServerLevel worldServer = MinecraftServer.getServer().getLevel(this.lodestoneWorld);
         World world = worldServer != null ? worldServer.getWorld() : null;
         return new Location(world, (double)this.lodestoneX, (double)this.lodestoneY, (double)this.lodestoneZ);
      }
   }

   public void setLodestone(Location lodestone) {
      Preconditions.checkArgument(lodestone == null || lodestone.getWorld() != null, "world is null");
      if (lodestone == null) {
         this.lodestoneWorld = null;
      } else {
         this.lodestoneWorld = ((CraftWorld)lodestone.getWorld()).getHandle().dimension();
         this.lodestoneX = lodestone.getBlockX();
         this.lodestoneY = lodestone.getBlockY();
         this.lodestoneZ = lodestone.getBlockZ();
      }

   }

   boolean hasLodestoneTracked() {
      return !this.tracked;
   }

   public boolean isLodestoneTracked() {
      return this.tracked;
   }

   public void setLodestoneTracked(boolean tracked) {
      this.tracked = tracked;
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasLodestone()) {
         hash = 73 * hash + this.lodestoneWorld.hashCode();
         hash = 73 * hash + this.lodestoneX;
         hash = 73 * hash + this.lodestoneY;
         hash = 73 * hash + this.lodestoneZ;
      }

      if (this.hasLodestoneTracked()) {
         hash = 73 * hash + (this.isLodestoneTracked() ? 1231 : 1237);
      }

      return original != hash ? CraftMetaCompass.class.hashCode() ^ hash : hash;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaCompass)) {
         return true;
      } else {
         boolean var10000;
         label35: {
            CraftMetaCompass that = (CraftMetaCompass)meta;
            if (this.hasLodestone()) {
               if (!that.hasLodestone() || !this.lodestoneWorld.equals(that.lodestoneWorld) || this.lodestoneX != that.lodestoneX || this.lodestoneY != that.lodestoneY || this.lodestoneZ != that.lodestoneZ) {
                  break label35;
               }
            } else if (that.hasLodestone()) {
               break label35;
            }

            if (this.tracked == that.tracked) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaCompass || this.isCompassEmpty());
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasLodestone()) {
         builder.put(LODESTONE_POS_WORLD.BUKKIT, this.lodestoneWorld.location().toString());
         builder.put(LODESTONE_POS_X.BUKKIT, this.lodestoneX);
         builder.put(LODESTONE_POS_Y.BUKKIT, this.lodestoneY);
         builder.put(LODESTONE_POS_Z.BUKKIT, this.lodestoneZ);
      }

      if (this.hasLodestoneTracked()) {
         builder.put(LODESTONE_TRACKED.BUKKIT, this.tracked);
      }

      return builder;
   }

   static {
      LODESTONE_TARGET = new CraftMetaItem.ItemMetaKeyType(DataComponents.LODESTONE_TRACKER, "LodestoneDimension");
      LODESTONE_POS = new ItemMetaKey("lodestone");
      LODESTONE_POS_WORLD = new ItemMetaKey("LodestonePosWorld");
      LODESTONE_POS_X = new ItemMetaKey("LodestonePosX");
      LODESTONE_POS_Y = new ItemMetaKey("LodestonePosY");
      LODESTONE_POS_Z = new ItemMetaKey("LodestonePosZ");
      LODESTONE_TRACKED = new ItemMetaKey("LodestoneTracked");
   }
}
