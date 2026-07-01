package pl.fuzjajadrowa.froggy.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class BaseFroggyEntity extends PathfinderMob implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SCREAMING = SynchedEntityData.defineId(BaseFroggyEntity.class, EntityDataSerializers.BOOLEAN);
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected BaseFroggyEntity(EntityType<? extends BaseFroggyEntity> entityType, Level level) {
        super(entityType, level);
        this.setInvulnerable(true);
        this.noPhysics = false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SCREAMING, false);
    }

    public boolean isScreaming() {
        return this.entityData.get(SCREAMING);
    }

    public void setScreaming(boolean screaming) {
        this.entityData.set(SCREAMING, screaming);
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
            if (this.isScreaming()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("scream"));
            }

            double speedSq = this.getDeltaMovement().horizontalDistanceSqr();
            if (speedSq > 0.01) {
                if (this.isSprinting() || speedSq > 0.08) {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
                } else {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
                }
            }
            
            return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }
}