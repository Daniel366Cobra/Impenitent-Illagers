package com.daniel366cobra.impenitent_illagers.init;

import com.daniel366cobra.impenitent_illagers.item.BallistaItem;
import com.daniel366cobra.impenitent_illagers.item.MusketBallItem;
import com.daniel366cobra.impenitent_illagers.item.MusketItem;
import com.daniel366cobra.impenitent_illagers.item.SpellCastingItem;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.impenitent_illagers.init.ModEntities.*;
import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.LOGGER;
import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

public class ModItems {

    // Items declaration
    public static ToolItem IRON_KNIFE = new SwordItem(ToolMaterials.IRON, 1, -0.6f, new Item.Settings().group(ItemGroup.COMBAT));
    public static MusketItem FLINTLOCK_MUSKET = new MusketItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(250));
    public static SpellCastingItem SIGIL_OF_VEXING = new SpellCastingItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(32), SpellCastingItem.Spell.VEX);
    public static SpellCastingItem FANG_OF_EVOCATION = new SpellCastingItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(1).maxDamage(32), SpellCastingItem.Spell.FANGS);

    public static Item MUSKET_BALL = new MusketBallItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(64));

    public static Item BALLISTA = new BallistaItem(new Item.Settings().group(ItemGroup.COMBAT).maxCount(1));

    public static final Item MARAUDER_ILLAGER_SPAWN_EGG = new SpawnEggItem(MARAUDER_ILLAGER, 7145737, 	25855, new Item.Settings().group(ItemGroup.MISC));
    public static final Item ARSONIST_ILLAGER_SPAWN_EGG = new SpawnEggItem(ARSONIST_ILLAGER, 	5911571, 	288301, new Item.Settings().group(ItemGroup.MISC));
    public static final Item KIDNAPPER_ILLAGER_SPAWN_EGG = new SpawnEggItem(KIDNAPPER_ILLAGER, 	11447982, 		8141370, new Item.Settings().group(ItemGroup.MISC));
    public static final Item INTERLOPER_ILLAGER_SPAWN_EGG = new SpawnEggItem(INTERLOPER_ILLAGER,7145737, 	25855, new Item.Settings().group(ItemGroup.MISC));

    public static void register() {
        // Registering items
        LOGGER.info("Registering items");
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "iron_knife"), IRON_KNIFE);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "musket_ball"), MUSKET_BALL);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID,"flintlock_musket"), FLINTLOCK_MUSKET);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "ballista"), BALLISTA);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "vexing_sigil"), SIGIL_OF_VEXING);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "evocation_fang"), FANG_OF_EVOCATION);

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "marauder_illager_spawn_egg"), MARAUDER_ILLAGER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "arsonist_illager_spawn_egg"), ARSONIST_ILLAGER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "kidnapper_illager_spawn_egg"), KIDNAPPER_ILLAGER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "interloper_illager_spawn_egg"), INTERLOPER_ILLAGER_SPAWN_EGG);
    }

}
