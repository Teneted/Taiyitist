package org.bukkit.craftbukkit.v1_21_R5.scoreboard;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.RenderType;

public final class CraftCriteria implements Criteria {
   static final Map<String, CraftCriteria> DEFAULTS;
   static final CraftCriteria DUMMY;
   final ObjectiveCriteria criteria;
   final String bukkitName;

   private CraftCriteria(String bukkitName) {
      this.bukkitName = bukkitName;
      this.criteria = DUMMY.criteria;
   }

   private CraftCriteria(ObjectiveCriteria criteria) {
      this.criteria = criteria;
      this.bukkitName = criteria.getName();
   }

   public String getName() {
      return this.bukkitName;
   }

   public boolean isReadOnly() {
      return this.criteria.isReadOnly();
   }

   public RenderType getDefaultRenderType() {
      return RenderType.values()[this.criteria.getDefaultRenderType().ordinal()];
   }

   static CraftCriteria getFromNMS(Objective objective) {
      return (CraftCriteria)DEFAULTS.get(objective.getCriteria().getName());
   }

   public static CraftCriteria getFromBukkit(String name) {
      CraftCriteria criteria = (CraftCriteria)DEFAULTS.get(name);
      return criteria != null ? criteria : (CraftCriteria)ObjectiveCriteria.byName(name).map(CraftCriteria::new).orElseGet(() -> {
         return new CraftCriteria(name);
      });
   }

   public boolean equals(Object that) {
      return !(that instanceof CraftCriteria) ? false : ((CraftCriteria)that).bukkitName.equals(this.bukkitName);
   }

   public int hashCode() {
      return this.bukkitName.hashCode() ^ CraftCriteria.class.hashCode();
   }

   static {
      ImmutableMap.Builder<String, CraftCriteria> defaults = ImmutableMap.builder();
      Iterator var1 = ObjectiveCriteria.CRITERIA_CACHE.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry<String, ObjectiveCriteria> entry = (Map.Entry)var1.next();
         String name = (String)entry.getKey();
         ObjectiveCriteria criteria = (ObjectiveCriteria)entry.getValue();
         defaults.put(name, new CraftCriteria(criteria));
      }

      DEFAULTS = defaults.build();
      DUMMY = (CraftCriteria)DEFAULTS.get("dummy");
   }
}
