package org.bukkit.craftbukkit.inventory.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.tag.CraftBlockTag;
import org.bukkit.inventory.meta.components.ToolComponent;

@SerializableAs("Tool")
public final class CraftToolComponent implements ToolComponent {
   private Tool handle;

   public CraftToolComponent(Tool tool) {
      this.handle = tool;
   }

   public CraftToolComponent(CraftToolComponent tool) {
      this.handle = tool.handle;
   }

   public CraftToolComponent(Map<String, Object> map) {
      Float speed = (Float)SerializableMeta.getObject(Float.class, map, "default-mining-speed", false);
      Integer damage = (Integer)SerializableMeta.getObject(Integer.class, map, "damage-per-block", false);
      Boolean canDestroy = (Boolean)SerializableMeta.getObject(Boolean.class, map, "can-destroy-blocks-in-creative", true);
      ImmutableList.Builder<ToolComponent.ToolRule> rules = ImmutableList.builder();
      Iterable<?> rawRuleList = (Iterable)SerializableMeta.getObject(Iterable.class, map, "rules", true);
      if (rawRuleList != null) {
         Iterator var7 = rawRuleList.iterator();

         while(var7.hasNext()) {
            Object obj = var7.next();
            Preconditions.checkArgument(obj instanceof ToolComponent.ToolRule, "Object (%s) in rule list is not valid", obj.getClass());
            CraftToolRule rule = new CraftToolRule((ToolComponent.ToolRule)obj);
            if (rule.handle.blocks().size() > 0) {
               rules.add(rule);
            }
         }
      }

      this.handle = new Tool(rules.build().stream().map(CraftToolRule::new).map(CraftToolRule::getHandle).toList(), speed, damage, canDestroy != null ? canDestroy : true);
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("default-mining-speed", this.getDefaultMiningSpeed());
      result.put("damage-per-block", this.getDamagePerBlock());
      result.put("can-destroy-blocks-in-creative", this.canDestroyBlocksInCreative());
      result.put("rules", this.getRules());
      return result;
   }

   public Tool getHandle() {
      return this.handle;
   }

   public float getDefaultMiningSpeed() {
      return this.handle.defaultMiningSpeed();
   }

   public void setDefaultMiningSpeed(float speed) {
      this.handle = new Tool(this.handle.rules(), speed, this.handle.damagePerBlock(), this.handle.canDestroyBlocksInCreative());
   }

   public int getDamagePerBlock() {
      return this.handle.damagePerBlock();
   }

   public void setDamagePerBlock(int damage) {
      Preconditions.checkArgument(damage >= 0, "damage must be >= 0, was %d", damage);
      this.handle = new Tool(this.handle.rules(), this.handle.defaultMiningSpeed(), damage, this.handle.canDestroyBlocksInCreative());
   }

   public boolean canDestroyBlocksInCreative() {
      return this.handle.canDestroyBlocksInCreative();
   }

   public void setCanDestroyBlocksInCreative(boolean destroy) {
      this.handle = new Tool(this.handle.rules(), this.handle.defaultMiningSpeed(), this.handle.damagePerBlock(), destroy);
   }

   public List<ToolComponent.ToolRule> getRules() {
      return (List)this.handle.rules().stream().map(CraftToolRule::new).collect(Collectors.toList());
   }

   public void setRules(List<ToolComponent.ToolRule> rules) {
      Preconditions.checkArgument(rules != null, "rules must not be null");
      this.handle = new Tool(rules.stream().map(CraftToolRule::new).map(CraftToolRule::getHandle).toList(), this.handle.defaultMiningSpeed(), this.handle.damagePerBlock(), this.handle.canDestroyBlocksInCreative());
   }

   public ToolComponent.ToolRule addRule(Material block, Float speed, Boolean correctForDrops) {
      Preconditions.checkArgument(block != null, "block must not be null");
      Preconditions.checkArgument(block.isBlock(), "block must be a block type, given %s", block.getKey());
      Holder.Reference<Block> nmsBlock = CraftBlockType.bukkitToMinecraft(block).builtInRegistryHolder();
      return this.addRule((HolderSet)HolderSet.direct(new Holder[]{nmsBlock}), speed, correctForDrops);
   }

   public ToolComponent.ToolRule addRule(Collection<Material> blocks, Float speed, Boolean correctForDrops) {
      List<Holder.Reference<Block>> nmsBlocks = new ArrayList(blocks.size());
      Iterator var5 = blocks.iterator();

      while(var5.hasNext()) {
         Material material = (Material)var5.next();
         Preconditions.checkArgument(material.isBlock(), "blocks contains non-block type: %s", material.getKey());
         nmsBlocks.add(CraftBlockType.bukkitToMinecraft(material).builtInRegistryHolder());
      }

      return this.addRule((HolderSet)HolderSet.direct(nmsBlocks), speed, correctForDrops);
   }

   public ToolComponent.ToolRule addRule(Tag<Material> tag, Float speed, Boolean correctForDrops) {
      Preconditions.checkArgument(tag instanceof CraftBlockTag, "tag must be a block tag");
      return this.addRule((HolderSet)((CraftBlockTag)tag).getHandle(), speed, correctForDrops);
   }

   private ToolComponent.ToolRule addRule(HolderSet<Block> blocks, Float speed, Boolean correctForDrops) {
      Tool.Rule rule = new Tool.Rule(blocks, Optional.ofNullable(speed), Optional.ofNullable(correctForDrops));
      List<Tool.Rule> rules = new ArrayList(this.handle.rules().size() + 1);
      rules.addAll(this.handle.rules());
      rules.add(rule);
      this.handle = new Tool(rules, this.handle.defaultMiningSpeed(), this.handle.damagePerBlock(), this.handle.canDestroyBlocksInCreative());
      return new CraftToolRule(rule);
   }

   public boolean removeRule(ToolComponent.ToolRule rule) {
      Preconditions.checkArgument(rule != null, "rule must not be null");
      List<Tool.Rule> rules = new ArrayList(this.handle.rules());
      boolean removed = rules.remove(((CraftToolRule)rule).handle);
      this.handle = new Tool(rules, this.handle.defaultMiningSpeed(), this.handle.damagePerBlock(), this.handle.canDestroyBlocksInCreative());
      return removed;
   }

   public int hashCode() {
      int hash = 7;
      hash = 73 * hash + Objects.hashCode(this.handle);
      return hash;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftToolComponent other = (CraftToolComponent)obj;
         return Objects.equals(this.handle, other.handle);
      }
   }

   public String toString() {
      return "CraftToolComponent{handle=" + String.valueOf(this.handle) + "}";
   }

   @SerializableAs("ToolRule")
   public static class CraftToolRule implements ToolComponent.ToolRule {
      private Tool.Rule handle;

      public CraftToolRule(Tool.Rule handle) {
         this.handle = handle;
      }

      public CraftToolRule(ToolComponent.ToolRule bukkit) {
         Tool.Rule toCopy = ((CraftToolRule)bukkit).handle;
         this.handle = new Tool.Rule(toCopy.blocks(), toCopy.speed(), toCopy.correctForDrops());
      }

      public CraftToolRule(Map<String, Object> map) {
         Float speed = (Float)SerializableMeta.getObject(Float.class, map, "speed", true);
         Boolean correct = (Boolean)SerializableMeta.getObject(Boolean.class, map, "correct-for-drops", true);
         HolderSet<Block> blocks = CraftHolderUtil.parse(SerializableMeta.getObject(Object.class, map, "blocks", false), Registries.BLOCK, BuiltInRegistries.BLOCK);
         this.handle = new Tool.Rule(blocks, Optional.ofNullable(speed), Optional.ofNullable(correct));
      }

      public Map<String, Object> serialize() {
         Map<String, Object> result = new LinkedHashMap();
         CraftHolderUtil.serialize(result, "blocks", this.handle.blocks());
         Float speed = this.getSpeed();
         if (speed != null) {
            result.put("speed", speed);
         }

         Boolean correct = this.isCorrectForDrops();
         if (correct != null) {
            result.put("correct-for-drops", correct);
         }

         return result;
      }

      public Tool.Rule getHandle() {
         return this.handle;
      }

      public Collection<Material> getBlocks() {
         return (Collection)this.handle.blocks().stream().map(Holder::value).map(CraftBlockType::minecraftToBukkit).collect(Collectors.toList());
      }

      public void setBlocks(Material block) {
         Preconditions.checkArgument(block != null, "block must not be null");
         Preconditions.checkArgument(block.isBlock(), "block must be a block type, given %s", block.getKey());
         this.handle = new Tool.Rule(HolderSet.direct(new Holder[]{CraftBlockType.bukkitToMinecraft(block).builtInRegistryHolder()}), this.handle.speed(), this.handle.correctForDrops());
      }

      public void setBlocks(Collection<Material> blocks) {
         Preconditions.checkArgument(blocks != null, "blocks must not be null");
         Iterator var2 = blocks.iterator();

         while(var2.hasNext()) {
            Material material = (Material)var2.next();
            Preconditions.checkArgument(material.isBlock(), "blocks contains non-block type: %s", material.getKey());
         }

         this.handle = new Tool.Rule(HolderSet.direct((List)blocks.stream().map(CraftBlockType::bukkitToMinecraft).map(Block::builtInRegistryHolder).collect(Collectors.toList())), this.handle.speed(), this.handle.correctForDrops());
      }

      public void setBlocks(Tag<Material> tag) {
         Preconditions.checkArgument(tag instanceof CraftBlockTag, "tag must be a block tag");
         this.handle = new Tool.Rule(((CraftBlockTag)tag).getHandle(), this.handle.speed(), this.handle.correctForDrops());
      }

      public Float getSpeed() {
         return (Float)this.handle.speed().orElse((Float) null);
      }

      public void setSpeed(Float speed) {
         this.handle = new Tool.Rule(this.handle.blocks(), Optional.ofNullable(speed), this.handle.correctForDrops());
      }

      public Boolean isCorrectForDrops() {
         return (Boolean)this.handle.correctForDrops().orElse((Boolean) null);
      }

      public void setCorrectForDrops(Boolean correct) {
         this.handle = new Tool.Rule(this.handle.blocks(), this.handle.speed(), Optional.ofNullable(correct));
      }

      public int hashCode() {
         int hash = 5;
         hash = 97 * hash + Objects.hashCode(this.handle);
         return hash;
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            CraftToolRule other = (CraftToolRule)obj;
            return Objects.equals(this.handle, other.handle);
         }
      }

      public String toString() {
         return "CraftToolRule{handle=" + String.valueOf(this.handle) + "}";
      }
   }
}
