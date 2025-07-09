package com.taiyitistmc.mixin.world.entity.monster;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Zombie.class)
public class MixinZombie {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static ZombieVillager convertVillagerToZombieVillager(ServerLevel serverLevel, Villager villager, net.minecraft.core.BlockPos blockPosition, boolean silent, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason spawnReason) {
        ZombieVillager zombieVillager = (ZombieVillager)villager.convertTo(EntityType.ZOMBIE_VILLAGER, ConversionParams.single(villager, true, true), (zombieVillagerx) -> {
            zombieVillagerx.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(zombieVillagerx.blockPosition()), EntitySpawnReason.CONVERSION, new Zombie.ZombieGroupData(false, true));
            zombieVillagerx.setVillagerData(villager.getVillagerData());
            zombieVillagerx.setGossips(villager.getGossips().copy());
            zombieVillagerx.setTradeOffers(villager.getOffers().copy());
            zombieVillagerx.setVillagerXp(villager.getVillagerXp());
            // CraftBukkit start
            if (!silent) {
                serverLevel.levelEvent((Entity) null, 1026, blockPosition, 0);
            }
            zombieVillagerx.bridge$pushTransformReason(transformReason);
            zombieVillagerx.pushSpawnCause(spawnReason);
        });
        return zombieVillager;
    }
}
