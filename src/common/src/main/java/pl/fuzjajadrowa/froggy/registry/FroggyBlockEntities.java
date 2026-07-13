package pl.fuzjajadrowa.froggy.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import pl.fuzjajadrowa.froggy.block.entity.PlayerPaintingBlockEntity;
import java.util.function.Supplier;

public class FroggyBlockEntities {
    public static Supplier<BlockEntityType<PlayerPaintingBlockEntity>> PLAYER_PAINTING;
}