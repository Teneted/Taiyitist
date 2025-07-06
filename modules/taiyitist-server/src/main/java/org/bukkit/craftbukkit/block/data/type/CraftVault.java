package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Vault;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftVault extends CraftBlockData implements Vault {
   private static final CraftBlockStateEnum<?, Vault.State> VAULT_STATE = getEnum("vault_state", Vault.State.class);
   private static final BooleanProperty OMINOUS = getBoolean("ominous");

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
}
