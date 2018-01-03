package me.masonic.mc.Utility;

import java.util.Calendar;
import java.util.Locale;

/**
 * Mason Project
 * 2017-7-7-0007
 */
public class TimeUtil {
    public static int getDayOfMonth() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    public static long getCurrentSTime(long cd) {
        return System.currentTimeMillis() / 1000 + cd;
    }

    public static long getCurrentSTime() {
        return System.currentTimeMillis() / 1000;
    }
}
