package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.profile.CraftPlayerProfile;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.Nullable;

public class CraftSkull extends CraftBlockEntityState<SkullBlockEntity> implements Skull {
   private static final int MAX_OWNER_LENGTH = 16;
   private ResolvableProfile profile;

   public CraftSkull(World world, SkullBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftSkull(CraftSkull state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public void load(SkullBlockEntity skull) {
      super.load(skull);
      ResolvableProfile owner = skull.getOwnerProfile();
      if (owner != null) {
         this.profile = owner;
      }

   }

   public boolean hasOwner() {
      return this.profile != null;
   }

   public String getOwner() {
      return this.hasOwner() ? (String)this.profile.name().orElse((Object)null) : null;
   }

   public boolean setOwner(String name) {
      if (name != null && name.length() <= 16) {
         GameProfile profile = (GameProfile)MinecraftServer.getServer().getProfileCache().get(name).orElse((Object)null);
         if (profile == null) {
            return false;
         } else {
            this.profile = new ResolvableProfile(profile);
            return true;
         }
      } else {
         return false;
      }
   }

   public OfflinePlayer getOwningPlayer() {
      if (this.hasOwner()) {
         if (this.profile.id().filter((u) -> {
            return !u.equals(Util.NIL_UUID);
         }).isPresent()) {
            return Bukkit.getOfflinePlayer((UUID)this.profile.id().get());
         }

         if (this.profile.name().filter((s) -> {
            return !s.isEmpty();
         }).isPresent()) {
            return Bukkit.getOfflinePlayer((String)this.profile.name().get());
         }
      }

      return null;
   }

   public void setOwningPlayer(OfflinePlayer player) {
      Preconditions.checkNotNull(player, "player");
      if (player instanceof CraftPlayer craftPlayer) {
         this.profile = new ResolvableProfile(craftPlayer.getProfile());
      } else {
         this.profile = new ResolvableProfile(new GameProfile(player.getUniqueId(), player.getName() == null ? "" : player.getName()));
      }

   }

   public PlayerProfile getOwnerProfile() {
      return !this.hasOwner() ? null : new CraftPlayerProfile(this.profile);
   }

   public void setOwnerProfile(PlayerProfile profile) {
      if (profile == null) {
         this.profile = null;
      } else {
         this.profile = CraftPlayerProfile.validateSkullProfile(((CraftPlayerProfile)profile).buildResolvableProfile());
      }

   }

   public NamespacedKey getNoteBlockSound() {
      ResourceLocation key = ((SkullBlockEntity)this.getSnapshot()).getNoteBlockSound();
      return key != null ? CraftNamespacedKey.fromMinecraft(key) : null;
   }

   public void setNoteBlockSound(@Nullable NamespacedKey namespacedKey) {
      if (namespacedKey == null) {
         ((SkullBlockEntity)this.getSnapshot()).noteBlockSound = null;
      } else {
         ((SkullBlockEntity)this.getSnapshot()).noteBlockSound = CraftNamespacedKey.toMinecraft(namespacedKey);
      }
   }

   public BlockFace getRotation() {
      BlockData blockData = this.getBlockData();
      BlockFace var10000;
      if (blockData instanceof Rotatable rotatable) {
         var10000 = rotatable.getRotation();
      } else {
         var10000 = ((Directional)blockData).getFacing();
      }

      return var10000;
   }

   public void setRotation(BlockFace rotation) {
      BlockData blockData = this.getBlockData();
      if (blockData instanceof Rotatable) {
         ((Rotatable)blockData).setRotation(rotation);
      } else {
         ((Directional)blockData).setFacing(rotation);
      }

      this.setBlockData(blockData);
   }

   public SkullType getSkullType() {
      switch (this.getType()) {
         case SKELETON_SKULL:
         case SKELETON_WALL_SKULL:
            return SkullType.SKELETON;
         case WITHER_SKELETON_SKULL:
         case WITHER_SKELETON_WALL_SKULL:
            return SkullType.WITHER;
         case ZOMBIE_HEAD:
         case ZOMBIE_WALL_HEAD:
            return SkullType.ZOMBIE;
         case PIGLIN_HEAD:
         case PIGLIN_WALL_HEAD:
            return SkullType.PIGLIN;
         case PLAYER_HEAD:
         case PLAYER_WALL_HEAD:
            return SkullType.PLAYER;
         case CREEPER_HEAD:
         case CREEPER_WALL_HEAD:
            return SkullType.CREEPER;
         case DRAGON_HEAD:
         case DRAGON_WALL_HEAD:
            return SkullType.DRAGON;
         default:
            throw new IllegalArgumentException("Unknown SkullType for " + String.valueOf(this.getType()));
      }
   }

   public void setSkullType(SkullType skullType) {
      throw new UnsupportedOperationException("Must change block type");
   }

   protected void applyTo(SkullBlockEntity skull) {
      super.applyTo(skull);
      if (this.getSkullType() == SkullType.PLAYER) {
         skull.setOwner(this.hasOwner() ? this.profile : null);
      }

   }

   public CraftSkull copy() {
      return new CraftSkull(this, (Location)null);
   }

   public CraftSkull copy(Location location) {
      return new CraftSkull(this, location);
   }
}
