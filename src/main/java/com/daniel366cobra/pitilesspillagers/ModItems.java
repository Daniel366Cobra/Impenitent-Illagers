package com.daniel366cobra.pitilesspillagers;

import com.daniel366cobra.pitilesspillagers.item.MusketBallItem;
import com.daniel366cobra.pitilesspillagers.item.MusketItem;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.pitilesspillagers.ModEntities.*;
import static com.daniel366cobra.pitilesspillagers.PitilessPillagers.LOGGER;

public class ModItems {

    // Items declaration
    public static ToolItem IRON_KNIFE = new SwordItem(ToolMaterials.IRON, 1, -0.6f, new Item.Settings().group(ItemGroup.COMBAT));
    public static MusketItem MUSKET = new MusketItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(250));

    public static Item MUSKET_BALL = new MusketBallItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(64));

    public static final Item ELITE_PILLAGER_SPAWN_EGG = new SpawnEggItem(ELITE_PILLAGER, 7145737, 	25855, new Item.Settings().group(ItemGroup.MISC));
    public static final Item ARSONIST_PILLAGER_SPAWN_EGG = new SpawnEggItem(ARSONIST_PILLAGER, 	5911571, 	288301, new Item.Settings().group(ItemGroup.MISC));
    public static final Item KIDNAPPER_PILLAGER_SPAWN_EGG = new SpawnEggItem(KIDNAPPER_PILLAGER, 	11447982, 		8141370, new Item.Settings().group(ItemGroup.MISC));
    public static final Item INTERLOPER_PILLAGER_SPAWN_EGG = new SpawnEggItem(INTERLOPER_PILLAGER,7145737, 	25855, new Item.Settings().group(ItemGroup.MISC));

    public static void register() {
        // Registering items
        LOGGER.info("Registering items");
        Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "iron_knife"), IRON_KNIFE);
        Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "musket_ball"), MUSKET_BALL);
        Registry.register(Registry.ITEM, new Identifier("pitilesspillagers","musket"), MUSKET);
        Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "elite_pillager_spawn_egg"), ELITE_PILLAGER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "arsonist_pillager_spawn_egg"), ARSONIST_PILLAGER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "kidnapper_pillager_spawn_egg"), KIDNAPPER_PILLAGER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier("pitilesspillagers", "interloper_pillager_spawn_egg"), INTERLOPER_PILLAGER_SPAWN_EGG);
    }

}
