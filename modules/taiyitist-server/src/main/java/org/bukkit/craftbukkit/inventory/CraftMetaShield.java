package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ShieldMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaShield extends CraftMetaItem implements ShieldMeta, BlockStateMeta {
   static final CraftMetaItem.ItemMetaKeyType<DyeColor> BASE_COLOR;
   private Banner banner;

   CraftMetaShield(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaShield craftMetaShield) {
         if (craftMetaShield.banner != null) {
            this.banner = (Banner)craftMetaShield.banner.copy();
         }
      } else if (meta instanceof CraftMetaBlockState state) {
         if (state.hasBlockState()) {
            BlockState var5 = state.getBlockState();
            if (var5 instanceof Banner) {
               Banner banner = (Banner)var5;
               this.banner = (Banner)banner.copy();
            }
         }
      }

   }

   CraftMetaShield(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, BASE_COLOR).ifPresent((color) -> {
         this.banner = getBlockState(org.bukkit.DyeColor.getByWoolData((byte)color.getId()));
      });
      getOrEmpty(tag, CraftMetaBanner.PATTERNS).ifPresent((entityTag) -> {
         List<BannerPatternLayers.Layer> patterns = entityTag.layers();

         for(int i = 0; i < Math.min(patterns.size(), 20); ++i) {
            BannerPatternLayers.Layer p = (BannerPatternLayers.Layer)patterns.get(i);
            org.bukkit.DyeColor color = org.bukkit.DyeColor.getByWoolData((byte)p.color().getId());
            PatternType pattern = CraftPatternType.minecraftHolderToBukkit(p.pattern());
            if (color != null && pattern != null) {
               this.addPattern(new Pattern(color, pattern));
            }
         }

      });
   }

   CraftMetaShield(Map<String, Object> map) {
      super(map);
      String baseColor = SerializableMeta.getString(map, BASE_COLOR.BUKKIT, true);
      if (baseColor != null) {
         this.banner = getBlockState(org.bukkit.DyeColor.valueOf(baseColor));
      }

      Iterable<?> rawPatternList = (Iterable)SerializableMeta.getObject(Iterable.class, map, CraftMetaBanner.PATTERNS.BUKKIT, true);
      if (rawPatternList != null) {
         Iterator var4 = rawPatternList.iterator();

         while(var4.hasNext()) {
            Object obj = var4.next();
            Preconditions.checkArgument(obj instanceof Pattern, "Object (%s) in pattern list is not valid", obj.getClass());
            this.addPattern((Pattern)obj);
         }

      }
   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.banner != null) {
         tag.put(BASE_COLOR, DyeColor.byId(this.banner.getBaseColor().getWoolData()));
         if (this.banner.numberOfPatterns() > 0) {
            List<BannerPatternLayers.Layer> newPatterns = new ArrayList();
            Iterator var3 = this.banner.getPatterns().iterator();

            while(var3.hasNext()) {
               Pattern p = (Pattern)var3.next();
               newPatterns.add(new BannerPatternLayers.Layer(CraftPatternType.bukkitToMinecraftHolder(p.getPattern()), DyeColor.byId(p.getColor().getWoolData())));
            }

            tag.put(CraftMetaBanner.PATTERNS, new BannerPatternLayers(newPatterns));
         }
      }

   }

   public List<Pattern> getPatterns() {
      return (List)(this.banner == null ? new ArrayList() : this.banner.getPatterns());
   }

   public void setPatterns(List<Pattern> patterns) {
      if (this.banner == null) {
         if (patterns.isEmpty()) {
            return;
         }

         this.banner = getBlockState((org.bukkit.DyeColor)null);
      }

      this.banner.setPatterns(patterns);
   }

   public void addPattern(Pattern pattern) {
      if (this.banner == null) {
         this.banner = getBlockState((org.bukkit.DyeColor)null);
      }

      this.banner.addPattern(pattern);
   }

   public Pattern getPattern(int i) {
      if (this.banner == null) {
         throw new IndexOutOfBoundsException(i);
      } else {
         return this.banner.getPattern(i);
      }
   }

   public Pattern removePattern(int i) {
      if (this.banner == null) {
         throw new IndexOutOfBoundsException(i);
      } else {
         return this.banner.removePattern(i);
      }
   }

   public void setPattern(int i, Pattern pattern) {
      if (this.banner == null) {
         throw new IndexOutOfBoundsException(i);
      } else {
         this.banner.setPattern(i, pattern);
      }
   }

   public int numberOfPatterns() {
      return this.banner == null ? 0 : this.banner.numberOfPatterns();
   }

   public org.bukkit.DyeColor getBaseColor() {
      return this.banner == null ? null : this.banner.getBaseColor();
   }

   public void setBaseColor(org.bukkit.DyeColor baseColor) {
      if (baseColor == null) {
         if (this.banner.numberOfPatterns() > 0) {
            this.banner.setBaseColor(org.bukkit.DyeColor.WHITE);
         } else {
            this.banner = null;
         }
      } else {
         if (this.banner == null) {
            this.banner = getBlockState(baseColor);
         }

         this.banner.setBaseColor(baseColor);
      }

   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.banner != null) {
         builder.put(BASE_COLOR.BUKKIT, this.banner.getBaseColor().toString());
         if (this.banner.numberOfPatterns() > 0) {
            builder.put(CraftMetaBanner.PATTERNS.BUKKIT, this.banner.getPatterns());
         }
      }

      return builder;
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.banner != null) {
         hash = 61 * hash + this.banner.hashCode();
      }

      return original != hash ? CraftMetaShield.class.hashCode() ^ hash : hash;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaShield) {
         CraftMetaShield that = (CraftMetaShield)meta;
         return Objects.equal(this.banner, that.banner);
      } else {
         return true;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaShield || this.banner == null);
   }

   boolean isEmpty() {
      return super.isEmpty() && this.banner == null;
   }

   public boolean hasBlockState() {
      return this.banner != null;
   }

   public BlockState getBlockState() {
      return (BlockState)(this.banner != null ? this.banner.copy() : getBlockState((org.bukkit.DyeColor)null));
   }

   public void setBlockState(BlockState blockState) {
      Preconditions.checkArgument(blockState != null, "blockState must not be null");
      Preconditions.checkArgument(blockState instanceof Banner, "Invalid blockState");
      this.banner = (Banner)blockState;
   }

   private static Banner getBlockState(org.bukkit.DyeColor color) {
      BlockPos pos = BlockPos.ZERO;
      Material stateMaterial = shieldToBannerHack(color);
      return (Banner)CraftBlockStates.getBlockState(pos, stateMaterial, (CompoundTag)null);
   }

   public CraftMetaShield clone() {
      CraftMetaShield meta = (CraftMetaShield)super.clone();
      if (this.banner != null) {
         meta.banner = (Banner)this.banner.copy();
      }

      return meta;
   }

   static Material shieldToBannerHack(org.bukkit.DyeColor color) {
      if (color == null) {
         return Material.WHITE_BANNER;
      } else {
         Material var10000;
         switch (color) {
            case WHITE -> var10000 = Material.WHITE_BANNER;
            case ORANGE -> var10000 = Material.ORANGE_BANNER;
            case MAGENTA -> var10000 = Material.MAGENTA_BANNER;
            case LIGHT_BLUE -> var10000 = Material.LIGHT_BLUE_BANNER;
            case YELLOW -> var10000 = Material.YELLOW_BANNER;
            case LIME -> var10000 = Material.LIME_BANNER;
            case PINK -> var10000 = Material.PINK_BANNER;
            case GRAY -> var10000 = Material.GRAY_BANNER;
            case LIGHT_GRAY -> var10000 = Material.LIGHT_GRAY_BANNER;
            case CYAN -> var10000 = Material.CYAN_BANNER;
            case PURPLE -> var10000 = Material.PURPLE_BANNER;
            case BLUE -> var10000 = Material.BLUE_BANNER;
            case BROWN -> var10000 = Material.BROWN_BANNER;
            case GREEN -> var10000 = Material.GREEN_BANNER;
            case RED -> var10000 = Material.RED_BANNER;
            case BLACK -> var10000 = Material.BLACK_BANNER;
            default -> throw new IllegalArgumentException("Unknown banner colour");
         }

         return var10000;
      }
   }

   static {
      BASE_COLOR = new CraftMetaItem.ItemMetaKeyType(DataComponents.BASE_COLOR, "Base", "base-color");
   }
}
