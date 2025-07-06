package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.mojang.serialization.DynamicOps;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtPrinterTagVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSongs;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.Overridden;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.inventory.components.CraftBlocksAttacksComponent;
import org.bukkit.craftbukkit.inventory.components.CraftCustomModelDataComponent;
import org.bukkit.craftbukkit.inventory.components.CraftEquippableComponent;
import org.bukkit.craftbukkit.inventory.components.CraftFoodComponent;
import org.bukkit.craftbukkit.inventory.components.CraftJukeboxComponent;
import org.bukkit.craftbukkit.inventory.components.CraftToolComponent;
import org.bukkit.craftbukkit.inventory.components.CraftUseCooldownComponent;
import org.bukkit.craftbukkit.inventory.components.CraftWeaponComponent;
import org.bukkit.craftbukkit.inventory.components.consumable.CraftConsumableComponent;
import org.bukkit.craftbukkit.inventory.tags.DeprecatedCustomTagContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.craftbukkit.tag.CraftDamageTag;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNBTTagConfigSerializer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.components.BlocksAttacksComponent;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.inventory.meta.components.WeaponComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.tag.DamageTypeTags;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaItem implements ItemMeta, Damageable, Repairable, BlockDataMeta {
   static final ItemMetaKeyType<Component> NAME;
   static final ItemMetaKeyType<Component> ITEM_NAME;
   static final ItemMetaKeyType<ItemLore> LORE;
   static final ItemMetaKeyType<CustomModelData> CUSTOM_MODEL_DATA;
   static final ItemMetaKeyType<Enchantable> ENCHANTABLE;
   static final ItemMetaKeyType<ItemEnchantments> ENCHANTMENTS;
   static final ItemMetaKeyType<Integer> REPAIR;
   static final ItemMetaKeyType<ItemAttributeModifiers> ATTRIBUTES;
   static final ItemMetaKey ATTRIBUTES_IDENTIFIER;
   static final ItemMetaKey ATTRIBUTES_SLOT;
   static final ItemMetaKeyType<TooltipDisplay> HIDEFLAGS;
   static final ItemMetaKey HIDE_TOOLTIP;
   static final ItemMetaKeyType<ResourceLocation> TOOLTIP_STYLE;
   static final ItemMetaKeyType<ResourceLocation> ITEM_MODEL;
   static final ItemMetaKeyType<Unit> UNBREAKABLE;
   static final ItemMetaKeyType<Boolean> ENCHANTMENT_GLINT_OVERRIDE;
   static final ItemMetaKeyType<Unit> GLIDER;
   static final ItemMetaKeyType<DamageResistant> DAMAGE_RESISTANT;
   static final ItemMetaKeyType<Integer> MAX_STACK_SIZE;
   static final ItemMetaKeyType<Rarity> RARITY;
   static final ItemMetaKeyType<UseRemainder> USE_REMAINDER;
   static final ItemMetaKeyType<UseCooldown> USE_COOLDOWN;
   static final ItemMetaKeyType<FoodProperties> FOOD;
   static final ItemMetaKeyType<Consumable> CONSUMABLE;
   static final ItemMetaKeyType<Tool> TOOL;
   static final ItemMetaKeyType<BlocksAttacks> BLOCKS_ATTACKS;
   static final ItemMetaKeyType<Weapon> WEAPON;
   static final ItemMetaKeyType<Equippable> EQUIPPABLE;
   static final ItemMetaKeyType<JukeboxPlayable> JUKEBOX_PLAYABLE;
   static final ItemMetaKeyType<Holder<SoundEvent>> BREAK_SOUND;
   static final ItemMetaKeyType<Integer> DAMAGE;
   static final ItemMetaKeyType<Integer> MAX_DAMAGE;
   static final ItemMetaKeyType<BlockItemStateProperties> BLOCK_DATA;
   static final ItemMetaKey BUKKIT_CUSTOM_TAG;
   static final ItemMetaKeyType<CustomData> CUSTOM_DATA;
   private Component displayName;
   private Component itemName;
   private List<Component> lore;
   private CraftCustomModelDataComponent customModelData;
   private Integer enchantableValue;
   private Map<String, String> blockData;
   private Map<Enchantment, Integer> enchantments;
   private Multimap<Attribute, AttributeModifier> attributeModifiers;
   private int repairCost;
   private SequencedSet<DataComponentType<?>> hiddenComponents;
   private boolean hideTooltip;
   private NamespacedKey tooltipStyle;
   private NamespacedKey itemModel;
   private boolean unbreakable;
   private Boolean enchantmentGlintOverride;
   private boolean glider;
   private TagKey<DamageType> damageResistant;
   private Integer maxStackSize;
   private ItemRarity rarity;
   private ItemStack useRemainder;
   private CraftUseCooldownComponent useCooldown;
   private CraftFoodComponent food;
   private CraftConsumableComponent consumable;
   private CraftToolComponent tool;
   private CraftBlocksAttacksComponent blocksAttacks;
   private CraftWeaponComponent weapon;
   private CraftEquippableComponent equippable;
   private CraftJukeboxComponent jukebox;
   private Holder<SoundEvent> breakSound;
   private int damage;
   private Integer maxDamage;
   private static final Set<DataComponentType> HANDLED_TAGS;
   private static final CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY;
   private CompoundTag customTag;
   protected DataComponentPatch.Builder unhandledTags = DataComponentPatch.builder();
   private Set<DataComponentType<?>> removedTags = Sets.newHashSet();
   private CraftPersistentDataContainer persistentDataContainer;
   private int version;

   CraftMetaItem(CraftMetaItem meta) {
      this.persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);
      this.version = CraftMagicNumbers.INSTANCE.getDataVersion();
      if (meta != null) {
         this.displayName = meta.displayName;
         this.itemName = meta.itemName;
         if (meta.lore != null) {
            this.lore = new ArrayList(meta.lore);
         }

         if (meta.hasCustomModelDataComponent()) {
            this.customModelData = new CraftCustomModelDataComponent(meta.customModelData);
         }

         this.enchantableValue = meta.enchantableValue;
         this.blockData = meta.blockData;
         if (meta.enchantments != null) {
            this.enchantments = new LinkedHashMap(meta.enchantments);
         }

         if (meta.hasAttributeModifiers()) {
            this.attributeModifiers = LinkedHashMultimap.create(meta.attributeModifiers);
         }

         this.repairCost = meta.repairCost;
         if (meta.hasItemFlags()) {
            this.hiddenComponents = new LinkedHashSet(meta.hiddenComponents);
         }

         this.hideTooltip = meta.hideTooltip;
         this.tooltipStyle = meta.tooltipStyle;
         this.itemModel = meta.itemModel;
         this.unbreakable = meta.unbreakable;
         this.enchantmentGlintOverride = meta.enchantmentGlintOverride;
         this.glider = meta.glider;
         this.damageResistant = meta.damageResistant;
         this.maxStackSize = meta.maxStackSize;
         this.rarity = meta.rarity;
         if (meta.hasUseRemainder()) {
            this.useRemainder = meta.useRemainder.clone();
         }

         if (meta.hasUseCooldown()) {
            this.useCooldown = new CraftUseCooldownComponent(meta.useCooldown);
         }

         if (meta.hasFood()) {
            this.food = new CraftFoodComponent(meta.food);
         }

         if (meta.hasConsumable()) {
            this.consumable = new CraftConsumableComponent(meta.consumable);
         }

         if (meta.hasTool()) {
            this.tool = new CraftToolComponent(meta.tool);
         }

         if (meta.hasBlocksAttacks()) {
            this.blocksAttacks = new CraftBlocksAttacksComponent(meta.blocksAttacks);
         }

         if (meta.hasWeapon()) {
            this.weapon = new CraftWeaponComponent(meta.weapon);
         }

         if (meta.hasEquippable()) {
            this.equippable = new CraftEquippableComponent(meta.equippable);
         }

         if (meta.hasJukeboxPlayable()) {
            this.jukebox = new CraftJukeboxComponent(meta.jukebox);
         }

         this.breakSound = this.breakSound;
         this.damage = meta.damage;
         this.maxDamage = meta.maxDamage;
         this.unhandledTags.copy(meta.unhandledTags.build());
         this.removedTags.addAll(meta.removedTags);
         this.persistentDataContainer.putAll(meta.persistentDataContainer.getRaw());
         this.customTag = meta.customTag;
         this.version = meta.version;
      }
   }

   CraftMetaItem(DataComponentPatch tag) {
      this.persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);
      this.version = CraftMagicNumbers.INSTANCE.getDataVersion();
      getOrEmpty(tag, NAME).ifPresent((component) -> {
         this.displayName = component;
      });
      getOrEmpty(tag, ITEM_NAME).ifPresent((component) -> {
         this.itemName = component;
      });
      getOrEmpty(tag, LORE).ifPresent((l) -> {
         List<Component> list = l.lines();
         this.lore = new ArrayList(list.size());

         for(int index = 0; index < list.size(); ++index) {
            Component line = (Component)list.get(index);
            this.lore.add(line);
         }

      });
      getOrEmpty(tag, CUSTOM_MODEL_DATA).ifPresent((i) -> {
         this.customModelData = new CraftCustomModelDataComponent(i);
      });
      getOrEmpty(tag, ENCHANTABLE).ifPresent((i) -> {
         this.enchantableValue = i.value();
      });
      getOrEmpty(tag, BLOCK_DATA).ifPresent((t) -> {
         this.blockData = t.properties();
      });
      getOrEmpty(tag, ENCHANTMENTS).ifPresent((en) -> {
         this.enchantments = buildEnchantments(en);
      });
      getOrEmpty(tag, ATTRIBUTES).ifPresent((en) -> {
         this.attributeModifiers = buildModifiers(en);
      });
      getOrEmpty(tag, REPAIR).ifPresent((i) -> {
         this.repairCost = i;
      });
      getOrEmpty(tag, HIDEFLAGS).ifPresent((h) -> {
         Iterator var2 = h.hiddenComponents().iterator();

         while(var2.hasNext()) {
            DataComponentType<?> hidden = (DataComponentType)var2.next();
            ItemFlag flag = CraftItemFlag.nmsToBukkit(hidden);
            if (flag != null) {
               this.addItemFlags(flag);
            }
         }

         this.hideTooltip = h.hideTooltip();
      });
      getOrEmpty(tag, TOOLTIP_STYLE).ifPresent((keyx) -> {
         this.tooltipStyle = CraftNamespacedKey.fromMinecraft(keyx);
      });
      getOrEmpty(tag, ITEM_MODEL).ifPresent((keyx) -> {
         this.itemModel = CraftNamespacedKey.fromMinecraft(keyx);
      });
      getOrEmpty(tag, UNBREAKABLE).ifPresent((u) -> {
         this.unbreakable = true;
      });
      getOrEmpty(tag, ENCHANTMENT_GLINT_OVERRIDE).ifPresent((override) -> {
         this.enchantmentGlintOverride = override;
      });
      getOrEmpty(tag, GLIDER).ifPresent((u) -> {
         this.glider = true;
      });
      getOrEmpty(tag, DAMAGE_RESISTANT).ifPresent((tags) -> {
         this.damageResistant = tags.types();
      });
      getOrEmpty(tag, MAX_STACK_SIZE).ifPresent((i) -> {
         this.maxStackSize = i;
      });
      getOrEmpty(tag, RARITY).ifPresent((enumItemRarity) -> {
         this.rarity = ItemRarity.valueOf(enumItemRarity.name());
      });
      getOrEmpty(tag, USE_REMAINDER).ifPresent((remainder) -> {
         this.useRemainder = CraftItemStack.asCraftMirror(remainder.convertInto());
      });
      getOrEmpty(tag, USE_COOLDOWN).ifPresent((cooldown) -> {
         this.useCooldown = new CraftUseCooldownComponent(cooldown);
      });
      getOrEmpty(tag, FOOD).ifPresent((foodInfo) -> {
         this.food = new CraftFoodComponent(foodInfo);
      });
      getOrEmpty(tag, CONSUMABLE).ifPresent((consumableInfo) -> {
         this.consumable = new CraftConsumableComponent(consumableInfo);
      });
      getOrEmpty(tag, TOOL).ifPresent((toolInfo) -> {
         this.tool = new CraftToolComponent(toolInfo);
      });
      getOrEmpty(tag, BLOCKS_ATTACKS).ifPresent((blocksInfo) -> {
         this.blocksAttacks = new CraftBlocksAttacksComponent(blocksInfo);
      });
      getOrEmpty(tag, WEAPON).ifPresent((weaponInfo) -> {
         this.weapon = new CraftWeaponComponent(weaponInfo);
      });
      getOrEmpty(tag, EQUIPPABLE).ifPresent((equippableInfo) -> {
         this.equippable = new CraftEquippableComponent(equippableInfo);
      });
      getOrEmpty(tag, JUKEBOX_PLAYABLE).ifPresent((jukeboxPlayable) -> {
         this.jukebox = new CraftJukeboxComponent(jukeboxPlayable);
      });
      getOrEmpty(tag, BREAK_SOUND).ifPresent((sound) -> {
         this.breakSound = sound;
      });
      getOrEmpty(tag, DAMAGE).ifPresent((i) -> {
         this.damage = i;
      });
      getOrEmpty(tag, MAX_DAMAGE).ifPresent((i) -> {
         this.maxDamage = i;
      });
      getOrEmpty(tag, CUSTOM_DATA).ifPresent((customData) -> {
         this.customTag = customData.copyTag();
         this.customTag.getCompound(BUKKIT_CUSTOM_TAG.NBT).ifPresent((compound) -> {
            Set<String> keys = compound.keySet();
            Iterator var3 = keys.iterator();

            while(var3.hasNext()) {
               String key = (String)var3.next();
               this.persistentDataContainer.put(key, compound.get(key).copy());
            }

            this.customTag.remove(BUKKIT_CUSTOM_TAG.NBT);
         });
         if (this.customTag.isEmpty()) {
            this.customTag = null;
         }

      });
      Set<Map.Entry<DataComponentType<?>, Optional<?>>> keys = tag.entrySet();
      Iterator var3 = keys.iterator();

      while(var3.hasNext()) {
         Map.Entry<DataComponentType<?>, Optional<?>> key = (Map.Entry)var3.next();
         if (!getHandledTags().contains(key.getKey())) {
            ((Optional)key.getValue()).ifPresent((value) -> {
               this.unhandledTags.set((DataComponentType)key.getKey(), value);
            });
         }

         if (((Optional)key.getValue()).isEmpty()) {
            this.removedTags.add((DataComponentType)key.getKey());
         }
      }

   }

   static Map<Enchantment, Integer> buildEnchantments(ItemEnchantments tag) {
      Map<Enchantment, Integer> enchantments = new LinkedHashMap(tag.size());
      tag.entrySet().forEach((entry) -> {
         Holder<net.minecraft.world.item.enchantment.Enchantment> id = (Holder)entry.getKey();
         int level = entry.getIntValue();
         Enchantment enchant = CraftEnchantment.minecraftHolderToBukkit(id);
         if (enchant != null) {
            enchantments.put(enchant, level);
         }

      });
      return enchantments;
   }

   static Multimap<Attribute, AttributeModifier> buildModifiers(ItemAttributeModifiers tag) {
      Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();
      List<ItemAttributeModifiers.Entry> mods = tag.modifiers();
      int size = mods.size();

      for(int i = 0; i < size; ++i) {
         ItemAttributeModifiers.Entry entry = (ItemAttributeModifiers.Entry)mods.get(i);
         net.minecraft.world.entity.ai.attributes.AttributeModifier nmsModifier = entry.modifier();
         if (nmsModifier != null) {
            AttributeModifier attribMod = CraftAttributeInstance.convert(nmsModifier);
            Attribute attribute = CraftAttribute.minecraftHolderToBukkit(entry.attribute());
            if (attribute != null) {
               if (entry.slot() != null) {
                  EquipmentSlotGroup slotName = entry.slot();
                  if (slotName == null) {
                     modifiers.put(attribute, attribMod);
                     continue;
                  }

                  org.bukkit.inventory.EquipmentSlotGroup slot = null;

                  try {
                     slot = CraftEquipmentSlot.getSlot(slotName);
                  } catch (IllegalArgumentException var12) {
                  }

                  if (slot == null) {
                     modifiers.put(attribute, attribMod);
                     continue;
                  }

                  attribMod = new AttributeModifier(attribMod.getKey(), attribMod.getAmount(), attribMod.getOperation(), slot);
               }

               modifiers.put(attribute, attribMod);
            }
         }
      }

      return modifiers;
   }

   CraftMetaItem(Map<String, Object> map) {
      this.persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);
      this.version = CraftMagicNumbers.INSTANCE.getDataVersion();
      this.displayName = CraftChatMessage.fromJSONOrString(SerializableMeta.getString(map, NAME.BUKKIT, true), true, false);
      this.itemName = CraftChatMessage.fromJSONOrNull(SerializableMeta.getString(map, ITEM_NAME.BUKKIT, true));
      Iterable<?> lore = (Iterable)SerializableMeta.getObject(Iterable.class, map, LORE.BUKKIT, true);
      if (lore != null) {
         safelyAdd(lore, this.lore = new ArrayList(), true);
      }

      Object customModelData = SerializableMeta.getObject(Object.class, map, CUSTOM_MODEL_DATA.BUKKIT, true);
      if (customModelData instanceof CustomModelDataComponent component) {
         this.setCustomModelDataComponent(component);
      } else {
         this.setCustomModelData((Integer)customModelData);
      }

      Integer enchantmentValue = (Integer)SerializableMeta.getObject(Integer.class, map, ENCHANTABLE.BUKKIT, true);
      if (enchantmentValue != null) {
         this.setEnchantable(enchantmentValue);
      }

      Object blockData = SerializableMeta.getObject(Object.class, map, BLOCK_DATA.BUKKIT, true);
      Iterator var8;
      String tooltipStyle;
      if (blockData != null) {
         Map<String, String> mapBlockData = new HashMap();
         if (blockData instanceof Map) {
            Iterator var44 = ((Map)blockData).entrySet().iterator();

            while(var44.hasNext()) {
               Map.Entry<?, ?> entry = (Map.Entry)var44.next();
               mapBlockData.put(entry.getKey().toString(), entry.getValue().toString());
            }
         } else {
            CompoundTag nbtBlockData = (CompoundTag)CraftNBTTagConfigSerializer.deserialize(blockData);
            var8 = nbtBlockData.keySet().iterator();

            while(var8.hasNext()) {
               tooltipStyle = (String)var8.next();
               mapBlockData.put(tooltipStyle, nbtBlockData.getStringOr(tooltipStyle, ""));
            }
         }

         this.blockData = mapBlockData;
      }

      this.enchantments = buildEnchantments(map, ENCHANTMENTS);
      this.attributeModifiers = buildModifiers(map, ATTRIBUTES);
      Integer repairCost = (Integer)SerializableMeta.getObject(Integer.class, map, REPAIR.BUKKIT, true);
      if (repairCost != null) {
         this.setRepairCost(repairCost);
      }

      Iterable<?> hideFlags = (Iterable)SerializableMeta.getObject(Iterable.class, map, HIDEFLAGS.BUKKIT, true);
      String itemModel;
      if (hideFlags != null) {
         var8 = hideFlags.iterator();

         while(var8.hasNext()) {
            Object hideFlagObject = var8.next();
            itemModel = (String)hideFlagObject;

            try {
               ItemFlag hideFlatEnum = CraftItemFlag.stringToBukkit(itemModel);
               this.addItemFlags(hideFlatEnum);
            } catch (IllegalArgumentException var40) {
            }
         }
      }

      Boolean hideTooltip = (Boolean)SerializableMeta.getObject(Boolean.class, map, HIDE_TOOLTIP.BUKKIT, true);
      if (hideTooltip != null) {
         this.setHideTooltip(hideTooltip);
      }

      tooltipStyle = SerializableMeta.getString(map, TOOLTIP_STYLE.BUKKIT, true);
      if (tooltipStyle != null) {
         this.setTooltipStyle(NamespacedKey.fromString(tooltipStyle));
      }

      itemModel = SerializableMeta.getString(map, ITEM_MODEL.BUKKIT, true);
      if (itemModel != null) {
         this.setItemModel(NamespacedKey.fromString(itemModel));
      }

      Boolean unbreakable = (Boolean)SerializableMeta.getObject(Boolean.class, map, UNBREAKABLE.BUKKIT, true);
      if (unbreakable != null) {
         this.setUnbreakable(unbreakable);
      }

      Boolean enchantmentGlintOverride = (Boolean)SerializableMeta.getObject(Boolean.class, map, ENCHANTMENT_GLINT_OVERRIDE.BUKKIT, true);
      if (enchantmentGlintOverride != null) {
         this.setEnchantmentGlintOverride(enchantmentGlintOverride);
      }

      Boolean glider = (Boolean)SerializableMeta.getObject(Boolean.class, map, GLIDER.BUKKIT, true);
      if (glider != null) {
         this.setGlider(glider);
      }

      String damageResistant = SerializableMeta.getString(map, DAMAGE_RESISTANT.BUKKIT, true);
      if (damageResistant != null) {
         Tag<org.bukkit.damage.DamageType> tag = Bukkit.getTag("damage_types", NamespacedKey.fromString(damageResistant), org.bukkit.damage.DamageType.class);
         if (tag != null) {
            this.setDamageResistant(tag);
         }
      }

      Integer maxStackSize = (Integer)SerializableMeta.getObject(Integer.class, map, MAX_STACK_SIZE.BUKKIT, true);
      if (maxStackSize != null) {
         this.setMaxStackSize(maxStackSize);
      }

      String rarity = SerializableMeta.getString(map, RARITY.BUKKIT, true);
      if (rarity != null) {
         this.setRarity(ItemRarity.valueOf(rarity));
      }

      ItemStack remainder = (ItemStack)SerializableMeta.getObject(ItemStack.class, map, USE_REMAINDER.BUKKIT, true);
      if (remainder != null) {
         this.setUseRemainder(remainder);
      }

      CraftUseCooldownComponent cooldown = (CraftUseCooldownComponent)SerializableMeta.getObject(CraftUseCooldownComponent.class, map, USE_COOLDOWN.BUKKIT, true);
      if (cooldown != null) {
         this.setUseCooldown(cooldown);
      }

      CraftFoodComponent food = (CraftFoodComponent)SerializableMeta.getObject(CraftFoodComponent.class, map, FOOD.BUKKIT, true);
      if (food != null) {
         this.setFood(food);
      }

      CraftConsumableComponent consumable = (CraftConsumableComponent)SerializableMeta.getObject(CraftConsumableComponent.class, map, CONSUMABLE.BUKKIT, true);
      if (consumable != null) {
         this.setConsumable(consumable);
      }

      CraftToolComponent tool = (CraftToolComponent)SerializableMeta.getObject(CraftToolComponent.class, map, TOOL.BUKKIT, true);
      if (tool != null) {
         this.setTool(tool);
      }

      CraftBlocksAttacksComponent blocksAttacks = (CraftBlocksAttacksComponent)SerializableMeta.getObject(CraftBlocksAttacksComponent.class, map, BLOCKS_ATTACKS.BUKKIT, true);
      if (blocksAttacks != null) {
         this.setBlocksAttacks(blocksAttacks);
      }

      CraftWeaponComponent weapon = (CraftWeaponComponent)SerializableMeta.getObject(CraftWeaponComponent.class, map, WEAPON.BUKKIT, true);
      if (weapon != null) {
         this.setWeapon(weapon);
      }

      CraftEquippableComponent equippable = (CraftEquippableComponent)SerializableMeta.getObject(CraftEquippableComponent.class, map, EQUIPPABLE.BUKKIT, true);
      if (equippable != null) {
         this.setEquippable(equippable);
      }

      CraftJukeboxComponent jukeboxPlayable = (CraftJukeboxComponent)SerializableMeta.getObject(CraftJukeboxComponent.class, map, JUKEBOX_PLAYABLE.BUKKIT, true);
      if (jukeboxPlayable != null) {
         this.setJukeboxPlayable(jukeboxPlayable);
      }

      String snd = SerializableMeta.getString(map, "break-sound", true);
      if (snd != null) {
         this.setBreakSound((Sound)Registry.SOUNDS.get(NamespacedKey.fromString(snd)));
      }

      Integer damage = (Integer)SerializableMeta.getObject(Integer.class, map, DAMAGE.BUKKIT, true);
      if (damage != null) {
         this.setDamage(damage);
      }

      Integer maxDamage = (Integer)SerializableMeta.getObject(Integer.class, map, MAX_DAMAGE.BUKKIT, true);
      if (maxDamage != null) {
         this.setMaxDamage(maxDamage);
      }

      String internal = SerializableMeta.getString(map, "internal", true);
      if (internal != null) {
         ByteArrayInputStream buf = new ByteArrayInputStream(Base64.getDecoder().decode(internal));

         try {
            CompoundTag internalTag = NbtIo.readCompressed(buf, NbtAccounter.unlimitedHeap());
            this.deserializeInternal(internalTag, map);
         } catch (IOException var39) {
            IOException ex = var39;
            Logger.getLogger(CraftMetaItem.class.getName()).log(Level.SEVERE, (String)null, ex);
         }
      }

      String unhandled = SerializableMeta.getString(map, "unhandled", true);
      Iterator var34;
      if (unhandled != null) {
         ByteArrayInputStream buf = new ByteArrayInputStream(Base64.getDecoder().decode(unhandled));

         try {
            CompoundTag unhandledTag = NbtIo.readCompressed(buf, NbtAccounter.unlimitedHeap());
            DataComponentPatch unhandledPatch = (DataComponentPatch)DataComponentPatch.CODEC.parse(MinecraftServer.getDefaultRegistryAccess().createSerializationContext(NbtOps.INSTANCE), unhandledTag).result().get();
            this.unhandledTags.copy(unhandledPatch);
            var34 = unhandledPatch.entrySet().iterator();

            while(var34.hasNext()) {
               Map.Entry<DataComponentType<?>, Optional<?>> entry = (Map.Entry)var34.next();
               if (!((Optional)entry.getValue()).isPresent()) {
                  DataComponentType<?> key = (DataComponentType)entry.getKey();
                  this.unhandledTags.clear(key);
                  this.removedTags.add(key);
               }
            }
         } catch (IOException var41) {
            IOException ex = var41;
            Logger.getLogger(CraftMetaItem.class.getName()).log(Level.SEVERE, (String)null, ex);
         }
      }

      Iterable<?> removed = (Iterable)SerializableMeta.getObject(Iterable.class, map, "removed", true);
      if (removed != null) {
         RegistryAccess registryAccess = CraftRegistry.getMinecraftRegistry();
         net.minecraft.core.Registry<DataComponentType<?>> componentTypeRegistry = registryAccess.lookupOrThrow(Registries.DATA_COMPONENT_TYPE);
         var34 = removed.iterator();

         while(var34.hasNext()) {
            Object removedObject = var34.next();
            String removedString = (String)removedObject;
            DataComponentType<?> component = (DataComponentType)componentTypeRegistry.getValue(ResourceLocation.parse(removedString));
            if (component != null) {
               this.removedTags.add(component);
            }
         }
      }

      Object nbtMap = SerializableMeta.getObject(Object.class, map, BUKKIT_CUSTOM_TAG.BUKKIT, true);
      if (nbtMap != null) {
         this.persistentDataContainer.putAll((CompoundTag)CraftNBTTagConfigSerializer.deserialize(nbtMap));
      }

      String custom = SerializableMeta.getString(map, "custom", true);
      if (custom != null) {
         ByteArrayInputStream buf = new ByteArrayInputStream(Base64.getDecoder().decode(custom));

         try {
            this.customTag = NbtIo.readCompressed(buf, NbtAccounter.unlimitedHeap());
         } catch (IOException var38) {
            IOException ex = var38;
            Logger.getLogger(CraftMetaItem.class.getName()).log(Level.SEVERE, (String)null, ex);
         }
      }

   }

   void deserializeInternal(CompoundTag tag, Object context) {
      tag.getList(ATTRIBUTES.NBT).ifPresent((ignore) -> {
         this.attributeModifiers = buildModifiersLegacy(tag, ATTRIBUTES);
      });
   }

   private static Multimap<Attribute, AttributeModifier> buildModifiersLegacy(CompoundTag tag, ItemMetaKey key) {
      Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();
      ListTag mods = tag.getListOrEmpty(key.NBT);
      int size = mods.size();

      for(int i = 0; i < size; ++i) {
         CompoundTag entry = mods.getCompoundOrEmpty(i);
         if (!entry.isEmpty()) {
            net.minecraft.world.entity.ai.attributes.AttributeModifier nmsModifier = (net.minecraft.world.entity.ai.attributes.AttributeModifier)entry.read(net.minecraft.world.entity.ai.attributes.AttributeModifier.MAP_CODEC).orElse((Object)null);
            if (nmsModifier != null) {
               AttributeModifier attribMod = CraftAttributeInstance.convert(nmsModifier);
               String attributeName = entry.getStringOr(ATTRIBUTES_IDENTIFIER.NBT, (String)null);
               if (attributeName != null && !attributeName.isEmpty()) {
                  Attribute attribute = CraftAttribute.stringToBukkit(attributeName);
                  if (attribute != null) {
                     String slotName = entry.getStringOr(ATTRIBUTES_SLOT.NBT, (String)null);
                     if (slotName != null && !slotName.isEmpty()) {
                        EquipmentSlot slot = null;

                        try {
                           slot = CraftEquipmentSlot.getSlot(net.minecraft.world.entity.EquipmentSlot.byName(slotName.toLowerCase(Locale.ROOT)));
                        } catch (IllegalArgumentException var14) {
                        }

                        if (slot == null) {
                           modifiers.put(attribute, attribMod);
                        } else {
                           attribMod = new AttributeModifier(attribMod.getKey(), attribMod.getAmount(), attribMod.getOperation(), slot.getGroup());
                           modifiers.put(attribute, attribMod);
                        }
                     } else {
                        modifiers.put(attribute, attribMod);
                     }
                  }
               }
            }
         }
      }

      return modifiers;
   }

   static Map<Enchantment, Integer> buildEnchantments(Map<String, Object> map, ItemMetaKey key) {
      Map<?, ?> ench = (Map)SerializableMeta.getObject(Map.class, map, key.BUKKIT, true);
      if (ench == null) {
         return null;
      } else {
         Map<Enchantment, Integer> enchantments = new LinkedHashMap(ench.size());
         Iterator var4 = ench.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry)var4.next();
            Enchantment enchantment = CraftEnchantment.stringToBukkit(entry.getKey().toString());
            if (enchantment != null && entry.getValue() instanceof Integer) {
               enchantments.put(enchantment, (Integer)entry.getValue());
            }
         }

         return enchantments;
      }
   }

   static Multimap<Attribute, AttributeModifier> buildModifiers(Map<String, Object> map, ItemMetaKey key) {
      Map<?, ?> mods = (Map)SerializableMeta.getObject(Map.class, map, key.BUKKIT, true);
      Multimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
      if (mods == null) {
         return result;
      } else {
         Iterator var4 = mods.keySet().iterator();

         while(true) {
            String attributeName;
            do {
               Object obj;
               do {
                  if (!var4.hasNext()) {
                     return result;
                  }

                  obj = var4.next();
               } while(!(obj instanceof String));

               attributeName = (String)obj;
            } while(Strings.isNullOrEmpty(attributeName));

            List<?> list = (List)SerializableMeta.getObject(List.class, mods, attributeName, true);
            if (list == null || list.isEmpty()) {
               return result;
            }

            Iterator var8 = list.iterator();

            while(var8.hasNext()) {
               Object o = var8.next();
               if (o instanceof AttributeModifier) {
                  AttributeModifier modifier = (AttributeModifier)o;
                  Attribute attribute = CraftAttribute.stringToBukkit(attributeName);
                  if (attribute != null) {
                     result.put(attribute, modifier);
                  }
               }
            }
         }
      }
   }

   @Overridden
   void applyToItem(Applicator itemTag) {
      if (this.hasDisplayName()) {
         itemTag.put(NAME, this.displayName);
      }

      if (this.hasItemName()) {
         itemTag.put(ITEM_NAME, this.itemName);
      }

      if (this.lore != null) {
         itemTag.put(LORE, new ItemLore(this.lore));
      }

      if (this.hasCustomModelDataComponent()) {
         itemTag.put(CUSTOM_MODEL_DATA, this.customModelData.getHandle());
      }

      if (this.hasEnchantable()) {
         itemTag.put(ENCHANTABLE, new Enchantable(this.enchantableValue));
      }

      if (this.hasBlockData()) {
         itemTag.put(BLOCK_DATA, new BlockItemStateProperties(this.blockData));
      }

      if (this.hiddenComponents != null || this.hideTooltip) {
         SequencedSet<DataComponentType<?>> hidden = this.hiddenComponents != null ? this.hiddenComponents : new LinkedHashSet();
         itemTag.put(HIDEFLAGS, new TooltipDisplay(this.hideTooltip, (SequencedSet)hidden));
      }

      this.applyEnchantments(this.enchantments, itemTag, ENCHANTMENTS, ItemFlag.HIDE_ENCHANTS);
      this.applyModifiers(this.attributeModifiers, itemTag);
      if (this.hasRepairCost()) {
         itemTag.put(REPAIR, this.repairCost);
      }

      if (this.hasTooltipStyle()) {
         itemTag.put(TOOLTIP_STYLE, CraftNamespacedKey.toMinecraft(this.getTooltipStyle()));
      }

      if (this.hasItemModel()) {
         itemTag.put(ITEM_MODEL, CraftNamespacedKey.toMinecraft(this.getItemModel()));
      }

      if (this.isUnbreakable()) {
         itemTag.put(UNBREAKABLE, Unit.INSTANCE);
      }

      if (this.hasEnchantmentGlintOverride()) {
         itemTag.put(ENCHANTMENT_GLINT_OVERRIDE, this.getEnchantmentGlintOverride());
      }

      if (this.isGlider()) {
         itemTag.put(GLIDER, Unit.INSTANCE);
      }

      if (this.hasDamageResistant()) {
         itemTag.put(DAMAGE_RESISTANT, new DamageResistant(this.damageResistant));
      }

      if (this.hasMaxStackSize()) {
         itemTag.put(MAX_STACK_SIZE, this.maxStackSize);
      }

      if (this.hasRarity()) {
         itemTag.put(RARITY, Rarity.valueOf(this.rarity.name()));
      }

      if (this.hasUseRemainder()) {
         itemTag.put(USE_REMAINDER, new UseRemainder(CraftItemStack.asNMSCopy(this.useRemainder)));
      }

      if (this.hasUseCooldown()) {
         itemTag.put(USE_COOLDOWN, this.useCooldown.getHandle());
      }

      if (this.hasFood()) {
         itemTag.put(FOOD, this.food.getHandle());
      }

      if (this.hasConsumable()) {
         itemTag.put(CONSUMABLE, this.consumable.getHandle());
      }

      if (this.hasTool()) {
         itemTag.put(TOOL, this.tool.getHandle());
      }

      if (this.hasBlocksAttacks()) {
         itemTag.put(BLOCKS_ATTACKS, this.blocksAttacks.getHandle());
      }

      if (this.hasWeapon()) {
         itemTag.put(WEAPON, this.weapon.getHandle());
      }

      if (this.hasEquippable()) {
         itemTag.put(EQUIPPABLE, this.equippable.getHandle());
      }

      if (this.hasJukeboxPlayable()) {
         itemTag.put(JUKEBOX_PLAYABLE, this.jukebox.getHandle());
      }

      if (this.hasBreakSound()) {
         itemTag.put(BREAK_SOUND, this.breakSound);
      }

      if (this.hasDamage()) {
         itemTag.put(DAMAGE, this.damage);
      }

      if (this.hasMaxDamage()) {
         itemTag.put(MAX_DAMAGE, this.maxDamage);
      }

      Iterator var7 = this.unhandledTags.build().entrySet().iterator();

      while(var7.hasNext()) {
         Map.Entry<DataComponentType<?>, Optional<?>> e = (Map.Entry)var7.next();
         ((Optional)e.getValue()).ifPresent((value) -> {
            itemTag.builder.set((DataComponentType)e.getKey(), value);
         });
      }

      var7 = this.removedTags.iterator();

      while(var7.hasNext()) {
         DataComponentType<?> removed = (DataComponentType)var7.next();
         if (!itemTag.builder.isSet(removed)) {
            itemTag.builder.remove(removed);
         }
      }

      CompoundTag customTag = this.customTag != null ? this.customTag.copy() : null;
      if (!this.persistentDataContainer.isEmpty()) {
         CompoundTag bukkitCustomCompound = new CompoundTag();
         Map<String, net.minecraft.nbt.Tag> rawPublicMap = this.persistentDataContainer.getRaw();
         Iterator var5 = rawPublicMap.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<String, net.minecraft.nbt.Tag> nbtBaseEntry = (Map.Entry)var5.next();
            bukkitCustomCompound.put((String)nbtBaseEntry.getKey(), (net.minecraft.nbt.Tag)nbtBaseEntry.getValue());
         }

         if (customTag == null) {
            customTag = new CompoundTag();
         }

         customTag.put(BUKKIT_CUSTOM_TAG.BUKKIT, bukkitCustomCompound);
      }

      if (customTag != null) {
         itemTag.put(CUSTOM_DATA, CustomData.of(customTag));
      }

   }

   void applyEnchantments(Map<Enchantment, Integer> enchantments, Applicator tag, ItemMetaKeyType<ItemEnchantments> key, ItemFlag itemFlag) {
      if (enchantments != null || this.hasItemFlag(itemFlag)) {
         ItemEnchantments.Mutable list = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
         if (enchantments != null) {
            Iterator var6 = enchantments.entrySet().iterator();

            while(var6.hasNext()) {
               Map.Entry<Enchantment, Integer> entry = (Map.Entry)var6.next();
               list.set(CraftEnchantment.bukkitToMinecraftHolder((Enchantment)entry.getKey()), (Integer)entry.getValue());
            }
         }

         tag.put(key, list.toImmutable());
      }
   }

   void applyModifiers(Multimap<Attribute, AttributeModifier> modifiers, Applicator tag) {
      if (modifiers != null && !modifiers.isEmpty()) {
         ItemAttributeModifiers.Builder list = ItemAttributeModifiers.builder();
         Iterator var4 = modifiers.entries().iterator();

         while(var4.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = (Map.Entry)var4.next();
            if (entry.getKey() != null && entry.getValue() != null) {
               net.minecraft.world.entity.ai.attributes.AttributeModifier nmsModifier = CraftAttributeInstance.convert((AttributeModifier)entry.getValue());
               Holder<net.minecraft.world.entity.ai.attributes.Attribute> name = CraftAttribute.bukkitToMinecraftHolder((Attribute)entry.getKey());
               if (name != null) {
                  EquipmentSlotGroup group = CraftEquipmentSlot.getNMSGroup(((AttributeModifier)entry.getValue()).getSlotGroup());
                  list.add(name, nmsModifier, group);
               }
            }
         }

         tag.put(ATTRIBUTES, list.build());
      }
   }

   boolean applicableTo(Material type) {
      if (type != Material.AIR && type.isItem()) {
         if (this.getClass() == CraftMetaItem.class) {
            return true;
         } else {
            return type.asItemType().getItemMetaClass() == this.getClass().getInterfaces()[0];
         }
      } else {
         return false;
      }
   }

   @Overridden
   boolean isEmpty() {
      return !this.hasDisplayName() && !this.hasItemName() && !this.hasLocalizedName() && !this.hasEnchants() && this.lore == null && !this.hasCustomModelDataComponent() && !this.hasEnchantable() && !this.hasBlockData() && !this.hasRepairCost() && this.unhandledTags.build().isEmpty() && this.removedTags.isEmpty() && this.persistentDataContainer.isEmpty() && !this.hasItemFlags() && !this.isHideTooltip() && !this.hasTooltipStyle() && !this.hasItemModel() && !this.isUnbreakable() && !this.hasEnchantmentGlintOverride() && !this.isGlider() && !this.hasDamageResistant() && !this.hasMaxStackSize() && !this.hasRarity() && !this.hasUseRemainder() && !this.hasUseCooldown() && !this.hasFood() && !this.hasConsumable() && !this.hasTool() && !this.hasBlocksAttacks() && !this.hasWeapon() && !this.hasJukeboxPlayable() && !this.hasBreakSound() && !this.hasEquippable() && !this.hasDamage() && !this.hasMaxDamage() && !this.hasAttributeModifiers() && this.customTag == null;
   }

   public String getDisplayName() {
      return CraftChatMessage.fromComponent(this.displayName);
   }

   public final void setDisplayName(String name) {
      this.displayName = CraftChatMessage.fromStringOrNull(name);
   }

   public boolean hasDisplayName() {
      return this.displayName != null;
   }

   public String getItemName() {
      return CraftChatMessage.fromComponent(this.itemName);
   }

   public final void setItemName(String name) {
      this.itemName = CraftChatMessage.fromStringOrNull(name);
   }

   public boolean hasItemName() {
      return this.itemName != null;
   }

   public String getLocalizedName() {
      return this.getDisplayName();
   }

   public void setLocalizedName(String name) {
   }

   public boolean hasLocalizedName() {
      return false;
   }

   public boolean hasLore() {
      return this.lore != null && !this.lore.isEmpty();
   }

   public boolean hasRepairCost() {
      return this.repairCost > 0;
   }

   public boolean hasEnchant(Enchantment ench) {
      Preconditions.checkArgument(ench != null, "Enchantment cannot be null");
      return this.hasEnchants() && this.enchantments.containsKey(ench);
   }

   public int getEnchantLevel(Enchantment ench) {
      Preconditions.checkArgument(ench != null, "Enchantment cannot be null");
      Integer level = this.hasEnchants() ? (Integer)this.enchantments.get(ench) : null;
      return level == null ? 0 : level;
   }

   public Map<Enchantment, Integer> getEnchants() {
      return this.hasEnchants() ? ImmutableMap.copyOf(this.enchantments) : ImmutableMap.of();
   }

   public boolean addEnchant(Enchantment ench, int level, boolean ignoreRestrictions) {
      Preconditions.checkArgument(ench != null, "Enchantment cannot be null");
      if (this.enchantments == null) {
         this.enchantments = new LinkedHashMap(4);
      }

      if (!ignoreRestrictions && (level < ench.getStartLevel() || level > ench.getMaxLevel())) {
         return false;
      } else {
         Integer old = (Integer)this.enchantments.put(ench, level);
         return old == null || old != level;
      }
   }

   public boolean removeEnchant(Enchantment ench) {
      Preconditions.checkArgument(ench != null, "Enchantment cannot be null");
      boolean enchantmentRemoved = this.hasEnchants() && this.enchantments.remove(ench) != null;
      if (enchantmentRemoved && this.enchantments.isEmpty()) {
         this.enchantments = null;
      }

      return enchantmentRemoved;
   }

   public void removeEnchantments() {
      if (this.hasEnchants()) {
         this.enchantments.clear();
      }

   }

   public boolean hasEnchants() {
      return this.enchantments != null && !this.enchantments.isEmpty();
   }

   public boolean hasConflictingEnchant(Enchantment ench) {
      return checkConflictingEnchants(this.enchantments, ench);
   }

   public void addItemFlags(ItemFlag... hideFlags) {
      ItemFlag[] var2 = hideFlags;
      int var3 = hideFlags.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemFlag f = var2[var4];
         Collection<DataComponentType<?>> nms = CraftItemFlag.bukkitToNMS(f);
         if (nms != null) {
            if (this.hiddenComponents == null) {
               this.hiddenComponents = new LinkedHashSet();
            }

            this.hiddenComponents.addAll(nms);
         }
      }

   }

   public void removeItemFlags(ItemFlag... hideFlags) {
      if (this.hiddenComponents != null) {
         ItemFlag[] var2 = hideFlags;
         int var3 = hideFlags.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ItemFlag f = var2[var4];
            Collection<DataComponentType<?>> nms = CraftItemFlag.bukkitToNMS(f);
            if (nms != null) {
               this.hiddenComponents.removeAll(nms);
            }
         }

      }
   }

   public Set<ItemFlag> getItemFlags() {
      Set<ItemFlag> currentFlags = EnumSet.noneOf(ItemFlag.class);
      ItemFlag[] var2 = ItemFlag.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemFlag f = var2[var4];
         if (this.hasItemFlag(f)) {
            currentFlags.add(f);
         }
      }

      return currentFlags;
   }

   public boolean hasItemFlag(ItemFlag flag) {
      Collection<DataComponentType<?>> nms = CraftItemFlag.bukkitToNMS(flag);
      return nms != null && this.hiddenComponents != null && this.hiddenComponents.containsAll(nms);
   }

   public boolean hasItemFlags() {
      return this.hiddenComponents != null && !this.hiddenComponents.isEmpty();
   }

   public List<String> getLore() {
      return this.lore == null ? null : new ArrayList(Lists.transform(this.lore, CraftChatMessage::fromComponent));
   }

   public void setLore(List<String> lore) {
      if (lore != null && !lore.isEmpty()) {
         if (this.lore == null) {
            this.lore = new ArrayList(lore.size());
         } else {
            this.lore.clear();
         }

         safelyAdd(lore, this.lore, false);
      } else {
         this.lore = null;
      }

   }

   public boolean hasCustomModelData() {
      if (this.customModelData != null) {
         List<Float> floats = this.customModelData.getFloats();
         return !floats.isEmpty();
      } else {
         return false;
      }
   }

   public int getCustomModelData() {
      Preconditions.checkState(this.hasCustomModelData(), "We don't have CustomModelData! Check hasCustomModelData first!");
      return ((Float)this.customModelData.getFloats().get(0)).intValue();
   }

   public boolean hasCustomModelDataComponent() {
      return this.customModelData != null;
   }

   public CustomModelDataComponent getCustomModelDataComponent() {
      return this.hasCustomModelDataComponent() ? new CraftCustomModelDataComponent(this.customModelData) : new CraftCustomModelDataComponent(new CustomModelData(List.of(), List.of(), List.of(), List.of()));
   }

   public void setCustomModelData(Integer data) {
      this.customModelData = data == null ? null : new CraftCustomModelDataComponent(new CustomModelData(List.of(data.floatValue()), List.of(), List.of(), List.of()));
   }

   public void setCustomModelDataComponent(CustomModelDataComponent customModelData) {
      this.customModelData = customModelData == null ? null : new CraftCustomModelDataComponent((CraftCustomModelDataComponent)customModelData);
   }

   public boolean hasEnchantable() {
      return this.enchantableValue != null;
   }

   public int getEnchantable() {
      Preconditions.checkState(this.hasEnchantable(), "We don't have Enchantable! Check hasEnchantable first!");
      return this.enchantableValue;
   }

   public void setEnchantable(Integer data) {
      this.enchantableValue = data;
   }

   public boolean hasBlockData() {
      return this.blockData != null;
   }

   public BlockData getBlockData(Material material) {
      BlockState defaultData = CraftBlockType.bukkitToMinecraft(material).defaultBlockState();
      return CraftBlockData.fromData(this.hasBlockData() ? (new BlockItemStateProperties(this.blockData)).apply(defaultData) : defaultData);
   }

   public void setBlockData(BlockData blockData) {
      this.blockData = blockData == null ? null : ((CraftBlockData)blockData).toStates(true);
   }

   public int getRepairCost() {
      return this.repairCost;
   }

   public void setRepairCost(int cost) {
      this.repairCost = cost;
   }

   public boolean isHideTooltip() {
      return this.hideTooltip;
   }

   public void setHideTooltip(boolean hideTooltip) {
      this.hideTooltip = hideTooltip;
   }

   public boolean hasTooltipStyle() {
      return this.tooltipStyle != null;
   }

   public NamespacedKey getTooltipStyle() {
      return this.tooltipStyle;
   }

   public void setTooltipStyle(NamespacedKey tooltipStyle) {
      this.tooltipStyle = tooltipStyle;
   }

   public boolean hasItemModel() {
      return this.itemModel != null;
   }

   public NamespacedKey getItemModel() {
      return this.itemModel;
   }

   public void setItemModel(NamespacedKey itemModel) {
      this.itemModel = itemModel;
   }

   public boolean isUnbreakable() {
      return this.unbreakable;
   }

   public void setUnbreakable(boolean unbreakable) {
      this.unbreakable = unbreakable;
   }

   public boolean hasEnchantmentGlintOverride() {
      return this.enchantmentGlintOverride != null;
   }

   public Boolean getEnchantmentGlintOverride() {
      Preconditions.checkState(this.hasEnchantmentGlintOverride(), "We don't have enchantment_glint_override! Check hasEnchantmentGlintOverride first!");
      return this.enchantmentGlintOverride;
   }

   public void setEnchantmentGlintOverride(Boolean override) {
      this.enchantmentGlintOverride = override;
   }

   public boolean isGlider() {
      return this.glider;
   }

   public void setGlider(boolean glider) {
      this.glider = glider;
   }

   public boolean isFireResistant() {
      return this.hasDamageResistant() && DamageTypeTags.IS_FIRE.equals(this.getDamageResistant());
   }

   public void setFireResistant(boolean fireResistant) {
      this.setDamageResistant(DamageTypeTags.IS_FIRE);
   }

   public boolean hasDamageResistant() {
      return this.damageResistant != null;
   }

   public Tag<org.bukkit.damage.DamageType> getDamageResistant() {
      return this.hasDamageResistant() ? Bukkit.getTag("damage_types", CraftNamespacedKey.fromMinecraft(this.damageResistant.location()), org.bukkit.damage.DamageType.class) : null;
   }

   public void setDamageResistant(Tag<org.bukkit.damage.DamageType> tag) {
      this.damageResistant = tag != null ? ((CraftDamageTag)tag).getHandle().key() : null;
   }

   public boolean hasMaxStackSize() {
      return this.maxStackSize != null;
   }

   public int getMaxStackSize() {
      Preconditions.checkState(this.hasMaxStackSize(), "We don't have max_stack_size! Check hasMaxStackSize first!");
      return this.maxStackSize;
   }

   public void setMaxStackSize(Integer max) {
      Preconditions.checkArgument(max == null || max > 0, "max_stack_size must be > 0");
      Preconditions.checkArgument(max == null || max <= 99, "max_stack_size must be <= 99");
      this.maxStackSize = max;
   }

   public boolean hasRarity() {
      return this.rarity != null;
   }

   public ItemRarity getRarity() {
      Preconditions.checkState(this.hasRarity(), "We don't have rarity! Check hasRarity first!");
      return this.rarity;
   }

   public void setRarity(ItemRarity rarity) {
      this.rarity = rarity;
   }

   public boolean hasUseRemainder() {
      return this.useRemainder != null;
   }

   public ItemStack getUseRemainder() {
      return this.useRemainder;
   }

   public void setUseRemainder(ItemStack useRemainder) {
      this.useRemainder = useRemainder;
   }

   public boolean hasUseCooldown() {
      return this.useCooldown != null;
   }

   public UseCooldownComponent getUseCooldown() {
      return this.hasUseCooldown() ? new CraftUseCooldownComponent(this.useCooldown) : new CraftUseCooldownComponent(new UseCooldown(1.0F));
   }

   public void setUseCooldown(UseCooldownComponent cooldown) {
      this.useCooldown = cooldown == null ? null : new CraftUseCooldownComponent((CraftUseCooldownComponent)cooldown);
   }

   public boolean hasFood() {
      return this.food != null;
   }

   public FoodComponent getFood() {
      return this.hasFood() ? new CraftFoodComponent(this.food) : new CraftFoodComponent(new FoodProperties(0, 0.0F, false));
   }

   public void setFood(FoodComponent food) {
      this.food = food == null ? null : new CraftFoodComponent((CraftFoodComponent)food);
   }

   public boolean hasConsumable() {
      return this.consumable != null;
   }

   public ConsumableComponent getConsumable() {
      return this.hasConsumable() ? new CraftConsumableComponent(this.consumable) : new CraftConsumableComponent(new Consumable(1.6F, ItemUseAnimation.EAT, SoundEvents.GENERIC_EAT, true, List.of()));
   }

   public void setConsumable(ConsumableComponent consumable) {
      this.consumable = consumable == null ? null : new CraftConsumableComponent((CraftConsumableComponent)consumable);
   }

   public boolean hasTool() {
      return this.tool != null;
   }

   public ToolComponent getTool() {
      return this.hasTool() ? new CraftToolComponent(this.tool) : new CraftToolComponent(new Tool(Collections.emptyList(), 1.0F, 1, true));
   }

   public void setTool(ToolComponent tool) {
      this.tool = tool == null ? null : new CraftToolComponent((CraftToolComponent)tool);
   }

   public boolean hasBlocksAttacks() {
      return this.blocksAttacks != null;
   }

   public BlocksAttacksComponent getBlocksAttacks() {
      return this.hasBlocksAttacks() ? new CraftBlocksAttacksComponent(this.blocksAttacks) : new CraftBlocksAttacksComponent(new BlocksAttacks(0.0F, 1.0F, Collections.emptyList(), new BlocksAttacks.ItemDamageFunction(0.0F, 0.0F, 0.0F), Optional.empty(), Optional.empty(), Optional.empty()));
   }

   public void setBlocksAttacks(BlocksAttacksComponent blocksAttacks) {
      this.blocksAttacks = blocksAttacks == null ? null : new CraftBlocksAttacksComponent((CraftBlocksAttacksComponent)blocksAttacks);
   }

   public boolean hasWeapon() {
      return this.weapon != null;
   }

   public WeaponComponent getWeapon() {
      return this.hasWeapon() ? new CraftWeaponComponent(this.weapon) : new CraftWeaponComponent(new Weapon(0));
   }

   public void setWeapon(WeaponComponent weapon) {
      this.weapon = weapon == null ? null : new CraftWeaponComponent((CraftWeaponComponent)weapon);
   }

   public boolean hasEquippable() {
      return this.equippable != null;
   }

   public EquippableComponent getEquippable() {
      return this.hasEquippable() ? new CraftEquippableComponent(this.equippable) : new CraftEquippableComponent(Equippable.builder(net.minecraft.world.entity.EquipmentSlot.HEAD).build());
   }

   public void setEquippable(EquippableComponent equippable) {
      this.equippable = equippable == null ? null : new CraftEquippableComponent((CraftEquippableComponent)equippable);
   }

   public boolean hasJukeboxPlayable() {
      return this.jukebox != null;
   }

   public JukeboxPlayableComponent getJukeboxPlayable() {
      return this.hasJukeboxPlayable() ? new CraftJukeboxComponent(this.jukebox) : new CraftJukeboxComponent(new JukeboxPlayable(new EitherHolder(JukeboxSongs.THIRTEEN)));
   }

   public void setJukeboxPlayable(JukeboxPlayableComponent jukeboxPlayable) {
      this.jukebox = jukeboxPlayable == null ? null : new CraftJukeboxComponent((CraftJukeboxComponent)jukeboxPlayable);
   }

   public boolean hasBreakSound() {
      return this.breakSound != null;
   }

   public Sound getBreakSound() {
      return this.breakSound != null ? CraftSound.minecraftHolderToBukkit(this.breakSound) : null;
   }

   public void setBreakSound(Sound sound) {
      this.breakSound = sound != null ? CraftSound.bukkitToMinecraftHolder(sound) : null;
   }

   public boolean hasAttributeModifiers() {
      return this.attributeModifiers != null && !this.attributeModifiers.isEmpty();
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
      return this.hasAttributeModifiers() ? ImmutableMultimap.copyOf(this.attributeModifiers) : null;
   }

   private void checkAttributeList() {
      if (this.attributeModifiers == null) {
         this.attributeModifiers = LinkedHashMultimap.create();
      }

   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nullable EquipmentSlot slot) {
      this.checkAttributeList();
      SetMultimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
      Iterator var3 = this.attributeModifiers.entries().iterator();

      while(true) {
         Map.Entry entry;
         do {
            if (!var3.hasNext()) {
               return result;
            }

            entry = (Map.Entry)var3.next();
         } while(((AttributeModifier)entry.getValue()).getSlot() != null && ((AttributeModifier)entry.getValue()).getSlot() != slot);

         result.put((Attribute)entry.getKey(), (AttributeModifier)entry.getValue());
      }
   }

   public Collection<AttributeModifier> getAttributeModifiers(@Nonnull Attribute attribute) {
      Preconditions.checkNotNull(attribute, "Attribute cannot be null");
      return this.attributeModifiers.containsKey(attribute) ? ImmutableList.copyOf(this.attributeModifiers.get(attribute)) : null;
   }

   public boolean addAttributeModifier(@Nonnull Attribute attribute, @Nonnull AttributeModifier modifier) {
      Preconditions.checkNotNull(attribute, "Attribute cannot be null");
      Preconditions.checkNotNull(modifier, "AttributeModifier cannot be null");
      this.checkAttributeList();
      Iterator var3 = this.attributeModifiers.entries().iterator();

      while(var3.hasNext()) {
         Map.Entry<Attribute, AttributeModifier> entry = (Map.Entry)var3.next();
         Preconditions.checkArgument(!((AttributeModifier)entry.getValue()).getKey().equals(modifier.getKey()), "Cannot register AttributeModifier. Modifier is already applied! %s", modifier);
      }

      return this.attributeModifiers.put(attribute, modifier);
   }

   public void setAttributeModifiers(@Nullable Multimap<Attribute, AttributeModifier> attributeModifiers) {
      if (attributeModifiers != null && !attributeModifiers.isEmpty()) {
         this.checkAttributeList();
         this.attributeModifiers.clear();
         Iterator<Map.Entry<Attribute, AttributeModifier>> iterator = attributeModifiers.entries().iterator();

         while(true) {
            while(iterator.hasNext()) {
               Map.Entry<Attribute, AttributeModifier> next = (Map.Entry)iterator.next();
               if (next.getKey() != null && next.getValue() != null) {
                  this.attributeModifiers.put((Attribute)next.getKey(), (AttributeModifier)next.getValue());
               } else {
                  iterator.remove();
               }
            }

            return;
         }
      } else {
         this.attributeModifiers = LinkedHashMultimap.create();
      }
   }

   public boolean removeAttributeModifier(@Nonnull Attribute attribute) {
      Preconditions.checkNotNull(attribute, "Attribute cannot be null");
      this.checkAttributeList();
      return !this.attributeModifiers.removeAll(attribute).isEmpty();
   }

   public boolean removeAttributeModifier(@Nullable EquipmentSlot slot) {
      this.checkAttributeList();
      int removed = 0;
      Iterator<Map.Entry<Attribute, AttributeModifier>> iter = this.attributeModifiers.entries().iterator();

      while(true) {
         Map.Entry entry;
         do {
            if (!iter.hasNext()) {
               return removed > 0;
            }

            entry = (Map.Entry)iter.next();
         } while(((AttributeModifier)entry.getValue()).getSlot() != null && ((AttributeModifier)entry.getValue()).getSlot() != slot);

         iter.remove();
         ++removed;
      }
   }

   public boolean removeAttributeModifier(@Nonnull Attribute attribute, @Nonnull AttributeModifier modifier) {
      Preconditions.checkNotNull(attribute, "Attribute cannot be null");
      Preconditions.checkNotNull(modifier, "AttributeModifier cannot be null");
      this.checkAttributeList();
      int removed = 0;
      Iterator<Map.Entry<Attribute, AttributeModifier>> iter = this.attributeModifiers.entries().iterator();

      while(true) {
         while(iter.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = (Map.Entry)iter.next();
            if (entry.getKey() != null && entry.getValue() != null) {
               if (entry.getKey() == attribute && ((AttributeModifier)entry.getValue()).getKey().equals(modifier.getKey())) {
                  iter.remove();
                  ++removed;
               }
            } else {
               iter.remove();
               ++removed;
            }
         }

         return removed > 0;
      }
   }

   public String getAsString() {
      Applicator tag = new Applicator();
      this.applyToItem(tag);
      DataComponentPatch patch = tag.build();
      net.minecraft.nbt.Tag nbt = (net.minecraft.nbt.Tag)DataComponentPatch.CODEC.encodeStart(MinecraftServer.getDefaultRegistryAccess().createSerializationContext(NbtOps.INSTANCE), patch).getOrThrow();
      return nbt.toString();
   }

   public String getAsComponentString() {
      Applicator tag = new Applicator();
      this.applyToItem(tag);
      DataComponentPatch patch = tag.build();
      RegistryAccess registryAccess = CraftRegistry.getMinecraftRegistry();
      DynamicOps<net.minecraft.nbt.Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);
      net.minecraft.core.Registry<DataComponentType<?>> componentTypeRegistry = registryAccess.lookupOrThrow(Registries.DATA_COMPONENT_TYPE);
      StringJoiner componentString = new StringJoiner(",", "[", "]");
      Iterator var7 = patch.entrySet().iterator();

      while(var7.hasNext()) {
         Map.Entry<DataComponentType<?>, Optional<?>> entry = (Map.Entry)var7.next();
         DataComponentType<?> componentType = (DataComponentType)entry.getKey();
         Optional<?> componentValue = (Optional)entry.getValue();
         String componentKey = ((ResourceKey)componentTypeRegistry.getResourceKey(componentType).orElseThrow()).location().toString();
         if (componentValue.isPresent()) {
            net.minecraft.nbt.Tag componentValueAsNBT = (net.minecraft.nbt.Tag)componentType.codecOrThrow().encodeStart(ops, componentValue.get()).getOrThrow();
            String componentValueAsNBTString = (new SnbtPrinterTagVisitor("", 0, new ArrayList())).visit(componentValueAsNBT);
            componentString.add(componentKey + "=" + componentValueAsNBTString);
         } else {
            componentString.add("!" + componentKey);
         }
      }

      return componentString.toString();
   }

   public CustomItemTagContainer getCustomTagContainer() {
      return new DeprecatedCustomTagContainer(this.getPersistentDataContainer());
   }

   public PersistentDataContainer getPersistentDataContainer() {
      return this.persistentDataContainer;
   }

   private static boolean compareModifiers(Multimap<Attribute, AttributeModifier> first, Multimap<Attribute, AttributeModifier> second) {
      if (first != null && second != null) {
         Iterator var2 = first.entries().iterator();

         Map.Entry entry;
         do {
            if (!var2.hasNext()) {
               var2 = second.entries().iterator();

               do {
                  if (!var2.hasNext()) {
                     return true;
                  }

                  entry = (Map.Entry)var2.next();
               } while(first.containsEntry(entry.getKey(), entry.getValue()));

               return false;
            }

            entry = (Map.Entry)var2.next();
         } while(second.containsEntry(entry.getKey(), entry.getValue()));

         return false;
      } else {
         return false;
      }
   }

   public boolean hasDamage() {
      return this.damage > 0;
   }

   public int getDamage() {
      return this.damage;
   }

   public void setDamage(int damage) {
      this.damage = damage;
   }

   public boolean hasMaxDamage() {
      return this.maxDamage != null;
   }

   public int getMaxDamage() {
      Preconditions.checkState(this.hasMaxDamage(), "We don't have max_damage! Check hasMaxDamage first!");
      return this.maxDamage;
   }

   public void setMaxDamage(Integer maxDamage) {
      this.maxDamage = maxDamage;
   }

   public final boolean equals(Object object) {
      if (object == null) {
         return false;
      } else if (object == this) {
         return true;
      } else {
         return !(object instanceof CraftMetaItem) ? false : CraftItemFactory.instance().equals((ItemMeta)this, (ItemMeta)((ItemMeta)object));
      }
   }

   @Overridden
   boolean equalsCommon(CraftMetaItem that) {
      boolean var10000;
      label342: {
         if (this.hasDisplayName()) {
            if (!that.hasDisplayName() || !this.displayName.equals(that.displayName)) {
               break label342;
            }
         } else if (that.hasDisplayName()) {
            break label342;
         }

         if (this.hasItemName()) {
            if (!that.hasItemName() || !this.itemName.equals(that.itemName)) {
               break label342;
            }
         } else if (that.hasItemName()) {
            break label342;
         }

         if (this.hasEnchants()) {
            if (!that.hasEnchants() || !this.enchantments.equals(that.enchantments)) {
               break label342;
            }
         } else if (that.hasEnchants()) {
            break label342;
         }

         if (Objects.equals(this.lore, that.lore)) {
            label343: {
               if (this.hasCustomModelDataComponent()) {
                  if (!that.hasCustomModelDataComponent() || !this.customModelData.equals(that.customModelData)) {
                     break label343;
                  }
               } else if (that.hasCustomModelDataComponent()) {
                  break label343;
               }

               if (this.hasEnchantable()) {
                  if (!that.hasEnchantable() || !this.enchantableValue.equals(that.enchantableValue)) {
                     break label343;
                  }
               } else if (that.hasEnchantable()) {
                  break label343;
               }

               if (this.hasBlockData()) {
                  if (!that.hasBlockData() || !this.blockData.equals(that.blockData)) {
                     break label343;
                  }
               } else if (that.hasBlockData()) {
                  break label343;
               }

               if (this.hasRepairCost()) {
                  if (!that.hasRepairCost() || this.repairCost != that.repairCost) {
                     break label343;
                  }
               } else if (that.hasRepairCost()) {
                  break label343;
               }

               if (this.hasAttributeModifiers()) {
                  if (!that.hasAttributeModifiers() || !compareModifiers(this.attributeModifiers, that.attributeModifiers)) {
                     break label343;
                  }
               } else if (that.hasAttributeModifiers()) {
                  break label343;
               }

               if (this.unhandledTags.equals(that.unhandledTags) && this.removedTags.equals(that.removedTags) && Objects.equals(this.customTag, that.customTag) && this.persistentDataContainer.equals(that.persistentDataContainer)) {
                  label278: {
                     if (this.hasItemFlags()) {
                        if (!that.hasItemFlags() || !this.hiddenComponents.equals(that.hiddenComponents)) {
                           break label278;
                        }
                     } else if (that.hasItemFlags()) {
                        break label278;
                     }

                     if (this.isHideTooltip() == that.isHideTooltip()) {
                        label344: {
                           if (this.hasTooltipStyle()) {
                              if (!that.hasTooltipStyle() || !this.tooltipStyle.equals(that.tooltipStyle)) {
                                 break label344;
                              }
                           } else if (that.hasTooltipStyle()) {
                              break label344;
                           }

                           if (this.hasItemModel()) {
                              if (!that.hasItemModel() || !this.itemModel.equals(that.itemModel)) {
                                 break label344;
                              }
                           } else if (that.hasItemModel()) {
                              break label344;
                           }

                           if (this.isUnbreakable() == that.isUnbreakable()) {
                              label255: {
                                 if (this.hasEnchantmentGlintOverride()) {
                                    if (!that.hasEnchantmentGlintOverride() || !this.enchantmentGlintOverride.equals(that.enchantmentGlintOverride)) {
                                       break label255;
                                    }
                                 } else if (that.hasEnchantmentGlintOverride()) {
                                    break label255;
                                 }

                                 if (this.glider == that.glider) {
                                    label345: {
                                       if (this.hasDamageResistant()) {
                                          if (!that.hasDamageResistant() || !this.damageResistant.equals(that.damageResistant)) {
                                             break label345;
                                          }
                                       } else if (that.hasDamageResistant()) {
                                          break label345;
                                       }

                                       if (this.hasMaxStackSize()) {
                                          if (!that.hasMaxStackSize() || !this.maxStackSize.equals(that.maxStackSize)) {
                                             break label345;
                                          }
                                       } else if (that.hasMaxStackSize()) {
                                          break label345;
                                       }

                                       if (this.rarity == that.rarity) {
                                          label346: {
                                             if (this.hasUseRemainder()) {
                                                if (!that.hasUseRemainder() || !this.useRemainder.equals(that.useRemainder)) {
                                                   break label346;
                                                }
                                             } else if (that.hasUseRemainder()) {
                                                break label346;
                                             }

                                             if (this.hasUseCooldown()) {
                                                if (!that.hasUseCooldown() || !this.useCooldown.equals(that.useCooldown)) {
                                                   break label346;
                                                }
                                             } else if (that.hasUseCooldown()) {
                                                break label346;
                                             }

                                             if (this.hasFood()) {
                                                if (!that.hasFood() || !this.food.equals(that.food)) {
                                                   break label346;
                                                }
                                             } else if (that.hasFood()) {
                                                break label346;
                                             }

                                             if (this.hasConsumable()) {
                                                if (!that.hasConsumable() || !this.consumable.equals(that.consumable)) {
                                                   break label346;
                                                }
                                             } else if (that.hasConsumable()) {
                                                break label346;
                                             }

                                             if (this.hasTool()) {
                                                if (!that.hasTool() || !this.tool.equals(that.tool)) {
                                                   break label346;
                                                }
                                             } else if (that.hasTool()) {
                                                break label346;
                                             }

                                             if (this.hasBlocksAttacks()) {
                                                if (!that.hasBlocksAttacks() || !this.blocksAttacks.equals(that.blocksAttacks)) {
                                                   break label346;
                                                }
                                             } else if (that.hasBlocksAttacks()) {
                                                break label346;
                                             }

                                             if (this.hasWeapon()) {
                                                if (!that.hasWeapon() || !this.weapon.equals(that.weapon)) {
                                                   break label346;
                                                }
                                             } else if (that.hasWeapon()) {
                                                break label346;
                                             }

                                             if (this.hasEquippable()) {
                                                if (!that.hasEquippable() || !this.equippable.equals(that.equippable)) {
                                                   break label346;
                                                }
                                             } else if (that.hasEquippable()) {
                                                break label346;
                                             }

                                             if (this.hasJukeboxPlayable()) {
                                                if (!that.hasJukeboxPlayable() || !this.jukebox.equals(that.jukebox)) {
                                                   break label346;
                                                }
                                             } else if (that.hasJukeboxPlayable()) {
                                                break label346;
                                             }

                                             if (this.hasBreakSound()) {
                                                if (!that.hasBreakSound() || !this.breakSound.equals(that.breakSound)) {
                                                   break label346;
                                                }
                                             } else if (that.hasBreakSound()) {
                                                break label346;
                                             }

                                             if (this.hasDamage()) {
                                                if (!that.hasDamage() || this.damage != that.damage) {
                                                   break label346;
                                                }
                                             } else if (that.hasDamage()) {
                                                break label346;
                                             }

                                             if (this.hasMaxDamage()) {
                                                if (!that.hasMaxDamage() || !this.maxDamage.equals(that.maxDamage)) {
                                                   break label346;
                                                }
                                             } else if (that.hasMaxDamage()) {
                                                break label346;
                                             }

                                             if (this.version == that.version) {
                                                var10000 = true;
                                                return var10000;
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      var10000 = false;
      return var10000;
   }

   @Overridden
   boolean notUncommon(CraftMetaItem meta) {
      return true;
   }

   public final int hashCode() {
      return this.applyHash();
   }

   @Overridden
   int applyHash() {
      int hash = 3;
      hash = 61 * hash + (this.hasDisplayName() ? this.displayName.hashCode() : 0);
      hash = 61 * hash + (this.hasItemName() ? this.itemName.hashCode() : 0);
      hash = 61 * hash + (this.lore != null ? this.lore.hashCode() : 0);
      hash = 61 * hash + (this.hasCustomModelDataComponent() ? this.customModelData.hashCode() : 0);
      hash = 61 * hash + (this.hasEnchantable() ? this.enchantableValue.hashCode() : 0);
      hash = 61 * hash + (this.hasBlockData() ? this.blockData.hashCode() : 0);
      hash = 61 * hash + (this.hasEnchants() ? this.enchantments.hashCode() : 0);
      hash = 61 * hash + (this.hasRepairCost() ? this.repairCost : 0);
      hash = 61 * hash + this.unhandledTags.hashCode();
      hash = 61 * hash + this.removedTags.hashCode();
      hash = 61 * hash + (this.customTag != null ? this.customTag.hashCode() : 0);
      hash = 61 * hash + (!this.persistentDataContainer.isEmpty() ? this.persistentDataContainer.hashCode() : 0);
      hash = 61 * hash + (this.hasItemFlags() ? this.hiddenComponents.hashCode() : 0);
      hash = 61 * hash + (this.isHideTooltip() ? 1231 : 1237);
      hash = 61 * hash + (this.hasTooltipStyle() ? this.tooltipStyle.hashCode() : 0);
      hash = 61 * hash + (this.hasItemModel() ? this.itemModel.hashCode() : 0);
      hash = 61 * hash + (this.isUnbreakable() ? 1231 : 1237);
      hash = 61 * hash + (this.hasEnchantmentGlintOverride() ? this.enchantmentGlintOverride.hashCode() : 0);
      hash = 61 * hash + (this.isGlider() ? 1231 : 1237);
      hash = 61 * hash + (this.hasDamageResistant() ? this.damageResistant.hashCode() : 0);
      hash = 61 * hash + (this.hasMaxStackSize() ? this.maxStackSize.hashCode() : 0);
      hash = 61 * hash + (this.hasRarity() ? this.rarity.hashCode() : 0);
      hash = 61 * hash + (this.hasUseRemainder() ? this.useRemainder.hashCode() : 0);
      hash = 61 * hash + (this.hasUseCooldown() ? this.useCooldown.hashCode() : 0);
      hash = 61 * hash + (this.hasFood() ? this.food.hashCode() : 0);
      hash = 61 * hash + (this.hasConsumable() ? this.consumable.hashCode() : 0);
      hash = 61 * hash + (this.hasTool() ? this.tool.hashCode() : 0);
      hash = 61 * hash + (this.hasBlocksAttacks() ? this.blocksAttacks.hashCode() : 0);
      hash = 61 * hash + (this.hasWeapon() ? this.weapon.hashCode() : 0);
      hash = 61 * hash + (this.hasJukeboxPlayable() ? this.jukebox.hashCode() : 0);
      hash = 61 * hash + (this.hasBreakSound() ? this.breakSound.hashCode() : 0);
      hash = 61 * hash + (this.hasEquippable() ? this.equippable.hashCode() : 0);
      hash = 61 * hash + (this.hasDamage() ? this.damage : 0);
      hash = 61 * hash + (this.hasMaxDamage() ? 1231 : 1237);
      hash = 61 * hash + (this.hasAttributeModifiers() ? this.attributeModifiers.hashCode() : 0);
      hash = 61 * hash + this.version;
      return hash;
   }

   @Overridden
   public CraftMetaItem clone() {
      try {
         CraftMetaItem clone = (CraftMetaItem)super.clone();
         if (this.lore != null) {
            clone.lore = new ArrayList(this.lore);
         }

         if (this.hasCustomModelDataComponent()) {
            clone.customModelData = new CraftCustomModelDataComponent(this.customModelData);
         }

         clone.enchantableValue = this.enchantableValue;
         clone.blockData = this.blockData;
         if (this.enchantments != null) {
            clone.enchantments = new LinkedHashMap(this.enchantments);
         }

         if (this.hasAttributeModifiers()) {
            clone.attributeModifiers = LinkedHashMultimap.create(this.attributeModifiers);
         }

         if (this.customTag != null) {
            clone.customTag = this.customTag.copy();
         }

         clone.removedTags = Sets.newHashSet(this.removedTags);
         clone.persistentDataContainer = new CraftPersistentDataContainer(this.persistentDataContainer.getRaw(), DATA_TYPE_REGISTRY);
         if (this.hasItemFlags()) {
            clone.hiddenComponents = new LinkedHashSet(this.hiddenComponents);
         }

         clone.hideTooltip = this.hideTooltip;
         clone.tooltipStyle = this.tooltipStyle;
         clone.itemModel = this.itemModel;
         clone.unbreakable = this.unbreakable;
         clone.enchantmentGlintOverride = this.enchantmentGlintOverride;
         clone.glider = this.glider;
         clone.damageResistant = this.damageResistant;
         clone.maxStackSize = this.maxStackSize;
         clone.rarity = this.rarity;
         if (this.hasUseRemainder()) {
            clone.useRemainder = this.useRemainder.clone();
         }

         if (this.hasUseCooldown()) {
            clone.useCooldown = new CraftUseCooldownComponent(this.useCooldown);
         }

         if (this.hasFood()) {
            clone.food = new CraftFoodComponent(this.food);
         }

         if (this.hasConsumable()) {
            clone.consumable = new CraftConsumableComponent(this.consumable);
         }

         if (this.hasTool()) {
            clone.tool = new CraftToolComponent(this.tool);
         }

         if (this.hasBlocksAttacks()) {
            clone.blocksAttacks = new CraftBlocksAttacksComponent(this.blocksAttacks);
         }

         if (this.hasWeapon()) {
            clone.weapon = new CraftWeaponComponent(this.weapon);
         }

         if (this.hasEquippable()) {
            clone.equippable = new CraftEquippableComponent(this.equippable);
         }

         if (this.hasJukeboxPlayable()) {
            clone.jukebox = new CraftJukeboxComponent(this.jukebox);
         }

         clone.breakSound = this.breakSound;
         clone.damage = this.damage;
         clone.maxDamage = this.maxDamage;
         clone.version = this.version;
         return clone;
      } catch (CloneNotSupportedException var2) {
         CloneNotSupportedException e = var2;
         throw new Error(e);
      }
   }

   public final Map<String, Object> serialize() {
      ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();
      map.put("meta-type", SerializableMeta.classMap.get(this.getClass()));
      this.serialize(map);
      return map.build();
   }

   @Overridden
   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      if (this.hasDisplayName()) {
         builder.put(NAME.BUKKIT, CraftChatMessage.toJSON(this.displayName));
      }

      if (this.hasItemName()) {
         builder.put(ITEM_NAME.BUKKIT, CraftChatMessage.toJSON(this.itemName));
      }

      ArrayList hideFlags;
      Iterator var3;
      if (this.hasLore()) {
         hideFlags = new ArrayList();
         var3 = this.lore.iterator();

         while(var3.hasNext()) {
            Component component = (Component)var3.next();
            hideFlags.add(CraftChatMessage.toJSON(component));
         }

         builder.put(LORE.BUKKIT, hideFlags);
      }

      if (this.hasCustomModelDataComponent()) {
         builder.put(CUSTOM_MODEL_DATA.BUKKIT, this.customModelData);
      }

      if (this.hasEnchantable()) {
         builder.put(ENCHANTABLE.BUKKIT, this.enchantableValue);
      }

      if (this.hasBlockData()) {
         builder.put(BLOCK_DATA.BUKKIT, this.blockData);
      }

      serializeEnchantments(this.enchantments, builder, ENCHANTMENTS);
      serializeModifiers(this.attributeModifiers, builder, ATTRIBUTES);
      if (this.hasRepairCost()) {
         builder.put(REPAIR.BUKKIT, this.repairCost);
      }

      hideFlags = new ArrayList();
      var3 = this.getItemFlags().iterator();

      while(var3.hasNext()) {
         ItemFlag hideFlagEnum = (ItemFlag)var3.next();
         hideFlags.add(CraftItemFlag.bukkitToString(hideFlagEnum));
      }

      if (!hideFlags.isEmpty()) {
         builder.put(HIDEFLAGS.BUKKIT, hideFlags);
      }

      if (this.isHideTooltip()) {
         builder.put(HIDE_TOOLTIP.BUKKIT, this.hideTooltip);
      }

      if (this.hasTooltipStyle()) {
         builder.put(TOOLTIP_STYLE.BUKKIT, this.tooltipStyle.toString());
      }

      if (this.hasItemModel()) {
         builder.put(ITEM_MODEL.BUKKIT, this.itemModel.toString());
      }

      if (this.isUnbreakable()) {
         builder.put(UNBREAKABLE.BUKKIT, this.unbreakable);
      }

      if (this.hasEnchantmentGlintOverride()) {
         builder.put(ENCHANTMENT_GLINT_OVERRIDE.BUKKIT, this.enchantmentGlintOverride);
      }

      if (this.isGlider()) {
         builder.put(GLIDER.BUKKIT, this.glider);
      }

      if (this.hasDamageResistant()) {
         builder.put(DAMAGE_RESISTANT.BUKKIT, this.damageResistant.location().toString());
      }

      if (this.hasMaxStackSize()) {
         builder.put(MAX_STACK_SIZE.BUKKIT, this.maxStackSize);
      }

      if (this.hasRarity()) {
         builder.put(RARITY.BUKKIT, this.rarity.name());
      }

      if (this.hasUseRemainder()) {
         builder.put(USE_REMAINDER.BUKKIT, this.useRemainder);
      }

      if (this.hasUseCooldown()) {
         builder.put(USE_COOLDOWN.BUKKIT, this.useCooldown);
      }

      if (this.hasFood()) {
         builder.put(FOOD.BUKKIT, this.food);
      }

      if (this.hasConsumable()) {
         builder.put(CONSUMABLE.BUKKIT, this.consumable);
      }

      if (this.hasTool()) {
         builder.put(TOOL.BUKKIT, this.tool);
      }

      if (this.hasBlocksAttacks()) {
         builder.put(BLOCKS_ATTACKS.BUKKIT, this.blocksAttacks);
      }

      if (this.hasWeapon()) {
         builder.put(WEAPON.BUKKIT, this.weapon);
      }

      if (this.hasEquippable()) {
         builder.put(EQUIPPABLE.BUKKIT, this.equippable);
      }

      if (this.hasJukeboxPlayable()) {
         builder.put(JUKEBOX_PLAYABLE.BUKKIT, this.jukebox);
      }

      if (this.hasBreakSound()) {
         builder.put(BREAK_SOUND.BUKKIT, this.getBreakSound().getKey().toString());
      }

      if (this.hasDamage()) {
         builder.put(DAMAGE.BUKKIT, this.damage);
      }

      if (this.hasMaxDamage()) {
         builder.put(MAX_DAMAGE.BUKKIT, this.maxDamage);
      }

      Map<String, net.minecraft.nbt.Tag> internalTags = new HashMap();
      this.serializeInternal(internalTags);
      IOException ex;
      ByteArrayOutputStream buf;
      if (!internalTags.isEmpty()) {
         CompoundTag internal = new CompoundTag();
         Iterator var5 = internalTags.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<String, net.minecraft.nbt.Tag> e = (Map.Entry)var5.next();
            internal.put((String)e.getKey(), (net.minecraft.nbt.Tag)e.getValue());
         }

         try {
            buf = new ByteArrayOutputStream();
            NbtIo.writeCompressed(internal, buf);
            builder.put("internal", Base64.getEncoder().encodeToString(buf.toByteArray()));
         } catch (IOException var12) {
            ex = var12;
            Logger.getLogger(CraftMetaItem.class.getName()).log(Level.SEVERE, (String)null, ex);
         }
      }

      if (!this.unhandledTags.isEmpty()) {
         net.minecraft.nbt.Tag unhandled = (net.minecraft.nbt.Tag)DataComponentPatch.CODEC.encodeStart(MinecraftServer.getDefaultRegistryAccess().createSerializationContext(NbtOps.INSTANCE), this.unhandledTags.build()).getOrThrow(IllegalStateException::new);

         try {
            buf = new ByteArrayOutputStream();
            NbtIo.writeCompressed((CompoundTag)unhandled, buf);
            builder.put("unhandled", Base64.getEncoder().encodeToString(buf.toByteArray()));
         } catch (IOException var11) {
            ex = var11;
            Logger.getLogger(CraftMetaItem.class.getName()).log(Level.SEVERE, (String)null, ex);
         }
      }

      if (!this.removedTags.isEmpty()) {
         RegistryAccess registryAccess = CraftRegistry.getMinecraftRegistry();
         net.minecraft.core.Registry<DataComponentType<?>> componentTypeRegistry = registryAccess.lookupOrThrow(Registries.DATA_COMPONENT_TYPE);
         List<String> removedTags = new ArrayList();
         Iterator var7 = this.removedTags.iterator();

         while(var7.hasNext()) {
            DataComponentType<?> removed = (DataComponentType)var7.next();
            String componentKey = ((ResourceKey)componentTypeRegistry.getResourceKey(removed).orElseThrow()).location().toString();
            removedTags.add(componentKey);
         }

         builder.put("removed", removedTags);
      }

      if (!this.persistentDataContainer.isEmpty()) {
         builder.put(BUKKIT_CUSTOM_TAG.BUKKIT, this.persistentDataContainer.serialize());
      }

      if (this.customTag != null) {
         try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            NbtIo.writeCompressed(this.customTag, buf);
            builder.put("custom", Base64.getEncoder().encodeToString(buf.toByteArray()));
         } catch (IOException var10) {
            IOException ex = var10;
            Logger.getLogger(CraftMetaItem.class.getName()).log(Level.SEVERE, (String)null, ex);
         }
      }

      return builder;
   }

   void serializeInternal(Map<String, net.minecraft.nbt.Tag> unhandledTags) {
   }

   static void serializeEnchantments(Map<Enchantment, Integer> enchantments, ImmutableMap.Builder<String, Object> builder, ItemMetaKey key) {
      if (enchantments != null && !enchantments.isEmpty()) {
         ImmutableMap.Builder<String, Integer> enchants = ImmutableMap.builder();
         Iterator var4 = enchantments.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<? extends Enchantment, Integer> enchant = (Map.Entry)var4.next();
            enchants.put(CraftEnchantment.bukkitToString((Enchantment)enchant.getKey()), (Integer)enchant.getValue());
         }

         builder.put(key.BUKKIT, enchants.build());
      }
   }

   static void serializeModifiers(Multimap<Attribute, AttributeModifier> modifiers, ImmutableMap.Builder<String, Object> builder, ItemMetaKey key) {
      if (modifiers != null && !modifiers.isEmpty()) {
         Map<String, List<Object>> mods = new LinkedHashMap();
         Iterator var4 = modifiers.entries().iterator();

         while(var4.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = (Map.Entry)var4.next();
            if (entry.getKey() != null) {
               Collection<AttributeModifier> modCollection = modifiers.get((Attribute)entry.getKey());
               if (modCollection != null && !modCollection.isEmpty()) {
                  mods.put(CraftAttribute.bukkitToString((Attribute)entry.getKey()), new ArrayList(modCollection));
               }
            }
         }

         builder.put(key.BUKKIT, mods);
      }
   }

   static void safelyAdd(Iterable<?> addFrom, Collection<Component> addTo, boolean possiblyJsonInput) {
      if (addFrom != null) {
         Iterator var3 = addFrom.iterator();

         while(var3.hasNext()) {
            Object object = var3.next();
            if (!(object instanceof String)) {
               if (object != null) {
                  String var10002 = String.valueOf(addFrom);
                  throw new IllegalArgumentException(var10002 + " cannot contain non-string " + object.getClass().getName());
               }

               addTo.add(Component.empty());
            } else {
               String entry = object.toString();
               Component component = possiblyJsonInput ? CraftChatMessage.fromJSONOrString(entry) : CraftChatMessage.fromStringOrNull(entry);
               if (component != null) {
                  addTo.add(component);
               } else {
                  addTo.add(Component.empty());
               }
            }
         }

      }
   }

   static boolean checkConflictingEnchants(Map<Enchantment, Integer> enchantments, Enchantment ench) {
      if (enchantments != null && !enchantments.isEmpty()) {
         Iterator var2 = enchantments.keySet().iterator();

         Enchantment enchant;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            enchant = (Enchantment)var2.next();
         } while(!enchant.conflictsWith(ench));

         return true;
      } else {
         return false;
      }
   }

   public final String toString() {
      String var10000 = (String)SerializableMeta.classMap.get(this.getClass());
      return var10000 + "_META:" + String.valueOf(this.serialize());
   }

   public int getVersion() {
      return this.version;
   }

   public void setVersion(int version) {
      this.version = version;
   }

   public static Set<DataComponentType> getHandledTags() {
      synchronized(HANDLED_TAGS) {
         if (HANDLED_TAGS.isEmpty()) {
            HANDLED_TAGS.addAll(Arrays.asList(NAME.TYPE, ITEM_NAME.TYPE, LORE.TYPE, CUSTOM_MODEL_DATA.TYPE, ENCHANTABLE.TYPE, BLOCK_DATA.TYPE, REPAIR.TYPE, ENCHANTMENTS.TYPE, HIDEFLAGS.TYPE, TOOLTIP_STYLE.TYPE, ITEM_MODEL.TYPE, UNBREAKABLE.TYPE, ENCHANTMENT_GLINT_OVERRIDE.TYPE, GLIDER.TYPE, DAMAGE_RESISTANT.TYPE, MAX_STACK_SIZE.TYPE, RARITY.TYPE, USE_REMAINDER.TYPE, USE_COOLDOWN.TYPE, FOOD.TYPE, CONSUMABLE.TYPE, TOOL.TYPE, BLOCKS_ATTACKS.TYPE, WEAPON.TYPE, EQUIPPABLE.TYPE, JUKEBOX_PLAYABLE.TYPE, BREAK_SOUND.TYPE, DAMAGE.TYPE, MAX_DAMAGE.TYPE, CUSTOM_DATA.TYPE, ATTRIBUTES.TYPE, CraftMetaArmor.TRIM.TYPE, CraftMetaArmorStand.ENTITY_TAG.TYPE, CraftMetaBanner.PATTERNS.TYPE, CraftMetaEntityTag.ENTITY_TAG.TYPE, CraftMetaLeatherArmor.COLOR.TYPE, CraftMetaMap.MAP_POST_PROCESSING.TYPE, CraftMetaMap.MAP_COLOR.TYPE, CraftMetaMap.MAP_ID.TYPE, CraftMetaPotion.POTION_CONTENTS.TYPE, CraftMetaShield.BASE_COLOR.TYPE, CraftMetaSkull.SKULL_PROFILE.TYPE, CraftMetaSkull.NOTE_BLOCK_SOUND.TYPE, CraftMetaSpawnEgg.ENTITY_TAG.TYPE, CraftMetaBlockState.BLOCK_ENTITY_TAG.TYPE, CraftMetaBook.BOOK_CONTENT.TYPE, CraftMetaBookSigned.BOOK_CONTENT.TYPE, CraftMetaFirework.FIREWORKS.TYPE, CraftMetaEnchantedBook.STORED_ENCHANTMENTS.TYPE, CraftMetaCharge.EXPLOSION.TYPE, CraftMetaKnowledgeBook.BOOK_RECIPES.TYPE, CraftMetaTropicalFishBucket.ENTITY_TAG.TYPE, CraftMetaTropicalFishBucket.BUCKET_ENTITY_TAG.TYPE, CraftMetaAxolotlBucket.ENTITY_TAG.TYPE, CraftMetaAxolotlBucket.BUCKET_ENTITY_TAG.TYPE, CraftMetaCrossbow.CHARGED_PROJECTILES.TYPE, CraftMetaSuspiciousStew.EFFECTS.TYPE, CraftMetaCompass.LODESTONE_TARGET.TYPE, CraftMetaBundle.ITEMS.TYPE, CraftMetaMusicInstrument.GOAT_HORN_INSTRUMENT.TYPE, CraftMetaOminousBottle.OMINOUS_BOTTLE_AMPLIFIER.TYPE));
         }

         return HANDLED_TAGS;
      }
   }

   protected static <T> Optional<? extends T> getOrEmpty(DataComponentPatch tag, ItemMetaKeyType<T> type) {
      Optional<? extends T> result = tag.get(type.TYPE);
      return result != null ? result : Optional.empty();
   }

   static {
      NAME = new ItemMetaKeyType(DataComponents.CUSTOM_NAME, "display-name");
      ITEM_NAME = new ItemMetaKeyType(DataComponents.ITEM_NAME, "item-name");
      LORE = new ItemMetaKeyType(DataComponents.LORE, "lore");
      CUSTOM_MODEL_DATA = new ItemMetaKeyType(DataComponents.CUSTOM_MODEL_DATA, "custom-model-data");
      ENCHANTABLE = new ItemMetaKeyType(DataComponents.ENCHANTABLE, "enchantable");
      ENCHANTMENTS = new ItemMetaKeyType(DataComponents.ENCHANTMENTS, "enchants");
      REPAIR = new ItemMetaKeyType(DataComponents.REPAIR_COST, "repair-cost");
      ATTRIBUTES = new ItemMetaKeyType(DataComponents.ATTRIBUTE_MODIFIERS, "attribute-modifiers");
      ATTRIBUTES_IDENTIFIER = new ItemMetaKey("AttributeName");
      ATTRIBUTES_SLOT = new ItemMetaKey("Slot");
      HIDEFLAGS = new ItemMetaKeyType(DataComponents.TOOLTIP_DISPLAY, "ItemFlags");
      HIDE_TOOLTIP = new ItemMetaKey("hide-tool-tip");
      TOOLTIP_STYLE = new ItemMetaKeyType(DataComponents.TOOLTIP_STYLE, "tool-tip-style");
      ITEM_MODEL = new ItemMetaKeyType(DataComponents.ITEM_MODEL, "item-model");
      UNBREAKABLE = new ItemMetaKeyType(DataComponents.UNBREAKABLE, "Unbreakable");
      ENCHANTMENT_GLINT_OVERRIDE = new ItemMetaKeyType(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, "enchantment-glint-override");
      GLIDER = new ItemMetaKeyType(DataComponents.GLIDER, "glider");
      DAMAGE_RESISTANT = new ItemMetaKeyType(DataComponents.DAMAGE_RESISTANT, "damage-resistant");
      MAX_STACK_SIZE = new ItemMetaKeyType(DataComponents.MAX_STACK_SIZE, "max-stack-size");
      RARITY = new ItemMetaKeyType(DataComponents.RARITY, "rarity");
      USE_REMAINDER = new ItemMetaKeyType(DataComponents.USE_REMAINDER, "use-remainder");
      USE_COOLDOWN = new ItemMetaKeyType(DataComponents.USE_COOLDOWN, "use-cooldown");
      FOOD = new ItemMetaKeyType(DataComponents.FOOD, "food");
      CONSUMABLE = new ItemMetaKeyType(DataComponents.CONSUMABLE, "consumable");
      TOOL = new ItemMetaKeyType(DataComponents.TOOL, "tool");
      BLOCKS_ATTACKS = new ItemMetaKeyType(DataComponents.BLOCKS_ATTACKS, "blocks-attacks");
      WEAPON = new ItemMetaKeyType(DataComponents.WEAPON, "weapon");
      EQUIPPABLE = new ItemMetaKeyType(DataComponents.EQUIPPABLE, "equippable");
      JUKEBOX_PLAYABLE = new ItemMetaKeyType(DataComponents.JUKEBOX_PLAYABLE, "jukebox-playable");
      BREAK_SOUND = new ItemMetaKeyType(DataComponents.BREAK_SOUND, "break-sound");
      DAMAGE = new ItemMetaKeyType(DataComponents.DAMAGE, "Damage");
      MAX_DAMAGE = new ItemMetaKeyType(DataComponents.MAX_DAMAGE, "max-damage");
      BLOCK_DATA = new ItemMetaKeyType(DataComponents.BLOCK_STATE, "BlockStateTag");
      BUKKIT_CUSTOM_TAG = new ItemMetaKey("PublicBukkitValues");
      CUSTOM_DATA = new ItemMetaKeyType(DataComponents.CUSTOM_DATA);
      HANDLED_TAGS = Sets.newHashSet();
      DATA_TYPE_REGISTRY = new CraftPersistentDataTypeRegistry();
   }

   static final class ItemMetaKeyType<T> extends ItemMetaKey {
      final DataComponentType<T> TYPE;

      ItemMetaKeyType(DataComponentType<T> type) {
         this(type, (String)null, (String)null);
      }

      ItemMetaKeyType(DataComponentType<T> type, String both) {
         this(type, both, both);
      }

      ItemMetaKeyType(DataComponentType<T> type, String nbt, String bukkit) {
         super(nbt, bukkit);
         this.TYPE = type;
      }
   }

   static final class Applicator {
      private final DataComponentPatch.Builder builder = DataComponentPatch.builder();

      <T> Applicator put(ItemMetaKeyType<T> key, T value) {
         this.builder.set(key.TYPE, value);
         return this;
      }

      <T> Applicator putIfAbsent(TypedDataComponent<?> component) {
         if (!this.builder.isSet(component.type())) {
            this.builder.set(component);
         }

         return this;
      }

      DataComponentPatch build() {
         return this.builder.build();
      }
   }
}
