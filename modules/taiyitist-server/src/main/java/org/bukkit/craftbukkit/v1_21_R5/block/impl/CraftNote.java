package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftNote extends CraftBlockData implements NoteBlock, Powerable {
   private static final CraftBlockStateEnum<?, Instrument> INSTRUMENT = getEnum(net.minecraft.world.level.block.NoteBlock.class, "instrument", Instrument.class);
   private static final IntegerProperty NOTE = getInteger(net.minecraft.world.level.block.NoteBlock.class, "note");
   private static final BooleanProperty POWERED = getBoolean(net.minecraft.world.level.block.NoteBlock.class, "powered");

   public CraftNote() {
   }

   public CraftNote(BlockState state) {
      super(state);
   }

   public Instrument getInstrument() {
      return (Instrument)this.get(INSTRUMENT);
   }

   public void setInstrument(Instrument instrument) {
      this.set(INSTRUMENT, instrument);
   }

   public Note getNote() {
      return new Note((Integer)this.get(NOTE));
   }

   public void setNote(Note note) {
      this.set(NOTE, Integer.valueOf(note.getId()));
   }

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }
}
