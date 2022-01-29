package nl.knokko.customitems.editor.util;

public class StringLength {

    public static String fixLength(String fullString, int maxLength) {
        if (fullString.length() <= maxLength) return fullString;
        else return fullString.substring(0, maxLength - 3) + "...";
    }
}
