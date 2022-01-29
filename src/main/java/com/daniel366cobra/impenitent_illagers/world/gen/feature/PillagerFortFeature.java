package com.daniel366cobra.impenitent_illagers.world.gen.feature;

import com.daniel366cobra.impenitent_illagers.structure.PillagerFortGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class PillagerFortFeature extends StructureFeature<DefaultFeatureConfig> {
    public PillagerFortFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec, StructureGeneratorFactory.simple(PillagerFortFeature::isFeatureChunk, PillagerFortFeature::addPieces));
    }

    private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos spawnXZPos = context.chunkPos().getCenterAtY(0);
        BlockPos spawnPosHeightCorrected = new BlockPos(context.chunkPos().getStartX(),
                context.chunkGenerator().getHeightInGround(spawnXZPos.getX(), spawnXZPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world()),
                context.chunkPos().getStartZ());

        PillagerFortGenerator.addPieces(context.structureManager(), spawnPosHeightCorrected, blockRotation, collector, context.random(), context.config());
    }

    private static boolean isFeatureChunk(StructureGeneratorFactory.Context<DefaultFeatureConfig> context) {

        int i = context.chunkPos().x >> 4;
        int j = context.chunkPos().z >> 4;
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setSeed((long)(i ^ j << 4) ^ context.seed());
        chunkRandom.nextInt();
        if (chunkRandom.nextInt(5) != 0) {
            return false;
        }

        BlockPos spawnXZPos = context.chunkPos().getCenterAtY(0);
        int landHeight = context.chunkGenerator().getHeightInGround(spawnXZPos.getX(), spawnXZPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world());
        VerticalBlockSample chunkCenterBlockColumn = context.chunkGenerator().getColumnSample(spawnXZPos.getX(), spawnXZPos.getZ(), context.world());
        BlockState topBlockState = chunkCenterBlockColumn.getState(landHeight);

        return topBlockState.getFluidState().isEmpty() && !isVillageOrOutpostNearby(context.chunkGenerator(), context.seed(), context.chunkPos());
    }

    private static boolean isVillageOrOutpostNearby(ChunkGenerator chunkGenerator, long seed, ChunkPos chunkPos) {
        StructureConfig villageStructureConfig = chunkGenerator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
        StructureConfig outpostStructureConfig = chunkGenerator.getStructuresConfig().getForType(StructureFeature.PILLAGER_OUTPOST);

        if (villageStructureConfig == null) {
            return false;
        } else if (outpostStructureConfig == null) {
            return false;
        } else {

            int currentX = chunkPos.x;
            int currentZ = chunkPos.z;

            for (int iterX = currentX - 10; iterX < currentX + 10; iterX++) {
                for (int iterZ = currentZ - 10; iterZ < currentZ + 10; iterZ++) {
                    ChunkPos villageStartChunk = StructureFeature.VILLAGE.getStartChunk(villageStructureConfig, seed, iterX, iterZ);
                    ChunkPos outpostStartChunk = StructureFeature.PILLAGER_OUTPOST.getStartChunk(outpostStructureConfig, seed, iterX, iterZ);

                    boolean isVillageStartChunk = (iterX == villageStartChunk.x && iterZ == villageStartChunk.z);
                    boolean isOutpostStartChunk = (iterX == outpostStartChunk.x && iterZ == outpostStartChunk.z);

                    if (isVillageStartChunk || isOutpostStartChunk) return true;
                }
            }
        }
        return false;
    }



}
