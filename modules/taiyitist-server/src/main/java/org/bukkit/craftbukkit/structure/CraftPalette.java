package org.bukkit.craftbukkit.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.structure.Palette;

public class CraftPalette implements Palette {
   private final StructureTemplate.Palette palette;
   private final RegistryAccess registry;

   public CraftPalette(StructureTemplate.Palette palette, RegistryAccess registry) {
      this.palette = palette;
      this.registry = registry;
   }

   public List<BlockState> getBlocks() {
      List<BlockState> blocks = new ArrayList();
      Iterator var2 = this.palette.blocks().iterator();

      while(var2.hasNext()) {
         StructureTemplate.StructureBlockInfo blockInfo = (StructureTemplate.StructureBlockInfo)var2.next();
         blocks.add(CraftBlockStates.getBlockState(this.registry, blockInfo.pos(), blockInfo.state(), blockInfo.nbt()));
      }

      return blocks;
   }

   public int getBlockCount() {
      return this.palette.blocks().size();
   }
}
