package pl.fuzjajadrowa.froggy.entity;

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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import pl.fuzjajadrowa.froggy.sound.FroggySounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class BaseFroggyEntity extends PathfinderMob implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SCREAMING = SynchedEntityData.defineId(BaseFroggyEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> EFFECT_STATE = SynchedEntityData.defineId(BaseFroggyEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> EFFECT_TIMER = SynchedEntityData.defineId(BaseFroggyEntity.class, EntityDataSerializers.INT);

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
    }
//?} else {
/*    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SCREAMING, false);
        this.entityData.define(EFFECT_STATE, 0);
        this.entityData.define(EFFECT_TIMER, 0);
    }
*/
//?}

    public boolean isScreaming() {
        return this.entityData.get(SCREAMING);
    }

    public void setScreaming(boolean screaming) {
        this.entityData.set(SCREAMING, screaming);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        int state = this.entityData.get(EFFECT_STATE);
        if (state > 0) {
            int timer = this.entityData.get(EFFECT_TIMER);
            timer--;
            this.entityData.set(EFFECT_TIMER, timer);
            if (timer <= 0) {
                this.discard();
                return;
            }

            if (state == 1 || state == 2) {
                this.navigation.stop();
                this.setDeltaMovement(Vec3.ZERO);
            } else if (state == 3) {
                // Fleeing: run away from nearest player
                Player player = this.level().getNearestPlayer(this, 30.0);
                if (player != null) {
                    if (this.tickCount % 10 == 0 || this.navigation.isDone()) {
                        Vec3 diff = this.position().subtract(player.position());
                        Vec3 direction = new Vec3(diff.x, 0, diff.z);
                        if (direction.lengthSqr() < 0.01) {
                            direction = new Vec3(this.random.nextDouble() - 0.5, 0, this.random.nextDouble() - 0.5);
                        }
                        Vec3 target = this.position().add(direction.normalize().scale(16));
                        this.navigation.moveTo(target.x, target.y, target.z, 1.8);
                    }
                }
            }
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        int state = this.entityData.get(EFFECT_STATE);
        if (state == 1 || state == 2) {
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!(this instanceof FroggyStalkerEntity) && this.entityData.get(EFFECT_STATE) == 0) {
            // Check cough syrup interaction
            if (itemStack.is(pl.fuzjajadrowa.froggy.item.FroggyItems.COUGH_SYRUP.get())) {
                this.setScreaming(false);
                if (this.level().isClientSide()) {
                    this.openCoughSyrupScreen();
                } else {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            // Check food interaction
//? if >=1.21.1 {
            if (itemStack.has(net.minecraft.core.component.DataComponents.FOOD)) {
//?} else {
/*            if (itemStack.getItem().isEdible()) {
*/
//?}
                this.setScreaming(false);
                if (!this.level().isClientSide()) {
                    this.feed(player, hand);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
        }

        return super.mobInteract(player, hand);
    }

    public void feed(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        this.entityData.set(EFFECT_STATE, 1);
        this.entityData.set(EFFECT_TIMER, 80); // 4 seconds
        this.setScreaming(false);
        this.navigation.stop();
        this.setDeltaMovement(Vec3.ZERO);

        // Play fart sound
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), FroggySounds.FART.get(), this.getSoundSource(), 1.0F, 1.0F);

        // Spawn fart particles on server
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, this.getX(), this.getY() + 0.2, this.getZ(), 5, 0.2, 0.1, 0.2, 0.02);
            serverLevel.sendParticles(new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(0.4f, 0.25f, 0.1f), 1.5f), this.getX(), this.getY() + 0.2, this.getZ(), 15, 0.2, 0.2, 0.2, 0.05);
        }
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
        if (this.entityData.get(EFFECT_STATE) != 0) {
            return; // Already has an effect
        }

        this.setScreaming(false);
        if (isCorrect) {
            this.entityData.set(EFFECT_STATE, 2);
            this.entityData.set(EFFECT_TIMER, 100); // 5 seconds
            this.navigation.stop();
            this.setDeltaMovement(Vec3.ZERO);

            // Play yippe.ogg sound
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), FroggySounds.YIPPE.get(), this.getSoundSource(), 1.0F, 1.0F);
        } else {
            this.entityData.set(EFFECT_STATE, 3);
            this.entityData.set(EFFECT_TIMER, 100); // 5 seconds
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, event -> {
            int state = this.entityData.get(EFFECT_STATE);
            if (this.isScreaming()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("scream"));
            }

            double speedSq = this.getDeltaMovement().horizontalDistanceSqr();
            if (speedSq > 0.01) {
                if (this.isSprinting() || speedSq > 0.08 || state == 3) {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
                } else {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
                }
            }

            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }
}