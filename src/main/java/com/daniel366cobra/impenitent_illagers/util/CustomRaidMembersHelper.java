package com.daniel366cobra.impenitent_illagers.util;

import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;

import java.util.Random;

public class CustomRaidMembersHelper {

    public static int getCount(CustomRaidMemberEnum member, int currentWave, int maxWaveCount, boolean extra) {
        return extra ? member.countInWave[maxWaveCount] : member.countInWave[currentWave];
    }

    public static int getBonusCount(CustomRaidMemberEnum member, Random random, int wave, LocalDifficulty localDifficulty, boolean extra) {
        int i;
        Difficulty difficulty = localDifficulty.getGlobalDifficulty();
        boolean easyDifficulty = difficulty == Difficulty.EASY;
        boolean normalDifficulty = difficulty == Difficulty.NORMAL;
        switch (member) {
            case WITCH: {
                if (!easyDifficulty && wave > 2 && wave != 4) {
                    i = 1;
                    break;
                }
                return 0;
            }
            case PILLAGER:
            case VINDICATOR: {
                if (easyDifficulty) {
                    i = random.nextInt(2);
                    break;
                }
                if (normalDifficulty) {
                    i = 1;
                    break;
                }
                i = 2;
                break;
            }
            case RAVAGER:
            case INTERLOPER: {
                i = !easyDifficulty && extra ? 1 : 0;
                break;
            }
            default: {
                return 0;
            }
        }
        return i > 0 ? random.nextInt(i + 1) : 0;
    }


}