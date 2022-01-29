package com.daniel366cobra.impenitent_illagers.init;

import com.daniel366cobra.impenitent_illagers.world.structures.PillagerFortFeature;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

public class ModStructureFeatures {


    public static StructureFeature<DefaultFeatureConfig> PILLAGER_FORT = new PillagerFortFeature(DefaultFeatureConfig.CODEC);


    public static void register() {
        FabricStructureBuilder.create(new Identifier(MOD_ID, "pillager_fort"), PILLAGER_FORT)
                .step(GenerationStep.Feature.TOP_LAYER_MODIFICATION)
                .defaultConfig(new StructureConfig(
                        10,
                        5,
                        383621031))
                .adjustsSurface()
                .register();
    }


}
