package com.player.journal.block;

import com.mojang.serialization.MapCodec;
import com.player.journal.block.entity.ResearchBenchBlockEntity;
import com.player.journal.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ResearchBenchBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty RIGHT_HALF = BooleanProperty.create("right_half");

    public static final MapCodec<ResearchBenchBlock> CODEC = simpleCodec(ResearchBenchBlock::new);

    @Override
    protected MapCodec<ResearchBenchBlock> codec() {
        return CODEC;
    }

    public ResearchBenchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(RIGHT_HALF, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(RIGHT_HALF)) return null;
        return new ResearchBenchBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, RIGHT_HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();
        BlockPos rightPos = pos.relative(facing.getCounterClockWise());

        if (context.getLevel().getBlockState(rightPos).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, facing).setValue(RIGHT_HALF, false);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos rightPos = pos.relative(facing.getCounterClockWise());
            level.setBlock(rightPos, state.setValue(RIGHT_HALF, true), 3);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        boolean isRightHalf = state.getValue(RIGHT_HALF);
        Direction facing = state.getValue(FACING);

        BlockPos otherPos = isRightHalf
                ? pos.relative(facing.getClockWise())
                : pos.relative(facing.getCounterClockWise());

        BlockState otherState = level.getBlockState(otherPos);

        if (otherState.is(this) && otherState.getValue(RIGHT_HALF) != isRightHalf) {
            level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        boolean isRightHalf = state.getValue(RIGHT_HALF);
        Direction blockFacing = state.getValue(FACING);
        Direction requiredDirection = isRightHalf ? blockFacing.getClockWise() : blockFacing.getCounterClockWise();

        if (facing == requiredDirection && !facingState.is(this)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            boolean isRightHalf = state.getValue(RIGHT_HALF);
            Direction facing = state.getValue(FACING);

            BlockPos targetPos = isRightHalf ? pos.relative(facing.getClockWise()) : pos;

            BlockEntity blockEntity = level.getBlockEntity(targetPos);
            if (blockEntity instanceof ResearchBenchBlockEntity menuProvider) {
                player.openMenu(menuProvider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() || state.getValue(RIGHT_HALF)) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.RESEARCH_BENCH.get(), ResearchBenchBlockEntity::serverTick);
    }
}