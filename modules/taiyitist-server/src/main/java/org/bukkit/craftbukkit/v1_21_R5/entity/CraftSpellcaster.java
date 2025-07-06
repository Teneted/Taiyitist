package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.SpellcasterIllager.IllagerSpell;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Spellcaster.Spell;

public class CraftSpellcaster extends CraftIllager implements Spellcaster {
   public CraftSpellcaster(CraftServer server, SpellcasterIllager entity) {
      super(server, entity);
   }

   public SpellcasterIllager getHandle() {
      return (SpellcasterIllager)super.getHandle();
   }

   public String toString() {
      return "CraftSpellcaster";
   }

   public Spellcaster.Spell getSpell() {
      return toBukkitSpell(this.getHandle().getCurrentSpell());
   }

   public void setSpell(Spellcaster.Spell spell) {
      Preconditions.checkArgument(spell != null, "Use Spell.NONE");
      this.getHandle().setIsCastingSpell(toNMSSpell(spell));
   }

   public static Spellcaster.Spell toBukkitSpell(SpellcasterIllager.IllagerSpell spell) {
      return Spell.valueOf(spell.name());
   }

   public static SpellcasterIllager.IllagerSpell toNMSSpell(Spellcaster.Spell spell) {
      return IllagerSpell.byId(spell.ordinal());
   }
}
