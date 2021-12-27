package com.daniel366cobra.pitilesspillagers;

import com.daniel366cobra.pitilesspillagers.entity.ArsonistPillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.ElitePillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.KidnapperPillagerEntity;
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

public class PitilessPillagers implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("pitilesspillagers");


	// Entities declaration
	public static final EntityType<MusketProjectileEntity> MUSKET_PROJECTILE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("pitilesspillagers","musket_projectile"),
			FabricEntityTypeBuilder.<MusketProjectileEntity>create(SpawnGroup.MISC, MusketProjectileEntity::new)
					.dimensions(EntityDimensions.fixed(0.4f, 0.4f)).trackedUpdateRate(10).trackRangeBlocks(20).forceTrackedVelocityUpdates(true).build()
	);

	public static final EntityType<ElitePillagerEntity> ELITE_PILLAGER = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("pitilesspillagers", "elite_pillager"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ElitePillagerEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build()
	);


	public static final EntityType<ArsonistPillagerEntity> ARSONIST_PILLAGER = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("pitilesspillagers", "arsonist_pillager"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ArsonistPillagerEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build()
	);

	public static final EntityType<KidnapperPillagerEntity> KIDNAPPER_PILLAGER = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("pitilesspillagers", "kidnapper_pillager"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, KidnapperPillagerEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build()
	);

	// Items declaration
	public static ToolItem IRON_KNIFE = new SwordItem(ToolMaterials.IRON, 1, -0.6f, new Item.Settings().group(ItemGroup.COMBAT));
	public static MusketItem MUSKET = new MusketItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(250));

	public static Item MUSKET_BALL = new MusketBallItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(64));

	public static final Item ELITE_PILLAGER_SPAWN_EGG = new SpawnEggItem(ELITE_PILLAGER, 7145737, 	25855, new Item.Settings().group(ItemGroup.MISC));
	public static final Item ARSONIST_PILLAGER_SPAWN_EGG = new SpawnEggItem(ARSONIST_PILLAGER, 	5911571, 	288301, new Item.Settings().group(ItemGroup.MISC));
	public static final Item KIDNAPPER_PILLAGER_SPAWN_EGG = new SpawnEggItem(KIDNAPPER_PILLAGER, 	11447982, 		8141370, new Item.Settings().group(ItemGroup.MISC));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Pitiless Pillagers v0.0.1 by Daniel366Cobra");

		// Registering items
		Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "iron_knife"), IRON_KNIFE);
		Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "musket_ball"), MUSKET_BALL);
		Registry.register(Registry.ITEM, new Identifier("pitilesspillagers","musket"), MUSKET);
		Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "elite_pillager_spawn_egg"), ELITE_PILLAGER_SPAWN_EGG);
		Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "arsonist_pillager_spawn_egg"), ARSONIST_PILLAGER_SPAWN_EGG);
		Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "kidnapper_pillager_spawn_egg"), KIDNAPPER_PILLAGER_SPAWN_EGG);

		//Registering sounds
		Registry.register(Registry.SOUND_EVENT, ModSounds.BULLET_HIT_ID, ModSounds.BULLET_HIT);
		Registry.register(Registry.SOUND_EVENT, ModSounds.BULLET_RICOCHET_ID, ModSounds.BULLET_RICOCHET);
		Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_SHOT_ID, ModSounds.MUSKET_SHOT);
		Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_MISFIRE_ID, ModSounds.MUSKET_MISFIRE);
		Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_COCK_START_ID, ModSounds.MUSKET_COCK_START);
		Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_COCK_HALF_ID, ModSounds.MUSKET_COCK_HALF);
		Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_COCK_FULL_ID, ModSounds.MUSKET_COCK_FULL);


		FabricDefaultAttributeRegistry.register(ELITE_PILLAGER, ElitePillagerEntity.createPillagerAttributes());
		FabricDefaultAttributeRegistry.register(ARSONIST_PILLAGER, ArsonistPillagerEntity.createPillagerAttributes());
		FabricDefaultAttributeRegistry.register(KIDNAPPER_PILLAGER, KidnapperPillagerEntity.createPillagerAttributes());

	}
}
