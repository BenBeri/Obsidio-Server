package com.benberi.cadesim.server.model;

import com.benberi.cadesim.server.util.RandomUtils;

import java.util.concurrent.ThreadLocalRandom;

public enum JobbersQuality {
    BASIC(new int[]{4, 7}, new int[]{5, 8}, new int[]{50, 55}, new int[]{1, 2}),
    ELITE(new int[]{10, 15}, new int[]{10, 20}, new int[]{65, 70}, new int[]{2, 3});

    private int[] fixDamageRange;
    private int[] fixBilgeRange;
    private int[] damageAfectBilgeRange;
    private int[] movesPerTurnRange;

    JobbersQuality(int[] fixDamageRange, int[] fixBilgeRange, int[] damageAffectBilgeRange, int[] movesPerTurnRange) {
        this.fixDamageRange = fixDamageRange;
        this.fixBilgeRange = fixBilgeRange;
        this.damageAfectBilgeRange = damageAffectBilgeRange;
        this.movesPerTurnRange = movesPerTurnRange;
    }

    public int rollDamageFix() {
        return RandomUtils.randInt(fixDamageRange[0], fixDamageRange[1]);
    }

}
