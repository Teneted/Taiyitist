package org.teneted.taiyitist.mixin.world.entity.player;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.player.InjectionInventory;

@Mixin(Inventory.class)
public class MixinInventory implements InjectionInventory {
}
