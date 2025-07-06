package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Vault;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftVault extends CraftBlockData implements Vault, Directional {
   private static final CraftBlockStateEnum<?, Vault.State> VAULT_STATE = getEnum(VaultBlock.class, "vault_state", Vault.State.class);
   private static final BooleanProperty OMINOUS = getBoolean(VaultBlock.class, "ominous");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(VaultBlock.class, "facing", BlockFace.class);

   public CraftVault() {
   }

   public CraftVault(BlockState state) {
      super(state);
   }

   public Vault.State getVaultState() {
      return (Vault.State)this.get(VAULT_STATE);
   }

   public Vault.State getTrialSpawnerState() {
      return this.getVaultState();
   }

   public void setVaultState(Vault.State state) {
      this.set(VAULT_STATE, state);
   }

   public void setTrialSpawnerState(Vault.State state) {
      this.setVaultState(state);
   }

   public boolean isOminous() {
      return (Boolean)this.get(OMINOUS);
   }

   public void setOminous(boolean ominous) {
      this.set(OMINOUS, ominous);
   }

   public BlockFace getFacing() {
      return (BlockFace)this.get(FACING);
   }

   public void setFacing(BlockFace facing) {
      this.set(FACING, facing);
   }

   public Set<BlockFace> getFaces() {
      return this.getValues(FACING);
   }
}
