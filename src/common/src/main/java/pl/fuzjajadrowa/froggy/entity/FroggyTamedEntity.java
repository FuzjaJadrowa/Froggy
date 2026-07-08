package pl.fuzjajadrowa.froggy.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.Optional;
import java.util.UUID;

public class FroggyTamedEntity extends BaseFroggyEntity {
    public static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(FroggyTamedEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Integer> TAMED_STATE = SynchedEntityData.defineId(FroggyTamedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SCREAM_DAMAGE = SynchedEntityData.defineId(FroggyTamedEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> INVENTORY_SIZE = SynchedEntityData.defineId(FroggyTamedEntity.class, EntityDataSerializers.INT);

    private final net.minecraft.world.SimpleContainer inventory = new net.minecraft.world.SimpleContainer(27);

    private int screamCooldown = 0;
    private int screamTimer = 0;
    private LivingEntity screamTarget = null;

    public FroggyTamedEntity(EntityType<? extends FroggyTamedEntity> entityType, Level level) {
        super(entityType, level);
        this.setInvulnerable(false);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 25.0)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected boolean isInvulnerableByDefault() {
        return false;
    }

//? if >=1.21.1 {
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(TAMED_STATE, 1);
        builder.define(SCREAM_DAMAGE, 5);
        builder.define(INVENTORY_SIZE, 3);
    }
//?} else {
/*    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
        this.entityData.define(TAMED_STATE, 1);
        this.entityData.define(SCREAM_DAMAGE, 5);
        this.entityData.define(INVENTORY_SIZE, 3);
    }
*/
//?}

    public Optional<UUID> getOwnerUUID() {
        return this.entityData.get(OWNER_UUID);
    }

    public void setOwnerUUID(UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public LivingEntity getOwner() {
        try {
            Optional<UUID> uuidOpt = this.getOwnerUUID();
            if (uuidOpt.isPresent()) {
                return this.level().getPlayerByUUID(uuidOpt.get());
            }
        } catch (Exception e) {
        }
        return null;
    }

    public int getTamedState() {
        return this.entityData.get(TAMED_STATE);
    }

    public void setTamedState(int state) {
        this.entityData.set(TAMED_STATE, state);
    }

    public int getScreamDamage() {
        return this.entityData.get(SCREAM_DAMAGE);
    }

    public void setScreamDamage(int damage) {
        this.entityData.set(SCREAM_DAMAGE, damage);
    }

    public int getInventorySize() {
        return this.entityData.get(INVENTORY_SIZE);
    }

    public void setInventorySize(int size) {
        this.entityData.set(INVENTORY_SIZE, size);
    }

    public net.minecraft.world.SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.FloatGoal(this));
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                int state = FroggyTamedEntity.this.getTamedState();
                if (state == 1 || FroggyTamedEntity.this.isScreaming()) return false;
                return super.canUse();
            }
        });
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        if (screamCooldown > 0) {
            screamCooldown--;
        }

        if (screamTimer > 0) {
            screamTimer--;
            this.navigation.stop();
            this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
            if (screamTarget != null && screamTarget.isAlive()) {
                this.getLookControl().setLookAt(screamTarget, 30.0F, 30.0F);
            }
            if (screamTimer == 0) {
                this.setScreaming(false);
                this.screamTarget = null;
            }
        }

        int state = this.getTamedState();
        if (state == 1) {
            this.navigation.stop();
            this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        } else if (state == 2) {
            if (screamCooldown == 0 && screamTimer == 0) {
                java.util.List<net.minecraft.world.entity.Mob> hostiles = this.level().getEntitiesOfClass(
                    net.minecraft.world.entity.Mob.class,
                    this.getBoundingBox().inflate(12.0),
                    entity -> entity instanceof net.minecraft.world.entity.monster.Enemy && entity.isAlive() && this.getSensing().hasLineOfSight(entity)
                );
                if (!hostiles.isEmpty()) {
                    net.minecraft.world.entity.Mob closest = null;
                    double closestDist = Double.MAX_VALUE;
                    for (net.minecraft.world.entity.Mob mob : hostiles) {
                        double dist = this.distanceToSqr(mob);
                        if (dist < closestDist) {
                            closestDist = dist;
                            closest = mob;
                        }
                    }
                    if (closest != null) {
                        this.screamTarget = closest;
                        this.screamTimer = 30;
                        this.screamCooldown = 120;
                        this.setScreaming(true);
                        this.navigation.stop();
                        this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
                        this.getLookControl().setLookAt(closest, 30.0F, 30.0F);

                        int r = this.random.nextInt(3);
                        net.minecraft.sounds.SoundEvent screamSound = r == 0 ? pl.fuzjajadrowa.froggy.registry.FroggySounds.SCREAM1.get() : (r == 1 ? pl.fuzjajadrowa.froggy.registry.FroggySounds.SCREAM2.get() : pl.fuzjajadrowa.froggy.registry.FroggySounds.SCREAM3.get());
                        this.playSound(screamSound, 1.0F, 1.0F);

                        closest.hurt(this.damageSources().mobAttack(this), (float) this.getScreamDamage());
                    }
                }
            }
        }
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        int state = this.getTamedState();
        if (state == 1 || this.isScreaming()) {
            this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        Optional<UUID> ownerOpt = this.getOwnerUUID();
        if (ownerOpt.isPresent() && ownerOpt.get().equals(player.getUUID())) {
            boolean upgraded = false;
            
            if (itemStack.is(pl.fuzjajadrowa.froggy.registry.FroggyItems.SPEAKER_UPGRADE.get())) {
                if (this.getScreamDamage() < 8) {
                    this.setScreamDamage(8);
                    upgraded = true;
                }
            } else if (itemStack.is(pl.fuzjajadrowa.froggy.registry.FroggyItems.MEGAPHONE_UPGRADE.get())) {
                if (this.getScreamDamage() < 13) {
                    this.setScreamDamage(13);
                    upgraded = true;
                }
            } else if (itemStack.is(pl.fuzjajadrowa.froggy.registry.FroggyItems.AMPLIFIER_UPGRADE.get())) {
                if (this.getScreamDamage() < 18) {
                    this.setScreamDamage(18);
                    upgraded = true;
                }
            } else if (itemStack.is(pl.fuzjajadrowa.froggy.registry.FroggyItems.SMALL_POUCH_UPGRADE.get())) {
                if (this.getInventorySize() < 9) {
                    this.setInventorySize(9);
                    upgraded = true;
                }
            } else if (itemStack.is(pl.fuzjajadrowa.froggy.registry.FroggyItems.MEDIUM_POUCH_UPGRADE.get())) {
                if (this.getInventorySize() < 18) {
                    this.setInventorySize(18);
                    upgraded = true;
                }
            } else if (itemStack.is(pl.fuzjajadrowa.froggy.registry.FroggyItems.LARGE_POUCH_UPGRADE.get())) {
                if (this.getInventorySize() < 27) {
                    this.setInventorySize(27);
                    upgraded = true;
                }
            }
            
            if (upgraded) {
                if (!this.level().isClientSide()) {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), 1.0F, 1.2F);
                    if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 0.5, this.getZ(), 10, 0.2, 0.2, 0.2, 0.05);
                    }
                }
                return net.minecraft.world.InteractionResult.sidedSuccess(this.level().isClientSide());
            }
            
            if (!this.level().isClientSide()) {
                if (pl.fuzjajadrowa.froggy.registry.FroggyMenus.openMenuDelegate != null && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    pl.fuzjajadrowa.froggy.registry.FroggyMenus.openMenuDelegate.accept(serverPlayer, this);
                }
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        
        return net.minecraft.world.InteractionResult.PASS;
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide()) {
            net.minecraft.world.Containers.dropContents(this.level(), this.blockPosition(), this.inventory);
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.getOwnerUUID().isPresent()) {
            tag.putUUID("Owner", this.getOwnerUUID().get());
        }
        tag.putInt("TamedState", this.getTamedState());
        tag.putInt("ScreamDamage", this.getScreamDamage());
        tag.putInt("InventorySize", this.getInventorySize());
        
        net.minecraft.nbt.ListTag listTag = new net.minecraft.nbt.ListTag();
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
                net.minecraft.nbt.CompoundTag itemTag = new net.minecraft.nbt.CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                //? if >=1.21.1 {
                stack.save(this.registryAccess(), itemTag);
                //?} else {
                /* stack.save(itemTag); */
                //?}
                listTag.add(itemTag);
            }
        }
        tag.put("Inventory", listTag);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) {
            this.setOwnerUUID(tag.getUUID("Owner"));
        }
        if (tag.contains("TamedState")) {
            this.setTamedState(tag.getInt("TamedState"));
        }
        if (tag.contains("ScreamDamage")) {
            this.setScreamDamage(tag.getInt("ScreamDamage"));
        }
        if (tag.contains("InventorySize")) {
            this.setInventorySize(tag.getInt("InventorySize"));
        }
        
        this.inventory.clearContent();
        if (tag.contains("Inventory", net.minecraft.nbt.Tag.TAG_LIST)) {
            net.minecraft.nbt.ListTag listTag = tag.getList("Inventory", net.minecraft.nbt.Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                net.minecraft.nbt.CompoundTag itemTag = listTag.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < this.inventory.getContainerSize()) {
                    ItemStack stack;
                    //? if >=1.21.1 {
                    stack = ItemStack.parse(this.registryAccess(), itemTag).orElse(ItemStack.EMPTY);
                    //?} else {
                    /* stack = ItemStack.of(itemTag); */
                    //?}
                    this.inventory.setItem(slot, stack);
                }
            }
        }
    }

    public static class FollowOwnerGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final FroggyTamedEntity frog;
        private LivingEntity owner;
        private final double speed;
        private int timeToRecalcPath;

        public FollowOwnerGoal(FroggyTamedEntity frog, double speed) {
            this.frog = frog;
            this.speed = speed;
            this.setFlags(java.util.EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.frog.getTamedState() != 0) {
                return false;
            }
            LivingEntity owner = this.frog.getOwner();
            if (owner == null) {
                return false;
            }
            if (this.frog.distanceToSqr(owner) < 9.0) {
                return false;
            }
            this.owner = owner;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.frog.getTamedState() == 0 && this.owner != null && this.owner.isAlive() && this.frog.distanceToSqr(this.owner) > 4.0;
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
            this.frog.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.frog.getLookControl().setLookAt(this.owner, 10.0F, 10.0F);
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                this.frog.getNavigation().moveTo(this.owner, this.speed);
            }
        }
    }
}