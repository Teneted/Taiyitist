package com.taiyitistmc.mixin.world.level.portal;

import com.taiyitistmc.injection.world.level.portal.InjectionPortalForcer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalForcer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.util.BlockStateListPopulator;
import org.bukkit.event.world.PortalCreateEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalForcer.class)
public abstract class MixinPortalForcer implements InjectionPortalForcer {

    // @formatter:off
    @Shadow public abstract Optional<BlockUtil.FoundRectangle> createPortal(BlockPos pos, Direction.Axis axis);
    @Shadow @Final private ServerLevel level;
    @Shadow public abstract Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos p_192986_, boolean p_192987_, WorldBorder p_192988_);
    // @formatter:on

    @Unique
    private AtomicReference<Integer> taiyitist$searchRadius = new AtomicReference<>();
    @Unique
    private transient BlockStateListPopulator taiyitist$populator;
    @Unique
    private transient Entity taiyitist$entity;
    @Unique
    private transient int taiyitist$createRadius = -1;

    @Override
    public Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos pos, WorldBorder worldBorder, int searchRadius) {
        this.taiyitist$searchRadius.set(searchRadius);
        try {
            return this.findPortalAround(pos, false, worldBorder);
        } finally {
            this.taiyitist$searchRadius.set(-1);
        }
    }

    @ModifyArg(method = "createPortal", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;spiralAround(Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;)Ljava/lang/Iterable;"))
    private int taiyitist$changeRadius(int i) {
        return this.taiyitist$createRadius == -1 ? i : this.taiyitist$createRadius;
    }

    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean taiyitist$captureBlocks1(ServerLevel serverWorld, BlockPos pos, BlockState state) {
        if (this.taiyitist$populator == null) {
            this.taiyitist$populator = new BlockStateListPopulator(serverWorld);
        }
        return this.taiyitist$populator.setBlock(pos, state, 3);
    }

    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean taiyitist$captureBlocks2(ServerLevel serverWorld, BlockPos pos, BlockState state, int flags) {
        if (this.taiyitist$populator == null) {
            this.taiyitist$populator = new BlockStateListPopulator(serverWorld);
        }
        return this.taiyitist$populator.setBlock(pos, state, flags);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "createPortal", cancellable = true, at = @At("RETURN"))
    private void taiyitist$portalCreate(BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir) {
        CraftWorld craftWorld =  this.level.getWorld();
        List<org.bukkit.block.BlockState> blockStates;
        if (this.taiyitist$populator == null) {
            blockStates = new ArrayList<>();
        } else {
            blockStates = (List) this.taiyitist$populator.getList();
        }
        PortalCreateEvent event = new PortalCreateEvent(blockStates, craftWorld, (this.taiyitist$entity == null) ? null : this.taiyitist$entity.getBukkitEntity(), PortalCreateEvent.CreateReason.NETHER_PAIR);

        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(Optional.empty());
            return;
        }
        if (this.taiyitist$populator != null) {
            this.taiyitist$populator.updateList();
        }
    }

    @Override
    public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos pos, Direction.Axis axis, Entity entity, int createRadius) {
        this.taiyitist$entity = entity;
        this.taiyitist$createRadius = createRadius;
        try {
            return this.createPortal(pos, axis);
        } finally {
            this.taiyitist$entity = null;
            this.taiyitist$createRadius = -1;
        }
    }
}
