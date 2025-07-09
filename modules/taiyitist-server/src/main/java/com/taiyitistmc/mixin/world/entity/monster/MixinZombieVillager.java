package com.taiyitistmc.mixin.world.entity.monster;

import com.taiyitistmc.bukkit.BukkitFieldHooks;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ZombieVillager.class)
public class MixinZombieVillager {

    private int lastTick = BukkitFieldHooks.currentTick(); // CraftBukkit - add field
}
