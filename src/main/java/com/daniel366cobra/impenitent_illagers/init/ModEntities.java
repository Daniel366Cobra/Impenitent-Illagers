package com.daniel366cobra.impenitent_illagers.init;

import com.daniel366cobra.impenitent_illagers.entity.mob.*;
import com.daniel366cobra.impenitent_illagers.entity.mount.BallistaEntity;
import com.daniel366cobra.impenitent_illagers.entity.projectile.MusketProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.LOGGER;
import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

public class ModEntities {

    // Entities declaration
    public static final EntityType<MusketProjectileEntity> MUSKET_PROJECTILE = FabricEntityTypeBuilder.<MusketProjectileEntity>create(SpawnGroup.MISC, MusketProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.4f)).trackedUpdateRate(10).trackRangeBlocks(20).forceTrackedVelocityUpdates(true).build();

    public static final EntityType<BallistaEntity> BALLISTA = FabricEntityTypeBuilder.<BallistaEntity>create(SpawnGroup.MISC, BallistaEntity::new)
            .dimensions(EntityDimensions.fixed(1.2f, 2.2f)).build();

    public static final EntityType<FriendlyVexEntity> FRIENDLY_VEX = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FriendlyVexEntity::new)
            .fireImmune().dimensions(EntityDimensions.fixed(0.4f, 0.8f)).trackRangeBlocks(8).build();

    public static final EntityType<MarauderIllagerEntity> MARAUDER_ILLAGER = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MarauderIllagerEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build();

    public static final EntityType<ArsonistIllagerEntity> ARSONIST_ILLAGER = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ArsonistIllagerEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build();

    public static final EntityType<KidnapperIllagerEntity> KIDNAPPER_ILLAGER = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, KidnapperIllagerEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build();

    public static final EntityType<InterloperIllagerEntity> INTERLOPER_ILLAGER = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InterloperIllagerEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f,1.95f)).build();

    public static void register() {
        LOGGER.info("Registering entities");
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "musket_projectile"), MUSKET_PROJECTILE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "ballista"), BALLISTA);

        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "friendly_vex"), FRIENDLY_VEX);

        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "marauder_illager"), MARAUDER_ILLAGER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "arsonist_illager"), ARSONIST_ILLAGER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "kidnapper_illager"), KIDNAPPER_ILLAGER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "interloper_illager"), INTERLOPER_ILLAGER);
    }

    public static void registerAttributes() {
        LOGGER.info("Registering entity attributes");
        FabricDefaultAttributeRegistry.register(FRIENDLY_VEX, FriendlyVexEntity.createVexAttributes());
        FabricDefaultAttributeRegistry.register(MARAUDER_ILLAGER, MarauderIllagerEntity.createPillagerAttributes());
        FabricDefaultAttributeRegistry.register(ARSONIST_ILLAGER, ArsonistIllagerEntity.createPillagerAttributes());
        FabricDefaultAttributeRegistry.register(KIDNAPPER_ILLAGER, KidnapperIllagerEntity.createPillagerAttributes());
        FabricDefaultAttributeRegistry.register(INTERLOPER_ILLAGER, InterloperIllagerEntity.createPillagerAttributes());
    }


}
