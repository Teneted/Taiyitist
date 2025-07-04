package com.mohistmc.banner.mixin.world.level.portal;

import com.mohistmc.banner.injection.world.level.portal.InjectionPortalForcer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalForcer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.world.PortalCreateEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PortalForcer.class, priority = 1500)
public abstract class MixinPortalForcer implements InjectionPortalForcer {

    @Shadow @Final protected ServerLevel level;

    private transient int banner$searchRadius = -1;

    @Override
    public void pushSearchRadius(int searchRadius) {
        this.banner$searchRadius = searchRadius;
    }

    @ModifyVariable(method = "findClosestPortalPosition", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    private int banner$useSearchRadius(int i) {
        return this.banner$searchRadius == -1 ? i : this.banner$searchRadius;
    }

    @ModifyArg(method = "createPortal", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;spiralAround(Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;)Ljava/lang/Iterable;"))
    private int banner$changeRadius(int i) {
        return this.banner$createRadius == -1 ? i : this.banner$createRadius;
    }

    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean banner$captureBlocks1(ServerLevel serverWorld, BlockPos pos, BlockState state) {
        if (this.banner$populator == null) {
            this.banner$populator = new BlockStateListPopulator(serverWorld);
        }
        return this.banner$populator.setBlock(pos, state, 3);
    }

    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean banner$captureBlocks2(ServerLevel serverWorld, BlockPos pos, BlockState state, int flags) {
        if (this.banner$populator == null) {
            this.banner$populator = new BlockStateListPopulator(serverWorld);
        }
        return this.banner$populator.setBlock(pos, state, flags);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "createPortal", cancellable = true, at = @At("RETURN"))
    private void banner$portalCreate(BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir) {
        CraftWorld craftWorld = this.level.getWorld();
        List<org.bukkit.block.BlockState> blockStates;
        if (this.banner$populator == null) {
            blockStates = new ArrayList<>();
        } else {
            blockStates = (List) this.banner$populator.getList();
        }
        PortalCreateEvent event = new PortalCreateEvent(blockStates, craftWorld, (this.banner$entity == null) ? null : this.banner$entity.getBukkitEntity(), PortalCreateEvent.CreateReason.NETHER_PAIR);

        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(Optional.empty());
            return;
        }
        if (this.banner$populator != null) {
            this.banner$populator.updateList();
        }
    }

    private transient BlockStateListPopulator banner$populator;
    private transient Entity banner$entity;
    private transient int banner$createRadius = -1;

    @Override
    public void pushPortalCreate(Entity entity, int createRadius) {
        this.banner$entity = entity;
        this.banner$createRadius = createRadius;
    }
}
