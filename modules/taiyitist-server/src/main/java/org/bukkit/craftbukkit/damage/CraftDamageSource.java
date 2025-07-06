package org.bukkit.craftbukkit.damage;

import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;

public class CraftDamageSource implements DamageSource {
   private final net.minecraft.world.damagesource.DamageSource damageSource;
   private final DamageType damageType;

   public CraftDamageSource(net.minecraft.world.damagesource.DamageSource damageSource) {
      this.damageSource = damageSource;
      this.damageType = CraftDamageType.minecraftHolderToBukkit(damageSource.typeHolder());
   }

   public net.minecraft.world.damagesource.DamageSource getHandle() {
      return this.damageSource;
   }

   public World getCausingEntityWorld() {
      Entity causingEntity = this.getCausingEntity();
      return causingEntity != null ? causingEntity.getWorld() : null;
   }

   public Block getDirectBlock() {
      return this.getHandle().getDirectBlock();
   }

   public DamageType getDamageType() {
      return this.damageType;
   }

   public Entity getCausingEntity() {
      net.minecraft.world.entity.Entity entity = this.getHandle().getCausingDamager();
      return entity != null ? entity.getBukkitEntity() : null;
   }

   public Entity getDirectEntity() {
      net.minecraft.world.entity.Entity entity = this.getHandle().getDamager();
      return entity != null ? entity.getBukkitEntity() : null;
   }

   public Location getDamageLocation() {
      Vec3 vec3D = this.getHandle().sourcePositionRaw();
      return vec3D != null ? CraftLocation.toBukkit(vec3D, this.getCausingEntityWorld()) : null;
   }

   public Location getSourceLocation() {
      Vec3 vec3D = this.getHandle().getSourcePosition();
      return vec3D != null ? CraftLocation.toBukkit(vec3D, this.getCausingEntityWorld()) : null;
   }

   public boolean isIndirect() {
      return this.getHandle().getCausingDamager() != this.getHandle().getDamager();
   }

   public float getFoodExhaustion() {
      return this.damageType.getExhaustion();
   }

   public boolean scalesWithDifficulty() {
      return this.getHandle().scalesWithDifficulty();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof DamageSource)) {
         return false;
      } else {
         DamageSource other = (DamageSource)obj;
         return Objects.equals(this.getDamageType(), other.getDamageType()) && Objects.equals(this.getCausingEntity(), other.getCausingEntity()) && Objects.equals(this.getDirectEntity(), other.getDirectEntity()) && Objects.equals(this.getDamageLocation(), other.getDamageLocation());
      }
   }

   public int hashCode() {
      int result = 1;
      result = 31 * result + this.damageType.hashCode();
      result = 31 * result + (this.getCausingEntity() != null ? this.getCausingEntity().hashCode() : 0);
      result = 31 * result + (this.getDirectEntity() != null ? this.getDirectEntity().hashCode() : 0);
      result = 31 * result + (this.getDamageLocation() != null ? this.getDamageLocation().hashCode() : 0);
      return result;
   }

   public String toString() {
      String var10000 = String.valueOf(this.getDamageType());
      return "DamageSource{damageType=" + var10000 + ",causingEntity=" + String.valueOf(this.getCausingEntity()) + ",directEntity=" + String.valueOf(this.getDirectEntity()) + ",damageLocation=" + String.valueOf(this.getDamageLocation()) + "}";
   }

   public static DamageSource buildFromBukkit(DamageType damageType, Entity causingEntity, Entity directEntity, Location damageLocation) {
      Holder<net.minecraft.world.damagesource.DamageType> holderDamageType = CraftDamageType.bukkitToMinecraftHolder(damageType);
      net.minecraft.world.entity.Entity nmsCausingEntity = null;
      if (causingEntity instanceof CraftEntity craftCausingEntity) {
         nmsCausingEntity = craftCausingEntity.getHandle();
      }

      net.minecraft.world.entity.Entity nmsDirectEntity = null;
      if (directEntity instanceof CraftEntity craftDirectEntity) {
         nmsDirectEntity = craftDirectEntity.getHandle();
      }

      Vec3 vec3D = damageLocation == null ? null : CraftLocation.toVec3D(damageLocation);
      return new CraftDamageSource(new net.minecraft.world.damagesource.DamageSource(holderDamageType, nmsDirectEntity, nmsCausingEntity, vec3D));
   }
}
