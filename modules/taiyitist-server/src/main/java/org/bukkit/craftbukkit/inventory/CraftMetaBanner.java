package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.inventory.meta.BannerMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBanner extends CraftMetaItem implements BannerMeta {
   static final CraftMetaItem.ItemMetaKeyType<BannerPatternLayers> PATTERNS;
   private List<Pattern> patterns = new ArrayList();

   CraftMetaBanner(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaBanner banner) {
         this.patterns = new ArrayList(banner.patterns);
      }
   }

   CraftMetaBanner(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, PATTERNS).ifPresent((entityTag) -> {
         List<BannerPatternLayers.Layer> patterns = entityTag.layers();

         for(int i = 0; i < Math.min(patterns.size(), 20); ++i) {
            BannerPatternLayers.Layer p = (BannerPatternLayers.Layer)patterns.get(i);
            DyeColor color = DyeColor.getByWoolData((byte)p.color().getId());
            PatternType pattern = CraftPatternType.minecraftHolderToBukkit(p.pattern());
            if (color != null && pattern != null) {
               this.patterns.add(new Pattern(color, pattern));
            }
         }

      });
   }

   CraftMetaBanner(Map<String, Object> map) {
      super(map);
      Iterable<?> rawPatternList = (Iterable)SerializableMeta.getObject(Iterable.class, map, PATTERNS.BUKKIT, true);
      if (rawPatternList != null) {
         Iterator var3 = rawPatternList.iterator();

         while(var3.hasNext()) {
            Object obj = var3.next();
            Preconditions.checkArgument(obj instanceof Pattern, "Object (%s) in pattern list is not valid", obj.getClass());
            this.addPattern((Pattern)obj);
         }

      }
   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      List<BannerPatternLayers.Layer> newPatterns = new ArrayList();
      Iterator var3 = this.patterns.iterator();

      while(var3.hasNext()) {
         Pattern p = (Pattern)var3.next();
         newPatterns.add(new BannerPatternLayers.Layer(CraftPatternType.bukkitToMinecraftHolder(p.getPattern()), net.minecraft.world.item.DyeColor.byId(p.getColor().getWoolData())));
      }

      tag.put(PATTERNS, new BannerPatternLayers(newPatterns));
   }

   public List<Pattern> getPatterns() {
      return new ArrayList(this.patterns);
   }

   public void setPatterns(List<Pattern> patterns) {
      this.patterns = new ArrayList(patterns);
   }

   public void addPattern(Pattern pattern) {
      this.patterns.add(pattern);
   }

   public Pattern getPattern(int i) {
      return (Pattern)this.patterns.get(i);
   }

   public Pattern removePattern(int i) {
      return (Pattern)this.patterns.remove(i);
   }

   public void setPattern(int i, Pattern pattern) {
      this.patterns.set(i, pattern);
   }

   public int numberOfPatterns() {
      return this.patterns.size();
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (!this.patterns.isEmpty()) {
         builder.put(PATTERNS.BUKKIT, ImmutableList.copyOf(this.patterns));
      }

      return builder;
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (!this.patterns.isEmpty()) {
         hash = 31 * hash + this.patterns.hashCode();
      }

      return original != hash ? CraftMetaBanner.class.hashCode() ^ hash : hash;
   }

   public boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaBanner) {
         CraftMetaBanner that = (CraftMetaBanner)meta;
         return this.patterns.equals(that.patterns);
      } else {
         return true;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaBanner || this.patterns.isEmpty());
   }

   boolean isEmpty() {
      return super.isEmpty() && this.patterns.isEmpty();
   }

   public CraftMetaBanner clone() {
      CraftMetaBanner meta = (CraftMetaBanner)super.clone();
      meta.patterns = new ArrayList(this.patterns);
      return meta;
   }

   static {
      PATTERNS = new CraftMetaItem.ItemMetaKeyType(DataComponents.BANNER_PATTERNS, "patterns");
   }
}
