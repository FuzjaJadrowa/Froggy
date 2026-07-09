package pl.fuzjajadrowa.froggy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import pl.fuzjajadrowa.froggy.registry.FroggySounds;

public class FroggyStalkerEntity extends BaseFroggyEntity {
    private static final int STATE_STALKING = 0;
    private static final int STATE_TELEPORTING = 1;
    private static final int STATE_CHARGING = 2;

    private int state = STATE_STALKING;
    private int stateTimer = 0;
    private int targetPlayerId = -1;
    private int noLookTimer = 0;

    public FroggyStalkerEntity(EntityType<? extends FroggyStalkerEntity> entityType, Level level) {
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

        if (this.entityData.get(EFFECT_STATE) > 0) {
            return;
        }

        switch (this.state) {
            case STATE_STALKING:
                this.navigation.stop();
                Player player = this.level().getNearestPlayer(this, 35.0);
                if (player != null && !player.isSpectator()) {
                    this.getLookControl().setLookAt(player, 10.0F, 10.0F);
                    this.setYRot(this.yHeadRot);

                    if (isPlayerLookingAtMe(player)) {
                        this.noLookTimer = 0;
                        this.state = STATE_TELEPORTING;
                        this.stateTimer = 10;
                        this.setInvisible(true);
                        this.targetPlayerId = player.getId();
                    } else {
                        this.noLookTimer++;
                        if (this.noLookTimer >= 1200) {
                            this.discard();
                            return;
                        }
                    }
                } else {
                    this.noLookTimer++;
                    if (this.noLookTimer >= 1200) {
                        this.discard();
                        return;
                    }
                }
                break;

            case STATE_TELEPORTING:
                this.stateTimer--;
                if (this.stateTimer <= 0) {
                    Player target = getTargetPlayer();
                    if (target != null) {
                        teleportInFrontOfPlayer(target);
                        this.setInvisible(false);
                        this.state = STATE_CHARGING;
                        this.stateTimer = 120;
                    } else {
                        this.discard();
                    }
                }
                break;

            case STATE_CHARGING:
                Player chargeTarget = getTargetPlayer();
                if (chargeTarget == null || !chargeTarget.isAlive() || chargeTarget.isSpectator()) {
                    this.discard();
                    return;
                }

                this.stateTimer--;
                if (this.stateTimer <= 0) {
                    this.discard();
                    return;
                }

                this.navigation.moveTo(chargeTarget, 2.0);

                if (this.tickCount % 15 == 0) {
                    this.playSound(FroggySounds.WALK.get(), 1.0F, 1.0F);
                }

                if (this.getBoundingBox().inflate(0.3).intersects(chargeTarget.getBoundingBox())) {
                    chargeTarget.hurt(chargeTarget.damageSources().mobAttack(this), 1.0F);
                    this.discard();
                }
                break;
        }
    }

    private Player getTargetPlayer() {
        if (this.targetPlayerId == -1) return null;
        net.minecraft.world.entity.Entity entity = this.level().getEntity(this.targetPlayerId);
        return entity instanceof Player ? (Player) entity : null;
    }

    private boolean isPlayerLookingAtMe(Player player) {
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 toMob = this.getEyePosition().subtract(player.getEyePosition()).normalize();
        double dot = lookVec.dot(toMob);
        if (dot > 0.97) {
            return player.hasLineOfSight(this);
        }
        return false;
    }

    private void teleportInFrontOfPlayer(Player player) {
        Vec3 look = player.getViewVector(1.0F);
        Vec3 targetPosVec = player.position().add(look.x * 10, 0, look.z * 10);
//? if >=1.21.1 {
        BlockPos targetPos = BlockPos.containing(targetPosVec.x, player.getY(), targetPosVec.z);
//?} else {
/*        BlockPos targetPos = new BlockPos((int) targetPosVec.x, (int) player.getY(), (int) targetPosVec.z);
*/
//?}
        BlockPos safePos = null;
        
        for (int dy : new int[]{0, 1, -1, 2, -2, 3, -3}) {
            BlockPos checkPos = targetPos.offset(0, dy, 0);
            if (this.level().getBlockState(checkPos.below()).isFaceSturdy(this.level(), checkPos.below(), net.minecraft.core.Direction.UP) && 
                this.level().getBlockState(checkPos).isAir() && 
                this.level().getBlockState(checkPos.above()).isAir()) {
                safePos = checkPos;
                break;
            }
        }
        
        if (safePos == null) {
            safePos = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetPos);
        }
        
        this.moveTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5, player.getYRot() + 180.0F, player.getXRot());
        this.setYBodyRot(player.getYRot() + 180.0F);
        this.setYHeadRot(player.getYRot() + 180.0F);
        this.navigation.stop();
    }
}