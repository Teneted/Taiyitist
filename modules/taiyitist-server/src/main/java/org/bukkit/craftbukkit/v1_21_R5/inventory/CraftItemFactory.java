package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntityType;
import org.bukkit.craftbukkit.v1_21_R5.inventory.components.CraftBlocksAttacksComponent;
import org.bukkit.craftbukkit.v1_21_R5.inventory.components.CraftCustomModelDataComponent;
import org.bukkit.craftbukkit.v1_21_R5.inventory.components.CraftEquippableComponent;
import org.bukkit.craftbukkit.v1_21_R5.inventory.components.CraftFoodComponent;
import org.bukkit.craftbukkit.v1_21_R5.inventory.components.CraftJukeboxComponent;
import org.bukkit.craftbukkit.v1_21_R5.inventory.components.CraftToolComponent;
import org.bukkit.craftbukkit.v1_21_R5.inventory.components.CraftUseCooldownComponent;
import org.bukkit.craftbukkit.v1_21_R5.inventory.components.CraftWeaponComponent;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftLegacy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftItemFactory implements ItemFactory {
   static final Color DEFAULT_LEATHER_COLOR = Color.fromRGB(10511680);
   private static final CraftItemFactory instance = new CraftItemFactory();
   private static final RandomSource randomSource = RandomSource.create();

   private CraftItemFactory() {
   }

   public boolean isApplicable(ItemMeta meta, ItemStack itemstack) {
      return itemstack == null ? false : this.isApplicable(meta, itemstack.getType());
   }

   public boolean isApplicable(ItemMeta meta, Material type) {
      type = CraftLegacy.fromLegacy(type);
      if (type != null && meta != null) {
         Preconditions.checkArgument(meta instanceof CraftMetaItem, "Meta of %s not created by %s", meta.getClass().toString(), CraftItemFactory.class.getName());
         return ((CraftMetaItem)meta).applicableTo(type);
      } else {
         return false;
      }
   }

   public ItemMeta getItemMeta(Material material) {
      Preconditions.checkArgument(material != null, "Material cannot be null");
      return this.getItemMeta(material, (CraftMetaItem)null);
   }

   private ItemMeta getItemMeta(Material material, CraftMetaItem meta) {
      material = CraftLegacy.fromLegacy(material);
      return (ItemMeta)(!material.isItem() ? new CraftMetaItem(meta) : ((CraftItemType)material.asItemType()).getItemMeta((ItemMeta)meta));
   }

   public boolean equals(ItemMeta meta1, ItemMeta meta2) {
      if (meta1 == meta2) {
         return true;
      } else if (meta1 != null) {
         Preconditions.checkArgument(meta1 instanceof CraftMetaItem, "First meta of %s does not belong to %s", meta1.getClass().getName(), CraftItemFactory.class.getName());
         if (meta2 != null) {
            Preconditions.checkArgument(meta2 instanceof CraftMetaItem, "Second meta of %s does not belong to %s", meta2.getClass().getName(), CraftItemFactory.class.getName());
            return this.equals((CraftMetaItem)meta1, (CraftMetaItem)meta2);
         } else {
            return ((CraftMetaItem)meta1).isEmpty();
         }
      } else {
         return ((CraftMetaItem)meta2).isEmpty();
      }
   }

   boolean equals(CraftMetaItem meta1, CraftMetaItem meta2) {
      return meta1.equalsCommon(meta2) && meta1.notUncommon(meta2) && meta2.notUncommon(meta1);
   }

   public static CraftItemFactory instance() {
      return instance;
   }

   public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) {
      Preconditions.checkArgument(stack != null, "ItemStack stack cannot be null");
      return this.asMetaFor(meta, stack.getType());
   }

   public ItemMeta asMetaFor(ItemMeta meta, Material material) {
      Preconditions.checkArgument(material != null, "Material cannot be null");
      Preconditions.checkArgument(meta instanceof CraftMetaItem, "ItemMeta of %s not created by %s", meta != null ? meta.getClass().toString() : "null", CraftItemFactory.class.getName());
      return this.getItemMeta(material, (CraftMetaItem)meta);
   }

   public Color getDefaultLeatherColor() {
      return DEFAULT_LEATHER_COLOR;
   }

   public ItemStack createItemStack(String input) throws IllegalArgumentException {
      try {
         ItemParser.ItemResult arg = (new ItemParser(MinecraftServer.getDefaultRegistryAccess())).parse(new StringReader(input));
         Item item = (Item)arg.item().value();
         net.minecraft.world.item.ItemStack nmsItemStack = new net.minecraft.world.item.ItemStack(item);
         DataComponentPatch nbt = arg.components();
         if (nbt != null) {
            nmsItemStack.applyComponents(nbt);
         }

         return CraftItemStack.asCraftMirror(nmsItemStack);
      } catch (CommandSyntaxException var6) {
         CommandSyntaxException ex = var6;
         throw new IllegalArgumentException("Could not parse ItemStack: " + input, ex);
      }
   }

   public Material getSpawnEgg(EntityType type) {
      if (type == EntityType.UNKNOWN) {
         return null;
      } else {
         net.minecraft.world.entity.EntityType<?> nmsType = CraftEntityType.bukkitToMinecraft(type);
         Item nmsItem = SpawnEggItem.byId(nmsType);
         return nmsItem == null ? null : CraftItemType.minecraftToBukkit(nmsItem);
      }
   }

   public ItemStack enchantItem(Entity entity, ItemStack itemStack, int level, boolean allowTreasures) {
      Preconditions.checkArgument(entity != null, "The entity must not be null");
      return enchantItem(((CraftEntity)entity).getHandle().random, itemStack, level, allowTreasures);
   }

   public ItemStack enchantItem(World world, ItemStack itemStack, int level, boolean allowTreasures) {
      Preconditions.checkArgument(world != null, "The world must not be null");
      return enchantItem(((CraftWorld)world).getHandle().random, itemStack, level, allowTreasures);
   }

   public ItemStack enchantItem(ItemStack itemStack, int level, boolean allowTreasures) {
      return enchantItem(randomSource, itemStack, level, allowTreasures);
   }

   private static ItemStack enchantItem(RandomSource source, ItemStack itemStack, int level, boolean allowTreasures) {
      Preconditions.checkArgument(itemStack != null, "ItemStack must not be null");
      Preconditions.checkArgument(!itemStack.getType().isAir(), "ItemStack must not be air");
      ItemStack itemStack = CraftItemStack.asCraftCopy(itemStack);
      CraftItemStack craft = (CraftItemStack)itemStack;
      RegistryAccess registry = CraftRegistry.getMinecraftRegistry();
      Optional<HolderSet.Named<Enchantment>> optional = allowTreasures ? Optional.empty() : registry.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);
      return CraftItemStack.asCraftMirror(EnchantmentHelper.enchantItem(source, craft.handle, level, registry, optional));
   }

   static {
      ConfigurationSerialization.registerClass(SerializableMeta.class);
      ConfigurationSerialization.registerClass(CraftCustomModelDataComponent.class);
      ConfigurationSerialization.registerClass(CraftBlocksAttacksComponent.class);
      ConfigurationSerialization.registerClass(CraftBlocksAttacksComponent.CraftDamageReduction.class);
      ConfigurationSerialization.registerClass(CraftEquippableComponent.class);
      ConfigurationSerialization.registerClass(CraftFoodComponent.class);
      ConfigurationSerialization.registerClass(CraftToolComponent.class);
      ConfigurationSerialization.registerClass(CraftToolComponent.CraftToolRule.class);
      ConfigurationSerialization.registerClass(CraftJukeboxComponent.class);
      ConfigurationSerialization.registerClass(CraftUseCooldownComponent.class);
      ConfigurationSerialization.registerClass(CraftWeaponComponent.class);
   }
}
