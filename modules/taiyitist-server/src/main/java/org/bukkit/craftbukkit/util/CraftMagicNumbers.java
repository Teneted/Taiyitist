package org.bukkit.craftbukkit.util;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.Bukkit;
import org.bukkit.FeatureFlag;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.UnsafeValues;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.legacy.CraftLegacy;
import org.bukkit.craftbukkit.CraftFeatureFlag;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.damage.CraftDamageEffect;
import org.bukkit.craftbukkit.damage.CraftDamageSourceBuilder;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.potion.CraftPotionType;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.potion.PotionType;

public final class CraftMagicNumbers implements UnsafeValues {
   public static final CraftMagicNumbers INSTANCE = new CraftMagicNumbers();
   private final Commodore commodore = new Commodore();
   private static final Map<Block, Material> BLOCK_MATERIAL = new HashMap();
   private static final Map<Item, Material> ITEM_MATERIAL = new HashMap();
   private static final Map<Material, Item> MATERIAL_ITEM = new HashMap();
   private static final Map<Material, Block> MATERIAL_BLOCK = new HashMap();
   private Biome customBiome;

   private CraftMagicNumbers() {
   }

   public static BlockState getBlock(MaterialData material) {
      return getBlock(material.getItemType(), material.getData());
   }

   public static BlockState getBlock(Material material, byte data) {
      return CraftLegacy.fromLegacyData(CraftLegacy.toLegacy(material), data);
   }

   public static MaterialData getMaterial(BlockState data) {
      return CraftLegacy.toLegacy(getMaterial(data.getBlock())).getNewData(toLegacyData(data));
   }

   public static Item getItem(Material material, short data) {
      return material.isLegacy() ? CraftLegacy.fromLegacyData(CraftLegacy.toLegacy(material), data) : getItem(material);
   }

   public static MaterialData getMaterialData(Item item) {
      return CraftLegacy.toLegacyData(getMaterial(item));
   }

   public static Material getMaterial(Block block) {
      return (Material)BLOCK_MATERIAL.get(block);
   }

   public static Material getMaterial(Item item) {
      return (Material)ITEM_MATERIAL.getOrDefault(item, Material.AIR);
   }

   public static Item getItem(Material material) {
      if (material != null && material.isLegacy()) {
         material = CraftLegacy.fromLegacy(material);
      }

      return (Item)MATERIAL_ITEM.get(material);
   }

   public static Block getBlock(Material material) {
      if (material != null && material.isLegacy()) {
         material = CraftLegacy.fromLegacy(material);
      }

      return (Block)MATERIAL_BLOCK.get(material);
   }

   public static ResourceLocation key(Material mat) {
      return CraftNamespacedKey.toMinecraft(mat.getKey());
   }

   public static byte toLegacyData(BlockState data) {
      return CraftLegacy.toLegacyData(data);
   }

   public Commodore getCommodore() {
      return this.commodore;
   }

   public Material toLegacy(Material material) {
      return CraftLegacy.toLegacy(material);
   }

   public Material fromLegacy(Material material) {
      return CraftLegacy.fromLegacy(material);
   }

   public Material fromLegacy(MaterialData material) {
      return CraftLegacy.fromLegacy(material);
   }

   public Material fromLegacy(MaterialData material, boolean itemPriority) {
      return CraftLegacy.fromLegacy(material, itemPriority);
   }

   public BlockData fromLegacy(Material material, byte data) {
      return CraftBlockData.fromData(getBlock(material, data));
   }

   public Material getMaterial(String material, int version) {
      Preconditions.checkArgument(material != null, "material == null");
      Preconditions.checkArgument(version <= this.getDataVersion(), "Newer version! Server downgrades are not supported!");
      if (version == this.getDataVersion()) {
         return Material.getMaterial(material);
      } else {
         Dynamic<Tag> name = new Dynamic(NbtOps.INSTANCE, StringTag.valueOf("minecraft:" + material.toLowerCase(Locale.ROOT)));
         Dynamic<Tag> converted = DataFixers.getDataFixer().update(References.ITEM_NAME, name, version, this.getDataVersion());
         if (name.equals(converted)) {
            converted = DataFixers.getDataFixer().update(References.BLOCK_NAME, name, version, this.getDataVersion());
         }

         return Material.matchMaterial(converted.asString(""));
      }
   }

   public String getMappingsVersion() {
      return "98b42190c84edaa346fd96106ee35d6f";
   }

   public int getDataVersion() {
      return SharedConstants.getCurrentVersion().dataVersion().version();
   }

   public ItemStack modifyItemStack(ItemStack stack, String arguments) {
      net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

      try {
         nmsStack.applyComponents((new ItemParser(Commands.createValidationContext(MinecraftServer.getDefaultRegistryAccess()))).parse(new StringReader(arguments)).components());
      } catch (CommandSyntaxException var5) {
         CommandSyntaxException ex = var5;
         Logger.getLogger(CraftMagicNumbers.class.getName()).log(Level.SEVERE, (String)null, ex);
      }

      stack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));
      return stack;
   }

   private static File getBukkitDataPackFolder() {
      return new File(MinecraftServer.getServer().getWorldPath(LevelResource.DATAPACK_DIR).toFile(), "bukkit");
   }

   public Advancement loadAdvancement(NamespacedKey key, String advancement) {
      Preconditions.checkArgument(Bukkit.getAdvancement(key) == null, "Advancement %s already exists", key);
      ResourceLocation minecraftkey = CraftNamespacedKey.toMinecraft(key);
      JsonElement jsonelement = JsonParser.parseString(advancement);
      net.minecraft.advancements.Advancement nms = (net.minecraft.advancements.Advancement)net.minecraft.advancements.Advancement.CODEC.parse(JsonOps.INSTANCE, jsonelement).getOrThrow(JsonParseException::new);
      if (nms != null) {
         MinecraftServer.getServer().getAdvancements().advancements.put(minecraftkey, new AdvancementHolder(minecraftkey, nms));
         Advancement bukkit = Bukkit.getAdvancement(key);
         if (bukkit != null) {
            File var10002 = getBukkitDataPackFolder();
            String var10003 = File.separator;
            File file = new File(var10002, "data" + var10003 + key.getNamespace() + File.separator + "advancements" + File.separator + key.getKey() + ".json");
            file.getParentFile().mkdirs();

            try {
               Files.write(advancement, file, Charsets.UTF_8);
            } catch (IOException var9) {
               IOException ex = var9;
               Bukkit.getLogger().log(Level.SEVERE, "Error saving advancement " + String.valueOf(key), ex);
            }

            MinecraftServer.getServer().getPlayerList().reloadResources();
            return bukkit;
         }
      }

      return null;
   }

   public boolean removeAdvancement(NamespacedKey key) {
      File var10002 = getBukkitDataPackFolder();
      String var10003 = File.separator;
      File file = new File(var10002, "data" + var10003 + key.getNamespace() + File.separator + "advancements" + File.separator + key.getKey() + ".json");
      return file.delete();
   }

   public void checkSupported(PluginDescriptionFile pdf) throws InvalidPluginException {
      ApiVersion toCheck = ApiVersion.getOrCreateVersion(pdf.getAPIVersion());
      ApiVersion minimumVersion = MinecraftServer.getServer().server.minimumAPI;
      if (toCheck.isNewerThan(ApiVersion.CURRENT)) {
         throw new InvalidPluginException("Unsupported API version " + pdf.getAPIVersion());
      } else if (toCheck.isOlderThan(minimumVersion)) {
         throw new InvalidPluginException("Plugin API version " + pdf.getAPIVersion() + " is lower than the minimum allowed version. Please update or replace it.");
      } else {
         if (toCheck.isOlderThan(ApiVersion.FLATTENING)) {
            CraftLegacy.init();
         }

         if (toCheck == ApiVersion.NONE) {
            Bukkit.getLogger().log(Level.WARNING, "Legacy plugin " + pdf.getFullName() + " does not specify an api-version.");
         }

      }
   }

   public static boolean isLegacy(PluginDescriptionFile pdf) {
      return pdf.getAPIVersion() == null;
   }

   public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
      try {
         clazz = this.commodore.convert(clazz, pdf.getName(), ApiVersion.getOrCreateVersion(pdf.getAPIVersion()), ((CraftServer)Bukkit.getServer()).activeCompatibilities);
      } catch (Exception var5) {
         Exception ex = var5;
         Bukkit.getLogger().log(Level.SEVERE, "Fatal error trying to convert " + pdf.getFullName() + ":" + path, ex);
      }

      return clazz;
   }

   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(Material material, EquipmentSlot slot) {
      return material.getDefaultAttributeModifiers(slot);
   }

   public CreativeCategory getCreativeCategory(Material material) {
      return material.getCreativeCategory();
   }

   public String getBlockTranslationKey(Material material) {
      return material.getBlockTranslationKey();
   }

   public String getItemTranslationKey(Material material) {
      return material.getItemTranslationKey();
   }

   public String getTranslationKey(EntityType entityType) {
      Preconditions.checkArgument(entityType.getName() != null, "Invalid name of EntityType %s for translation key", entityType);
      return (String)net.minecraft.world.entity.EntityType.byString(entityType.getName()).map(net.minecraft.world.entity.EntityType::getDescriptionId).orElseThrow();
   }

   public String getTranslationKey(ItemStack itemStack) {
      net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      return nmsItemStack.getItem().getDescriptionId();
   }

   public String getTranslationKey(Attribute attribute) {
      return attribute.getTranslationKey();
   }

   public FeatureFlag getFeatureFlag(NamespacedKey namespacedKey) {
      Preconditions.checkArgument(namespacedKey != null, "NamespaceKey cannot be null");
      return CraftFeatureFlag.getFromNMS(namespacedKey);
   }

   public PotionType.InternalPotionData getInternalPotionData(NamespacedKey namespacedKey) {
      Potion potionRegistry = (Potion)CraftRegistry.getMinecraftRegistry(Registries.POTION).getOptional(CraftNamespacedKey.toMinecraft(namespacedKey)).orElseThrow();
      return new CraftPotionType(namespacedKey, potionRegistry);
   }

   public DamageEffect getDamageEffect(String key) {
      Preconditions.checkArgument(key != null, "key cannot be null");
      return CraftDamageEffect.getById(key);
   }

   public DamageSource.Builder createDamageSourceBuilder(DamageType damageType) {
      return new CraftDamageSourceBuilder(damageType);
   }

   public String get(Class<?> aClass, String s) {
      return aClass == Enchantment.class ? FieldRename.convertEnchantmentName(ApiVersion.CURRENT, s) : s;
   }

   public <B extends Keyed> B get(Registry<B> registry, NamespacedKey namespacedKey) {
      return CraftRegistry.get(registry, namespacedKey, ApiVersion.CURRENT);
   }

   public Biome getCustomBiome() {
      if (this.customBiome == null) {
         this.customBiome = new CraftBiome(NamespacedKey.minecraft("custom"), (Holder)null);
      }

      return this.customBiome;
   }

   public Villager.ReputationType createReputationType(String key) {
      return (Villager.ReputationType)Optional.ofNullable((CraftVillager.CraftReputationType)CraftVillager.CraftReputationType.BY_ID.get(key)).orElseThrow(() -> {
         return new IllegalArgumentException("Invalid ReputationType key: " + key);
      });
   }

   public Villager.ReputationEvent createReputationEvent(String key) {
      return (Villager.ReputationEvent)Optional.ofNullable((ReputationEventType)ReputationEventType.BY_ID.get(key)).map(CraftVillager.CraftReputationEvent::new).orElseThrow(() -> {
         return new IllegalArgumentException("Invalid ReputationEvent key: " + key);
      });
   }

   static {
      Iterator var0 = BuiltInRegistries.BLOCK.iterator();

      while(var0.hasNext()) {
         Block block = (Block)var0.next();
         BLOCK_MATERIAL.put(block, Material.getMaterial(BuiltInRegistries.BLOCK.getKey(block).getPath().toUpperCase(Locale.ROOT)));
      }

      var0 = BuiltInRegistries.ITEM.iterator();

      while(var0.hasNext()) {
         Item item = (Item)var0.next();
         ITEM_MATERIAL.put(item, Material.getMaterial(BuiltInRegistries.ITEM.getKey(item).getPath().toUpperCase(Locale.ROOT)));
      }

      Material[] var5 = Material.values();
      int var7 = var5.length;

      for(int var2 = 0; var2 < var7; ++var2) {
         Material material = var5[var2];
         if (!material.isLegacy()) {
            ResourceLocation key = key(material);
            BuiltInRegistries.ITEM.getOptional(key).ifPresent((itemx) -> {
               MATERIAL_ITEM.put(material, itemx);
            });
            BuiltInRegistries.BLOCK.getOptional(key).ifPresent((blockx) -> {
               MATERIAL_BLOCK.put(material, blockx);
            });
         }
      }

   }

   public static class NBT {
      public static final int TAG_END = 0;
      public static final int TAG_BYTE = 1;
      public static final int TAG_SHORT = 2;
      public static final int TAG_INT = 3;
      public static final int TAG_LONG = 4;
      public static final int TAG_FLOAT = 5;
      public static final int TAG_DOUBLE = 6;
      public static final int TAG_BYTE_ARRAY = 7;
      public static final int TAG_STRING = 8;
      public static final int TAG_LIST = 9;
      public static final int TAG_COMPOUND = 10;
      public static final int TAG_INT_ARRAY = 11;
      public static final int TAG_ANY_NUMBER = 99;
   }
}
