package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueOutput;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.util.BlockVector;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBlockState extends CraftMetaItem implements BlockStateMeta {
   private static final Set<Material> SHULKER_BOX_MATERIALS;
   static final CraftMetaItem.ItemMetaKeyType<CustomData> BLOCK_ENTITY_TAG;
   final Material material;
   private CraftBlockEntityState<?> blockEntityTag;
   private BlockVector position;
   private CompoundTag internalTag;

   CraftMetaBlockState(CraftMetaItem meta, Material material) {
      super(meta);
      this.material = material;
      if (meta instanceof CraftMetaBlockState te && ((CraftMetaBlockState)meta).material == material) {
         this.blockEntityTag = te.blockEntityTag;
         this.position = te.position;
      } else {
         this.blockEntityTag = null;
      }
   }

   CraftMetaBlockState(DataComponentPatch tag, Material material) {
      super(tag);
      this.material = material;
      getOrEmpty(tag, BLOCK_ENTITY_TAG).ifPresent((blockTag) -> {
         CompoundTag nbt = blockTag.copyTag();
         this.blockEntityTag = getBlockState(material, nbt);
         if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z")) {
            this.position = new BlockVector(nbt.getIntOr("x", 0), nbt.getIntOr("y", 0), nbt.getIntOr("z", 0));
         }

      });
      if (!tag.isEmpty()) {
         CraftBlockEntityState<?> blockEntityTag = this.blockEntityTag;
         if (blockEntityTag == null) {
            blockEntityTag = getBlockState(material, (CompoundTag)null);
         }

         PatchedDataComponentMap map = new PatchedDataComponentMap(DataComponentMap.EMPTY);
         map.applyPatch(tag);
         Set<DataComponentType<?>> applied = blockEntityTag.applyComponents(map, tag);
         Iterator var6 = applied.iterator();

         while(var6.hasNext()) {
            DataComponentType<?> seen = (DataComponentType)var6.next();
            this.unhandledTags.clear(seen);
         }

         if (!applied.isEmpty()) {
            this.blockEntityTag = blockEntityTag;
         }
      }

   }

   CraftMetaBlockState(Map<String, Object> map) {
      super(map);
      String matName = SerializableMeta.getString(map, "blockMaterial", true);
      Material m = Material.getMaterial(matName);
      if (m != null) {
         this.material = m;
      } else {
         this.material = Material.AIR;
      }

      if (this.internalTag != null) {
         this.blockEntityTag = getBlockState(this.material, this.internalTag);
         this.internalTag = null;
      }

      this.position = (BlockVector)SerializableMeta.getObject(BlockVector.class, map, "blockPosition", true);
   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      TagValueOutput nbt = null;
      if (this.blockEntityTag != null) {
         nbt = this.blockEntityTag.getItemNBT();
         Iterator var3 = this.blockEntityTag.collectComponents().iterator();

         while(var3.hasNext()) {
            TypedDataComponent<?> component = (TypedDataComponent)var3.next();
            tag.putIfAbsent(component);
         }
      }

      if (this.position != null) {
         if (nbt == null) {
            nbt = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
         }

         nbt.putInt("x", this.position.getBlockX());
         nbt.putInt("y", this.position.getBlockY());
         nbt.putInt("z", this.position.getBlockZ());
      }

      if (nbt != null && !nbt.isEmpty()) {
         CraftBlockEntityState<?> tile = this.blockEntityTag != null ? this.blockEntityTag : getBlockState(this.material, (CompoundTag)null);
         tile.addEntityType(nbt);
         tag.put(BLOCK_ENTITY_TAG, CustomData.of(nbt.buildResult()));
      }

   }

   void deserializeInternal(CompoundTag tag, Object context) {
      super.deserializeInternal(tag, context);
      this.internalTag = (CompoundTag)tag.getCompound(BLOCK_ENTITY_TAG.NBT).orElse(this.internalTag);
   }

   void serializeInternal(Map<String, Tag> internalTags) {
      if (this.blockEntityTag != null) {
         internalTags.put(BLOCK_ENTITY_TAG.NBT, this.blockEntityTag.getSnapshotNBT());
      }

   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      builder.put("blockMaterial", this.material.name());
      if (this.position != null) {
         builder.put("blockPosition", this.position);
      }

      return builder;
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.blockEntityTag != null) {
         hash = 61 * hash + this.blockEntityTag.hashCode();
      }

      if (this.position != null) {
         hash = 61 * hash + this.position.hashCode();
      }

      return original != hash ? CraftMetaBlockState.class.hashCode() ^ hash : hash;
   }

   public boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaBlockState)) {
         return true;
      } else {
         CraftMetaBlockState that = (CraftMetaBlockState)meta;
         return Objects.equal(this.blockEntityTag, that.blockEntityTag) && Objects.equal(this.position, that.position);
      }
   }

   boolean isBlockStateEmpty() {
      return this.blockEntityTag == null && this.position == null;
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaBlockState || this.isBlockStateEmpty());
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isBlockStateEmpty();
   }

   public CraftMetaBlockState clone() {
      CraftMetaBlockState meta = (CraftMetaBlockState)super.clone();
      if (this.blockEntityTag != null) {
         meta.blockEntityTag = this.blockEntityTag.copy();
      }

      if (this.position != null) {
         meta.position = this.position.clone();
      }

      return meta;
   }

   public boolean hasBlockState() {
      return this.blockEntityTag != null;
   }

   public BlockState getBlockState() {
      return this.blockEntityTag != null ? this.blockEntityTag.copy() : getBlockState(this.material, (CompoundTag)null);
   }

   private static CraftBlockEntityState<?> getBlockState(Material material, CompoundTag blockEntityTag) {
      BlockPos pos = BlockPos.ZERO;
      Material stateMaterial = material != Material.SHIELD ? material : shieldToBannerHack(blockEntityTag);
      if (blockEntityTag != null) {
         if (material == Material.SHIELD) {
            blockEntityTag.putString("id", "minecraft:banner");
         } else if (material != Material.BEE_NEST && material != Material.BEEHIVE) {
            if (SHULKER_BOX_MATERIALS.contains(material)) {
               blockEntityTag.putString("id", "minecraft:shulker_box");
            }
         } else {
            blockEntityTag.putString("id", "minecraft:beehive");
         }

         pos = BlockEntity.getPosFromTag((ChunkPos)null, blockEntityTag);
      }

      return (CraftBlockEntityState)CraftBlockStates.getBlockState(pos, stateMaterial, blockEntityTag);
   }

   public void setBlockState(BlockState blockState) {
      Preconditions.checkArgument(blockState != null, "blockState must not be null");
      Material stateMaterial = this.material != Material.SHIELD ? this.material : shieldToBannerHack((CompoundTag)null);
      Class<?> blockStateType = CraftBlockStates.getBlockStateType(stateMaterial);
      Preconditions.checkArgument(blockStateType == blockState.getClass() && blockState instanceof CraftBlockEntityState, "Invalid blockState for %s", this.material);
      this.blockEntityTag = (CraftBlockEntityState)blockState;
   }

   private static Material shieldToBannerHack(CompoundTag tag) {
      if (tag != null) {
         tag.getCompound("components").flatMap((components) -> {
            return components.getString("minecraft:base_color").map((baseColor) -> {
               DyeColor color = DyeColor.getByWoolData((byte)net.minecraft.world.item.DyeColor.byName(baseColor, net.minecraft.world.item.DyeColor.WHITE).getId());
               return CraftMetaShield.shieldToBannerHack(color);
            });
         }).orElse(Material.WHITE_BANNER);
      }

      return Material.WHITE_BANNER;
   }

   static {
      SHULKER_BOX_MATERIALS = Sets.newHashSet(new Material[]{Material.SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.BLACK_SHULKER_BOX});
      BLOCK_ENTITY_TAG = new CraftMetaItem.ItemMetaKeyType(DataComponents.BLOCK_ENTITY_DATA, "BlockEntityTag");
   }
}
