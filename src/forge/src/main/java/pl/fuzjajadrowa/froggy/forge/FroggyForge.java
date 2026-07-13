package pl.fuzjajadrowa.froggy.forge;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
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
import pl.fuzjajadrowa.froggy.registry.FroggyEntities;
import pl.fuzjajadrowa.froggy.entity.FroggyJumpscareEntity;
import pl.fuzjajadrowa.froggy.entity.FroggySleepingEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyStalkerEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyBoredEntity;
import pl.fuzjajadrowa.froggy.registry.FroggySounds;
import pl.fuzjajadrowa.froggy.registry.FroggyItems;
import pl.fuzjajadrowa.froggy.spawner.FroggySpawner;
import pl.fuzjajadrowa.froggy.network.FroggyPackets;

@Mod(Froggy.MOD_ID)
public final class FroggyForge {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Froggy.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Froggy.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Froggy.MOD_ID);
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Froggy.MOD_ID);

    public static final RegistryObject<SoundEvent> SCREAM1 = SOUNDS.register("scream1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "scream1")));
    public static final RegistryObject<SoundEvent> SCREAM2 = SOUNDS.register("scream2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "scream2")));
    public static final RegistryObject<SoundEvent> SCREAM3 = SOUNDS.register("scream3", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "scream3")));
    public static final RegistryObject<SoundEvent> SLEEPING = SOUNDS.register("sleeping", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "sleeping")));
    public static final RegistryObject<SoundEvent> WALK = SOUNDS.register("walk", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "walk")));
    public static final RegistryObject<SoundEvent> FART = SOUNDS.register("fart", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "fart")));
    public static final RegistryObject<SoundEvent> YIPPE = SOUNDS.register("yippe", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "yippe")));
    public static final RegistryObject<SoundEvent> MLEM = SOUNDS.register("mlem", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "mlem")));
    public static final RegistryObject<SoundEvent> BUZZ = SOUNDS.register("buzz", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Froggy.MOD_ID, "buzz")));

    public static final RegistryObject<Item> COUGH_SYRUP = ITEMS.register("cough_syrup",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> SWEET_BOTTLE = ITEMS.register("sweet_bottle",
            () -> new pl.fuzjajadrowa.froggy.item.SweetBottleItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> FLY_IN_A_BOTTLE = ITEMS.register("fly_in_a_bottle",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> SPEAKER_UPGRADE = ITEMS.register("speaker_upgrade",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MEGAPHONE_UPGRADE = ITEMS.register("megaphone_upgrade",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AMPLIFIER_UPGRADE = ITEMS.register("amplifier_upgrade",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SMALL_POUCH_UPGRADE = ITEMS.register("small_pouch_upgrade",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MEDIUM_POUCH_UPGRADE = ITEMS.register("medium_pouch_upgrade",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LARGE_POUCH_UPGRADE = ITEMS.register("large_pouch_upgrade",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<net.minecraft.world.level.block.Block> FROGGY_BED = BLOCKS.register("froggy_bed",
            () -> new pl.fuzjajadrowa.froggy.block.FroggyBedBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(0.2F).sound(net.minecraft.world.level.block.SoundType.MOSS).noOcclusion()));
    public static final RegistryObject<Item> FROGGY_BED_ITEM = ITEMS.register("froggy_bed",
            () -> new net.minecraft.world.item.BlockItem(FROGGY_BED.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Froggy.MOD_ID);

    public static final RegistryObject<CreativeModeTab> FROGGY_TAB = CREATIVE_MODE_TABS.register("froggy", () ->
            CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.froggy"))
                    .icon(() -> new net.minecraft.world.item.ItemStack(COUGH_SYRUP.get()))
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
                    })
                    .build());

    public static final RegistryObject<EntityType<FroggyStalkerEntity>> FROGGY_STALKER = ENTITY_TYPES.register("froggy_stalker",
            () -> EntityType.Builder.of(FroggyStalkerEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.1f)
                    .build("froggy_stalker"));

    public static final RegistryObject<EntityType<FroggyEntities.FroggyJumpscareEntity>> FROGGY_JUMPSCARE = ENTITY_TYPES.register("froggy_jumpscare",
            () -> EntityType.Builder.of(FroggyEntities.FroggyJumpscareEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.1f)
                    .build("froggy_jumpscare"));

    public static final RegistryObject<EntityType<FroggySleepingEntity>> FROGGY_SLEEPING = ENTITY_TYPES.register("froggy_sleeping",
            () -> EntityType.Builder.of(FroggySleepingEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.1f)
                    .build("froggy_sleeping"));

    public static final RegistryObject<EntityType<FroggyBoredEntity>> FROGGY_BORED = ENTITY_TYPES.register("froggy_bored",
            () -> EntityType.Builder.of(FroggyBoredEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.1f)
                    .build("froggy_bored"));

    public static final RegistryObject<EntityType<pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity>> FROGGY_TAMED = ENTITY_TYPES.register("froggy_tamed",
            () -> EntityType.Builder.of(pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.1f)
                    .build("froggy_tamed"));
    public static final RegistryObject<EntityType<pl.fuzjajadrowa.froggy.entity.FroggyTraderEntity>> FROGGY_TRADER = ENTITY_TYPES.register("froggy_trader",
            () -> EntityType.Builder.of(pl.fuzjajadrowa.froggy.entity.FroggyTraderEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.1f)
                    .build("froggy_trader"));

    public static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Froggy.MOD_ID);

    public static final RegistryObject<net.minecraft.world.inventory.MenuType<pl.fuzjajadrowa.froggy.menu.FroggyTamedMenu>> FROGGY_TAMED_MENU = MENUS.register("froggy_tamed",
            () -> net.minecraftforge.common.extensions.IForgeMenuType.create((windowId, inv, data) -> new pl.fuzjajadrowa.froggy.menu.FroggyTamedMenu(windowId, inv, data.readInt())));

    public FroggyForge() {
        Froggy.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        SOUNDS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
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
        pl.fuzjajadrowa.froggy.registry.FroggyBlocks.FROGGY_BED = FROGGY_BED;

        pl.fuzjajadrowa.froggy.registry.FroggyMenus.FROGGY_TAMED = FROGGY_TAMED_MENU;
        pl.fuzjajadrowa.froggy.registry.FroggyMenus.openMenuDelegate = (player, froggy) -> {
            net.minecraftforge.network.NetworkHooks.openScreen(player, new net.minecraft.world.SimpleMenuProvider(
                (windowId, inv, p) -> new pl.fuzjajadrowa.froggy.menu.FroggyTamedMenu(windowId, inv, froggy),
                net.minecraft.network.chat.Component.translatable("entity.froggy.froggy_tamed")
            ), buf -> buf.writeInt(froggy.getId()));
        };

        FroggyPackets.register();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            pl.fuzjajadrowa.froggy.network.FroggyPacketSender.sender = (entityId, isCorrect) -> {
                FroggyPackets.INSTANCE.sendToServer(new pl.fuzjajadrowa.froggy.network.FroggyCoughSyrupPacket(entityId, isCorrect));
            };
            pl.fuzjajadrowa.froggy.network.FroggyPacketSender.stateSender = (entityId, newState) -> {
                FroggyPackets.INSTANCE.sendToServer(new pl.fuzjajadrowa.froggy.network.FroggyTamedStatePacket(entityId, newState));
            };
        }

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(GameEvents.class);
    }

    @Mod.EventBusSubscriber(modid = Froggy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        }

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(FROGGY_STALKER.get(), FroggyStalkerEntity.createAttributes().build());
            event.put(FROGGY_JUMPSCARE.get(), FroggyEntities.FroggyJumpscareEntity.createAttributes().build());
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
        }

        @SubscribeEvent
        public static void onClientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                net.minecraft.client.gui.screens.MenuScreens.register(FROGGY_TAMED_MENU.get(), pl.fuzjajadrowa.froggy.client.FroggyTamedScreen::new);
            });
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
