package pl.fuzjajadrowa.froggy.registry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity;
import pl.fuzjajadrowa.froggy.menu.FroggyTamedMenu;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class FroggyMenus {
    public static Supplier<MenuType<FroggyTamedMenu>> FROGGY_TAMED;
    public static BiConsumer<ServerPlayer, FroggyTamedEntity> openMenuDelegate;
}