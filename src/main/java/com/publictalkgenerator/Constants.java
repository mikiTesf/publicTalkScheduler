package com.publictalkgenerator;

import java.util.HashMap;

public class Constants {
    public static final HashMap<String, Integer> AMMonths;
    static {
        AMMonths = new HashMap<>();
        AMMonths.put("ጥር", 1);
        AMMonths.put("የካቲት", 2);
        AMMonths.put("መጋቢት", 3);
        AMMonths.put("ሚያዝያ", 4);
        AMMonths.put("ግንቦት", 5);
        AMMonths.put("ሰኔ", 6);
        AMMonths.put("ሐምሌ", 7);
        AMMonths.put("ነሐሴ", 8);
        AMMonths.put("መስከረም", 9);
        AMMonths.put("ጥቅምት", 10);
        AMMonths.put("ህዳር", 11);
        AMMonths.put("ታህሳሥ", 12);
    }

    public static final int MINIMUM_FREE_WEEKS = 3;
    public static final int MINIMUM_ELDERS_LEFT_IN_CONG = 2;
    public static final double PERCENTAGE_OF_FREE_CONGREGATIONS_IN_A_WEEK = 0.5;
}
