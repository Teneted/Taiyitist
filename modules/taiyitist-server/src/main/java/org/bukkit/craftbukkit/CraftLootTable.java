package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

public class CraftLootTable implements LootTable {
   private final net.minecraft.world.level.storage.loot.LootTable handle;
   private final NamespacedKey key;

   public static LootTable minecraftToBukkit(ResourceLocation minecraft) {
      return minecraft == null ? null : Bukkit.getLootTable(CraftNamespacedKey.fromMinecraft(minecraft));
   }

   public static LootTable minecraftToBukkit(ResourceKey<net.minecraft.world.level.storage.loot.LootTable> minecraft) {
      return minecraft == null ? null : Bukkit.getLootTable(minecraftToBukkitKey(minecraft));
   }

   public static NamespacedKey minecraftToBukkitKey(ResourceKey<net.minecraft.world.level.storage.loot.LootTable> minecraft) {
      return minecraft == null ? null : CraftNamespacedKey.fromMinecraft(minecraft.location());
   }

   public static ResourceKey<net.minecraft.world.level.storage.loot.LootTable> bukkitToMinecraft(LootTable table) {
      return table == null ? null : bukkitKeyToMinecraft(table.getKey());
   }

   public static ResourceKey<net.minecraft.world.level.storage.loot.LootTable> bukkitKeyToMinecraft(NamespacedKey key) {
      return key == null ? null : ResourceKey.create(Registries.LOOT_TABLE, CraftNamespacedKey.toMinecraft(key));
   }

   public CraftLootTable(NamespacedKey key, net.minecraft.world.level.storage.loot.LootTable handle) {
      this.handle = handle;
      this.key = key;
   }

   public net.minecraft.world.level.storage.loot.LootTable getHandle() {
      return this.handle;
   }

   public Collection<ItemStack> populateLoot(Random random, LootContext context) {
      Preconditions.checkArgument(context != null, "LootContext cannot be null");
      LootParams nmsContext = this.convertContext(context, random);
      List<net.minecraft.world.item.ItemStack> nmsItems = this.handle.getRandomItems(nmsContext);
      Collection<ItemStack> bukkit = new ArrayList(nmsItems.size());
      Iterator var6 = nmsItems.iterator();

      while(var6.hasNext()) {
         net.minecraft.world.item.ItemStack item = (net.minecraft.world.item.ItemStack)var6.next();
         if (!item.isEmpty()) {
            bukkit.add(CraftItemStack.asBukkitCopy(item));
         }
      }

      return bukkit;
   }

   public void fillInventory(Inventory inventory, Random random, LootContext context) {
      Preconditions.checkArgument(inventory != null, "Inventory cannot be null");
      Preconditions.checkArgument(context != null, "LootContext cannot be null");
      LootParams nmsContext = this.convertContext(context, random);
      CraftInventory craftInventory = (CraftInventory)inventory;
      Container handle = craftInventory.getInventory();
      this.getHandle().fillInventory(handle, nmsContext, random.nextLong(), true);
   }

   public NamespacedKey getKey() {
      return this.key;
   }

   private LootParams convertContext(LootContext context, Random random) {
      Preconditions.checkArgument(context != null, "LootContext cannot be null");
      Location loc = context.getLocation();
      Preconditions.checkArgument(loc.getWorld() != null, "LootContext.getLocation#getWorld cannot be null");
      ServerLevel handle = ((CraftWorld)loc.getWorld()).getHandle();
      LootParams.Builder builder = new LootParams.Builder(handle);
      if (random != null) {
      }

      this.setMaybe(builder, LootContextParams.ORIGIN, CraftLocation.toVec3D(loc));
      if (this.getHandle() != net.minecraft.world.level.storage.loot.LootTable.EMPTY) {
         if (context.getLootedEntity() != null) {
            Entity nmsLootedEntity = ((CraftEntity)context.getLootedEntity()).getHandle();
            this.setMaybe(builder, LootContextParams.THIS_ENTITY, nmsLootedEntity);
            this.setMaybe(builder, LootContextParams.DAMAGE_SOURCE, handle.damageSources().generic());
            this.setMaybe(builder, LootContextParams.ORIGIN, nmsLootedEntity.position());
         }

         if (context.getKiller() != null) {
            Player nmsKiller = ((CraftHumanEntity)context.getKiller()).getHandle();
            this.setMaybe(builder, LootContextParams.ATTACKING_ENTITY, nmsKiller);
            this.setMaybe(builder, LootContextParams.DAMAGE_SOURCE, handle.damageSources().playerAttack(nmsKiller));
            this.setMaybe(builder, LootContextParams.LAST_DAMAGE_PLAYER, nmsKiller);
            this.setMaybe(builder, LootContextParams.TOOL, nmsKiller.getUseItem());
         }
      }

      ContextKeySet.Builder nmsBuilder = new ContextKeySet.Builder();
      Iterator var7 = this.getHandle().getParamSet().required().iterator();

      ContextKey param;
      while(var7.hasNext()) {
         param = (ContextKey)var7.next();
         nmsBuilder.required(param);
      }

      var7 = this.getHandle().getParamSet().allowed().iterator();

      while(var7.hasNext()) {
         param = (ContextKey)var7.next();
         if (!this.getHandle().getParamSet().required().contains(param)) {
            nmsBuilder.optional(param);
         }
      }

      return builder.create(this.getHandle().getParamSet());
   }

   private <T> void setMaybe(LootParams.Builder builder, ContextKey<T> param, T value) {
      if (this.getHandle().getParamSet().required().contains(param) || this.getHandle().getParamSet().allowed().contains(param)) {
         builder.withParameter(param, value);
      }

   }

   public static LootContext convertContext(net.minecraft.world.level.storage.loot.LootContext info) {
      Vec3 position = (Vec3)info.getOptionalParameter(LootContextParams.ORIGIN);
      if (position == null) {
         position = ((Entity)info.getOptionalParameter(LootContextParams.THIS_ENTITY)).position();
      }

      Location location = CraftLocation.toBukkit((Vec3)position, (World)info.getLevel().getWorld());
      LootContext.Builder contextBuilder = new LootContext.Builder(location);
      if (info.hasParameter(LootContextParams.ATTACKING_ENTITY)) {
         CraftEntity killer = ((Entity)info.getOptionalParameter(LootContextParams.ATTACKING_ENTITY)).getBukkitEntity();
         if (killer instanceof CraftHumanEntity) {
            contextBuilder.killer((CraftHumanEntity)killer);
         }
      }

      if (info.hasParameter(LootContextParams.THIS_ENTITY)) {
         contextBuilder.lootedEntity(((Entity)info.getOptionalParameter(LootContextParams.THIS_ENTITY)).getBukkitEntity());
      }

      contextBuilder.luck(info.getLuck());
      return contextBuilder.build();
   }

   public String toString() {
      return this.getKey().toString();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof LootTable table)) {
         return false;
      } else {
         return table.getKey().equals(this.getKey());
      }
   }
}
