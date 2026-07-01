package pl.fuzjajadrowa.froggy.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import pl.fuzjajadrowa.froggy.sound.FroggySounds;

public class FroggyJumpscareEntity extends BaseFroggyEntity {
    private static final int STATE_APPROACHING = 0;
    private static final int STATE_SCREAMING = 1;
    private static final int STATE_FLEEING = 2;

    private int state = STATE_APPROACHING;
    private int approachTimer = 0;
    private int screamTimer = 0;
    private int fleeTimer = 0;
    private Vec3 fleeTarget = null;

    public FroggyJumpscareEntity(EntityType<? extends FroggyJumpscareEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        Player player = this.level().getNearestPlayer(this, 30.0);
        if (player == null || player.isSpectator()) {
            if (this.tickCount > 60) {
                this.discard();
            }
            return;
        }

        switch (this.state) {
            case STATE_APPROACHING:
                this.navigation.moveTo(player, 0.5);
                this.approachTimer++;
                
                double distSq = this.distanceToSqr(player);
                if (distSq <= 6.25 || this.approachTimer > 160) {
                    triggerScream(player);
                }
                break;

            case STATE_SCREAMING:
                this.navigation.stop();
                this.setDeltaMovement(Vec3.ZERO);
                this.lookAt(player, 180.0F, 180.0F);
                this.setYRot(this.yHeadRot);

                this.screamTimer--;
                if (this.screamTimer <= 0) {
                    this.setScreaming(false);
                    startFleeing(player);
                }
                break;

            case STATE_FLEEING:
                this.fleeTimer--;
                if (this.fleeTimer <= 0) {
                    this.discard();
                    return;
                }

                if (this.tickCount % 10 == 0 || this.navigation.isDone()) {
                    Vec3 diff = this.position().subtract(player.position());
                    Vec3 direction = new Vec3(diff.x, 0, diff.z);
                    if (direction.lengthSqr() < 0.01) {
                        direction = new Vec3(this.random.nextDouble() - 0.5, 0, this.random.nextDouble() - 0.5);
                    }
                    this.fleeTarget = this.position().add(direction.normalize().scale(16));
                    this.navigation.moveTo(this.fleeTarget.x, this.fleeTarget.y, this.fleeTarget.z, 1.8);
                }
                break;
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.state == STATE_SCREAMING) {
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            super.travel(travelVector);
        }
    }

    private void triggerScream(Player player) {
        this.state = STATE_SCREAMING;
        this.screamTimer = 30;
        this.setScreaming(true);
        this.navigation.stop();
        this.setDeltaMovement(Vec3.ZERO);
        
        int r = this.random.nextInt(3);
        SoundEvent screamSound = r == 0 ? FroggySounds.SCREAM1.get() : (r == 1 ? FroggySounds.SCREAM2.get() : FroggySounds.SCREAM3.get());
        this.playSound(screamSound, 1.0F, 1.0F);
    }

    private void startFleeing(Player player) {
        this.state = STATE_FLEEING;
        this.fleeTimer = 100;

        Vec3 diff = this.position().subtract(player.position());
        Vec3 direction = new Vec3(diff.x, 0, diff.z);
        if (direction.lengthSqr() < 0.01) {
            direction = new Vec3(this.random.nextDouble() - 0.5, 0, this.random.nextDouble() - 0.5);
        }
        this.fleeTarget = this.position().add(direction.normalize().scale(16));
        this.navigation.moveTo(this.fleeTarget.x, this.fleeTarget.y, this.fleeTarget.z, 1.8);
    }
}