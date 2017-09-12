package com.benberi.cadesim.server.util;

import java.util.Random;

public class RandomUtils {
    public static int randInt(int min, int max) {
        Random rand = new Random();
       return rand.nextInt((max - min) + 1) + min;
    }
}
