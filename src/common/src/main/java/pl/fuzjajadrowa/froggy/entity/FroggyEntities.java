package pl.fuzjajadrowa.froggy.entity;

import net.minecraft.world.entity.EntityType;
import java.util.function.Supplier;

public class FroggyEntities {
    public static Supplier<EntityType<FroggyStalkerEntity>> STALKER;
    public static Supplier<EntityType<FroggyJumpscareEntity>> JUMPSCARE;
    public static Supplier<EntityType<FroggySleepingEntity>> SLEEPING;
}