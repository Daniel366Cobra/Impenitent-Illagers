package com.daniel366cobra.impenitent_illagers.util;

import com.daniel366cobra.impenitent_illagers.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;

public enum CustomRaidMemberEnum {
    VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
    EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
    PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
    ELITE_PILLAGER(ModEntities.MARAUDER_ILLAGER, new int[]{0, 2, 1, 1, 2, 2, 2, 0}),
    WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
    KIDNAPPER(ModEntities.KIDNAPPER_ILLAGER, new int[]{0, 0, 1, 0, 0, 1, 0, 1}),
    ARSONIST(ModEntities.ARSONIST_ILLAGER, new int[]{0, 0, 1, 0, 0, 1, 0, 1}),
    RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2}),
    INTERLOPER(ModEntities.INTERLOPER_ILLAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 1});

    public final EntityType<? extends RaiderEntity> type;
    final int[] countInWave;

    CustomRaidMemberEnum(EntityType<? extends RaiderEntity> type, int[] countInWave) {
        this.type = type;
        this.countInWave = countInWave;
    }

}
