package org.bukkit.craftbukkit.block.banner;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.registry.CraftOldEnumRegistryItem;

public class CraftPatternType extends CraftOldEnumRegistryItem<PatternType, BannerPattern> implements PatternType {
   private static int count = 0;

   public static PatternType minecraftToBukkit(BannerPattern minecraft) {
      return (PatternType)CraftRegistry.minecraftToBukkit(minecraft, Registries.BANNER_PATTERN, Registry.BANNER_PATTERN);
   }

   public static PatternType minecraftHolderToBukkit(Holder<BannerPattern> minecraft) {
      return minecraftToBukkit((BannerPattern)minecraft.value());
   }

   public static BannerPattern bukkitToMinecraft(PatternType bukkit) {
      return (BannerPattern)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<BannerPattern> bukkitToMinecraftHolder(PatternType bukkit) {
      Preconditions.checkArgument(bukkit != null);
      net.minecraft.core.Registry<BannerPattern> registry = CraftRegistry.getMinecraftRegistry(Registries.BANNER_PATTERN);
      if (registry.wrapAsHolder(bukkitToMinecraft(bukkit)) instanceof Holder.Reference<BannerPattern> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own banner pattern without properly registering it.");
      }
   }

   public CraftPatternType(NamespacedKey key, Holder<BannerPattern> handle) {
      super(key, handle, count++);
   }

   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   public String getIdentifier() {
      String var10000;
      switch (this.name()) {
         case "BASE" -> var10000 = "b";
         case "SQUARE_BOTTOM_LEFT" -> var10000 = "bl";
         case "SQUARE_BOTTOM_RIGHT" -> var10000 = "br";
         case "SQUARE_TOP_LEFT" -> var10000 = "tl";
         case "SQUARE_TOP_RIGHT" -> var10000 = "tr";
         case "STRIPE_BOTTOM" -> var10000 = "bs";
         case "STRIPE_TOP" -> var10000 = "ts";
         case "STRIPE_LEFT" -> var10000 = "ls";
         case "STRIPE_RIGHT" -> var10000 = "rs";
         case "STRIPE_CENTER" -> var10000 = "cs";
         case "STRIPE_MIDDLE" -> var10000 = "ms";
         case "STRIPE_DOWNRIGHT" -> var10000 = "drs";
         case "STRIPE_DOWNLEFT" -> var10000 = "dls";
         case "SMALL_STRIPES" -> var10000 = "ss";
         case "CROSS" -> var10000 = "cr";
         case "STRAIGHT_CROSS" -> var10000 = "sc";
         case "TRIANGLE_BOTTOM" -> var10000 = "bt";
         case "TRIANGLE_TOP" -> var10000 = "tt";
         case "TRIANGLES_BOTTOM" -> var10000 = "bts";
         case "TRIANGLES_TOP" -> var10000 = "tts";
         case "DIAGONAL_LEFT" -> var10000 = "ld";
         case "DIAGONAL_UP_RIGHT" -> var10000 = "rd";
         case "DIAGONAL_UP_LEFT" -> var10000 = "lud";
         case "DIAGONAL_RIGHT" -> var10000 = "rud";
         case "CIRCLE" -> var10000 = "mc";
         case "RHOMBUS" -> var10000 = "mr";
         case "HALF_VERTICAL" -> var10000 = "vh";
         case "HALF_HORIZONTAL" -> var10000 = "hh";
         case "HALF_VERTICAL_RIGHT" -> var10000 = "vhr";
         case "HALF_HORIZONTAL_BOTTOM" -> var10000 = "hhb";
         case "BORDER" -> var10000 = "bo";
         case "CURLY_BORDER" -> var10000 = "cbo";
         case "CREEPER" -> var10000 = "cre";
         case "GRADIENT" -> var10000 = "gra";
         case "GRADIENT_UP" -> var10000 = "gru";
         case "BRICKS" -> var10000 = "bri";
         case "SKULL" -> var10000 = "sku";
         case "FLOWER" -> var10000 = "flo";
         case "MOJANG" -> var10000 = "moj";
         case "GLOBE" -> var10000 = "glb";
         case "PIGLIN" -> var10000 = "pig";
         case "FLOW" -> var10000 = "flw";
         case "GUSTER" -> var10000 = "gus";
         default -> var10000 = this.getKey().toString();
      }

      return var10000;
   }
}
