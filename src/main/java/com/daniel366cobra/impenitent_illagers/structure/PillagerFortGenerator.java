package com.daniel366cobra.impenitent_illagers.structure;

import com.daniel366cobra.impenitent_illagers.init.ModStructurePieces;
import com.daniel366cobra.impenitent_illagers.structure.processors.MossifyAndErodeProcessor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.random.ChunkRandom;

import java.util.Random;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

public class PillagerFortGenerator {

    static final BlockPos DEFAULT_POSITION = new BlockPos(7, 0, 7);

    private static final Identifier[] FORT_VARIANTS = new Identifier[]{
            new Identifier(MOD_ID,"pillager_fort/variant_1"),
            new Identifier(MOD_ID,"pillager_fort/variant_2"),
            new Identifier(MOD_ID,"pillager_fort/variant_3"),
            new Identifier(MOD_ID,"pillager_fort/variant_4")
    };


    public PillagerFortGenerator(ChunkRandom random, int startX, int startZ) {
    }

    public static void addPieces(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, StructurePiecesCollector collector, ChunkRandom random, DefaultFeatureConfig config) {
        Identifier identifier = Util.getRandom(FORT_VARIANTS, random);
        collector.addPiece(new PillagerFortGenerator.Piece(structureManager, identifier, blockPos, blockRotation));
    }

    public static class Piece
            extends SimpleStructurePiece {

        public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation) {
            super(ModStructurePieces.PILLAGER_FORT_PIECE, 0, manager, identifier, identifier.toString(), Piece.createPlacementData(rotation), pos);

        }

        public Piece(StructureManager manager, NbtCompound nbt) {
            super(ModStructurePieces.PILLAGER_FORT_PIECE, nbt, manager, identifier -> Piece.createPlacementData(BlockRotation.valueOf(nbt.getString("Rot"))));

        }

        public Piece(StructureContext structureContext, NbtCompound nbtCompound) {
            this(structureContext.structureManager(), nbtCompound);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            super.writeNbt(context, nbt);
            nbt.putString("Rot", this.placementData.getRotation().name());
        }

        private static StructurePlacementData createPlacementData(BlockRotation rotation) {
            return new StructurePlacementData().setRotation(rotation).setMirror(BlockMirror.NONE).setPosition(DEFAULT_POSITION).addProcessor(new MossifyAndErodeProcessor(0.5f, 0.15f));
        }

        @Override
        protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {

        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos) {

            super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pos);
        }
    }
}
