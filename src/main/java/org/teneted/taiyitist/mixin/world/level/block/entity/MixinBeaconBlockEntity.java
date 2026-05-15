package org.teneted.taiyitist.mixin.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.injection.world.level.block.entity.InjectionBeaconBlockEntity;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class MixinBeaconBlockEntity implements InjectionBeaconBlockEntity {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static List getHumansInRange(Level level, BlockPos worldPosition, int i) {
        {
            double d0 = (double) (i * 10 + 10);

            AABB aabb = (new AABB(worldPosition)).inflate(d0).expandTowards(0.0D, (double) level.getHeight(), 0.0D);
            List<Player> list = level.<Player>getEntitiesOfClass(Player.class, aabb);

            return list;
        }
    }
}
