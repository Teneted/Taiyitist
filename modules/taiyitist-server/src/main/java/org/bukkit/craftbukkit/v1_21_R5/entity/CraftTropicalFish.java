package org.bukkit.craftbukkit.v1_21_R5.entity;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.TropicalFish.Pattern;

public class CraftTropicalFish extends CraftFish implements TropicalFish {
   public CraftTropicalFish(CraftServer server, net.minecraft.world.entity.animal.TropicalFish entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.TropicalFish getHandle() {
      return (net.minecraft.world.entity.animal.TropicalFish)this.entity;
   }

   public String toString() {
      return "CraftTropicalFish";
   }

   public DyeColor getPatternColor() {
      return getPatternColor(this.getHandle().getPackedVariant());
   }

   public void setPatternColor(DyeColor color) {
      this.getHandle().setPackedVariant(getData(color, this.getBodyColor(), this.getPattern()));
   }

   public DyeColor getBodyColor() {
      return getBodyColor(this.getHandle().getPackedVariant());
   }

   public void setBodyColor(DyeColor color) {
      this.getHandle().setPackedVariant(getData(this.getPatternColor(), color, this.getPattern()));
   }

   public TropicalFish.Pattern getPattern() {
      return getPattern(this.getHandle().getPackedVariant());
   }

   public void setPattern(TropicalFish.Pattern pattern) {
      this.getHandle().setPackedVariant(getData(this.getPatternColor(), this.getBodyColor(), pattern));
   }

   public static int getData(DyeColor patternColor, DyeColor bodyColor, TropicalFish.Pattern type) {
      return patternColor.getWoolData() << 24 | bodyColor.getWoolData() << 16 | CraftTropicalFish.CraftPattern.values()[type.ordinal()].getDataValue();
   }

   public static DyeColor getPatternColor(int data) {
      return DyeColor.getByWoolData((byte)(data >> 24 & 255));
   }

   public static DyeColor getBodyColor(int data) {
      return DyeColor.getByWoolData((byte)(data >> 16 & 255));
   }

   public static TropicalFish.Pattern getPattern(int data) {
      return CraftTropicalFish.CraftPattern.fromData(data & '\uffff');
   }

   public static enum CraftPattern {
      KOB(0, false),
      SUNSTREAK(1, false),
      SNOOPER(2, false),
      DASHER(3, false),
      BRINELY(4, false),
      SPOTTY(5, false),
      FLOPPER(0, true),
      STRIPEY(1, true),
      GLITTER(2, true),
      BLOCKFISH(3, true),
      BETTY(4, true),
      CLAYFISH(5, true);

      private final int variant;
      private final boolean large;
      private static final Map<Integer, TropicalFish.Pattern> BY_DATA = new HashMap();

      public static TropicalFish.Pattern fromData(int data) {
         return (TropicalFish.Pattern)BY_DATA.get(data);
      }

      private CraftPattern(int variant, boolean large) {
         this.variant = variant;
         this.large = large;
      }

      public int getDataValue() {
         return this.variant << 8 | (this.large ? 1 : 0);
      }

      // $FF: synthetic method
      private static CraftPattern[] $values() {
         return new CraftPattern[]{KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH};
      }

      static {
         CraftPattern[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            CraftPattern type = var0[var2];
            BY_DATA.put(type.getDataValue(), Pattern.values()[type.ordinal()]);
         }

      }
   }
}
