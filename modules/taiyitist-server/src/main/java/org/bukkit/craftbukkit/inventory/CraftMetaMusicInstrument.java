package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.InstrumentComponent;
import org.bukkit.MusicInstrument;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.CraftMusicInstrument;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.inventory.meta.MusicInstrumentMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaMusicInstrument extends CraftMetaItem implements MusicInstrumentMeta {
   static final CraftMetaItem.ItemMetaKeyType<InstrumentComponent> GOAT_HORN_INSTRUMENT;
   private MusicInstrument instrument;

   CraftMetaMusicInstrument(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaMusicInstrument craftMetaMusicInstrument) {
         this.instrument = craftMetaMusicInstrument.instrument;
      }

   }

   CraftMetaMusicInstrument(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, GOAT_HORN_INSTRUMENT).ifPresent((instrument) -> {
         instrument.instrument().unwrap(CraftRegistry.getMinecraftRegistry()).ifPresent((holder) -> {
            this.instrument = CraftMusicInstrument.minecraftHolderToBukkit(holder);
         });
      });
   }

   CraftMetaMusicInstrument(Map<String, Object> map) {
      super(map);
      String instrumentString = SerializableMeta.getString(map, GOAT_HORN_INSTRUMENT.BUKKIT, true);
      if (instrumentString != null) {
         this.instrument = CraftMusicInstrument.stringToBukkit(instrumentString);
      }

   }

   void applyToItem(CraftMetaItem.Applicator tag) {
      super.applyToItem(tag);
      if (this.instrument != null) {
         tag.put(GOAT_HORN_INSTRUMENT, new InstrumentComponent(CraftMusicInstrument.bukkitToMinecraftHolder(this.instrument)));
      }

   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaMusicInstrument) {
         CraftMetaMusicInstrument that = (CraftMetaMusicInstrument)meta;
         return this.instrument == that.instrument;
      } else {
         return true;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaMusicInstrument || this.isInstrumentEmpty());
   }

   boolean isEmpty() {
      return super.isEmpty() && this.isInstrumentEmpty();
   }

   boolean isInstrumentEmpty() {
      return this.instrument == null;
   }

   int applyHash() {
      int orginal;
      int hash = orginal = super.applyHash();
      if (this.hasInstrument()) {
         hash = 61 * hash + this.instrument.hashCode();
      }

      return orginal != hash ? CraftMetaMusicInstrument.class.hashCode() ^ hash : hash;
   }

   public CraftMetaMusicInstrument clone() {
      CraftMetaMusicInstrument meta = (CraftMetaMusicInstrument)super.clone();
      meta.instrument = this.instrument;
      return meta;
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasInstrument()) {
         builder.put(GOAT_HORN_INSTRUMENT.BUKKIT, CraftMusicInstrument.bukkitToString(this.instrument));
      }

      return builder;
   }

   public MusicInstrument getInstrument() {
      return this.instrument;
   }

   public boolean hasInstrument() {
      return this.instrument != null;
   }

   public void setInstrument(MusicInstrument instrument) {
      this.instrument = instrument;
   }

   static {
      GOAT_HORN_INSTRUMENT = new CraftMetaItem.ItemMetaKeyType(DataComponents.INSTRUMENT, "instrument");
   }
}
