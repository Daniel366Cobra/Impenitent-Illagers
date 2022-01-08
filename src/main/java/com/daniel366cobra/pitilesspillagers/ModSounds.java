package com.daniel366cobra.pitilesspillagers;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.pitilesspillagers.PitilessPillagers.LOGGER;

public class ModSounds {

    public static final Identifier BULLET_HIT_ID = new Identifier("pitilesspillagers:bullet_hit");
    public static SoundEvent BULLET_HIT = new SoundEvent(BULLET_HIT_ID);

    public static final Identifier BULLET_RICOCHET_ID = new Identifier("pitilesspillagers:bullet_ricochet");
    public static SoundEvent BULLET_RICOCHET = new SoundEvent(BULLET_RICOCHET_ID);

    public static final Identifier MUSKET_SHOT_ID = new Identifier("pitilesspillagers:musket_shot");
    public static SoundEvent MUSKET_SHOT = new SoundEvent(MUSKET_SHOT_ID);

    public static final Identifier MUSKET_MISFIRE_ID = new Identifier("pitilesspillagers:musket_misfire");
    public static SoundEvent MUSKET_MISFIRE = new SoundEvent(MUSKET_MISFIRE_ID);

    public static final Identifier MUSKET_COCK_START_ID = new Identifier("pitilesspillagers:musket_cock_start");
    public static SoundEvent MUSKET_COCK_START = new SoundEvent(MUSKET_COCK_START_ID);

    public static final Identifier MUSKET_COCK_HALF_ID = new Identifier("pitilesspillagers:musket_cock_half");
    public static SoundEvent MUSKET_COCK_HALF = new SoundEvent(MUSKET_COCK_HALF_ID);

    public static final Identifier MUSKET_POWDER_POUR_ID = new Identifier("pitilesspillagers:musket_powder_pour");
    public static SoundEvent MUSKET_POWDER_POUR = new SoundEvent(MUSKET_POWDER_POUR_ID);

    public static final Identifier MUSKET_COCK_FULL_ID = new Identifier("pitilesspillagers:musket_cock_full");
    public static SoundEvent MUSKET_COCK_FULL = new SoundEvent(MUSKET_COCK_FULL_ID);

    public static void register() {
        //Registering sounds
        LOGGER.info("Registering sounds");
        Registry.register(Registry.SOUND_EVENT, ModSounds.BULLET_HIT_ID, ModSounds.BULLET_HIT);
        Registry.register(Registry.SOUND_EVENT, ModSounds.BULLET_RICOCHET_ID, ModSounds.BULLET_RICOCHET);
        Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_SHOT_ID, ModSounds.MUSKET_SHOT);
        Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_MISFIRE_ID, ModSounds.MUSKET_MISFIRE);
        Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_COCK_START_ID, ModSounds.MUSKET_COCK_START);
        Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_COCK_HALF_ID, ModSounds.MUSKET_COCK_HALF);
        Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_POWDER_POUR_ID, ModSounds.MUSKET_POWDER_POUR);
        Registry.register(Registry.SOUND_EVENT, ModSounds.MUSKET_COCK_FULL_ID, ModSounds.MUSKET_COCK_FULL);
    }

}
