package pl.fuzjajadrowa.froggy.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import pl.fuzjajadrowa.froggy.entity.FroggyEntities;
import pl.fuzjajadrowa.froggy.entity.FroggyJumpscareEntity;
import pl.fuzjajadrowa.froggy.entity.FroggySleepingEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyStalkerEntity;
import pl.fuzjajadrowa.froggy.entity.FroggyBoredEntity;
import pl.fuzjajadrowa.froggy.config.FroggyConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class FroggySpawner {
    private static final WeakHashMap<ServerPlayer, Integer> spawnCooldowns = new WeakHashMap<>();
    private static final WeakHashMap<ServerPlayer, Integer> sleepingCheckCooldowns = new WeakHashMap<>();
    private static int totalOtherSpawns = 0;

    public static void tickPlayer(ServerPlayer player) {
        if (player.isSpectator() || player.isCreative()) {
            return;
        }

        ServerLevel level = player.serverLevel();

        if (FroggyConfig.spawnStalker || FroggyConfig.spawnJumpscare || FroggyConfig.spawnBored) {
            int cooldown = spawnCooldowns.computeIfAbsent(player, p -> getRandomCooldown(player.getRandom()));
            cooldown--;
            if (cooldown <= 0) {
                boolean spawned = trySpawnAmbientFroggy(player, level);
                if (spawned) {
                    spawnCooldowns.put(player, getRandomCooldown(player.getRandom()));
                } else {
                    spawnCooldowns.put(player, 1000);
                }
            } else {
                spawnCooldowns.put(player, cooldown);
            }
        }

        if (FroggyConfig.spawnSleeping) {
            int sleepCooldown = sleepingCheckCooldowns.computeIfAbsent(player, p -> FroggyConfig.sleepingCheckInterval);
            sleepCooldown--;
            if (sleepCooldown <= 0) {
                trySpawnSleepingFroggy(player, level);
                sleepingCheckCooldowns.put(player, FroggyConfig.sleepingCheckInterval);
            } else {
                sleepingCheckCooldowns.put(player, sleepCooldown);
            }
        }
    }

    private static int getRandomCooldown(net.minecraft.util.RandomSource random) {
        int min = FroggyConfig.minSpawnRate;
        int maxRandom = FroggyConfig.maxRandomAdded;
        return min + (maxRandom > 0 ? random.nextInt(maxRandom) : 0);
    }

    private static boolean trySpawnAmbientFroggy(ServerPlayer player, ServerLevel level) {
        net.minecraft.util.RandomSource random = player.getRandom();

        List<String> pool = new ArrayList<>();
        if (FroggyConfig.spawnStalker && FroggyConfig.weightStalker > 0) {
            for (int i = 0; i < FroggyConfig.weightStalker; i++) pool.add("stalker");
        }
        if (FroggyConfig.spawnJumpscare && FroggyConfig.weightJumpscare > 0) {
            for (int i = 0; i < FroggyConfig.weightJumpscare; i++) pool.add("jumpscare");
        }
        if (FroggyConfig.spawnBored && FroggyConfig.weightBored > 0 && totalOtherSpawns >= 3) {
            for (int i = 0; i < FroggyConfig.weightBored; i++) pool.add("bored");
        }

        if (pool.isEmpty()) {
            return false;
        }

        String chosen = pool.get(random.nextInt(pool.size()));
        return switch (chosen) {
            case "stalker" -> trySpawnStalker(player, level);
            case "jumpscare" -> trySpawnJumpscare(player, level);
            case "bored" -> trySpawnBored(player, level);
            default -> false;
        };
    }

    private static boolean trySpawnBored(ServerPlayer player, ServerLevel level) {
        net.minecraft.util.RandomSource random = player.getRandom();
        Vec3 playerPos = player.position();
        Vec3 look = player.getViewVector(1.0F);

        for (int i = 0; i < 30; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 6.0 + random.nextDouble() * 6.0;
            double x = playerPos.x + Math.cos(angle) * distance;
            double z = playerPos.z + Math.sin(angle) * distance;

            BlockPos spawnPos = new BlockPos((int) x, (int) playerPos.y, (int) z);
            spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos);

            if (isValidSpawnSpot(level, spawnPos, player, true)) {
                Vec3 toSpawn = new Vec3(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5).subtract(playerPos).normalize();
                double dot = look.dot(toSpawn);

                if (dot > 0.7) {
                    FroggyBoredEntity bored = FroggyEntities.BORED.get().create(level);
                    if (bored != null) {
                        bored.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                        level.addFreshEntity(bored);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean trySpawnStalker(ServerPlayer player, ServerLevel level) {
        net.minecraft.util.RandomSource random = player.getRandom();
        Vec3 playerPos = player.position();
        
        for (int i = 0; i < 30; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 20.0 + random.nextDouble() * 10.0;
            double x = playerPos.x + Math.cos(angle) * distance;
            double z = playerPos.z + Math.sin(angle) * distance;
            
            BlockPos spawnPos = new BlockPos((int) x, (int) playerPos.y, (int) z);
            spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos);
            
            if (isValidSpawnSpot(level, spawnPos, player, true)) {
                FroggyStalkerEntity stalker = FroggyEntities.STALKER.get().create(level);
                if (stalker != null) {
                    stalker.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                    level.addFreshEntity(stalker);
                    totalOtherSpawns++;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean trySpawnJumpscare(ServerPlayer player, ServerLevel level) {
        net.minecraft.util.RandomSource random = player.getRandom();
        Vec3 playerPos = player.position();
        Vec3 look = player.getViewVector(1.0F);
        
        for (int i = 0; i < 30; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 6.0 + random.nextDouble() * 6.0;
            double x = playerPos.x + Math.cos(angle) * distance;
            double z = playerPos.z + Math.sin(angle) * distance;
            
            BlockPos spawnPos = new BlockPos((int) x, (int) playerPos.y, (int) z);
            spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos);
            
            if (isValidSpawnSpot(level, spawnPos, player, false)) {
                Vec3 toSpawn = new Vec3(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5).subtract(playerPos).normalize();
                double dot = look.dot(toSpawn);
                
                if (dot < 0.3) {
                    FroggyJumpscareEntity jumpscare = FroggyEntities.JUMPSCARE.get().create(level);
                    if (jumpscare != null) {
                        jumpscare.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                        level.addFreshEntity(jumpscare);
                        totalOtherSpawns++;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isValidSpawnSpot(ServerLevel level, BlockPos pos, ServerPlayer player, boolean requireLineOfSight) {
        BlockState belowState = level.getBlockState(pos.below());
        if (!belowState.isSolid() || belowState.is(BlockTags.LEAVES)) {
            return false;
        }
        if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
            return false;
        }
        
        Vec3 playerEye = player.getEyePosition();
        Vec3 spawnEye = new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        BlockHitResult hitResult = level.clip(new net.minecraft.world.level.ClipContext(
            playerEye,
            spawnEye,
            net.minecraft.world.level.ClipContext.Block.VISUAL,
            net.minecraft.world.level.ClipContext.Fluid.NONE,
            player
        ));
        boolean hasLineOfSight = hitResult.getType() == net.minecraft.world.phys.HitResult.Type.MISS;
        
        return hasLineOfSight;
    }

    private static void trySpawnSleepingFroggy(ServerPlayer player, ServerLevel level) {
        BlockPos playerPos = player.blockPosition();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        List<BlockPos> beds = new ArrayList<>();
        
        for (int dx = -16; dx <= 16; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
                for (int dz = -16; dz <= 16; dz++) {
                    mutablePos.set(playerPos.getX() + dx, playerPos.getY() + dy, playerPos.getZ() + dz);
                    BlockState state = level.getBlockState(mutablePos);
                    if (state.is(BlockTags.BEDS)) {
                        if (state.hasProperty(BedBlock.PART) && state.getValue(BedBlock.PART) == BedPart.HEAD) {
                            beds.add(mutablePos.immutable());
                        }
                    }
                }
            }
        }

        if (beds.isEmpty()) {
            return;
        }

        net.minecraft.util.RandomSource random = player.getRandom();
        if (random.nextDouble() < FroggyConfig.sleepingSpawnChance) {
            BlockPos bedPos = beds.get(random.nextInt(beds.size()));
            BlockState bedState = level.getBlockState(bedPos);
            
            boolean alreadyHasSleeping = !level.getEntitiesOfClass(
                FroggySleepingEntity.class, 
                new AABB(bedPos).inflate(1.5)
            ).isEmpty();

            if (!alreadyHasSleeping) {
                FroggySleepingEntity sleeping = FroggyEntities.SLEEPING.get().create(level);
                if (sleeping != null) {
                    Direction facing = bedState.hasProperty(BedBlock.FACING) ? bedState.getValue(BedBlock.FACING) : Direction.NORTH;
                    float yaw = facing.toYRot();
                    sleeping.moveTo(bedPos.getX() + 0.5, bedPos.getY() + 0.56, bedPos.getZ() + 0.5, yaw, 0.0F);
                    sleeping.setYBodyRot(yaw);
                    sleeping.setYHeadRot(yaw);
                    level.addFreshEntity(sleeping);
                    totalOtherSpawns++;
                }
            }
        }
    }
}