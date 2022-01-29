package com.daniel366cobra.impenitent_illagers;

import com.daniel366cobra.impenitent_illagers.init.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImpenitentIllagers implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static final String MOD_ID = "impenitent_illagers";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);




	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info(MOD_ID + " v0.0.1 by Daniel366Cobra");

		ModStructureProcessors.register();
		ModStructurePieces.register();
		ModStructureFeatures.register();
		ModConfiguredStructures.registerAndAddToBiomes();
		ModEntities.register();
		ModEntities.registerAttributes();
		ModItems.register();
		ModSounds.register();

	}
}
