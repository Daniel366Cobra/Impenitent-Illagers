package com.daniel366cobra.impenitent_illagers.init;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

public class ModConfiguredStructures {


        public static ConfiguredStructureFeature<?, ?> CONFIGURED_PILLAGER_FORT = ModStructureFeatures.PILLAGER_FORT
                .configure(DefaultFeatureConfig.DEFAULT);


        public static void registerAndAddToBiomes() {

            Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, new Identifier(MOD_ID, "pillager_fort"), CONFIGURED_PILLAGER_FORT);
            BiomeModifications.addStructure(
                    BiomeSelectors.categories(
                            Biome.Category.PLAINS,
                            Biome.Category.SAVANNA,
                            Biome.Category.SWAMP),
                    RegistryKey.of(
                            Registry.CONFIGURED_STRUCTURE_FEATURE_KEY,
                            BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(ModConfiguredStructures.CONFIGURED_PILLAGER_FORT))
            );
        }
    }


