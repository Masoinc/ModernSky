package me.masonic.mc.Utility;

/**
 * Mason Project
 * 2017-7-7-0007
 */
public class TimeUtility {
    public static long getCurrentSTime(long cd) {
        return System.currentTimeMillis() / 1000 + cd;
    }

    public static long getCurrentSTime() {
        return System.currentTimeMillis() / 1000;
    }
}
