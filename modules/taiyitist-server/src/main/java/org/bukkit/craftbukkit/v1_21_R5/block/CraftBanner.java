package org.bukkit.craftbukkit.v1_21_R5.block;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.craftbukkit.v1_21_R5.block.banner.CraftPatternType;

public class CraftBanner extends CraftBlockEntityState<BannerBlockEntity> implements Banner {
   private DyeColor base;
   private List<Pattern> patterns;

   public CraftBanner(World world, BannerBlockEntity tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftBanner(CraftBanner state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
      this.base = state.getBaseColor();
      this.patterns = new ArrayList(state.getPatterns());
   }

   public void load(BannerBlockEntity banner) {
      super.load(banner);
      this.base = DyeColor.getByWoolData((byte)((AbstractBannerBlock)this.data.getBlock()).getColor().getId());
      this.patterns = new ArrayList();
      if (banner.getPatterns() != null) {
         for(int i = 0; i < banner.getPatterns().layers().size(); ++i) {
            BannerPatternLayers.Layer p = (BannerPatternLayers.Layer)banner.getPatterns().layers().get(i);
            this.patterns.add(new Pattern(DyeColor.getByWoolData((byte)p.color().getId()), CraftPatternType.minecraftHolderToBukkit(p.pattern())));
         }
      }

   }

   public DyeColor getBaseColor() {
      return this.base;
   }

   public void setBaseColor(DyeColor color) {
      Preconditions.checkArgument(color != null, "color");
      this.base = color;
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

   protected void applyTo(BannerBlockEntity banner) {
      super.applyTo(banner);
      banner.baseColor = net.minecraft.world.item.DyeColor.byId(this.base.getWoolData());
      List<BannerPatternLayers.Layer> newPatterns = new ArrayList();
      Iterator var3 = this.patterns.iterator();

      while(var3.hasNext()) {
         Pattern p = (Pattern)var3.next();
         newPatterns.add(new BannerPatternLayers.Layer(CraftPatternType.bukkitToMinecraftHolder(p.getPattern()), net.minecraft.world.item.DyeColor.byId(p.getColor().getWoolData())));
      }

      banner.setPatterns(new BannerPatternLayers(newPatterns));
   }

   public CraftBanner copy() {
      return new CraftBanner(this, (Location)null);
   }

   public CraftBanner copy(Location location) {
      return new CraftBanner(this, location);
   }
}
