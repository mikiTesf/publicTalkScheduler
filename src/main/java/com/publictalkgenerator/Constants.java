package com.publictalkgenerator;

import java.util.HashMap;

public class Constants {
    public static final HashMap<Integer, String> AMMonths;
    static {
        AMMonths = new HashMap<>();
        AMMonths.put(1, "ጥር");
        AMMonths.put(2, "የካቲት");
        AMMonths.put(3, "መጋቢት");
        AMMonths.put(4, "ሚያዝያ");
        AMMonths.put(5, "ግንቦት");
        AMMonths.put(6, "ሰኔ");
        AMMonths.put(7, "ሐምሌ");
        AMMonths.put(8, "ነሐሴ");
        AMMonths.put(9, "መስከረም");
        AMMonths.put(10, "ጥቅምት");
        AMMonths.put(11, "ህዳር");
        AMMonths.put(12, "ታህሳሥ");
    }
    public static final int MINIMUM_FREE_WEEKS = 3;
    public static final int MINIMUM_ELDERS_LEFT_IN_CONG = 2;
    public static final double PERCENTAGE_OF_FREE_CONGREGATIONS_IN_A_WEEK = 0.5;
}
