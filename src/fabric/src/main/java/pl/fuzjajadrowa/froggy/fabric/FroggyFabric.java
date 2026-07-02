package pl.fuzjajadrowa.froggy.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import pl.fuzjajadrowa.froggy.Froggy;
import pl.fuzjajadrowa.froggy.client.FroggyRenderer;
import pl.fuzjajadrowa.froggy.client.FroggySleepingRenderer;
import pl.fuzjajadrowa.froggy.entity.FroggyEntities;
import pl.fuzjajadrowa.froggy.entity.FroggyJumpscareEntity;
import pl.fuzjajadrowa.froggy.entity.FroggySleepingEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyStalkerEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyBoredEntity;
import pl.fuzjajadrowa.froggy.sound.FroggySounds;
import pl.fuzjajadrowa.froggy.item.FroggyItems;
import pl.fuzjajadrowa.froggy.spawner.FroggySpawner;

public final class FroggyFabric implements ModInitializer, ClientModInitializer {
    public static SoundEvent SCREAM1;
    public static SoundEvent SCREAM2;
    public static SoundEvent SCREAM3;
    public static SoundEvent SLEEPING;
    public static SoundEvent WALK;
    public static SoundEvent FART;
    public static SoundEvent YIPPE;

    public static EntityType<FroggyStalkerEntity> FROGGY_STALKER;
    public static EntityType<FroggyJumpscareEntity> FROGGY_JUMPSCARE;
    public static EntityType<FroggySleepingEntity> FROGGY_SLEEPING;
    public static EntityType<FroggyBoredEntity> FROGGY_BORED;

    public static Item COUGH_SYRUP;

    @Override
    public void onInitialize() {
        Froggy.init();

        SCREAM1 = registerSound("scream1");
        SCREAM2 = registerSound("scream2");
        SCREAM3 = registerSound("scream3");
        SLEEPING = registerSound("sleeping");
        WALK = registerSound("walk");
        FART = registerSound("fart");
        YIPPE = registerSound("yippe");

        FroggySounds.SCREAM1 = () -> SCREAM1;
        FroggySounds.SCREAM2 = () -> SCREAM2;
        FroggySounds.SCREAM3 = () -> SCREAM3;
        FroggySounds.SLEEPING = () -> SLEEPING;
        FroggySounds.WALK = () -> WALK;
        FroggySounds.FART = () -> FART;
        FroggySounds.YIPPE = () -> YIPPE;

        FROGGY_STALKER = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "froggy_stalker"),
                EntityType.Builder.of(FroggyStalkerEntity::new, MobCategory.MONSTER)
                        .sized(0.6f, 1.8f)
                        .build("froggy_stalker")
        );

        FROGGY_JUMPSCARE = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "froggy_jumpscare"),
                EntityType.Builder.of(FroggyJumpscareEntity::new, MobCategory.MONSTER)
                        .sized(0.6f, 1.8f)
                        .build("froggy_jumpscare")
        );

        FROGGY_SLEEPING = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "froggy_sleeping"),
                EntityType.Builder.of(FroggySleepingEntity::new, MobCategory.MONSTER)
                        .sized(0.6f, 1.8f)
                        .build("froggy_sleeping")
        );

        FROGGY_BORED = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "froggy_bored"),
                EntityType.Builder.of(FroggyBoredEntity::new, MobCategory.MONSTER)
                        .sized(0.6f, 1.8f)
                        .build("froggy_bored")
        );

        COUGH_SYRUP = Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "cough_syrup"),
                new Item(new Item.Properties().stacksTo(1))
        );

        FroggyEntities.STALKER = () -> FROGGY_STALKER;
        FroggyEntities.JUMPSCARE = () -> FROGGY_JUMPSCARE;
        FroggyEntities.SLEEPING = () -> FROGGY_SLEEPING;
        FroggyEntities.BORED = () -> FROGGY_BORED;

        FroggyItems.COUGH_SYRUP = () -> COUGH_SYRUP;

        FabricDefaultAttributeRegistry.register(FROGGY_STALKER, FroggyStalkerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FROGGY_JUMPSCARE, FroggyJumpscareEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FROGGY_SLEEPING, FroggySleepingEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FROGGY_BORED, FroggyBoredEntity.createAttributes());

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                FroggySpawner.tickPlayer(player);
            }
        });

        PayloadTypeRegistry.playC2S().register(pl.fuzjajadrowa.froggy.network.FroggyCoughSyrupPayload.TYPE, pl.fuzjajadrowa.froggy.network.FroggyCoughSyrupPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(pl.fuzjajadrowa.froggy.network.FroggyCoughSyrupPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                pl.fuzjajadrowa.froggy.entity.BaseFroggyEntity.handleCoughSyrupChoice(context.player(), payload.entityId(), payload.isCorrect());
            });
        });
    }

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(FroggyFabric.FROGGY_STALKER, FroggyRenderer::new);
        EntityRendererRegistry.register(FroggyFabric.FROGGY_JUMPSCARE, FroggyRenderer::new);
        EntityRendererRegistry.register(FroggyFabric.FROGGY_SLEEPING, FroggySleepingRenderer::new);
        EntityRendererRegistry.register(FroggyFabric.FROGGY_BORED, FroggyRenderer::new);

        pl.fuzjajadrowa.froggy.network.FroggyPacketSender.sender = (entityId, isCorrect) -> {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new pl.fuzjajadrowa.froggy.network.FroggyCoughSyrupPayload(entityId, isCorrect));
        };
    }

    private static SoundEvent registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, name);
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, soundEvent);
    }
}