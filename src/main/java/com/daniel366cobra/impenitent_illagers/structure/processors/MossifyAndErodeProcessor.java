package com.daniel366cobra.impenitent_illagers.structure.processors;

import com.daniel366cobra.impenitent_illagers.ImpenitentIllagers;
import com.daniel366cobra.impenitent_illagers.init.ModStructureProcessors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.WallShape;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

public class MossifyAndErodeProcessor extends StructureProcessor {

    public static final Codec<MossifyAndErodeProcessor> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                    Codec.FLOAT.fieldOf("mossiness").forGetter(mossifyAndErodeProcessor -> mossifyAndErodeProcessor.mossiness),
                    Codec.FLOAT.fieldOf("erosion").forGetter(mossifyAndErodeProcessor -> mossifyAndErodeProcessor.erosion))
            .apply(instance, instance.stable(MossifyAndErodeProcessor::new)));

    private final float mossiness;
    private final float erosion;

    private static final Map<Block, Block> MOSSIFICATION_MAP = Map.of(
            Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE,
            Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB,
            Blocks.COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS,
            Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE_WALL
    );

    private static final Map<Block, Block> EROSION_MAP = Map.of(
            Blocks.STONE_BRICKS, Blocks.STONE,
            Blocks.STONE_BRICK_SLAB, Blocks.STONE_SLAB,
            Blocks.STONE_BRICK_STAIRS, Blocks.STONE_STAIRS,
            Blocks.STONE_BRICK_WALL, Blocks.ANDESITE_WALL
    );


    public MossifyAndErodeProcessor(float mossiness, float erosion) {
        this.mossiness = mossiness;
        this.erosion = erosion;
    }

    @Nullable
    @Override
    public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, Structure.StructureBlockInfo structureBlockInfoLocal, Structure.StructureBlockInfo structureBlockInfoWorld, StructurePlacementData data) {

            BlockState blockState = structureBlockInfoWorld.state;
            BlockPos blockPos = structureBlockInfoWorld.pos;
            Random random = data.getRandom(blockPos);

            BlockState blockState2 = null;
            if (structureBlockInfoLocal.pos.getY() <= 3) {
                if (MOSSIFICATION_MAP.containsKey(blockState.getBlock())) {
                    blockState2 = mossifyBlock(blockState, random);
                }
            } else {
                if (EROSION_MAP.containsKey(blockState.getBlock())) {
                    blockState2 = erodeBlock(blockState, random);
                }
            }

            if (blockState2 != null) {
                return new Structure.StructureBlockInfo(blockPos, blockState2, structureBlockInfoWorld.nbt);
            }

        return structureBlockInfoWorld;
    }

    private BlockState erodeBlock(BlockState blockState, Random random) {
        if (random.nextFloat() > erosion) {
            return null;
        } else {
            if (blockState.isIn(BlockTags.STAIRS)) {
                Direction direction = blockState.get(StairsBlock.FACING);
                BlockHalf blockHalf = blockState.get(StairsBlock.HALF);

                return EROSION_MAP.get(blockState.getBlock()).getDefaultState()
                        .with(StairsBlock.FACING, direction).
                        with(StairsBlock.HALF, blockHalf);

            } else if (blockState.isIn(BlockTags.WALLS)) {
                WallShape northShape = blockState.get(WallBlock.NORTH_SHAPE);
                WallShape eastShape = blockState.get(WallBlock.EAST_SHAPE);
                WallShape southShape = blockState.get(WallBlock.SOUTH_SHAPE);
                WallShape westShape = blockState.get(WallBlock.WEST_SHAPE);

                return EROSION_MAP.get(blockState.getBlock()).getDefaultState()
                        .with(WallBlock.NORTH_SHAPE, northShape)
                        .with(WallBlock.EAST_SHAPE, eastShape)
                        .with(WallBlock.SOUTH_SHAPE, southShape)
                        .with(WallBlock.WEST_SHAPE, westShape);
            }

            return EROSION_MAP.get(blockState.getBlock()).getDefaultState();
        }
    }

    @Nullable
    private BlockState mossifyBlock(BlockState blockState, Random random) {

            if (random.nextFloat() > mossiness) {
                return null;
            } else {
                if (blockState.isIn(BlockTags.STAIRS)) {
                    Direction direction = blockState.get(StairsBlock.FACING);
                    BlockHalf blockHalf = blockState.get(StairsBlock.HALF);

                    return MOSSIFICATION_MAP.get(blockState.getBlock()).getDefaultState()
                            .with(StairsBlock.FACING, direction).
                            with(StairsBlock.HALF, blockHalf);

                } else if (blockState.isIn(BlockTags.WALLS)) {
                    WallShape northShape = blockState.get(WallBlock.NORTH_SHAPE);
                    WallShape eastShape = blockState.get(WallBlock.EAST_SHAPE);
                    WallShape southShape = blockState.get(WallBlock.SOUTH_SHAPE);
                    WallShape westShape = blockState.get(WallBlock.WEST_SHAPE);

                    return MOSSIFICATION_MAP.get(blockState.getBlock()).getDefaultState()
                            .with(WallBlock.NORTH_SHAPE, northShape)
                            .with(WallBlock.EAST_SHAPE, eastShape)
                            .with(WallBlock.SOUTH_SHAPE, southShape)
                            .with(WallBlock.WEST_SHAPE, westShape);
                }

                return MOSSIFICATION_MAP.get(blockState.getBlock()).getDefaultState();
            }

    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.MOSSIFY_AND_ERODE_PROCESSOR;
    }
}
