package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.monster.SpellcasterIllager.IllagerSpell;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Evoker;

public class CraftEvoker extends CraftSpellcaster implements Evoker {
   public CraftEvoker(CraftServer server, net.minecraft.world.entity.monster.Evoker entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Evoker getHandle() {
      return (net.minecraft.world.entity.monster.Evoker)super.getHandle();
   }

   public String toString() {
      return "CraftEvoker";
   }

   public Evoker.Spell getCurrentSpell() {
      return Evoker.Spell.values()[this.getHandle().getCurrentSpell().ordinal()];
   }

   public void setCurrentSpell(Evoker.Spell spell) {
      this.getHandle().setIsCastingSpell(spell == null ? IllagerSpell.NONE : IllagerSpell.byId(spell.ordinal()));
   }
}
