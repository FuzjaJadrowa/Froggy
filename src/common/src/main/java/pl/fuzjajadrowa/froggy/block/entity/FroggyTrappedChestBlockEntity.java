package pl.fuzjajadrowa.froggy.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pl.fuzjajadrowa.froggy.entity.FroggyJumpscareEntity;
import pl.fuzjajadrowa.froggy.registry.FroggyBlockEntities;
import pl.fuzjajadrowa.froggy.registry.FroggyEntities;

public class FroggyTrappedChestBlockEntity extends ChestBlockEntity {
    private boolean triggered = false;
    private int triggerTicks = 0;
    private Player triggerPlayer;
    private int jumpscareEntityId = -1;

    public FroggyTrappedChestBlockEntity(BlockPos pos, BlockState state) {
        super(FroggyBlockEntities.FROGGY_TRAPPED_CHEST.get(), pos, state);
    }

    public void trigger(Player player) {
        if (!this.triggered) {
            this.triggered = true;
            this.triggerPlayer = player;
            this.setChanged();
        }
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!this.triggered) {
            return;
        }

        this.triggerTicks++;

        if (this.triggerTicks == 1) {
            level.blockEvent(pos, state.getBlock(), 1, 1);
            //? if >=1.21.1 {
            level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            //?} else {
            /* level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F); */
            //?}
        } else if (this.triggerTicks == 10) {
            FroggyJumpscareEntity jumpscare = FroggyEntities.JUMPSCARE.get().create(level);
            if (jumpscare != null) {
                double x = pos.getX() + 0.5;
                double y = pos.getY() - 0.7; // Start inside the chest
                double z = pos.getZ() + 0.5;

                float yRot = 0.0f;
                float xRot = 0.0f;
                if (this.triggerPlayer != null) {
                    double dx = this.triggerPlayer.getX() - x;
                    double dz = this.triggerPlayer.getZ() - z;
                    yRot = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;

                    double dy = this.triggerPlayer.getEyeY() - (y + jumpscare.getEyeHeight());
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    xRot = (float) (-(Math.atan2(dy, dist) * 180.0 / Math.PI));
                }

                jumpscare.moveTo(x, y, z, yRot, xRot);
                jumpscare.setYBodyRot(yRot);
                jumpscare.setYHeadRot(yRot);

                jumpscare.setFromChest(true);
                jumpscare.setScreaming(false); // Idle while sliding up

                level.addFreshEntity(jumpscare);
                this.jumpscareEntityId = jumpscare.getId();
            }
        } else if (this.triggerTicks == 20) {
            if (this.jumpscareEntityId != -1) {
                net.minecraft.world.entity.Entity entity = level.getEntity(this.jumpscareEntityId);
                if (entity instanceof FroggyJumpscareEntity jumpscare) {
                    jumpscare.setScreaming(true); // Start screaming after sliding out

                    int r = level.random.nextInt(3);
                    net.minecraft.sounds.SoundEvent screamSound = r == 0 ? pl.fuzjajadrowa.froggy.registry.FroggySounds.SCREAM1.get() : (r == 1 ? pl.fuzjajadrowa.froggy.registry.FroggySounds.SCREAM2.get() : pl.fuzjajadrowa.froggy.registry.FroggySounds.SCREAM3.get());
                    //? if >=1.21.1 {
                    level.playSound(null, pos, screamSound, SoundSource.BLOCKS, 1.0F, 1.0F);
                    //?} else {
                    /* level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, screamSound, SoundSource.BLOCKS, 1.0F, 1.0F); */
                    //?}
                }
            }
        }

        if (this.triggerTicks >= 11 && this.triggerTicks < 50) {
            if (this.jumpscareEntityId != -1) {
                net.minecraft.world.entity.Entity entity = level.getEntity(this.jumpscareEntityId);
                if (entity instanceof FroggyJumpscareEntity jumpscare) {
                    double x = pos.getX() + 0.5;
                    double z = pos.getZ() + 0.5;
                    
                    double y;
                    if (this.triggerTicks < 20) {
                        double progress = (this.triggerTicks - 10) / 10.0;
                        y = pos.getY() - 0.7 + progress * 0.9;
                    } else if (this.triggerTicks < 40) {
                        y = pos.getY() + 0.2;
                    } else {
                        double progress = (this.triggerTicks - 40) / 10.0;
                        y = pos.getY() + 0.2 - progress * 0.9;
                    }

                    float yRot = 0.0f;
                    float xRot = 0.0f;
                    if (this.triggerPlayer != null) {
                        double dx = this.triggerPlayer.getX() - x;
                        double dz = this.triggerPlayer.getZ() - z;
                        yRot = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;

                        double dy = this.triggerPlayer.getEyeY() - (y + jumpscare.getEyeHeight());
                        double dist = Math.sqrt(dx * dx + dz * dz);
                        xRot = (float) (-(Math.atan2(dy, dist) * 180.0 / Math.PI));
                    }

                    jumpscare.moveTo(x, y, z, yRot, xRot);
                    jumpscare.setYBodyRot(yRot);
                    jumpscare.setYHeadRot(yRot);
                }
            }
        }

        if (this.triggerTicks == 50) {
            if (this.jumpscareEntityId != -1) {
                net.minecraft.world.entity.Entity entity = level.getEntity(this.jumpscareEntityId);
                if (entity != null) {
                    entity.discard();
                }
            }
            level.blockEvent(pos, state.getBlock(), 1, 0);
            //? if >=1.21.1 {
            level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            //?} else {
            /* level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F); */
            //?}
        } else if (this.triggerTicks >= 60) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            //? if >=1.21.1 {
            level.explode(null, x, y, z, 2.0F, Level.ExplosionInteraction.NONE);
            //?} else {
            /* level.explode(null, x, y, z, 2.0F, Level.ExplosionInteraction.NONE); */
            //?}

            level.destroyBlock(pos, false);
        }
    }
}