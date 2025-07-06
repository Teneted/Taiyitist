package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftNoteBlock extends CraftBlockData implements NoteBlock {
   private static final CraftBlockStateEnum<?, Instrument> INSTRUMENT = getEnum("instrument", Instrument.class);
   private static final IntegerProperty NOTE = getInteger("note");

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
}
