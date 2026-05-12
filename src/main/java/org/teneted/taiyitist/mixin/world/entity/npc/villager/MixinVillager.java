package org.teneted.taiyitist.mixin.world.entity.npc.villager;

import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.npc.villager.InjectionVillager;

@Mixin(Villager.class)
public class MixinVillager implements InjectionVillager {
}
