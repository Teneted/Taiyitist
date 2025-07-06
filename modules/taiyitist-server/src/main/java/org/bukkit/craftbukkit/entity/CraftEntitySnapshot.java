package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;

public class CraftEntitySnapshot implements EntitySnapshot {
   private final CompoundTag data;
   private final EntityType type;

   private CraftEntitySnapshot(CompoundTag data, EntityType type) {
      this.data = data;
      this.type = type;
   }

   public EntityType getEntityType() {
      return this.type;
   }

   public Entity createEntity(World world) {
      net.minecraft.world.entity.Entity internal = this.createInternal(world);
      return internal.getBukkitEntity();
   }

   public Entity createEntity(Location location) {
      Preconditions.checkArgument(location.getWorld() != null, "Location has no world");
      net.minecraft.world.entity.Entity internal = this.createInternal(location.getWorld());
      internal.setPos(location.getX(), location.getY(), location.getZ());
      return location.getWorld().addEntity(internal.getBukkitEntity());
   }

   public String getAsString() {
      return this.data.toString();
   }

   private net.minecraft.world.entity.Entity createInternal(World world) {
      Level nms = ((CraftWorld)world).getHandle();
      net.minecraft.world.entity.Entity internal = net.minecraft.world.entity.EntityType.loadEntityRecursive(this.data, nms, EntitySpawnReason.LOAD, Function.identity());
      if (internal == null) {
         internal = CraftEntityType.bukkitToMinecraft(this.type).create(nms, EntitySpawnReason.LOAD);
      }

      Preconditions.checkArgument(internal != null, "Error creating new entity.");
      ValueInput val = TagValueInput.create(ProblemReporter.DISCARDING, ((Level)nms).registryAccess(), this.data);
      internal.load(val);
      return internal;
   }

   public CompoundTag getData() {
      return this.data;
   }

   public static CraftEntitySnapshot create(CraftEntity entity) {
      TagValueOutput tag = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.getHandle().registryAccess());
      return !entity.getHandle().saveAsPassenger(tag, false) ? null : new CraftEntitySnapshot(tag.buildResult(), entity.getType());
   }

   public static CraftEntitySnapshot create(CompoundTag tag, EntityType type) {
      return tag != null && !tag.isEmpty() && type != null ? new CraftEntitySnapshot(tag, type) : null;
   }

   public static CraftEntitySnapshot create(CompoundTag tag) {
      EntityType type = (EntityType)tag.read("id", net.minecraft.world.entity.EntityType.CODEC).map(CraftEntityType::minecraftToBukkit).orElse((Object)null);
      return create(tag, type);
   }
}
