//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.DoubleBlockCombiner.BlockType;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.teneted.taiyitist.injection.world.level.block.InjectionChestBlock;

public class ChestBlock extends AbstractChestBlock<ChestBlockEntity> implements SimpleWaterloggedBlock, InjectionChestBlock {
    public static final MapCodec<ChestBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("open_sound").forGetter(ChestBlock::getOpenChestSound), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("close_sound").forGetter(ChestBlock::getCloseChestSound), propertiesCodec()).apply(i, (openSound, closeSound, p) -> new ChestBlock(() -> BlockEntityType.CHEST, openSound, closeSound, p)));
    public static final EnumProperty<Direction> FACING;
    public static final EnumProperty<ChestType> TYPE;
    public static final BooleanProperty WATERLOGGED;
    public static final int EVENT_SET_OPEN_COUNT = 1;
    private static final VoxelShape SHAPE;
    private static final Map<Direction, VoxelShape> HALF_SHAPES;
    private final SoundEvent openSound;
    private final SoundEvent closeSound;
    private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<Container>> CHEST_COMBINER;
    public static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER;

    public static class DoubleChestCombiner implements DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> {

        @Override
        public Optional<MenuProvider> acceptDouble(ChestBlockEntity first, ChestBlockEntity second) {
            final CompoundContainer container = new CompoundContainer(first, second);
            return Optional.of(new Dummy.DoubleInventory(first, second, container));
        }

        @Override
        public Optional<MenuProvider> acceptSingle(ChestBlockEntity single) {
            return Optional.of(single);
        }

        @Override
        public Optional<MenuProvider> acceptNone() {
            return Optional.empty();
        }

        public static class Dummy {
            public static class DoubleInventory implements MenuProvider {
                private final ChestBlockEntity first;
                private final ChestBlockEntity second;
                public final CompoundContainer container;

                public DoubleInventory(ChestBlockEntity first, ChestBlockEntity second, CompoundContainer container) {
                    this.first = first;
                    this.second = second;
                    this.container = container;
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
                    if (first.canOpen(player) && second.canOpen(player)) {
                        first.unpackLootTable(inventory.player);
                        second.unpackLootTable(inventory.player);
                        return ChestMenu.sixRows(containerId, inventory, container);
                    } else {
                        Direction connectedDirection = ChestBlock.getConnectedDirection(first.getBlockState());
                        Vec3 firstCenter = first.getBlockPos().getCenter();
                        Vec3 centerBetweenChests = firstCenter.add((double)connectedDirection.getStepX() / (double)2.0F, (double)0.0F, (double)connectedDirection.getStepZ() / (double)2.0F);
                        BaseContainerBlockEntity.sendChestLockedNotifications(centerBetweenChests, player, this.getDisplayName());
                        return null;
                    }
                }

                @Override
                public Component getDisplayName() {
                    if (first.hasCustomName()) {
                        return first.getDisplayName();
                    } else {
                        return (Component)(second.hasCustomName() ? second.getDisplayName() : Component.translatable("container.chestDouble"));
                    }
                }
                // CraftBukkit end
            }
        }
    }

    public MapCodec<? extends ChestBlock> codec() {
        return CODEC;
    }

    public ChestBlock(final Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityType, final SoundEvent openSound, final SoundEvent closeSound, final BlockBehaviour.Properties properties) {
        super(properties, blockEntityType);
        this.openSound = openSound;
        this.closeSound = closeSound;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, ChestType.SINGLE)).setValue(WATERLOGGED, false));
    }

    public static DoubleBlockCombiner.BlockType getBlockType(final BlockState state) {
        ChestType type = (ChestType)state.getValue(TYPE);
        if (type == ChestType.SINGLE) {
            return BlockType.SINGLE;
        } else {
            return type == ChestType.RIGHT ? BlockType.FIRST : BlockType.SECOND;
        }
    }

    protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
        if ((Boolean)state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (this.chestCanConnectTo(neighbourState) && directionToNeighbour.getAxis().isHorizontal()) {
            ChestType neighbourType = (ChestType)neighbourState.getValue(TYPE);
            if (state.getValue(TYPE) == ChestType.SINGLE && neighbourType != ChestType.SINGLE && state.getValue(FACING) == neighbourState.getValue(FACING) && getConnectedDirection(neighbourState) == directionToNeighbour.getOpposite()) {
                return (BlockState)state.setValue(TYPE, neighbourType.getOpposite());
            }
        } else if (getConnectedDirection(state) == directionToNeighbour) {
            return (BlockState)state.setValue(TYPE, ChestType.SINGLE);
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    public boolean chestCanConnectTo(final BlockState blockState) {
        return blockState.is(this);
    }

    protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        VoxelShape var10000;
        switch ((ChestType)state.getValue(TYPE)) {
            case SINGLE:
                var10000 = SHAPE;
                break;
            case LEFT:
            case RIGHT:
                var10000 = (VoxelShape)HALF_SHAPES.get(getConnectedDirection(state));
                break;
            default:
                throw new MatchException((String)null, (Throwable)null);
        }

        return var10000;
    }

    public static Direction getConnectedDirection(final BlockState state) {
        Direction facing = (Direction)state.getValue(FACING);
        return state.getValue(TYPE) == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
    }

    public static BlockPos getConnectedBlockPos(final BlockPos pos, final BlockState state) {
        Direction connectedDirection = getConnectedDirection(state);
        return pos.relative(connectedDirection);
    }

    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        ChestType type = ChestType.SINGLE;
        Direction facingDirection = context.getHorizontalDirection().getOpposite();
        FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
        boolean secondaryUse = context.isSecondaryUseActive();
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isHorizontal() && secondaryUse) {
            Direction neighbourFacing = this.candidatePartnerFacing(context.getLevel(), context.getClickedPos(), clickedFace.getOpposite());
            if (neighbourFacing != null && neighbourFacing.getAxis() != clickedFace.getAxis()) {
                facingDirection = neighbourFacing;
                type = neighbourFacing.getCounterClockWise() == clickedFace.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
            }
        }

        if (type == ChestType.SINGLE && !secondaryUse) {
            type = this.getChestType(context.getLevel(), context.getClickedPos(), facingDirection);
        }

        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facingDirection)).setValue(TYPE, type)).setValue(WATERLOGGED, replacedFluidState.is(Fluids.WATER));
    }

    protected ChestType getChestType(final Level level, final BlockPos pos, final Direction facingDirection) {
        if (facingDirection == this.candidatePartnerFacing(level, pos, facingDirection.getClockWise())) {
            return ChestType.LEFT;
        } else {
            return facingDirection == this.candidatePartnerFacing(level, pos, facingDirection.getCounterClockWise()) ? ChestType.RIGHT : ChestType.SINGLE;
        }
    }

    protected FluidState getFluidState(final BlockState state) {
        return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    private @Nullable Direction candidatePartnerFacing(final Level level, final BlockPos pos, final Direction neighbourDirection) {
        BlockState state = level.getBlockState(pos.relative(neighbourDirection));
        return this.chestCanConnectTo(state) && state.getValue(TYPE) == ChestType.SINGLE ? (Direction)state.getValue(FACING) : null;
    }

    protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel) {
            MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
            if (menuProvider != null) {
                player.openMenu(menuProvider);
                player.awardStat(this.getOpenChestStat());
                PiglinAi.angerNearbyPiglins(serverLevel, player, true);
            }
        }

        return InteractionResult.SUCCESS;
    }

    protected Stat<Identifier> getOpenChestStat() {
        return Stats.CUSTOM.get(Stats.OPEN_CHEST);
    }

    public BlockEntityType<? extends ChestBlockEntity> blockEntityType() {
        return (BlockEntityType)this.blockEntityType.get();
    }

    public static @Nullable Container getContainer(final ChestBlock block, final BlockState state, final Level level, final BlockPos pos, final boolean ignoreBeingBlocked) {
        return (Container)((Optional)block.combine(state, level, pos, ignoreBeingBlocked).apply(CHEST_COMBINER)).orElse((Object)null);
    }

    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(final BlockState state, final Level level, final BlockPos pos, final boolean ignoreBeingBlocked) {
        BiPredicate<LevelAccessor, BlockPos> predicate;
        if (ignoreBeingBlocked) {
            predicate = (levelAccessor, blockPos) -> false;
        } else {
            predicate = ChestBlock::isChestBlockedAt;
        }

        return DoubleBlockCombiner.combineWithNeigbour((BlockEntityType)this.blockEntityType.get(), ChestBlock::getBlockType, ChestBlock::getConnectedDirection, FACING, state, level, pos, predicate);
    }

    protected @Nullable MenuProvider getMenuProvider(final BlockState state, final Level level, final BlockPos pos) {
        return (MenuProvider)((Optional)this.combine(state, level, pos, false).apply(MENU_PROVIDER_COMBINER)).orElse((Object)null);
    }

    public static DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction> opennessCombiner(final LidBlockEntity entity) {
        return new DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction>() {
            public Float2FloatFunction acceptDouble(final ChestBlockEntity first, final ChestBlockEntity second) {
                return (partialTickTime) -> Math.max(first.getOpenNess(partialTickTime), second.getOpenNess(partialTickTime));
            }

            public Float2FloatFunction acceptSingle(final ChestBlockEntity single) {
                Objects.requireNonNull(single);
                return single::getOpenNess;
            }

            public Float2FloatFunction acceptNone() {
                LidBlockEntity var10000 = entity;
                Objects.requireNonNull(var10000);
                return var10000::getOpenNess;
            }
        };
    }

    public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        return new ChestBlockEntity(worldPosition, blockState);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
        return level.isClientSide() ? createTickerHelper(type, this.blockEntityType(), ChestBlockEntity::lidAnimateTick) : null;
    }

    public static boolean isChestBlockedAt(final LevelAccessor level, final BlockPos pos) {
        return isBlockedChestByBlock(level, pos) || isCatSittingOnChest(level, pos);
    }

    private static boolean isBlockedChestByBlock(final BlockGetter level, final BlockPos pos) {
        BlockPos above = pos.above();
        return level.getBlockState(above).isRedstoneConductor(level, above);
    }

    private static boolean isCatSittingOnChest(final LevelAccessor level, final BlockPos pos) {
        List<Cat> cats = level.getEntitiesOfClass(Cat.class, new AABB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1)));
        if (!cats.isEmpty()) {
            for(Cat cat : cats) {
                if (cat.isInSittingPose()) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean hasAnalogOutputSignal(final BlockState state) {
        return true;
    }

    protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(this, state, level, pos, false));
    }

    protected BlockState rotate(final BlockState state, final Rotation rotation) {
        return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
    }

    protected BlockState mirror(final BlockState state, final Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, TYPE, WATERLOGGED});
    }

    protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
        return false;
    }

    protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChestBlockEntity) {
            ((ChestBlockEntity)blockEntity).recheckOpen();
        }

    }

    public SoundEvent getOpenChestSound() {
        return this.openSound;
    }

    public SoundEvent getCloseChestSound() {
        return this.closeSound;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        TYPE = BlockStateProperties.CHEST_TYPE;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE = Block.column((double)14.0F, (double)0.0F, (double)14.0F);
        HALF_SHAPES = Shapes.rotateHorizontal(Block.boxZ((double)14.0F, (double)0.0F, (double)14.0F, (double)0.0F, (double)15.0F));
        CHEST_COMBINER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<Container>>() {
            public Optional<Container> acceptDouble(final ChestBlockEntity first, final ChestBlockEntity second) {
                return Optional.of(new CompoundContainer(first, second));
            }

            public Optional<Container> acceptSingle(final ChestBlockEntity single) {
                return Optional.of(single);
            }

            public Optional<Container> acceptNone() {
                return Optional.empty();
            }
        };
        MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>>() {
            public Optional<MenuProvider> acceptDouble(final ChestBlockEntity first, final ChestBlockEntity second) {
                final Container container = new CompoundContainer(first, second);
                return Optional.of(new MenuProvider() {
                    {
                        Objects.requireNonNull(null);
                    }

                    public @Nullable AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
                        if (first.canOpen(player) && second.canOpen(player)) {
                            first.unpackLootTable(inventory.player);
                            second.unpackLootTable(inventory.player);
                            return ChestMenu.sixRows(containerId, inventory, container);
                        } else {
                            Direction connectedDirection = ChestBlock.getConnectedDirection(first.getBlockState());
                            Vec3 firstCenter = first.getBlockPos().getCenter();
                            Vec3 centerBetweenChests = firstCenter.add((double)connectedDirection.getStepX() / (double)2.0F, (double)0.0F, (double)connectedDirection.getStepZ() / (double)2.0F);
                            BaseContainerBlockEntity.sendChestLockedNotifications(centerBetweenChests, player, this.getDisplayName());
                            return null;
                        }
                    }

                    public Component getDisplayName() {
                        if (first.hasCustomName()) {
                            return first.getDisplayName();
                        } else {
                            return (Component)(second.hasCustomName() ? second.getDisplayName() : Component.translatable("container.chestDouble"));
                        }
                    }
                });
            }

            public Optional<MenuProvider> acceptSingle(final ChestBlockEntity single) {
                return Optional.of(single);
            }

            public Optional<MenuProvider> acceptNone() {
                return Optional.empty();
            }
        };
    }
}
