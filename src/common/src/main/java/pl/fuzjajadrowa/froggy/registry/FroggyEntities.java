package pl.fuzjajadrowa.froggy.registry;

import net.minecraft.world.entity.EntityType;
import pl.fuzjajadrowa.froggy.entity.FroggyBoredEntity;
import pl.fuzjajadrowa.froggy.entity.FroggySleepingEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyStalkerEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyJumpscareEntity;

import java.util.function.Supplier;

public class FroggyEntities {
    public static Supplier<EntityType<FroggyStalkerEntity>> STALKER;
    public static Supplier<EntityType<FroggyJumpscareEntity>> JUMPSCARE;
    public static Supplier<EntityType<FroggySleepingEntity>> SLEEPING;
    public static Supplier<EntityType<FroggyBoredEntity>> BORED;
    public static Supplier<EntityType<pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity>> TAMED;
    public static Supplier<EntityType<pl.fuzjajadrowa.froggy.entity.FroggyTraderEntity>> TRADER;
}