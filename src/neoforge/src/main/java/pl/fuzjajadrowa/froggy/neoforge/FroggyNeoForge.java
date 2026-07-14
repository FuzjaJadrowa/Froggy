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
import pl.fuzjajadrowa.froggy.registry.FroggyEntities;
import pl.fuzjajadrowa.froggy.entity.FroggySleepingEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyStalkerEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyBoredEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyJumpscareEntity;
import pl.fuzjajadrowa.froggy.registry.FroggySounds;
import pl.fuzjajadrowa.froggy.registry.FroggyItems;
import pl.fuzjajadrowa.froggy.spawner.FroggySpawner;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

@Mod(Froggy.MOD_ID)
public final class FroggyNeoForge {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Froggy.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Froggy.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Froggy.MOD_ID);
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Froggy.MOD_ID);
    public static final DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Froggy.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SCREAM1 = SOUNDS.register("scream1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "scream1")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SCREAM2 = SOUNDS.register("scream2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "scream2")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SCREAM3 = SOUNDS.register("scream3", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "scream3")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SLEEPING = SOUNDS.register("sleeping", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "sleeping")));
    public static final DeferredHolder<SoundEvent, SoundEvent> WALK = SOUNDS.register("walk", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "walk")));
    public static final DeferredHolder<SoundEvent, SoundEvent> FART = SOUNDS.register("fart", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "fart")));
    public static final DeferredHolder<SoundEvent, SoundEvent> YIPPE = SOUNDS.register("yippe", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "yippe")));
    public static final DeferredHolder<SoundEvent, SoundEvent> MLEM = SOUNDS.register("mlem", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "mlem")));
    public static final DeferredHolder<SoundEvent, SoundEvent> BUZZ = SOUNDS.register("buzz", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "buzz")));

    public static final DeferredHolder<Item, Item> COUGH_SYRUP = ITEMS.register("cough_syrup",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> SWEET_BOTTLE = ITEMS.register("sweet_bottle",
            () -> new pl.fuzjajadrowa.froggy.item.SweetBottleItem(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> FLY_IN_A_BOTTLE = ITEMS.register("fly_in_a_bottle",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> SPEAKER_UPGRADE = ITEMS.register("speaker_upgrade",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> MEGAPHONE_UPGRADE = ITEMS.register("megaphone_upgrade",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> AMPLIFIER_UPGRADE = ITEMS.register("amplifier_upgrade",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SMALL_POUCH_UPGRADE = ITEMS.register("small_pouch_upgrade",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> MEDIUM_POUCH_UPGRADE = ITEMS.register("medium_pouch_upgrade",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> LARGE_POUCH_UPGRADE = ITEMS.register("large_pouch_upgrade",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.Block> FROGGY_BED = BLOCKS.register("froggy_bed",
            () -> new pl.fuzjajadrowa.froggy.block.FroggyBedBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(0.2F).sound(net.minecraft.world.level.block.SoundType.MOSS).noOcclusion()));
    public static final DeferredHolder<Item, Item> FROGGY_BED_ITEM = ITEMS.register("froggy_bed",
            () -> new net.minecraft.world.item.BlockItem(FROGGY_BED.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.Block> PLAYER_PAINTING = BLOCKS.register("player_painting",
            () -> new pl.fuzjajadrowa.froggy.block.PlayerPaintingBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(0.2F).sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion()));
    public static final DeferredHolder<Item, Item> PLAYER_PAINTING_ITEM = ITEMS.register("player_painting",
            () -> new net.minecraft.world.item.BlockItem(PLAYER_PAINTING.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<net.minecraft.world.level.block.entity.BlockEntityType<?>, net.minecraft.world.level.block.entity.BlockEntityType<pl.fuzjajadrowa.froggy.block.entity.PlayerPaintingBlockEntity>> PLAYER_PAINTING_BE = BLOCK_ENTITIES.register("player_painting",
            () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(pl.fuzjajadrowa.froggy.block.entity.PlayerPaintingBlockEntity::new, PLAYER_PAINTING.get()).build(null));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Froggy.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FROGGY_TAB = CREATIVE_MODE_TABS.register("froggy", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.froggy"))
                    .icon(() -> new ItemStack(COUGH_SYRUP.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(COUGH_SYRUP.get());
                        output.accept(SWEET_BOTTLE.get());
                        output.accept(FLY_IN_A_BOTTLE.get());
                        output.accept(SPEAKER_UPGRADE.get());
                        output.accept(MEGAPHONE_UPGRADE.get());
                        output.accept(AMPLIFIER_UPGRADE.get());
                        output.accept(SMALL_POUCH_UPGRADE.get());
                        output.accept(MEDIUM_POUCH_UPGRADE.get());
                        output.accept(LARGE_POUCH_UPGRADE.get());
                        output.accept(FROGGY_BED_ITEM.get());
                        output.accept(PLAYER_PAINTING_ITEM.get());
                    })
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<FroggyStalkerEntity>> FROGGY_STALKER = ENTITY_TYPES.register("froggy_stalker",
            () -> EntityType.Builder.of(FroggyStalkerEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.1f)
                    .build("froggy_stalker"));

    public static final DeferredHolder<EntityType<?>, EntityType<FroggyJumpscareEntity>> FROGGY_JUMPSCARE = ENTITY_TYPES.register("froggy_jumpscare",
            () -> EntityType.Builder.of(FroggyJumpscareEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.1f)
                    .build("froggy_jumpscare"));

    public static final DeferredHolder<EntityType<?>, EntityType<FroggySleepingEntity>> FROGGY_SLEEPING = ENTITY_TYPES.register("froggy_sleeping",
            () -> EntityType.Builder.of(FroggySleepingEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.1f)
                    .build("froggy_sleeping"));

    public static final DeferredHolder<EntityType<?>, EntityType<FroggyBoredEntity>> FROGGY_BORED = ENTITY_TYPES.register("froggy_bored",
            () -> EntityType.Builder.of(FroggyBoredEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.1f)
                    .build("froggy_bored"));

    public static final DeferredHolder<EntityType<?>, EntityType<pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity>> FROGGY_TAMED = ENTITY_TYPES.register("froggy_tamed",
            () -> EntityType.Builder.of(pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.1f)
                    .build("froggy_tamed"));
    public static final DeferredHolder<EntityType<?>, EntityType<pl.fuzjajadrowa.froggy.entity.FroggyTraderEntity>> FROGGY_TRADER = ENTITY_TYPES.register("froggy_trader",
            () -> EntityType.Builder.of(pl.fuzjajadrowa.froggy.entity.FroggyTraderEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.1f)
                    .build("froggy_trader"));

    public static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.MENU, Froggy.MOD_ID);

    public static final DeferredHolder<net.minecraft.world.inventory.MenuType<?>, net.minecraft.world.inventory.MenuType<pl.fuzjajadrowa.froggy.menu.FroggyTamedMenu>> FROGGY_TAMED_MENU = MENUS.register("froggy_tamed",
            () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((windowId, inv, data) -> new pl.fuzjajadrowa.froggy.menu.FroggyTamedMenu(windowId, inv, data.readInt())));

    public FroggyNeoForge(IEventBus modEventBus, net.neoforged.fml.ModContainer modContainer) {
        Froggy.init();

        SOUNDS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENUS.register(modEventBus);

        FroggySounds.SCREAM1 = SCREAM1;
        FroggySounds.SCREAM2 = SCREAM2;
        FroggySounds.SCREAM3 = SCREAM3;
        FroggySounds.SLEEPING = SLEEPING;
        FroggySounds.WALK = WALK;
        FroggySounds.FART = FART;
        FroggySounds.YIPPE = YIPPE;
        FroggySounds.MLEM = MLEM;
        FroggySounds.BUZZ = BUZZ;

        FroggyEntities.STALKER = FROGGY_STALKER;
        FroggyEntities.JUMPSCARE = FROGGY_JUMPSCARE;
        FroggyEntities.SLEEPING = FROGGY_SLEEPING;
        FroggyEntities.BORED = FROGGY_BORED;
        FroggyEntities.TAMED = FROGGY_TAMED;
        FroggyEntities.TRADER = FROGGY_TRADER;

        FroggyItems.COUGH_SYRUP = COUGH_SYRUP;
        FroggyItems.SWEET_BOTTLE = SWEET_BOTTLE;
        FroggyItems.FLY_IN_A_BOTTLE = FLY_IN_A_BOTTLE;
        FroggyItems.SPEAKER_UPGRADE = SPEAKER_UPGRADE;
        FroggyItems.MEGAPHONE_UPGRADE = MEGAPHONE_UPGRADE;
        FroggyItems.AMPLIFIER_UPGRADE = AMPLIFIER_UPGRADE;
        FroggyItems.SMALL_POUCH_UPGRADE = SMALL_POUCH_UPGRADE;
        FroggyItems.MEDIUM_POUCH_UPGRADE = MEDIUM_POUCH_UPGRADE;
        FroggyItems.LARGE_POUCH_UPGRADE = LARGE_POUCH_UPGRADE;
        FroggyItems.FROGGY_BED = FROGGY_BED_ITEM;
        FroggyItems.PLAYER_PAINTING = PLAYER_PAINTING_ITEM;
        pl.fuzjajadrowa.froggy.registry.FroggyBlocks.FROGGY_BED = FROGGY_BED;
        pl.fuzjajadrowa.froggy.registry.FroggyBlocks.PLAYER_PAINTING = PLAYER_PAINTING;
        pl.fuzjajadrowa.froggy.registry.FroggyBlockEntities.PLAYER_PAINTING = PLAYER_PAINTING_BE;

        pl.fuzjajadrowa.froggy.registry.FroggyMenus.FROGGY_TAMED = FROGGY_TAMED_MENU;
        pl.fuzjajadrowa.froggy.registry.FroggyMenus.openMenuDelegate = (player, froggy) -> {
            player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                (windowId, inv, p) -> new pl.fuzjajadrowa.froggy.menu.FroggyTamedMenu(windowId, inv, froggy),
                net.minecraft.network.chat.Component.translatable("entity.froggy.froggy_tamed")
            ), buf -> buf.writeInt(froggy.getId()));
        };

        if (net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.CLIENT) {
            pl.fuzjajadrowa.froggy.network.FroggyPacketSender.sender = (entityId, isCorrect) -> {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new pl.fuzjajadrowa.froggy.network.FroggyCoughSyrupPayload(entityId, isCorrect));
            };
            pl.fuzjajadrowa.froggy.network.FroggyPacketSender.stateSender = (entityId, newState) -> {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new pl.fuzjajadrowa.froggy.network.FroggyTamedStatePayload(entityId, newState));
            };
            ClientSetup.registerConfigScreen(modContainer);
            modEventBus.addListener(ClientSetup::registerScreens);
        }
    }

    private static class ClientSetup {
        public static void registerConfigScreen(net.neoforged.fml.ModContainer modContainer) {
            modContainer.registerExtensionPoint(
                net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                (container, parent) -> new pl.fuzjajadrowa.froggy.client.FroggyConfigScreen(parent)
            );
        }

        public static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
            event.register(FROGGY_TAMED_MENU.get(), pl.fuzjajadrowa.froggy.client.FroggyTamedScreen::new);
        }
    }

    @EventBusSubscriber(modid = Froggy.MOD_ID)
    public static class ModEvents {
        @SubscribeEvent
        public static void buildContents(net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) {
        }

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(FROGGY_STALKER.get(), FroggyStalkerEntity.createAttributes().build());
            event.put(FROGGY_JUMPSCARE.get(), FroggyJumpscareEntity.createAttributes().build());
            event.put(FROGGY_SLEEPING.get(), FroggySleepingEntity.createAttributes().build());
            event.put(FROGGY_BORED.get(), FroggyBoredEntity.createAttributes().build());
            event.put(FROGGY_TAMED.get(), pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity.createAttributes().build());
            event.put(FROGGY_TRADER.get(), pl.fuzjajadrowa.froggy.entity.FroggyTraderEntity.createAttributes().build());
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(FROGGY_STALKER.get(), FroggyRenderer::new);
            event.registerEntityRenderer(FROGGY_JUMPSCARE.get(), FroggyRenderer::new);
            event.registerEntityRenderer(FROGGY_SLEEPING.get(), FroggySleepingRenderer::new);
            event.registerEntityRenderer(FROGGY_BORED.get(), FroggyRenderer::new);
            event.registerEntityRenderer(FROGGY_TAMED.get(), FroggyRenderer::new);
            event.registerEntityRenderer(FROGGY_TRADER.get(), FroggyRenderer::new);
            event.registerBlockEntityRenderer(PLAYER_PAINTING_BE.get(), pl.fuzjajadrowa.froggy.client.PlayerPaintingBlockEntityRenderer::new);
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
            registrar.playToServer(
                pl.fuzjajadrowa.froggy.network.FroggyTamedStatePayload.TYPE,
                pl.fuzjajadrowa.froggy.network.FroggyTamedStatePayload.CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        net.minecraft.world.entity.Entity entity = context.player().level().getEntity(payload.entityId());
                        if (entity instanceof pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity froggy) {
                            if (froggy.getOwnerUUID().isPresent() && froggy.getOwnerUUID().get().equals(context.player().getUUID())) {
                                froggy.setTamedState(payload.newState());
                            }
                        }
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

        @SubscribeEvent
        public static void onRegisterCommands(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
            pl.fuzjajadrowa.froggy.command.FroggyCommands.register(event.getDispatcher());
        }
    }
}