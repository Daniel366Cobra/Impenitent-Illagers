package com.daniel366cobra.pitilesspillagers;

import com.daniel366cobra.pitilesspillagers.entity.mob.ArsonistPillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.mob.ElitePillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.mob.InterloperPillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.mob.KidnapperPillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.projectile.MusketProjectileEntity;
import com.daniel366cobra.pitilesspillagers.item.MusketBallItem;
import com.daniel366cobra.pitilesspillagers.item.MusketItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.daniel366cobra.pitilesspillagers.ModItems.*;

public class PitilessPillagers implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("pitilesspillagers");





	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Pitiless Pillagers v0.0.1 by Daniel366Cobra");

		ModItems.register();
		ModSounds.register();
		ModEntities.registerAttributes();

	}
}
