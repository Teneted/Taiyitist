package org.bukkit.craftbukkit.v1_21_R5.inventory.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_21_R5.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.LoomInventory;
import org.bukkit.inventory.view.LoomView;

public class CraftLoomView extends CraftInventoryView<LoomMenu, LoomInventory> implements LoomView {
   public CraftLoomView(HumanEntity player, LoomInventory viewing, LoomMenu container) {
      super(player, viewing, container);
   }

   public List<PatternType> getSelectablePatterns() {
      List<Holder<BannerPattern>> selectablePatterns = ((LoomMenu)this.container).getSelectablePatterns();
      List<PatternType> patternTypes = new ArrayList(selectablePatterns.size());
      Iterator var3 = selectablePatterns.iterator();

      while(var3.hasNext()) {
         Holder<BannerPattern> selectablePattern = (Holder)var3.next();
         patternTypes.add(CraftPatternType.minecraftHolderToBukkit(selectablePattern));
      }

      return patternTypes;
   }

   public int getSelectedPatternIndex() {
      return ((LoomMenu)this.container).getSelectedBannerPatternIndex();
   }
}
