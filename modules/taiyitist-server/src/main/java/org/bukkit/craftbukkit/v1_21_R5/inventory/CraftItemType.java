package org.bukkit.craftbukkit.v1_21_R5.inventory;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.v1_21_R5.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.attribute.CraftAttribute;
import org.bukkit.craftbukkit.v1_21_R5.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlockType;
import org.bukkit.craftbukkit.v1_21_R5.registry.CraftRegistryItem;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftMagicNumbers;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftItemType<M extends ItemMeta> extends CraftRegistryItem<Item> implements ItemType.Typed<M> {
   private final Supplier<CraftItemMetas.ItemMetaData<M>> itemMetaData = Suppliers.memoize(() -> {
      return CraftItemMetas.getItemMetaData(this);
   });

   public static Material minecraftToBukkit(Item item) {
      return CraftMagicNumbers.getMaterial(item);
   }

   public static Item bukkitToMinecraft(Material material) {
      return CraftMagicNumbers.getItem(material);
   }

   public static ItemType minecraftToBukkitNew(Item minecraft) {
      return (ItemType)CraftRegistry.minecraftToBukkit(minecraft, Registries.ITEM, Registry.ITEM);
   }

   public static Item bukkitToMinecraftNew(ItemType bukkit) {
      return (Item)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public CraftItemType(NamespacedKey key, Holder<Item> handle) {
      super(key, handle);
   }

   @NotNull
   public ItemType.Typed<ItemMeta> typed() {
      return this.typed(ItemMeta.class);
   }

   @NotNull
   public <Other extends ItemMeta> ItemType.Typed<Other> typed(@NotNull Class<Other> itemMetaType) {
      if (itemMetaType.isAssignableFrom(((CraftItemMetas.ItemMetaData)this.itemMetaData.get()).metaClass())) {
         return this;
      } else {
         String var10002 = String.valueOf(this.isRegistered() ? this.getKeyOrThrow() : this.toString());
         throw new IllegalArgumentException("Cannot type item type " + var10002 + " to meta type " + itemMetaType.getSimpleName());
      }
   }

   @NotNull
   public ItemStack createItemStack() {
      return this.createItemStack(1, (Consumer)null);
   }

   @NotNull
   public ItemStack createItemStack(int amount) {
      return this.createItemStack(amount, (Consumer)null);
   }

   @NotNull
   public ItemStack createItemStack(Consumer<? super M> metaConfigurator) {
      return this.createItemStack(1, metaConfigurator);
   }

   @NotNull
   public ItemStack createItemStack(int amount, @Nullable Consumer<? super M> metaConfigurator) {
      ItemStack itemStack = new ItemStack(this.asMaterial(), amount);
      if (metaConfigurator != null) {
         ItemMeta itemMeta = itemStack.getItemMeta();
         metaConfigurator.accept(itemMeta);
         itemStack.setItemMeta(itemMeta);
      }

      return itemStack;
   }

   public M getItemMeta(net.minecraft.world.item.ItemStack itemStack) {
      return (ItemMeta)((CraftItemMetas.ItemMetaData)this.itemMetaData.get()).fromItemStack().apply(itemStack);
   }

   public M getItemMeta(ItemMeta itemMeta) {
      return (ItemMeta)((CraftItemMetas.ItemMetaData)this.itemMetaData.get()).fromItemMeta().apply(this, (CraftMetaItem)itemMeta);
   }

   public boolean hasBlockType() {
      return this.getHandle() instanceof BlockItem;
   }

   @NotNull
   public BlockType getBlockType() {
      Object var2 = this.getHandle();
      if (var2 instanceof BlockItem block) {
         return CraftBlockType.minecraftToBukkitNew(block.getBlock());
      } else {
         Object var10002 = this.isRegistered() ? this.getKeyOrThrow() : this.toString();
         throw new IllegalStateException("The item type " + String.valueOf(var10002) + " has no corresponding block type");
      }
   }

   public Class<M> getItemMetaClass() {
      if (this == ItemType.AIR) {
         throw new UnsupportedOperationException("Air does not have ItemMeta");
      } else {
         return ((CraftItemMetas.ItemMetaData)this.itemMetaData.get()).metaClass();
      }
   }

   public int getMaxStackSize() {
      return this == AIR ? 0 : (Integer)((Item)this.getHandle()).components().getOrDefault(DataComponents.MAX_STACK_SIZE, 64);
   }

   public short getMaxDurability() {
      return ((Integer)((Item)this.getHandle()).components().getOrDefault(DataComponents.MAX_DAMAGE, 0)).shortValue();
   }

   public boolean isEdible() {
      return ((Item)this.getHandle()).components().has(DataComponents.FOOD);
   }

   public boolean isRecord() {
      return ((Item)this.getHandle()).components().has(DataComponents.JUKEBOX_PLAYABLE);
   }

   public boolean isFuel() {
      return MinecraftServer.getServer().fuelValues().isFuel(new net.minecraft.world.item.ItemStack((ItemLike)this.getHandle()));
   }

   public boolean isCompostable() {
      return ComposterBlock.COMPOSTABLES.containsKey(this.getHandle());
   }

   public float getCompostChance() {
      boolean var10000 = this.isCompostable();
      Object var10001 = this.isRegistered() ? this.getKeyOrThrow() : this.toString();
      Preconditions.checkArgument(var10000, "The item type " + String.valueOf(var10001) + " is not compostable");
      return ComposterBlock.COMPOSTABLES.getFloat(this.getHandle());
   }

   public ItemType getCraftingRemainingItem() {
      net.minecraft.world.item.ItemStack expectedItem = ((Item)this.getHandle()).getCraftingRemainder();
      return expectedItem.isEmpty() ? null : minecraftToBukkitNew(expectedItem.getItem());
   }

   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
      ImmutableMultimap.Builder<Attribute, AttributeModifier> defaultAttributes = ImmutableMultimap.builder();
      ItemAttributeModifiers nmsDefaultAttributes = (ItemAttributeModifiers)((Item)this.getHandle()).components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
      nmsDefaultAttributes.forEach(CraftEquipmentSlot.getNMS(slot), (key, value) -> {
         Attribute attribute = CraftAttribute.minecraftToBukkit((net.minecraft.world.entity.ai.attributes.Attribute)key.value());
         defaultAttributes.put(attribute, CraftAttributeInstance.convert(value, slot));
      });
      return defaultAttributes.build();
   }

   public CreativeCategory getCreativeCategory() {
      return CreativeCategory.BUILDING_BLOCKS;
   }

   public boolean isEnabledByFeature(@NotNull World world) {
      Preconditions.checkNotNull(world, "World cannot be null");
      return ((Item)this.getHandle()).isEnabled(((CraftWorld)world).getHandle().enabledFeatures());
   }

   @NotNull
   public String getTranslationKey() {
      return ((Item)this.getHandle()).getDescriptionId();
   }

   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   public Material asMaterial() {
      return (Material)Registry.MATERIAL.get(this.getKeyOrThrow());
   }
}
