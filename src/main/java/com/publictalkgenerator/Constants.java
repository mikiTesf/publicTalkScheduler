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
        // The calendar object used in the ExcelFileGenerator represent's the 12th month with 0
        monthName.put(0, "ታህሳሥ");
    }

    public static final int MINIMUM_FREE_WEEKS = 3;
    public static final int MINIMUM_ELDERS_LEFT_IN_CONG = 2;
    public static final double PERCENTAGE_OF_FREE_CONGREGATIONS_IN_A_WEEK = 0.5;

    // title(s), labels and column names used in the UI
    public static final String FRAME_TITLE = "የንግግር ፕሮግራም አመንጪ";
    // congregation table column title(s)
    public static final String CONGREGATION_TABLE_ID_TITLE           = "#";
    public static final String CONGREGATION_TABLE_NAME_TITLE         = "ስም";
    public static final String CONGREGATION_TABLE_TOTAL_ELDERS_TITLE = "የሽማግሌዎች ብዛት";
    // talk table column title(s)
    public static final String TALK_TABLE_ID_TITLE          = "#";
    public static final String TALK_TABLE_TITLE_TITLE       = "ርዕስ";
    public static final String TALK_TABLE_TALK_NUMBER_TITLE = "ቁጥር";
    // elder table column title(s)
    public static final String ELDER_TABLE_ID_TITLE           = "#";
    public static final String ELDER_TABLE_FIRST_NAME_TITLE   = "ስም";
    public static final String ELDER_TABLE_MIDDLE_NAME_TITLE  = "የአባት ስም";
    public static final String ELDER_TABLE_LAST_NAME_TITLE    = "የአያት ስም";
    public static final String ELDER_TABLE_PHONE_NUMBER_TITLE = "የስልክ ቁጥር";
    public static final String ELDER_TABLE_TALK_NUMBER_TITLE  = "የንግግር ቁጥር";
    public static final String ELDER_TABLE_CONGREGATION_TITLE = "ጉባኤ";
    // button names
    public static final String ADD_RECORD    = "ጨምር";
    public static final String UPDATE_RECORD = "አዘምን";
    public static final String REMOVE_RECORD = "ሰርዝ";
    // ****************** JOptionPane dialogue messages ******************
    // JOptionPane dialogue box titles
    public static final String SUCCESS_TITLE = "ተሳክቷል";
    public static final String FAIL_TITLE    = "ስተህት";
    // congregation messages
    public static final String CONGREGATION_ADDED_MESSAGE   = "ጉባኤው ተጨምሯል";
    public static final String CONGREGATION_UPDATED_MESSAGE = "የጉባኤው አይነታዎች ተዘምነዋል";
    public static final String CONGREGATION_REMOVED_MESSAGE = "ጉባኤው ተሰርዟል";
    // talk messages
    public static final String TALK_ADDED_MESSAGE   = "ንግግሩ ተጨምሯል";
    public static final String TALK_UPDATED_MESSAGE = "የንግግሩ አይነታዎች ተዘምነዋል";
    public static final String TALK_REMOVED_MESSAGE = "ንግግሩ ተሰርዟል";
    // elder message
    public static final String ELDER_ADDED_MESSAGE   = "ተናጋሪው ተጨምሯል";
    public static final String ELDER_UPDATED_MESSAGE = "የተናጋሪው አይነታዎች ተዘምነዋል";
    public static final String ELDER_REMOVED_MESSAGE = "ተናጋሪው ተሰርዟል";
}
