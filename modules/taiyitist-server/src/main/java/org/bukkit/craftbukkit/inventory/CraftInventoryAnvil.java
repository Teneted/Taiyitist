package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.function.Consumer;
import net.minecraft.world.Container;
import org.bukkit.Location;
import org.bukkit.craftbukkit.inventory.view.CraftAnvilView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;

public class CraftInventoryAnvil extends CraftResultInventory implements AnvilInventory {
   private static final int DEFAULT_REPAIR_COST = 0;
   private static final int DEFAULT_REPAIR_COST_AMOUNT = 0;
   private static final int DEFAULT_MAXIMUM_REPAIR_COST = 40;
   private final Location location;
   private String renameText;
   private int repairCost;
   private int repairCostAmount;
   private int maximumRepairCost;

   public CraftInventoryAnvil(Location location, Container inventory, Container resultInventory) {
      super(inventory, resultInventory);
      this.location = location;
      this.renameText = null;
      this.repairCost = 0;
      this.repairCostAmount = 0;
      this.maximumRepairCost = 40;
   }

   public Location getLocation() {
      return this.location;
   }

   public String getRenameText() {
      this.syncWithArbitraryViewValue((cav) -> {
         this.renameText = cav.getRenameText();
      });
      return this.renameText;
   }

   public int getRepairCostAmount() {
      this.syncWithArbitraryViewValue((cav) -> {
         this.repairCostAmount = cav.getRepairItemCountCost();
      });
      return this.repairCostAmount;
   }

   public void setRepairCostAmount(int amount) {
      this.repairCostAmount = amount;
      this.syncViews((cav) -> {
         cav.setRepairItemCountCost(amount);
      });
   }

   public int getRepairCost() {
      this.syncWithArbitraryViewValue((cav) -> {
         this.repairCost = cav.getRepairCost();
      });
      return this.repairCost;
   }

   public void setRepairCost(int i) {
      this.repairCost = i;
      this.syncViews((cav) -> {
         cav.setRepairCost(i);
      });
   }

   public int getMaximumRepairCost() {
      this.syncWithArbitraryViewValue((cav) -> {
         this.maximumRepairCost = cav.getMaximumRepairCost();
      });
      return this.maximumRepairCost;
   }

   public void setMaximumRepairCost(int levels) {
      Preconditions.checkArgument(levels >= 0, "Maximum repair cost must be positive (or 0)");
      this.maximumRepairCost = levels;
      this.syncViews((cav) -> {
         cav.setMaximumRepairCost(levels);
      });
   }

   public boolean isRepairCostSet() {
      return this.repairCost != 0;
   }

   public boolean isRepairCostAmountSet() {
      return this.repairCostAmount != 0;
   }

   public boolean isMaximumRepairCostSet() {
      return this.maximumRepairCost != 40;
   }

   private void syncViews(Consumer<CraftAnvilView> consumer) {
      Iterator var2 = this.getViewers().iterator();

      while(var2.hasNext()) {
         HumanEntity viewer = (HumanEntity)var2.next();
         InventoryView var5 = viewer.getOpenInventory();
         if (var5 instanceof CraftAnvilView cav) {
            consumer.accept(cav);
         }
      }

   }

   private void syncWithArbitraryViewValue(Consumer<CraftAnvilView> consumer) {
      if (!this.getViewers().isEmpty()) {
         HumanEntity entity = (HumanEntity)this.getViewers().get(0);
         if (entity != null) {
            InventoryView var4 = entity.getOpenInventory();
            if (var4 instanceof CraftAnvilView) {
               CraftAnvilView cav = (CraftAnvilView)var4;
               consumer.accept(cav);
            }
         }

      }
   }
}
