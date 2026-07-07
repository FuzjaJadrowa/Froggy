package pl.fuzjajadrowa.froggy.entity;

import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import pl.fuzjajadrowa.froggy.registry.FroggyItems;
import pl.fuzjajadrowa.froggy.registry.FroggySounds;

public class FroggySleepingEntity extends BaseFroggyEntity {
    private int disappearTimer = 0;
    private int snoringTimer = 100;

    public FroggySleepingEntity(EntityType<? extends FroggySleepingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
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

        if (this.isScreaming()) {
            this.disappearTimer--;
            if (this.disappearTimer <= 0) {
                this.discard();
            }
        } else {
            this.setDeltaMovement(Vec3.ZERO);

            this.snoringTimer--;
            if (this.snoringTimer <= 0) {
                this.playSound(FroggySounds.SLEEPING.get(), 0.6F, 1.0F);
                this.snoringTimer = 1000 + this.random.nextInt(400);
            }
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.entityData.get(EFFECT_STATE) > 0) {
            super.travel(travelVector);
            return;
        }
        if (!this.isScreaming()) {
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.entityData.get(EFFECT_STATE) > 0) {
            return InteractionResult.PASS;
        }

        if (!this.isScreaming()) {
            if (!this.level().isClientSide()) {
                wakeUp(player);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    private void wakeUp(Player player) {
        this.setScreaming(true);
        this.disappearTimer = 40;

        for (ServerPlayer serverPlayer : this.level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(32.0))) {
            serverPlayer.connection.send(new ClientboundStopSoundPacket(FroggySounds.SLEEPING.get().getLocation(), SoundSource.NEUTRAL));
        }

        int r = this.random.nextInt(3);
        SoundEvent screamSound = r == 0 ? FroggySounds.SCREAM1.get() : (r == 1 ? FroggySounds.SCREAM2.get() : FroggySounds.SCREAM3.get());
        this.playSound(screamSound, 1.0F, 1.0F);

        this.lookAt(player, 180.0F, 180.0F);
        this.setYRot(this.yHeadRot);
    }
}