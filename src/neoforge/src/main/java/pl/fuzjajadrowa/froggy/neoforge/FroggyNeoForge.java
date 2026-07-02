package pl.fuzjajadrowa.froggy.neoforge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
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

@Mod(Froggy.MOD_ID)
public final class FroggyNeoForge {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Froggy.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Froggy.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Froggy.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SCREAM1 = SOUNDS.register("scream1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "scream1")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SCREAM2 = SOUNDS.register("scream2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "scream2")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SCREAM3 = SOUNDS.register("scream3", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "scream3")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SLEEPING = SOUNDS.register("sleeping", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "sleeping")));
    public static final DeferredHolder<SoundEvent, SoundEvent> WALK = SOUNDS.register("walk", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "walk")));
    public static final DeferredHolder<SoundEvent, SoundEvent> FART = SOUNDS.register("fart", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "fart")));
    public static final DeferredHolder<SoundEvent, SoundEvent> YIPPE = SOUNDS.register("yippe", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "yippe")));

    public static final DeferredHolder<EntityType<?>, EntityType<FroggyStalkerEntity>> FROGGY_STALKER = ENTITY_TYPES.register("froggy_stalker",
            () -> EntityType.Builder.of(FroggyStalkerEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build("froggy_stalker"));

    public static final DeferredHolder<EntityType<?>, EntityType<FroggyJumpscareEntity>> FROGGY_JUMPSCARE = ENTITY_TYPES.register("froggy_jumpscare",
            () -> EntityType.Builder.of(FroggyJumpscareEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build("froggy_jumpscare"));

    public static final DeferredHolder<EntityType<?>, EntityType<FroggySleepingEntity>> FROGGY_SLEEPING = ENTITY_TYPES.register("froggy_sleeping",
            () -> EntityType.Builder.of(FroggySleepingEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build("froggy_sleeping"));

    public static final DeferredHolder<EntityType<?>, EntityType<FroggyBoredEntity>> FROGGY_BORED = ENTITY_TYPES.register("froggy_bored",
            () -> EntityType.Builder.of(FroggyBoredEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build("froggy_bored"));

    public static final DeferredHolder<Item, Item> COUGH_SYRUP = ITEMS.register("cough_syrup",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public FroggyNeoForge(IEventBus modEventBus, net.neoforged.fml.ModContainer modContainer) {
        Froggy.init();

        SOUNDS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);

        FroggySounds.SCREAM1 = SCREAM1;
        FroggySounds.SCREAM2 = SCREAM2;
        FroggySounds.SCREAM3 = SCREAM3;
        FroggySounds.SLEEPING = SLEEPING;
        FroggySounds.WALK = WALK;
        FroggySounds.FART = FART;
        FroggySounds.YIPPE = YIPPE;

        FroggyEntities.STALKER = FROGGY_STALKER;
        FroggyEntities.JUMPSCARE = FROGGY_JUMPSCARE;
        FroggyEntities.SLEEPING = FROGGY_SLEEPING;
        FroggyEntities.BORED = FROGGY_BORED;

        FroggyItems.COUGH_SYRUP = COUGH_SYRUP;

        if (net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.CLIENT) {
            pl.fuzjajadrowa.froggy.network.FroggyPacketSender.sender = (entityId, isCorrect) -> {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new pl.fuzjajadrowa.froggy.network.FroggyCoughSyrupPayload(entityId, isCorrect));
            };
            ClientSetup.registerConfigScreen(modContainer);
        }
    }

    private static class ClientSetup {
        public static void registerConfigScreen(net.neoforged.fml.ModContainer modContainer) {
            modContainer.registerExtensionPoint(
                net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                (container, parent) -> new pl.fuzjajadrowa.froggy.client.FroggyConfigScreen(parent)
            );
        }
    }

    @EventBusSubscriber(modid = Froggy.MOD_ID)
    public static class ModEvents {
        @SubscribeEvent
        public static void buildContents(net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.FOOD_AND_DRINKS) {
                event.accept(COUGH_SYRUP.get());
            }
        }

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(FROGGY_STALKER.get(), FroggyStalkerEntity.createAttributes().build());
            event.put(FROGGY_JUMPSCARE.get(), FroggyJumpscareEntity.createAttributes().build());
            event.put(FROGGY_SLEEPING.get(), FroggySleepingEntity.createAttributes().build());
            event.put(FROGGY_BORED.get(), FroggyBoredEntity.createAttributes().build());
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(FROGGY_STALKER.get(), FroggyRenderer::new);
            event.registerEntityRenderer(FROGGY_JUMPSCARE.get(), FroggyRenderer::new);
            event.registerEntityRenderer(FROGGY_SLEEPING.get(), FroggySleepingRenderer::new);
            event.registerEntityRenderer(FROGGY_BORED.get(), FroggyRenderer::new);
        }

        @SubscribeEvent
        public static void registerPayloads(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
            final net.neoforged.neoforge.network.registration.PayloadRegistrar registrar = event.registrar(Froggy.MOD_ID);
            registrar.playToServer(
                pl.fuzjajadrowa.froggy.network.FroggyCoughSyrupPayload.TYPE,
                pl.fuzjajadrowa.froggy.network.FroggyCoughSyrupPayload.CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        pl.fuzjajadrowa.froggy.entity.BaseFroggyEntity.handleCoughSyrupChoice(context.player(), payload.entityId(), payload.isCorrect());
                    });
                }
            );
        }
    }

    @EventBusSubscriber(modid = Froggy.MOD_ID)
    public static class GameEvents {
        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                FroggySpawner.tickPlayer(serverPlayer);
            }
        }
    }
}