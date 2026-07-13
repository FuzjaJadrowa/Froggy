package pl.fuzjajadrowa.froggy.world;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import pl.fuzjajadrowa.froggy.registry.FroggyBlocks;

import java.util.Optional;

public class FroggyHouseGenerator {
    public static void tryGenerate(ServerLevel level, FroggyWorldData data, BlockPos playerPos) {
        if (data.isGenerated()) return;

        RandomSource random = level.getRandom();

        int maxAttempts = 100;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 300 + random.nextDouble() * 700;
            int x = playerPos.getX() + (int) (Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int) (Math.sin(angle) * distance);

            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (surfaceY <= level.getMinBuildHeight() || surfaceY >= level.getMaxBuildHeight() - 20) {
                continue;
            }

            BlockPos targetPos = new BlockPos(x, surfaceY, z);

            var biomeHolder = level.getBiome(targetPos);
            var biomeKey = biomeHolder.unwrapKey().orElse(null);
            if (biomeKey != null) {
                String path = biomeKey.location().getPath();
                if (path.contains("ocean") || path.contains("river") || path.contains("beach")) {
                    continue;
                }
            }

            if (generateStructure(level, targetPos, random)) {
                data.setGenerated(true);
                data.setHousePos(x, surfaceY - 4, z);
                break;
            }
        }
    }

    private static boolean generateStructure(ServerLevel level, BlockPos surfacePos, RandomSource random) {
        StructureTemplateManager manager = level.getServer().getStructureManager();
        
        ResourceLocation structureId = 
            //? if >=1.21.1 {
            ResourceLocation.fromNamespaceAndPath("froggy", "froggy_house");
            //?} else {
            /* new ResourceLocation("froggy", "froggy_house"); */
            //?}

        Optional<StructureTemplate> templateOpt = manager.get(structureId);
        if (templateOpt.isEmpty()) {
            return false;
        }

        StructureTemplate template = templateOpt.get();
        BlockPos placePos = new BlockPos(surfacePos.getX(), surfacePos.getY() - 4, surfacePos.getZ());



        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setIgnoreEntities(false)
                .setMirror(net.minecraft.world.level.block.Mirror.NONE)
                .setRotation(net.minecraft.world.level.block.Rotation.NONE);

        template.placeInWorld(level, placePos, placePos, settings, random, 2);

        net.minecraft.core.Vec3i size = template.getSize();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < size.getX(); dx++) {
            for (int dy = 0; dy < size.getY(); dy++) {
                for (int dz = 0; dz < size.getZ(); dz++) {
                    mutablePos.set(placePos.getX() + dx, placePos.getY() + dy, placePos.getZ() + dz);
                    
                    net.minecraft.world.level.block.state.BlockState blockState = level.getBlockState(mutablePos);
                    if (blockState.is(FroggyBlocks.PLAYER_PAINTING.get())) {
                        BlockEntity be = level.getBlockEntity(mutablePos);
                        if (be == null) {
                            level.setBlockEntity(new pl.fuzjajadrowa.froggy.block.entity.PlayerPaintingBlockEntity(mutablePos.immutable(), blockState));
                        }
                    }

                    BlockEntity be = level.getBlockEntity(mutablePos);
                    if (be instanceof ChestBlockEntity chest) {
                        ResourceLocation lootTableId = 
                            //? if >=1.21.1 {
                            ResourceLocation.fromNamespaceAndPath("froggy", "chests/froggy_house");
                            //?} else {
                            /* new ResourceLocation("froggy", "chests/froggy_house"); */
                            //?}

                        //? if >=1.21.1 {
                        net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> lootTableKey = 
                            net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, lootTableId);
                        chest.setLootTable(lootTableKey, random.nextLong());
                        //?} else {
                        /* chest.setLootTable(lootTableId, random.nextLong()); */
                        //?}
                        chest.setChanged();
                    }
                }
            }
        }

        return true;
    }
}