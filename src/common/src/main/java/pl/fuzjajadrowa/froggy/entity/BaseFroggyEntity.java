package pl.fuzjajadrowa.froggy.entity;

import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import pl.fuzjajadrowa.froggy.registry.FroggyItems;
import pl.fuzjajadrowa.froggy.registry.FroggySounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class BaseFroggyEntity extends Animal implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SCREAMING = SynchedEntityData.defineId(BaseFroggyEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> EFFECT_STATE = SynchedEntityData.defineId(BaseFroggyEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> EFFECT_TIMER = SynchedEntityData.defineId(BaseFroggyEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<ItemStack> EATEN_ITEM = SynchedEntityData.defineId(BaseFroggyEntity.class, EntityDataSerializers.ITEM_STACK);

    public static final int STATE_NONE = 0;
    public static final int STATE_FED = 1;
    public static final int STATE_CORRECT_CHOICE = 2;
    public static final int STATE_INCORRECT_CHOICE = 3;
    public static final int STATE_EATING = 4;
    public static final int STATE_WAITING_FOR_CHOICE = 5;
    public static final int STATE_EATING_FOOD = 6;

    public static int lastInteractedEntityId = -1;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected BaseFroggyEntity(EntityType<? extends BaseFroggyEntity> entityType, Level level) {
        super(entityType, level);
        this.setInvulnerable(true);
        this.noPhysics = false;
    }

//? if >=1.21.1 {
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SCREAMING, false);
        builder.define(EFFECT_STATE, 0);
        builder.define(EFFECT_TIMER, 0);
        builder.define(EATEN_ITEM, ItemStack.EMPTY);
    }
//?} else {
/*    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SCREAMING, false);
        this.entityData.define(EFFECT_STATE, 0);
        this.entityData.define(EFFECT_TIMER, 0);
        this.entityData.define(EATEN_ITEM, ItemStack.EMPTY);
    }
*/
//?}

    public boolean isScreaming() {
        return this.entityData.get(SCREAMING);
    }

    public void setScreaming(boolean screaming) {
        this.entityData.set(SCREAMING, screaming);
    }

    public int getEffectState() {
        return this.entityData.get(EFFECT_STATE);
    }

    public ItemStack getEatenItem() {
        return this.entityData.get(EATEN_ITEM);
    }

    public void stopSoundsAndTTS() {
        if (!this.level().isClientSide()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                for (ServerPlayer serverPlayer : serverLevel.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(32.0))) {
                    serverPlayer.connection.send(new ClientboundStopSoundPacket(FroggySounds.SCREAM1.get().getLocation(), SoundSource.NEUTRAL));
                    serverPlayer.connection.send(new ClientboundStopSoundPacket(FroggySounds.SCREAM2.get().getLocation(), SoundSource.NEUTRAL));
                    serverPlayer.connection.send(new ClientboundStopSoundPacket(FroggySounds.SCREAM3.get().getLocation(), SoundSource.NEUTRAL));
                    serverPlayer.connection.send(new ClientboundStopSoundPacket(FroggySounds.SLEEPING.get().getLocation(), SoundSource.NEUTRAL));
                }
            }
        } else {
            try {
                Class<?> narratorClass = Class.forName("com.mojang.text2speech.Narrator");
                Object narrator = narratorClass.getMethod("getNarrator").invoke(null);
                narratorClass.getMethod("clear").invoke(narrator);
            } catch (Throwable t) {
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            if (this.entityData.get(EFFECT_STATE) == STATE_WAITING_FOR_CHOICE && lastInteractedEntityId == this.getId()) {
                lastInteractedEntityId = -1;
                this.openCoughSyrupScreen();
            }
            return;
        }

        int state = this.entityData.get(EFFECT_STATE);
        if (state > 0) {
            if (state != STATE_WAITING_FOR_CHOICE) {
                int timer = this.entityData.get(EFFECT_TIMER);
                timer--;
                this.entityData.set(EFFECT_TIMER, timer);
                if ((state == STATE_EATING || state == STATE_EATING_FOOD) && timer == 44) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), FroggySounds.MLEM.get(), this.getSoundSource(), 1.0F, 1.0F);
                }
                if (timer <= 0) {
                    if (state == STATE_EATING) {
                        this.entityData.set(EFFECT_STATE, STATE_WAITING_FOR_CHOICE);
                    } else if (state == STATE_EATING_FOOD) {
                        this.entityData.set(EFFECT_STATE, STATE_FED);
                        this.entityData.set(EFFECT_TIMER, 40);

                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), FroggySounds.FART.get(), this.getSoundSource(), 1.0F, 1.0F);

                        if (this.level() instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, this.getX(), this.getY() + 0.2, this.getZ(), 5, 0.2, 0.1, 0.2, 0.02);
                            serverLevel.sendParticles(new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(0.4f, 0.25f, 0.1f), 1.5f), this.getX(), this.getY() + 0.2, this.getZ(), 15, 0.2, 0.2, 0.2, 0.05);
                        }
                    } else {
                        if (state == STATE_CORRECT_CHOICE) {
                            net.minecraft.core.BlockPos pos = this.blockPosition();
                            this.level().setBlock(pos, net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(), 3);
                            net.minecraft.world.level.block.entity.BlockEntity blockEntity = this.level().getBlockEntity(pos);
                            if (blockEntity instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
                                chest.setItem(13, new ItemStack(net.minecraft.world.item.Items.DIAMOND, 1));
                            }
                        }
                        this.discard();
                        return;
                    }
                }
            }

            if (state == STATE_FED || state == STATE_CORRECT_CHOICE || state == STATE_EATING || state == STATE_EATING_FOOD || state == STATE_WAITING_FOR_CHOICE) {
                this.navigation.stop();
                this.setDeltaMovement(Vec3.ZERO);
                if (state == STATE_EATING || state == STATE_EATING_FOOD) {
                    this.setScreaming(false);
                }
            } else if (state == STATE_INCORRECT_CHOICE) {
                Player player = this.level().getNearestPlayer(this, 30.0);
                if (player != null) {
                    if (this.tickCount % 10 == 0 || this.navigation.isDone()) {
                        Vec3 diff = this.position().subtract(player.position());
                        Vec3 direction = new Vec3(diff.x, 0, diff.z);
                        if (direction.lengthSqr() < 0.01) {
                            direction = new Vec3(this.random.nextDouble() - 0.5, 0, this.random.nextDouble() - 0.5);
                        }
                        Vec3 target = this.position().add(direction.normalize().scale(16));
                        this.navigation.moveTo(target.x, target.y, target.z, 1.4); // reduced to 1.4
                    }
                }
            }
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        int state = this.entityData.get(EFFECT_STATE);
        if (state == STATE_FED || state == STATE_CORRECT_CHOICE || state == STATE_EATING || state == STATE_EATING_FOOD || state == STATE_WAITING_FOR_CHOICE) {
            super.travel(Vec3.ZERO);
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(FroggyItems.FLY_IN_A_BOTTLE.get())) {
            if (!(this instanceof FroggyTamedEntity)) {
                if (!this.level().isClientSide()) {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    if (this.random.nextFloat() < 0.50F) {
                        FroggyTamedEntity tamed = new FroggyTamedEntity(pl.fuzjajadrowa.froggy.registry.FroggyEntities.TAMED.get(), this.level());
                        tamed.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                        tamed.setYBodyRot(this.yBodyRot);
                        tamed.setYHeadRot(this.yHeadRot);
                        tamed.setDeltaMovement(this.getDeltaMovement());
                        tamed.setOwnerUUID(player.getUUID());
                        
                        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART, this.getX(), this.getY() + 0.5, this.getZ(), 8, 0.2, 0.2, 0.2, 0.02);
                            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                                net.minecraft.advancements.CriteriaTriggers.TAME_ANIMAL.trigger(serverPlayer, tamed);
                            }
                        }
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), 1.0F, 1.0F);
                        
                        this.level().addFreshEntity(tamed);
                        this.discard();
                    } else {
                        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 8, 0.2, 0.2, 0.2, 0.02);
                        }
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
        }

        if (this.entityData.get(EFFECT_STATE) > 0) {
            return InteractionResult.PASS;
        }

        if (this.entityData.get(EFFECT_STATE) == 0) {
            if (itemStack.is(FroggyItems.COUGH_SYRUP.get())) {
                this.setScreaming(false);
                this.stopSoundsAndTTS();
                ItemStack syrupStack = new ItemStack(FroggyItems.COUGH_SYRUP.get());
                if (this.level().isClientSide()) {
                    lastInteractedEntityId = this.getId();
                    this.entityData.set(EFFECT_STATE, STATE_EATING);
                    this.entityData.set(EATEN_ITEM, syrupStack);
                } else {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    this.entityData.set(EFFECT_STATE, STATE_EATING);
                    this.entityData.set(EFFECT_TIMER, 60);
                    this.entityData.set(EATEN_ITEM, syrupStack);
                    this.navigation.stop();
                    this.setDeltaMovement(Vec3.ZERO);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            if (!(this instanceof FroggyStalkerEntity)) {
//? if >=1.21.1 {
                if (itemStack.has(net.minecraft.core.component.DataComponents.FOOD)) {
//?} else {
/*              if (itemStack.getItem().isEdible()) {
*/
//?}
                    this.setScreaming(false);
                    this.stopSoundsAndTTS();
                    ItemStack eaten = itemStack.copy();
                    eaten.setCount(1);
                    if (this.level().isClientSide()) {
                        this.entityData.set(EFFECT_STATE, STATE_EATING_FOOD);
                        this.entityData.set(EATEN_ITEM, eaten);
                    } else {
                        this.feed(player, hand, eaten);
                    }
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }
            }
        }

        return super.mobInteract(player, hand);
    }

    public void feed(Player player, InteractionHand hand, ItemStack eatenStack) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        this.entityData.set(EFFECT_STATE, STATE_EATING_FOOD);
        this.entityData.set(EFFECT_TIMER, 60);
        this.entityData.set(EATEN_ITEM, eatenStack);
        this.setScreaming(false);
        this.navigation.stop();
        this.setDeltaMovement(Vec3.ZERO);
    }

    private void openCoughSyrupScreen() {
        try {
            Class<?> screenClass = Class.forName("pl.fuzjajadrowa.froggy.client.CoughSyrupScreen");
            Object screen = screenClass.getConstructor(int.class).newInstance(this.getId());
            net.minecraft.client.Minecraft.getInstance().setScreen((net.minecraft.client.gui.screens.Screen) screen);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void handleCoughSyrupChoice(Player player, int entityId, boolean isCorrect) {
        Entity entity = player.level().getEntity(entityId);
        if (entity instanceof BaseFroggyEntity froggy) {
            froggy.onCoughSyrupResult(player, isCorrect);
        }
    }

    public void onCoughSyrupResult(Player player, boolean isCorrect) {
        if (this.entityData.get(EFFECT_STATE) != STATE_WAITING_FOR_CHOICE) {
            return;
        }

        this.setScreaming(false);
        if (isCorrect) {
            this.entityData.set(EFFECT_STATE, STATE_CORRECT_CHOICE);
            this.entityData.set(EFFECT_TIMER, 100);
            this.navigation.stop();
            this.setDeltaMovement(Vec3.ZERO);

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), FroggySounds.YIPPE.get(), this.getSoundSource(), 1.0F, 1.0F);
        } else {
            this.entityData.set(EFFECT_STATE, STATE_INCORRECT_CHOICE);
            this.entityData.set(EFFECT_TIMER, 100);
            net.minecraft.world.entity.ai.attributes.AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                speedAttr.setBaseValue(0.25);
            }
        }
    }

    protected boolean isInvulnerableByDefault() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (!isInvulnerableByDefault()) {
            //? if >=1.21.1 {
            return super.isInvulnerableTo(source);
            //?} else {
            /* return super.isInvulnerableTo(source); */
            //?}
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!isInvulnerableByDefault()) {
            return super.hurt(source, amount);
        }
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public net.minecraft.world.entity.AgeableMob getBreedOffspring(ServerLevel level, net.minecraft.world.entity.AgeableMob parent) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, event -> {
            int state = this.entityData.get(EFFECT_STATE);
            if (state == STATE_EATING || state == STATE_EATING_FOOD) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("eat"));
            }
            if (this.isScreaming()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("scream"));
            }

            double speedSq = this.getDeltaMovement().horizontalDistanceSqr();
            float walkSpeed = this.walkAnimation.speed();
            boolean isMoving = walkSpeed > 0.01F || speedSq > 0.01;
            if (isMoving) {
                if (this.isSprinting() || speedSq > 0.08 || walkSpeed > 0.35F || state == STATE_INCORRECT_CHOICE) {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
                } else {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
                }
            }

            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }
}