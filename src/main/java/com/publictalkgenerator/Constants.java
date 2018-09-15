package com.publictalkgenerator;

import java.util.HashMap;

public class Constants {
    public static final HashMap<String, Integer> monthNumber;
    public static final HashMap<Integer, String> monthName;
    static {
        monthNumber = new HashMap<>();
        monthNumber.put("ጥር", 1);
        monthNumber.put("የካቲት", 2);
        monthNumber.put("መጋቢት", 3);
        monthNumber.put("ሚያዝያ", 4);
        monthNumber.put("ግንቦት", 5);
        monthNumber.put("ሰኔ", 6);
        monthNumber.put("ሐምሌ", 7);
        monthNumber.put("ነሐሴ", 8);
        monthNumber.put("መስከረም", 9);
        monthNumber.put("ጥቅምት", 10);
        monthNumber.put("ህዳር", 11);
        monthNumber.put("ታህሳሥ", 12);

        monthName = new HashMap<>();
        monthName.put(1, "ጥር");
        monthName.put(2, "የካቲት");
        monthName.put(3, "መጋቢት");
        monthName.put(4, "ሚያዝያ");
        monthName.put(5, "ግንቦት");
        monthName.put(6, "ሰኔ");
        monthName.put(7, "ሐምሌ");
        monthName.put(8, "ነሐሴ");
        monthName.put(9, "መስከረም");
        monthName.put(10, "ጥቅምት");
        monthName.put(11, "ህዳር");
        monthName.put(12, "ታህሳሥ");
    }

    public static final int MINIMUM_FREE_WEEKS = 3;
    public static final int MINIMUM_ELDERS_LEFT_IN_CONG = 2;
    public static final double PERCENTAGE_OF_FREE_CONGREGATIONS_IN_A_WEEK = 0.5;
}
