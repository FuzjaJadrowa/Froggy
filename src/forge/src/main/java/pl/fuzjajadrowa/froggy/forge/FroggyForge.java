package pl.fuzjajadrowa.froggy.forge;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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
import pl.fuzjajadrowa.froggy.network.FroggyPackets;

@Mod(Froggy.MOD_ID)
public final class FroggyForge {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Froggy.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Froggy.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Froggy.MOD_ID);

    public static final RegistryObject<SoundEvent> SCREAM1 = SOUNDS.register("scream1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "scream1")));
    public static final RegistryObject<SoundEvent> SCREAM2 = SOUNDS.register("scream2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "scream2")));
    public static final RegistryObject<SoundEvent> SCREAM3 = SOUNDS.register("scream3", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "scream3")));
    public static final RegistryObject<SoundEvent> SLEEPING = SOUNDS.register("sleeping", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "sleeping")));
    public static final RegistryObject<SoundEvent> WALK = SOUNDS.register("walk", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "walk")));
    public static final RegistryObject<SoundEvent> FART = SOUNDS.register("fart", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "fart")));
    public static final RegistryObject<SoundEvent> YIPPE = SOUNDS.register("yippe", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "yippe")));

    public static final RegistryObject<EntityType<FroggyStalkerEntity>> FROGGY_STALKER = ENTITY_TYPES.register("froggy_stalker",
            () -> EntityType.Builder.of(FroggyStalkerEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build("froggy_stalker"));

    public static final RegistryObject<EntityType<FroggyJumpscareEntity>> FROGGY_JUMPSCARE = ENTITY_TYPES.register("froggy_jumpscare",
            () -> EntityType.Builder.of(FroggyJumpscareEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build("froggy_jumpscare"));

    public static final RegistryObject<EntityType<FroggySleepingEntity>> FROGGY_SLEEPING = ENTITY_TYPES.register("froggy_sleeping",
            () -> EntityType.Builder.of(FroggySleepingEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build("froggy_sleeping"));

    public static final RegistryObject<EntityType<FroggyBoredEntity>> FROGGY_BORED = ENTITY_TYPES.register("froggy_bored",
            () -> EntityType.Builder.of(FroggyBoredEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .build("froggy_bored"));

    public static final RegistryObject<Item> COUGH_SYRUP = ITEMS.register("cough_syrup",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public FroggyForge() {
        Froggy.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

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

        // Register custom packets
        FroggyPackets.register();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(GameEvents.class);
    }

    @Mod.EventBusSubscriber(modid = Froggy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
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
        public static void onClientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
            ModLoadingContext.get().registerExtensionPoint(
                net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory(
                    (mc, parent) -> new pl.fuzjajadrowa.froggy.client.FroggyConfigScreen(parent)
                )
            );
        }
    }

    public static class GameEvents {
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer serverPlayer) {
                FroggySpawner.tickPlayer(serverPlayer);
            }
        }
    }
}
