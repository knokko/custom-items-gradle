package nl.knokko.customitems.util;

public class ColorCodes {

    public static String stripColorCodes(String original) {
        StringBuilder uncolored = new StringBuilder();
        int[] originalChars = original.codePoints().toArray();
        for (int index = 0; index < originalChars.length; index++) {
            // 167 is the code of the color character
            if (originalChars[index] == 167) {
                index++;
            } else {
                uncolored.append(new String(originalChars, index, 1));
            }
        }
        return uncolored.toString();
    }
}
