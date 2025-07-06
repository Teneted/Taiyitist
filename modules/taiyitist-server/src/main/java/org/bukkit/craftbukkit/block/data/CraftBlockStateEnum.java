package org.bukkit.craftbukkit.block.data;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.block.CraftBlock;

public record CraftBlockStateEnum<N extends Enum<N> & StringRepresentable, B extends Enum<B>>(EnumProperty<N> nms, Class<B> bukkit, N[] nmsValues, B[] bukkitValues) {
   public CraftBlockStateEnum(EnumProperty<N> nms, Class<B> bukkit) {
      this(nms, bukkit, (N[]) nms.getValueClass().getEnumConstants(), (B[]) bukkit.getEnumConstants());
   }

   public CraftBlockStateEnum(EnumProperty<N> nms, Class<B> bukkit, N[] nmsValues, B[] bukkitValues) {
      this.nms = nms;
      this.bukkit = bukkit;
      this.nmsValues = nmsValues;
      this.bukkitValues = bukkitValues;
   }

   B toBukkit(N nms) {
      return nms instanceof Direction ? (B) CraftBlock.notchToBlockFace((Direction) nms) : this.bukkitValues[nms.ordinal()];
   }

   N toNMS(B bukkit) {
      return bukkit instanceof BlockFace ? (N) CraftBlock.blockFaceToNotch((BlockFace) bukkit) : this.nmsValues[bukkit.ordinal()];
   }

   Set<B> getValues() {
      ImmutableSet.Builder<B> values = ImmutableSet.builder();
      Iterator var2 = this.nms.getPossibleValues().iterator();

      while(var2.hasNext()) {
         N e = (N) var2.next();
         values.add(this.toBukkit(e));
      }

      return values.build();
   }

   public EnumProperty<N> nms() {
      return this.nms;
   }

   public Class<B> bukkit() {
      return this.bukkit;
   }

   public N[] nmsValues() {
      return this.nmsValues;
   }

   public B[] bukkitValues() {
      return this.bukkitValues;
   }
}
