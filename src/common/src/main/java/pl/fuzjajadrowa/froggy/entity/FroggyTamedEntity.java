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
                if (state == 1 || FroggyTamedEntity.this.isScreaming() || (state == 2 && FroggyTamedEntity.this.getAttackTarget() != null)) return false;
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
            if (screamTarget != null && screamTarget.isAlive()) {
                this.getLookControl().setLookAt(screamTarget, 30.0F, 30.0F);
                double dx = screamTarget.getX() - this.getX();
                double dz = screamTarget.getZ() - this.getZ();
                float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
                this.setYRot(yaw);
                this.setYHeadRot(yaw);
                this.setYBodyRot(yaw);
            }
            if (screamTimer == 0) {
                this.setScreaming(false);
                this.screamTarget = null;
            }
        }

        int state = this.getTamedState();
        if (state == 1) {
            this.navigation.stop();
        } else if (state == 2) {
            if (screamCooldown == 0 && screamTimer == 0) {
                LivingEntity target = this.getAttackTarget();
                if (target != null) {
                    double distSq = this.distanceToSqr(target);
                    if (distSq <= 36.0) {
                        this.screamTarget = target;
                        this.screamTimer = 30;
                        this.screamCooldown = 120;
                        this.setScreaming(true);
                        this.navigation.stop();
                        
                        double dx = target.getX() - this.getX();
                        double dz = target.getZ() - this.getZ();
                        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
                        this.setYRot(yaw);
                        this.setYHeadRot(yaw);
                        this.setYBodyRot(yaw);

                        int r = this.random.nextInt(3);
                        net.minecraft.sounds.SoundEvent screamSound = r == 0 ? pl.fuzjajadrowa.froggy.registry.FroggySounds.SCREAM1.get() : (r == 1 ? pl.fuzjajadrowa.froggy.registry.FroggySounds.SCREAM2.get() : pl.fuzjajadrowa.froggy.registry.FroggySounds.SCREAM3.get());
                        this.playSound(screamSound, 1.0F, 1.0F);

                        target.hurt(this.damageSources().mobAttack(this), (float) this.getScreamDamage());

                        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            double startX = this.getX();
                            double startY = this.getY() + this.getEyeHeight();
                            double startZ = this.getZ();
                            double endX = target.getX();
                            double endY = target.getY() + target.getBbHeight() / 2.0;
                            double endZ = target.getZ();
                            double diffX = endX - startX;
                            double diffY = endY - startY;
                            double diffZ = endZ - startZ;
                            double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
                            int steps = (int) (dist * 10);
                            for (int i = 0; i <= steps; i++) {
                                double ratio = (double) i / steps;
                                double px = startX + diffX * ratio;
                                double py = startY + diffY * ratio;
                                double pz = startZ + diffZ * ratio;
                                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
                            }
                        }
                    } else {
                        this.navigation.moveTo(target, 1.25D);
                        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    }
                }
            }
        }
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        int state = this.getTamedState();
        if (state == 1 || this.isScreaming()) {
            super.travel(net.minecraft.world.phys.Vec3.ZERO);
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        Optional<UUID> ownerOpt = this.getOwnerUUID();
        if (ownerOpt.isPresent() && ownerOpt.get().equals(player.getUUID())) {
            if (itemStack.is(pl.fuzjajadrowa.froggy.registry.FroggyItems.FLY_IN_A_BOTTLE.get())) {
                if (this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    this.heal(5.0F);
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.GENERIC_EAT, this.getSoundSource(), 1.0F, 1.0F);
                    if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 0.5, this.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
                    }
                    return net.minecraft.world.InteractionResult.sidedSuccess(this.level().isClientSide());
                }
            }

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
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, this.getSoundSource(), 1.0F, 1.0F);
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

    public LivingEntity getAttackTarget() {
        if (this.screamTarget != null && this.screamTarget.isAlive()) {
            return this.screamTarget;
        }

        LivingEntity owner = this.getOwner();
        if (owner != null) {
            LivingEntity playerTarget = owner.getLastHurtMob();
            if (playerTarget != null && playerTarget != owner && playerTarget != this && playerTarget.isAlive() && this.distanceToSqr(playerTarget) <= 144.0 && this.getSensing().hasLineOfSight(playerTarget)) {
                return playerTarget;
            }
        }

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
            return closest;
        }

        return null;
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
            int state = this.frog.getTamedState();
            if (state != 0 && state != 2) {
                return false;
            }
            if (this.frog.isScreaming() || (state == 2 && this.frog.getAttackTarget() != null)) {
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
            int state = this.frog.getTamedState();
            if (state != 0 && state != 2) {
                return false;
            }
            if (this.frog.isScreaming() || (state == 2 && this.frog.getAttackTarget() != null)) {
                return false;
            }
            return this.owner != null && this.owner.isAlive() && this.frog.distanceToSqr(this.owner) > 4.0;
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

        private void teleportToOwner() {
            net.minecraft.core.BlockPos blockpos = this.owner.blockPosition();
            for (int i = 0; i < 10; ++i) {
                int j = this.frog.getRandom().nextInt(7) - 3;
                int k = this.frog.getRandom().nextInt(3) - 1;
                int l = this.frog.getRandom().nextInt(7) - 3;
                int tx = blockpos.getX() + j;
                int ty = blockpos.getY() + k;
                int tz = blockpos.getZ() + l;
                net.minecraft.core.BlockPos targetPos = new net.minecraft.core.BlockPos(tx, ty, tz);
                if (this.frog.level().getBlockState(targetPos).isAir() && 
                    this.frog.level().getBlockState(targetPos.above()).isAir() && 
                    !this.frog.level().getBlockState(targetPos.below()).isAir()) {
                    this.frog.moveTo(tx + 0.5, ty, tz + 0.5, this.frog.getYRot(), this.frog.getXRot());
                    this.frog.getNavigation().stop();
                    return;
                }
            }
        }

        @Override
        public void tick() {
            this.frog.getLookControl().setLookAt(this.owner, 10.0F, 10.0F);
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (this.frog.distanceToSqr(this.owner) >= 144.0) {
                    this.teleportToOwner();
                } else {
                    this.frog.getNavigation().moveTo(this.owner, this.speed);
                }
            }
        }
    }
}