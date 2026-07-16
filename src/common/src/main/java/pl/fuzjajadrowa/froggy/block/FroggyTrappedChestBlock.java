package pl.fuzjajadrowa.froggy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import pl.fuzjajadrowa.froggy.block.entity.FroggyTrappedChestBlockEntity;
import pl.fuzjajadrowa.froggy.registry.FroggyBlockEntities;

import java.util.function.Supplier;

public class FroggyTrappedChestBlock extends ChestBlock {
    public FroggyTrappedChestBlock(Properties properties) {
        super(properties, () -> FroggyBlockEntities.FROGGY_TRAPPED_CHEST.get());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FroggyTrappedChestBlockEntity(pos, state);
    }

    //? if >=1.21.1 {
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FroggyTrappedChestBlockEntity chestBe) {
                chestBe.trigger(player);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    //?} else {
    /* @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FroggyTrappedChestBlockEntity chestBe) {
                chestBe.trigger(player);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    } */
    //?}

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return createTickerHelper(type, FroggyBlockEntities.FROGGY_TRAPPED_CHEST.get(), ChestBlockEntity::lidAnimateTick);
        } else {
            return (lvl, pos, st, be) -> {
                if (be instanceof FroggyTrappedChestBlockEntity chestBe) {
                    chestBe.serverTick(lvl, pos, st);
                }
            };
        }
    }
}