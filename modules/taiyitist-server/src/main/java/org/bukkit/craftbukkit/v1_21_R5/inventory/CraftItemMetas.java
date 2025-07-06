package org.bukkit.craftbukkit.v1_21_R5.inventory;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.ShieldMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

public final class CraftItemMetas {
   private static final ItemMetaData<ItemMeta> EMPTY_META_DATA = new ItemMetaData(ItemMeta.class, (item) -> {
      return null;
   }, (type, meta) -> {
      return null;
   });
   private static final ItemMetaData<ItemMeta> ITEM_META_DATA = new ItemMetaData(ItemMeta.class, (item) -> {
      return new CraftMetaItem(item.getComponentsPatch());
   }, (type, meta) -> {
      return new CraftMetaItem(meta);
   });
   private static final ItemMetaData<BookMeta> SIGNED_BOOK_META_DATA = new ItemMetaData(BookMeta.class, (item) -> {
      return new CraftMetaBookSigned(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaBookSigned var10000;
      if (meta instanceof CraftMetaBookSigned signed) {
         var10000 = signed;
      } else {
         var10000 = new CraftMetaBookSigned(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<BookMeta> WRITABLE_BOOK_META_DATA = new ItemMetaData(BookMeta.class, (item) -> {
      return new CraftMetaBook(item.getComponentsPatch());
   }, (type, meta) -> {
      return (BookMeta)(meta != null && meta.getClass().equals(CraftMetaBook.class) ? (BookMeta)meta : new CraftMetaBook(meta));
   });
   private static final ItemMetaData<SkullMeta> SKULL_META_DATA = new ItemMetaData(SkullMeta.class, (item) -> {
      return new CraftMetaSkull(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaSkull var10000;
      if (meta instanceof CraftMetaSkull skull) {
         var10000 = skull;
      } else {
         var10000 = new CraftMetaSkull(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<ArmorMeta> ARMOR_META_DATA = new ItemMetaData(ArmorMeta.class, (item) -> {
      return new CraftMetaArmor(item.getComponentsPatch());
   }, (type, meta) -> {
      return (ArmorMeta)(meta != null && meta.getClass().equals(CraftMetaArmor.class) ? (ArmorMeta)meta : new CraftMetaArmor(meta));
   });
   private static final ItemMetaData<ColorableArmorMeta> COLORABLE_ARMOR_META_DATA = new ItemMetaData(ColorableArmorMeta.class, (item) -> {
      return new CraftMetaColorableArmor(item.getComponentsPatch());
   }, (type, meta) -> {
      Object var10000;
      if (meta instanceof ColorableArmorMeta colorable) {
         var10000 = colorable;
      } else {
         var10000 = new CraftMetaColorableArmor(meta);
      }

      return (ColorableArmorMeta)var10000;
   });
   private static final ItemMetaData<LeatherArmorMeta> LEATHER_ARMOR_META_DATA = new ItemMetaData(LeatherArmorMeta.class, (item) -> {
      return new CraftMetaLeatherArmor(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaLeatherArmor var10000;
      if (meta instanceof CraftMetaLeatherArmor leather) {
         var10000 = leather;
      } else {
         var10000 = new CraftMetaLeatherArmor(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<PotionMeta> POTION_META_DATA = new ItemMetaData(PotionMeta.class, (item) -> {
      return new CraftMetaPotion(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaPotion var10000;
      if (meta instanceof CraftMetaPotion potion) {
         var10000 = potion;
      } else {
         var10000 = new CraftMetaPotion(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<MapMeta> MAP_META_DATA = new ItemMetaData(MapMeta.class, (item) -> {
      return new CraftMetaMap(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaMap var10000;
      if (meta instanceof CraftMetaMap map) {
         var10000 = map;
      } else {
         var10000 = new CraftMetaMap(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<FireworkMeta> FIREWORK_META_DATA = new ItemMetaData(FireworkMeta.class, (item) -> {
      return new CraftMetaFirework(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaFirework var10000;
      if (meta instanceof CraftMetaFirework firework) {
         var10000 = firework;
      } else {
         var10000 = new CraftMetaFirework(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<FireworkEffectMeta> CHARGE_META_DATA = new ItemMetaData(FireworkEffectMeta.class, (item) -> {
      return new CraftMetaCharge(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaCharge var10000;
      if (meta instanceof CraftMetaCharge charge) {
         var10000 = charge;
      } else {
         var10000 = new CraftMetaCharge(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<EnchantmentStorageMeta> ENCHANTED_BOOK_META_DATA = new ItemMetaData(EnchantmentStorageMeta.class, (item) -> {
      return new CraftMetaEnchantedBook(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaEnchantedBook var10000;
      if (meta instanceof CraftMetaEnchantedBook enchantedBook) {
         var10000 = enchantedBook;
      } else {
         var10000 = new CraftMetaEnchantedBook(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<BannerMeta> BANNER_META_DATA = new ItemMetaData(BannerMeta.class, (item) -> {
      return new CraftMetaBanner(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaBanner var10000;
      if (meta instanceof CraftMetaBanner banner) {
         var10000 = banner;
      } else {
         var10000 = new CraftMetaBanner(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<SpawnEggMeta> SPAWN_EGG_META_DATA = new ItemMetaData(SpawnEggMeta.class, (item) -> {
      return new CraftMetaSpawnEgg(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaSpawnEgg var10000;
      if (meta instanceof CraftMetaSpawnEgg spawnEgg) {
         var10000 = spawnEgg;
      } else {
         var10000 = new CraftMetaSpawnEgg(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<ItemMeta> ARMOR_STAND_META_DATA = new ItemMetaData(ItemMeta.class, (item) -> {
      return new CraftMetaArmorStand(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaArmorStand var10000;
      if (meta instanceof CraftMetaArmorStand armorStand) {
         var10000 = armorStand;
      } else {
         var10000 = new CraftMetaArmorStand(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<KnowledgeBookMeta> KNOWLEDGE_BOOK_META_DATA = new ItemMetaData(KnowledgeBookMeta.class, (item) -> {
      return new CraftMetaKnowledgeBook(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaKnowledgeBook var10000;
      if (meta instanceof CraftMetaKnowledgeBook knowledgeBook) {
         var10000 = knowledgeBook;
      } else {
         var10000 = new CraftMetaKnowledgeBook(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<BlockStateMeta> BLOCK_STATE_META_DATA = new ItemMetaData(BlockStateMeta.class, (item) -> {
      return new CraftMetaBlockState(item.getComponentsPatch(), CraftItemType.minecraftToBukkit(item.getItem()));
   }, (type, meta) -> {
      return new CraftMetaBlockState(meta, type.asMaterial());
   });
   private static final ItemMetaData<ShieldMeta> SHIELD_META_DATA = new ItemMetaData(ShieldMeta.class, (item) -> {
      return new CraftMetaShield(item.getComponentsPatch());
   }, (type, meta) -> {
      return new CraftMetaShield(meta);
   });
   private static final ItemMetaData<TropicalFishBucketMeta> TROPICAL_FISH_BUCKET_META_DATA = new ItemMetaData(TropicalFishBucketMeta.class, (item) -> {
      return new CraftMetaTropicalFishBucket(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaTropicalFishBucket var10000;
      if (meta instanceof CraftMetaTropicalFishBucket tropicalFishBucket) {
         var10000 = tropicalFishBucket;
      } else {
         var10000 = new CraftMetaTropicalFishBucket(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<AxolotlBucketMeta> AXOLOTL_BUCKET_META_DATA = new ItemMetaData(AxolotlBucketMeta.class, (item) -> {
      return new CraftMetaAxolotlBucket(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaAxolotlBucket var10000;
      if (meta instanceof CraftMetaAxolotlBucket axolotlBucket) {
         var10000 = axolotlBucket;
      } else {
         var10000 = new CraftMetaAxolotlBucket(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<CrossbowMeta> CROSSBOW_META_DATA = new ItemMetaData(CrossbowMeta.class, (item) -> {
      return new CraftMetaCrossbow(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaCrossbow var10000;
      if (meta instanceof CraftMetaCrossbow crossbow) {
         var10000 = crossbow;
      } else {
         var10000 = new CraftMetaCrossbow(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<SuspiciousStewMeta> SUSPICIOUS_STEW_META_DATA = new ItemMetaData(SuspiciousStewMeta.class, (item) -> {
      return new CraftMetaSuspiciousStew(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaSuspiciousStew var10000;
      if (meta instanceof CraftMetaSuspiciousStew suspiciousStew) {
         var10000 = suspiciousStew;
      } else {
         var10000 = new CraftMetaSuspiciousStew(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<ItemMeta> ENTITY_TAG_META_DATA = new ItemMetaData(ItemMeta.class, (item) -> {
      return new CraftMetaEntityTag(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaEntityTag var10000;
      if (meta instanceof CraftMetaEntityTag entityTag) {
         var10000 = entityTag;
      } else {
         var10000 = new CraftMetaEntityTag(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<CompassMeta> COMPASS_META_DATA = new ItemMetaData(CompassMeta.class, (item) -> {
      return new CraftMetaCompass(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaCompass var10000;
      if (meta instanceof CraftMetaCompass compass) {
         var10000 = compass;
      } else {
         var10000 = new CraftMetaCompass(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<BundleMeta> BUNDLE_META_DATA = new ItemMetaData(BundleMeta.class, (item) -> {
      return new CraftMetaBundle(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaBundle var10000;
      if (meta instanceof CraftMetaBundle bundle) {
         var10000 = bundle;
      } else {
         var10000 = new CraftMetaBundle(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<MusicInstrumentMeta> MUSIC_INSTRUMENT_META_DATA = new ItemMetaData(MusicInstrumentMeta.class, (item) -> {
      return new CraftMetaMusicInstrument(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaMusicInstrument var10000;
      if (meta instanceof CraftMetaMusicInstrument musicInstrument) {
         var10000 = musicInstrument;
      } else {
         var10000 = new CraftMetaMusicInstrument(meta);
      }

      return var10000;
   });
   private static final ItemMetaData<OminousBottleMeta> OMINOUS_BOTTLE_META_DATA = new ItemMetaData(OminousBottleMeta.class, (item) -> {
      return new CraftMetaOminousBottle(item.getComponentsPatch());
   }, (type, meta) -> {
      CraftMetaOminousBottle var10000;
      if (meta instanceof CraftMetaOminousBottle musicInstrument) {
         var10000 = musicInstrument;
      } else {
         var10000 = new CraftMetaOminousBottle(meta);
      }

      return var10000;
   });

   public static <I extends ItemMeta> ItemMetaData<I> getItemMetaData(CraftItemType<?> itemType) {
      Item itemHandle = (Item)itemType.getHandle();
      Block var10000;
      if (itemHandle instanceof BlockItem itemBlock) {
         var10000 = itemBlock.getBlock();
      } else {
         var10000 = null;
      }

      Block blockHandle = var10000;
      if (itemType == ItemType.AIR) {
         return asType(EMPTY_META_DATA);
      } else if (itemType == ItemType.WRITTEN_BOOK) {
         return asType(SIGNED_BOOK_META_DATA);
      } else if (itemType == ItemType.WRITABLE_BOOK) {
         return asType(WRITABLE_BOOK_META_DATA);
      } else if (itemType != ItemType.CREEPER_HEAD && itemType != ItemType.DRAGON_HEAD && itemType != ItemType.PIGLIN_HEAD && itemType != ItemType.PLAYER_HEAD && itemType != ItemType.SKELETON_SKULL && itemType != ItemType.WITHER_SKELETON_SKULL && itemType != ItemType.ZOMBIE_HEAD) {
         if (itemType != ItemType.CHAINMAIL_HELMET && itemType != ItemType.CHAINMAIL_CHESTPLATE && itemType != ItemType.CHAINMAIL_LEGGINGS && itemType != ItemType.CHAINMAIL_BOOTS && itemType != ItemType.DIAMOND_HELMET && itemType != ItemType.DIAMOND_CHESTPLATE && itemType != ItemType.DIAMOND_LEGGINGS && itemType != ItemType.DIAMOND_BOOTS && itemType != ItemType.GOLDEN_HELMET && itemType != ItemType.GOLDEN_CHESTPLATE && itemType != ItemType.GOLDEN_LEGGINGS && itemType != ItemType.GOLDEN_BOOTS && itemType != ItemType.IRON_HELMET && itemType != ItemType.IRON_CHESTPLATE && itemType != ItemType.IRON_LEGGINGS && itemType != ItemType.IRON_BOOTS && itemType != ItemType.NETHERITE_HELMET && itemType != ItemType.NETHERITE_CHESTPLATE && itemType != ItemType.NETHERITE_LEGGINGS && itemType != ItemType.NETHERITE_BOOTS && itemType != ItemType.TURTLE_HELMET) {
            if (itemType != ItemType.LEATHER_HELMET && itemType != ItemType.LEATHER_CHESTPLATE && itemType != ItemType.LEATHER_LEGGINGS && itemType != ItemType.LEATHER_BOOTS && itemType != ItemType.WOLF_ARMOR) {
               if (itemType == ItemType.LEATHER_HORSE_ARMOR) {
                  return asType(LEATHER_ARMOR_META_DATA);
               } else if (itemType != ItemType.POTION && itemType != ItemType.SPLASH_POTION && itemType != ItemType.LINGERING_POTION && itemType != ItemType.TIPPED_ARROW) {
                  if (itemType == ItemType.FILLED_MAP) {
                     return asType(MAP_META_DATA);
                  } else if (itemType == ItemType.FIREWORK_ROCKET) {
                     return asType(FIREWORK_META_DATA);
                  } else if (itemType == ItemType.FIREWORK_STAR) {
                     return asType(CHARGE_META_DATA);
                  } else if (itemType == ItemType.ENCHANTED_BOOK) {
                     return asType(ENCHANTED_BOOK_META_DATA);
                  } else if (itemHandle instanceof BannerItem) {
                     return asType(BANNER_META_DATA);
                  } else if (itemHandle instanceof SpawnEggItem) {
                     return asType(SPAWN_EGG_META_DATA);
                  } else if (itemType == ItemType.ARMOR_STAND) {
                     return asType(ARMOR_STAND_META_DATA);
                  } else if (itemType == ItemType.KNOWLEDGE_BOOK) {
                     return asType(KNOWLEDGE_BOOK_META_DATA);
                  } else if (itemType != ItemType.FURNACE && itemType != ItemType.CHEST && itemType != ItemType.TRAPPED_CHEST && itemType != ItemType.JUKEBOX && itemType != ItemType.DISPENSER && itemType != ItemType.DROPPER && !(itemHandle instanceof SignItem) && itemType != ItemType.SPAWNER && itemType != ItemType.BREWING_STAND && itemType != ItemType.ENCHANTING_TABLE && itemType != ItemType.COMMAND_BLOCK && itemType != ItemType.REPEATING_COMMAND_BLOCK && itemType != ItemType.CHAIN_COMMAND_BLOCK && itemType != ItemType.BEACON && itemType != ItemType.DAYLIGHT_DETECTOR && itemType != ItemType.HOPPER && itemType != ItemType.COMPARATOR && itemType != ItemType.STRUCTURE_BLOCK && !(blockHandle instanceof ShulkerBoxBlock) && itemType != ItemType.ENDER_CHEST && itemType != ItemType.BARREL && itemType != ItemType.BELL && itemType != ItemType.BLAST_FURNACE && itemType != ItemType.CAMPFIRE && itemType != ItemType.SOUL_CAMPFIRE && itemType != ItemType.JIGSAW && itemType != ItemType.LECTERN && itemType != ItemType.SMOKER && itemType != ItemType.BEEHIVE && itemType != ItemType.BEE_NEST && itemType != ItemType.SCULK_CATALYST && itemType != ItemType.SCULK_SHRIEKER && itemType != ItemType.SCULK_SENSOR && itemType != ItemType.CALIBRATED_SCULK_SENSOR && itemType != ItemType.CHISELED_BOOKSHELF && itemType != ItemType.DECORATED_POT && itemType != ItemType.SUSPICIOUS_SAND && itemType != ItemType.SUSPICIOUS_GRAVEL && itemType != ItemType.CRAFTER && itemType != ItemType.TRIAL_SPAWNER && itemType != ItemType.VAULT && itemType != ItemType.CREAKING_HEART && itemType != ItemType.TEST_BLOCK && itemType != ItemType.TEST_INSTANCE_BLOCK) {
                     if (itemType == ItemType.SHIELD) {
                        return asType(SHIELD_META_DATA);
                     } else if (itemType == ItemType.TROPICAL_FISH_BUCKET) {
                        return asType(TROPICAL_FISH_BUCKET_META_DATA);
                     } else if (itemType == ItemType.AXOLOTL_BUCKET) {
                        return asType(AXOLOTL_BUCKET_META_DATA);
                     } else if (itemType == ItemType.CROSSBOW) {
                        return asType(CROSSBOW_META_DATA);
                     } else if (itemType == ItemType.SUSPICIOUS_STEW) {
                        return asType(SUSPICIOUS_STEW_META_DATA);
                     } else if (itemType != ItemType.COD_BUCKET && itemType != ItemType.PUFFERFISH_BUCKET && itemType != ItemType.SALMON_BUCKET && itemType != ItemType.ITEM_FRAME && itemType != ItemType.GLOW_ITEM_FRAME && itemType != ItemType.PAINTING) {
                        if (itemType == ItemType.COMPASS) {
                           return asType(COMPASS_META_DATA);
                        } else if (itemHandle instanceof BundleItem) {
                           return asType(BUNDLE_META_DATA);
                        } else if (itemType == ItemType.GOAT_HORN) {
                           return asType(MUSIC_INSTRUMENT_META_DATA);
                        } else {
                           return itemType == ItemType.OMINOUS_BOTTLE ? asType(OMINOUS_BOTTLE_META_DATA) : asType(ITEM_META_DATA);
                        }
                     } else {
                        return asType(ENTITY_TAG_META_DATA);
                     }
                  } else {
                     return asType(BLOCK_STATE_META_DATA);
                  }
               } else {
                  return asType(POTION_META_DATA);
               }
            } else {
               return asType(COLORABLE_ARMOR_META_DATA);
            }
         } else {
            return asType(ARMOR_META_DATA);
         }
      } else {
         return asType(SKULL_META_DATA);
      }
   }

   private static <I extends ItemMeta> ItemMetaData<I> asType(ItemMetaData<?> metaData) {
      return metaData;
   }

   private CraftItemMetas() {
   }

   public static record ItemMetaData<I extends ItemMeta>(Class<I> metaClass, Function<ItemStack, I> fromItemStack, BiFunction<ItemType.Typed<I>, CraftMetaItem, I> fromItemMeta) {
      public ItemMetaData(Class<I> metaClass, Function<ItemStack, I> fromItemStack, BiFunction<ItemType.Typed<I>, CraftMetaItem, I> fromItemMeta) {
         this.metaClass = metaClass;
         this.fromItemStack = fromItemStack;
         this.fromItemMeta = fromItemMeta;
      }

      public Class<I> metaClass() {
         return this.metaClass;
      }

      public Function<ItemStack, I> fromItemStack() {
         return this.fromItemStack;
      }

      public BiFunction<ItemType.Typed<I>, CraftMetaItem, I> fromItemMeta() {
         return this.fromItemMeta;
      }
   }
}
