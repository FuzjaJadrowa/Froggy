package pl.fuzjajadrowa.froggy.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FroggyBoredEntity extends BaseFroggyEntity {
    private int approachTimer = 0;
    private boolean spoken = false;

    public FroggyBoredEntity(EntityType<? extends FroggyBoredEntity> entityType, Level level) {
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

        if (this.entityData.get(EFFECT_STATE) != 0) {
            return;
        }

        Player player = this.level().getNearestPlayer(this, 30.0);
        if (player == null || player.isSpectator()) {
            if (this.tickCount > 60) {
                this.discard();
            }
            return;
        }

        this.navigation.moveTo(player, 0.5);
        this.approachTimer++;

        double distSq = this.distanceToSqr(player);
        if (distSq <= 6.25 || this.approachTimer > 160) {
            if (!spoken) {
                spoken = true;
                this.approachTimer = 0; // Reuse as ticks elapsed since speaking
                // Broadcast entity event 60 to play the TTS on the client
                this.level().broadcastEntityEvent(this, (byte) 60);
            }
        }

        if (spoken) {
            this.navigation.stop();
            this.setDeltaMovement(Vec3.ZERO);
            if (this.approachTimer > 80) {
                this.discard();
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 60) {
            playBoredTTS();
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void playBoredTTS() {
        try {
            Class<?> i18nClass = Class.forName("net.minecraft.client.resources.language.I18n");
            String text = (String) i18nClass.getMethod("get", String.class, Object[].class)
                    .invoke(null, "tts.froggy.bored", new Object[0]);
            
            Class<?> narratorClass = Class.forName("com.mojang.text2speech.Narrator");
            Object narrator = narratorClass.getMethod("getNarrator").invoke(null);
            narratorClass.getMethod("say", String.class, boolean.class).invoke(narrator, text, true);
        } catch (Throwable t) {
        }
    }
}