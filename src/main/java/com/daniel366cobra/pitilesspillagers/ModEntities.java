package com.daniel366cobra.pitilesspillagers;

import com.daniel366cobra.pitilesspillagers.entity.mob.ArsonistPillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.mob.ElitePillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.mob.InterloperPillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.mob.KidnapperPillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.projectile.MusketProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.pitilesspillagers.PitilessPillagers.LOGGER;

public class ModEntities {

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

    public static final EntityType<InterloperPillagerEntity> INTERLOPER_PILLAGER = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("pitilesspillagers", "interloper_pillager"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InterloperPillagerEntity::new).dimensions(EntityDimensions.fixed(0.6f,1.95f)).build()
    );

    public static void registerAttributes() {
        LOGGER.info("Registering entity attributes");
        FabricDefaultAttributeRegistry.register(ELITE_PILLAGER, ElitePillagerEntity.createPillagerAttributes());
        FabricDefaultAttributeRegistry.register(ARSONIST_PILLAGER, ArsonistPillagerEntity.createPillagerAttributes());
        FabricDefaultAttributeRegistry.register(KIDNAPPER_PILLAGER, KidnapperPillagerEntity.createPillagerAttributes());
        FabricDefaultAttributeRegistry.register(INTERLOPER_PILLAGER, InterloperPillagerEntity.createPillagerAttributes());
    }


}
