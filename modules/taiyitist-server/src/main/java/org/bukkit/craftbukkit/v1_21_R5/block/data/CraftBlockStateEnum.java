package org.bukkit.craftbukkit.v1_21_R5.block.data;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlock;

public record CraftBlockStateEnum<N extends Enum<N> & StringRepresentable, B extends Enum<B>>(EnumProperty<N> nms, Class<B> bukkit, N[] nmsValues, B[] bukkitValues) {
   public CraftBlockStateEnum(EnumProperty<N> nms, Class<B> bukkit) {
      this(nms, bukkit, (Enum[])nms.getValueClass().getEnumConstants(), (Enum[])bukkit.getEnumConstants());
   }

   public CraftBlockStateEnum(EnumProperty<N> nms, Class<B> bukkit, N[] nmsValues, B[] bukkitValues) {
      this.nms = nms;
      this.bukkit = bukkit;
      this.nmsValues = nmsValues;
      this.bukkitValues = bukkitValues;
   }

   B toBukkit(N nms) {
      return (Enum)(nms instanceof Direction ? CraftBlock.notchToBlockFace((Direction)nms) : this.bukkitValues[nms.ordinal()]);
   }

   N toNMS(B bukkit) {
      return (Enum)(bukkit instanceof BlockFace ? CraftBlock.blockFaceToNotch((BlockFace)bukkit) : this.nmsValues[bukkit.ordinal()]);
   }

   Set<B> getValues() {
      ImmutableSet.Builder<B> values = ImmutableSet.builder();
      Iterator var2 = this.nms.getPossibleValues().iterator();

      while(var2.hasNext()) {
         N e = (Enum)var2.next();
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
