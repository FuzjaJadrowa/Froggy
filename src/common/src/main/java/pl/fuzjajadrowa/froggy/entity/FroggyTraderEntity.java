package pl.fuzjajadrowa.froggy.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundTag;

public class FroggyTraderEntity extends BaseFroggyEntity implements Merchant {
    private Player tradingPlayer;
    private MerchantOffers offers;
    private int xp;
    private int level = 1;

    public FroggyTraderEntity(EntityType<? extends FroggyTraderEntity> entityType, Level level) {
        super(entityType, level);
        this.setInvulnerable(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected boolean isInvulnerableByDefault() {
        return false;
    }

    @Override
    public void setTradingPlayer(Player player) {
        this.tradingPlayer = player;
    }

    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            for (int i = 1; i <= this.level; i++) {
                this.populateTradeOffersForLevel(i);
            }
        }
        return this.offers;
    }

    @Override
    public void overrideOffers(MerchantOffers offers) {
        this.offers = offers;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        if (this.level() instanceof net.minecraft.server.level.ServerLevel) {
            this.xp += offer.getXp();
            this.checkForLevelUp();
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
    }

    @Override
    public net.minecraft.sounds.SoundEvent getNotifyTradeSound() {
        return pl.fuzjajadrowa.froggy.registry.FroggySounds.YIPPE.get();
    }

    @Override
    public boolean showProgressBar() {
        return true;
    }

    @Override
    public int getVillagerXp() {
        return this.xp;
    }

    @Override
    public void overrideXp(int xp) {
        this.xp = xp;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide();
    }

    //? if >=1.21.1 {
    @Override
    public boolean canRestock() {
        return true;
    }
    //?}

    private void checkForLevelUp() {
        int nextLevelThreshold = getXpNeededForNextLevel(this.level);
        if (this.xp >= nextLevelThreshold && this.level < 5) {
            this.level++;
            this.populateTradeOffersForLevel(this.level);

            if (this.tradingPlayer != null && !this.level().isClientSide()) {
                //? if >=1.21.1 {
                int containerId = this.tradingPlayer.containerMenu.containerId;
                this.tradingPlayer.sendMerchantOffers(containerId, this.getOffers(), this.level, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
                //?} else {
                /* int containerId = this.tradingPlayer.containerMenu.containerId;
                this.tradingPlayer.sendMerchantOffers(containerId, this.getOffers(), this.level, this.getVillagerXp(), this.showProgressBar(), false); */
                //?}
            }
        }
    }

    private static int getXpNeededForNextLevel(int currentLevel) {
        switch (currentLevel) {
            case 1: return 10;
            case 2: return 70;
            case 3: return 150;
            case 4: return 250;
            default: return Integer.MAX_VALUE;
        }
    }

    private void addOffer(net.minecraft.world.item.Item buyItem, int buyCount, net.minecraft.world.item.Item sellItem, int sellCount, int maxUses, int xp, float priceMultiplier) {
        //? if >=1.21.1 {
        this.offers.add(new MerchantOffer(
                new net.minecraft.world.item.trading.ItemCost(buyItem, buyCount),
                new ItemStack(sellItem, sellCount),
                maxUses, xp, priceMultiplier
        ));
        //?} else {
        /* this.offers.add(new MerchantOffer(
                new ItemStack(buyItem, buyCount),
                new ItemStack(sellItem, sellCount),
                maxUses, xp, priceMultiplier
        )); */
        //?}
    }

    private void populateTradeOffersForLevel(int lvl) {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
        }
        switch (lvl) {
            case 1:
                this.addOffer(Items.EMERALD, 1, Items.SWEET_BERRIES, 8, 16, 2, 0.05F);
                this.addOffer(Items.EMERALD, 1, Items.APPLE, 4, 16, 2, 0.05F);
                break;
            case 2:
                this.addOffer(Items.EMERALD, 2, Items.COOKIE, 6, 12, 5, 0.05F);
                this.addOffer(Items.EMERALD, 1, Items.MELON_SLICE, 8, 12, 5, 0.05F);
                break;
            case 3:
                this.addOffer(Items.EMERALD, 3, Items.BREAD, 6, 12, 10, 0.05F);
                this.addOffer(Items.EMERALD, 3, Items.PUMPKIN_PIE, 2, 12, 10, 0.05F);
                break;
            case 4:
                this.addOffer(Items.EMERALD, 4, Items.GOLDEN_CARROT, 3, 12, 15, 0.05F);
                this.addOffer(Items.EMERALD, 5, Items.CAKE, 1, 8, 15, 0.05F);
                break;
            case 5:
                this.addOffer(Items.EMERALD, 8, Items.GOLDEN_APPLE, 1, 12, 30, 0.05F);
                break;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        //? if >=1.21.1 {
        if (itemStack.has(net.minecraft.core.component.DataComponents.FOOD)) {
        //?} else {
        /* if (itemStack.getItem().isEdible()) { */
        //?}
            if (this.foodCooldown > 0) {
                return InteractionResult.PASS;
            }
            this.setScreaming(false);
            this.stopSoundsAndTTS();
            ItemStack eaten = itemStack.copy();
            eaten.setCount(1);
            this.foodCooldown = 3600; // 3 minutes
            if (this.level().isClientSide()) {
                this.entityData.set(EFFECT_STATE, STATE_EATING_FOOD);
                this.entityData.set(EATEN_ITEM, eaten);
            } else {
                this.feed(player, hand, eaten);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        if (!this.level().isClientSide()) {
            if (this.getOffers().isEmpty()) {
                for (int i = 1; i <= this.level; i++) {
                    this.populateTradeOffersForLevel(i);
                }
            }
            this.setTradingPlayer(player);
            
            net.minecraft.network.chat.Component displayName = this.getDisplayName();
            java.util.OptionalInt optionalint = player.openMenu(new net.minecraft.world.SimpleMenuProvider((containerId, playerInventory, p) -> {
                return new net.minecraft.world.inventory.MerchantMenu(containerId, playerInventory, this);
            }, displayName));
            if (optionalint.isPresent()) {
                MerchantOffers merchantoffers = this.getOffers();
                if (!merchantoffers.isEmpty()) {
                    //? if >=1.21.1 {
                    player.sendMerchantOffers(optionalint.getAsInt(), merchantoffers, this.level, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
                    //?} else {
                    /* player.sendMerchantOffers(optionalint.getAsInt(), merchantoffers, this.level, this.getVillagerXp(), this.showProgressBar(), false); */
                    //?}
                }
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Xp", this.xp);
        tag.putInt("TraderLevel", this.level);
        if (this.offers != null) {
            //? if >=1.21.1 {
            net.minecraft.world.item.trading.MerchantOffers.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, this.offers)
                .result().ifPresent(tagVal -> tag.put("Offers", tagVal));
            //?} else {
            /* tag.put("Offers", this.offers.createTag()); */
            //?}
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.xp = tag.getInt("Xp");
        if (tag.contains("TraderLevel")) {
            this.level = tag.getInt("TraderLevel");
        } else {
            this.level = 1;
        }
        if (tag.contains("Offers")) {
            //? if >=1.21.1 {
            this.offers = net.minecraft.world.item.trading.MerchantOffers.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.get("Offers"))
                .result().orElse(new net.minecraft.world.item.trading.MerchantOffers());
            //?} else {
            /* this.offers = new net.minecraft.world.item.trading.MerchantOffers(tag.getCompound("Offers")); */
            //?}
        }
    }
}