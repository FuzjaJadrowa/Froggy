package pl.fuzjajadrowa.froggy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import pl.fuzjajadrowa.froggy.block.entity.PlayerPaintingBlockEntity;

public class PlayerPaintingBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PlayerPaintingBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> Block.box(-16.0D, -16.0D, 0.0D, 32.0D, 32.0D, 1.0D);
            case EAST -> Block.box(0.0D, -16.0D, -16.0D, 1.0D, 32.0D, 32.0D);
            case WEST -> Block.box(15.0D, -16.0D, -16.0D, 16.0D, 32.0D, 32.0D);
            default -> Block.box(-16.0D, -16.0D, 15.0D, 32.0D, 32.0D, 16.0D);
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        if (direction.getAxis().isHorizontal()) {
            return this.defaultBlockState().setValue(FACING, direction);
        } else {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlayerPaintingBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof PlayerPaintingBlockEntity painting) {
                painting.serverTick(lvl, pos, st);
            }
        };
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos wallPos = pos.relative(facing.getOpposite());
        return level.getBlockState(wallPos).isFaceSturdy(level, wallPos, facing);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!state.canSurvive(level, pos)) {
            if (!level.isClientSide) {
                level.removeBlock(pos, false);
                popResource(level, pos, new net.minecraft.world.item.ItemStack(this));
            }
        }
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player) {
        if (!level.isClientSide) {
            level.removeBlock(pos, false);
            if (!player.isCreative()) {
                popResource(level, pos, new net.minecraft.world.item.ItemStack(this));
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.PAINTING_BREAK, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}