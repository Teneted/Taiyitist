package org.bukkit.craftbukkit.v1_21_R5.block;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemType;
import org.bukkit.craftbukkit.v1_21_R5.registry.CraftRegistryItem;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class CraftBlockType<B extends BlockData> extends CraftRegistryItem<Block> implements BlockType.Typed<B> {
   private final Class<B> blockDataClass = CraftBlockData.fromData(((Block)this.getHandle()).defaultBlockState()).getClass().getInterfaces()[0];
   private final boolean interactable = isInteractable((Block)this.getHandle());
   private static final Class<?>[] USE_WITHOUT_ITEM_ARGS = new Class[]{BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class};
   private static final Class<?>[] USE_ITEM_ON_ARGS = new Class[]{ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class};

   public static Material minecraftToBukkit(Block block) {
      return CraftMagicNumbers.getMaterial(block);
   }

   public static Block bukkitToMinecraft(Material material) {
      return CraftMagicNumbers.getBlock(material);
   }

   public static BlockType minecraftToBukkitNew(Block minecraft) {
      return (BlockType)CraftRegistry.minecraftToBukkit(minecraft, Registries.BLOCK, Registry.BLOCK);
   }

   public static Block bukkitToMinecraftNew(BlockType bukkit) {
      return (Block)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   private static boolean hasMethod(Class<?> clazz, Class<?>... params) {
      boolean hasMethod = false;
      Method[] var3 = clazz.getDeclaredMethods();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method method = var3[var5];
         if (Arrays.equals(method.getParameterTypes(), params)) {
            Preconditions.checkArgument(!hasMethod, "More than one matching method for %s, args %s", clazz, Arrays.toString(params));
            hasMethod = true;
         }
      }

      return hasMethod;
   }

   private static boolean isInteractable(Block block) {
      Class<?> clazz = block.getClass();
      boolean hasMethod = hasMethod(clazz, USE_WITHOUT_ITEM_ARGS) || hasMethod(clazz, USE_ITEM_ON_ARGS);
      if (!hasMethod && clazz.getSuperclass() != BlockBehaviour.class) {
         clazz = clazz.getSuperclass();
         hasMethod = hasMethod(clazz, USE_WITHOUT_ITEM_ARGS) || hasMethod(clazz, USE_ITEM_ON_ARGS);
      }

      return hasMethod;
   }

   public CraftBlockType(NamespacedKey key, Holder<Block> handle) {
      super(key, handle);
   }

   @NotNull
   public BlockType.Typed<BlockData> typed() {
      return this.typed(BlockData.class);
   }

   @NotNull
   public <Other extends BlockData> BlockType.Typed<Other> typed(@NotNull Class<Other> blockDataType) {
      if (blockDataType.isAssignableFrom(this.blockDataClass)) {
         return this;
      } else {
         String var10002 = String.valueOf(this.isRegistered() ? this.getKeyOrThrow() : this.toString());
         throw new IllegalArgumentException("Cannot type block type " + var10002 + " to blockdata type " + blockDataType.getSimpleName());
      }
   }

   public boolean hasItemType() {
      if (this == AIR) {
         return true;
      } else {
         return ((Block)this.getHandle()).asItem() != Items.AIR;
      }
   }

   @NotNull
   public ItemType getItemType() {
      if (this == AIR) {
         return ItemType.AIR;
      } else {
         Item item = ((Block)this.getHandle()).asItem();
         Preconditions.checkArgument(item != Items.AIR, "The block type %s has no corresponding item type", this.isRegistered() ? this.getKeyOrThrow() : this.toString());
         return CraftItemType.minecraftToBukkitNew(item);
      }
   }

   public Class<B> getBlockDataClass() {
      return this.blockDataClass;
   }

   public B createBlockData() {
      return this.createBlockData((String)null);
   }

   public B createBlockData(Consumer<? super B> consumer) {
      B data = this.createBlockData();
      if (consumer != null) {
         consumer.accept(data);
      }

      return data;
   }

   public B createBlockData(String data) {
      return CraftBlockData.newData(this, data);
   }

   public boolean isSolid() {
      return ((Block)this.getHandle()).defaultBlockState().blocksMotion();
   }

   public boolean isAir() {
      return ((Block)this.getHandle()).defaultBlockState().isAir();
   }

   public boolean isEnabledByFeature(@NotNull World world) {
      Preconditions.checkNotNull(world, "World cannot be null");
      return ((Block)this.getHandle()).isEnabled(((CraftWorld)world).getHandle().enabledFeatures());
   }

   public boolean isFlammable() {
      return ((Block)this.getHandle()).defaultBlockState().ignitedByLava();
   }

   public boolean isBurnable() {
      return ((FireBlock)Blocks.FIRE).igniteOdds.getOrDefault(this.getHandle(), 0) > 0;
   }

   public boolean isOccluding() {
      return ((Block)this.getHandle()).defaultBlockState().isRedstoneConductor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
   }

   public boolean hasGravity() {
      return this.getHandle() instanceof Fallable;
   }

   public boolean isInteractable() {
      return this.interactable;
   }

   public float getHardness() {
      return ((Block)this.getHandle()).defaultBlockState().destroySpeed;
   }

   public float getBlastResistance() {
      return ((Block)this.getHandle()).getExplosionResistance();
   }

   public float getSlipperiness() {
      return ((Block)this.getHandle()).getFriction();
   }

   @NotNull
   public String getTranslationKey() {
      return ((Block)this.getHandle()).getDescriptionId();
   }

   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   public Material asMaterial() {
      return (Material)Registry.MATERIAL.get(this.getKeyOrThrow());
   }
}
