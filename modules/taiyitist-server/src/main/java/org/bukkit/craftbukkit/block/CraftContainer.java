package org.bukkit.craftbukkit.block;

import java.util.Collections;
import java.util.Optional;
import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.inventory.ItemStack;

public abstract class CraftContainer<T extends BaseContainerBlockEntity> extends CraftBlockEntityState<T> implements Container {
   public CraftContainer(World world, T tileEntity) {
      super((World)world, (BlockEntity)tileEntity);
   }

   protected CraftContainer(CraftContainer<T> state, Location location) {
      super((CraftBlockEntityState)state, (Location)location);
   }

   public boolean isLocked() {
      return ((BaseContainerBlockEntity)this.getSnapshot()).lockKey != LockCode.NO_LOCK;
   }

   public String getLock() {
      Optional<? extends Component> customName = ((BaseContainerBlockEntity)this.getSnapshot()).lockKey.predicate().components().exact().asPatch().get(DataComponents.CUSTOM_NAME);
      return customName != null ? (String)customName.map(CraftChatMessage::fromComponent).orElse("") : "";
   }

   public void setLock(String key) {
      if (key == null) {
         ((BaseContainerBlockEntity)this.getSnapshot()).lockKey = LockCode.NO_LOCK;
      } else {
         DataComponentExactPredicate predicate = DataComponentExactPredicate.builder().expect(DataComponents.CUSTOM_NAME, CraftChatMessage.fromStringOrNull(key)).build();
         ((BaseContainerBlockEntity)this.getSnapshot()).lockKey = new LockCode(new ItemPredicate(Optional.empty(), Ints.ANY, new DataComponentMatchers(predicate, Collections.emptyMap())));
      }

   }

   public void setLockItem(ItemStack key) {
      if (key == null) {
         ((BaseContainerBlockEntity)this.getSnapshot()).lockKey = LockCode.NO_LOCK;
      } else {
         ((BaseContainerBlockEntity)this.getSnapshot()).lockKey = new LockCode(CraftItemStack.asCriterionConditionItem(key));
      }

   }

   public String getCustomName() {
      T container = (BaseContainerBlockEntity)this.getSnapshot();
      return container.name != null ? CraftChatMessage.fromComponent(container.getCustomName()) : null;
   }

   public void setCustomName(String name) {
      ((BaseContainerBlockEntity)this.getSnapshot()).name = CraftChatMessage.fromStringOrNull(name);
   }

   protected void applyTo(T container) {
      super.applyTo(container);
      if (((BaseContainerBlockEntity)this.getSnapshot()).name == null) {
         container.name = null;
      }

   }

   public abstract CraftContainer<T> copy();

   public abstract CraftContainer<T> copy(Location var1);
}
