package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Vault;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

public class CraftVault extends CraftBlockEntityState<VaultBlockEntity> implements Vault {
   private CraftVaultConfiguration config;

   public CraftVault(World world, VaultBlockEntity tileEntity) {
      super(world, tileEntity);
   }

   protected CraftVault(CraftVault state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public CraftVault copy() {
      return new CraftVault(this, (Location)null);
   }

   public CraftVault copy(Location location) {
      return new CraftVault(this, location);
   }

   public double getActivationRange() {
      return this.config.activationRange;
   }

   public void setActivationRange(double range) {
      this.config.activationRange = range;
   }

   public double getDeactivationRange() {
      return this.config.deactivationRange;
   }

   public void setDeactivationRange(double range) {
      this.config.deactivationRange = range;
   }

   public LootTable getLootTable() {
      return CraftLootTable.minecraftToBukkit(this.config.lootTable);
   }

   public void setLootTable(LootTable lootTable) {
      Preconditions.checkArgument(lootTable != null, "LootTable cannot be null");
      this.config.lootTable = CraftLootTable.bukkitToMinecraft(lootTable);
   }

   public LootTable getDisplayLootTable() {
      return (LootTable)this.config.overrideLootTableToDisplay.map(CraftLootTable::minecraftToBukkit).orElse((LootTable) null);
   }

   public void setDisplayLootTable(LootTable lootTable) {
      this.config.overrideLootTableToDisplay = Optional.ofNullable(CraftLootTable.bukkitToMinecraft(lootTable));
   }

   public ItemStack getKeyItem() {
      return CraftItemStack.asBukkitCopy(this.config.keyItem);
   }

   public void setKeyItem(ItemStack keyItem) {
      Preconditions.checkArgument(keyItem != null, "Key item cannot be null");
      this.config.keyItem = CraftItemStack.asNMSCopy(keyItem);
   }

   protected void load(VaultBlockEntity tileEntity) {
      super.load(tileEntity);
      if (this.config == null) {
         this.config = new CraftVaultConfiguration();
      }

      if (tileEntity != null) {
         this.config.loadFromConfig(tileEntity.getConfig());
      }

   }

   protected void applyTo(VaultBlockEntity tileEntity) {
      super.applyTo(tileEntity);
      tileEntity.setConfig(this.config.toMinecraft());
   }

   public Set<UUID> getRewardedPlayers() {
      this.requirePlaced();
      VaultServerData serverData = ((VaultBlockEntity)this.getTileEntity()).getServerData();
      Objects.requireNonNull(serverData, "serverData should not be null for placed Vault");
      return Collections.unmodifiableSet(serverData.getRewardedPlayers());
   }

   static class CraftVaultConfiguration {
      private ResourceKey<net.minecraft.world.level.storage.loot.LootTable> lootTable;
      private double activationRange;
      private double deactivationRange;
      private net.minecraft.world.item.ItemStack keyItem;
      private Optional<ResourceKey<net.minecraft.world.level.storage.loot.LootTable>> overrideLootTableToDisplay;
      private PlayerDetector playerDetector;
      private PlayerDetector.EntitySelector entitySelector;

      private void loadFromConfig(VaultConfig minecraft) {
         this.lootTable = minecraft.lootTable();
         this.activationRange = minecraft.activationRange();
         this.deactivationRange = minecraft.deactivationRange();
         this.keyItem = minecraft.keyItem();
         this.overrideLootTableToDisplay = minecraft.overrideLootTableToDisplay();
         this.playerDetector = minecraft.playerDetector();
         this.entitySelector = minecraft.entitySelector();
      }

      private VaultConfig toMinecraft() {
         return new VaultConfig(this.lootTable, this.activationRange, this.deactivationRange, this.keyItem, this.overrideLootTableToDisplay, this.playerDetector, this.entitySelector);
      }
   }
}
